package com.lineclient.home.homelineclient.ws;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lineclient.home.homelineclient.net.NetDataConstants;
import com.lineclient.home.homelineclient.tools.Debug;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yangfengyuan on 16/7/28.
 */
public class WsService extends Service {

    private boolean isToTryAuto = true;
    private WsGetDataInterface wsGetDataInterface;
    private URI serviceUrl;
    private WebSocketWorker webSocketWorker;
    private boolean isConnect = false;
    private MsgHandler msgHandler = new MsgHandler(this);

    public interface WsGetDataInterface {

        void onClose();

        void onError(String error);

        void onMessage(String msg);

        void onOpen();

    }

    public class LocalBinder extends Binder {
        public WsService getService() {
            return WsService.this;
        }
    }

    public boolean isConnect() {
        return isConnect;
    }

    public boolean isToTryAuto() {
        return isToTryAuto;
    }

    public void setToTryAuto(boolean toTryAuto) {
        isToTryAuto = toTryAuto;
    }

    public void setWsGetDataInterface(WsGetDataInterface wsGetDataInterface) {
        this.wsGetDataInterface = wsGetDataInterface;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        try {

            serviceUrl = new URI(NetDataConstants.WS_URL);

            startWs();

        } catch (URISyntaxException e) {

            e.printStackTrace();

        }

        return new LocalBinder();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void startWs() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectWSService();

            }
        }).start();

    }

    public boolean retryConnect() {

        try {
            if (!webSocketWorker.connectBlocking()) {
                webSocketWorker.connect();
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void connectWSService() {
        webSocketWorker = new WebSocketWorker(serviceUrl, new Draft_17());
        webSocketWorker.connect();
    }

    public void closeConnext() {
        webSocketWorker.close();
    }

    public void sendData(String msg) {
        webSocketWorker.send(msg);
    }

    private static class MsgHandler extends Handler {

        WeakReference<WsService> weakReference;

        MsgHandler(WsService object) {
            weakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            WsService object = weakReference.get();
            if (object != null) {
                switch (msg.what) {
                    case 0:
                        object.wsGetDataInterface.onClose();
                        break;
                    case 1:
                        object.wsGetDataInterface.onOpen();
                        break;
                    case 2:
                        object.wsGetDataInterface.onError((String) msg.obj);
                        break;
                    case 3:
                        String data = (String) msg.obj;
                        object.wsGetDataInterface.onMessage(data);
                        break;
                }
            }

        }

    }

    private class WebSocketWorker extends WebSocketClient {

        public WebSocketWorker(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        @Override
        public void onClose(int arg0, String arg1, boolean arg2) {
            msgHandler.sendEmptyMessage(0);
            isConnect = false;
        }

        @Override
        public void onError(Exception error) {

            isConnect = false;

            String errorMsg = error.getMessage();

            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = "未知连接错误";
            }

            Message msg = new Message();
            msg.what = 2;
            msg.obj = errorMsg;
            msgHandler.sendMessage(msg);

            if (isToTryAuto) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isConnect) {

                    connectWSService();
                }

            }

        }

        @Override
        public void onMessage(String data) {
            Message msg = new Message();
            msg.what = 3;
            msg.obj = data;
            msgHandler.sendMessage(msg);
        }

        @Override
        public void onOpen(ServerHandshake arg0) {
            msgHandler.sendEmptyMessage(1);
            isConnect = true;
        }

    }
}
