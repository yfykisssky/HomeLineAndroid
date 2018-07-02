package com.lineclient.home.homelineclient.ws;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.lineclient.home.homelineclient.tools.AESHelper;
import com.lineclient.home.homelineclient.tools.DataUtils;
import com.lineclient.home.homelineclient.tools.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WsUtils {

    public interface WSServiceInterface extends WsService.WsGetDataInterface {

        void serviceConnect();

        void serviceDisconnect();

    }

    private List<WSServiceInterface> wsServiceInterfaceList = new ArrayList<>();
    private WsService wsService;
    private ServiceConnection wSServiceConnection;
    private boolean wsConnectState = false;
    private Context context;
    private static WsUtils instance;

    public void addListener(WSServiceInterface wsServiceInterface) {
        synchronized (wsServiceInterfaceList) {
            wsServiceInterfaceList.add(wsServiceInterface);
        }
    }

    public static WsUtils getInstance(Context context) {

        if (instance == null) {
            synchronized (WsUtils.class) {
                if (instance == null) {
                    instance = new WsUtils(context);
                }
            }
        }
        return instance;
    }

    public WsUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public void startWsService() {

        if (!wsConnectState) {
            wSServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    wsService = ((WsService.LocalBinder) service).getService();
                    wsService.setWsGetDataInterface(wsServiceInterface);
                    wsServiceInterface.serviceConnect();
                    wsConnectState = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    wsServiceInterface.serviceDisconnect();
                    wsService = null;
                    wsConnectState = false;
                }
            };

            Intent intent = new Intent(context, WsService.class);

            context.bindService(intent, wSServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }


    private WSServiceInterface wsServiceInterface = new WSServiceInterface() {
        @Override
        public void serviceConnect() {
            handleInterList(0, null);
        }

        @Override
        public void serviceDisconnect() {
            handleInterList(1, null);
        }

        @Override
        public void onClose() {
            handleInterList(2, null);
        }

        @Override
        public void onError(String error) {
            handleInterList(3, error);
        }

        @Override
        public void onMessage(String msg) {
            if (!Debug.debug) {
                msg = WsUtils.decryptAESData(msg);
            }
            handleInterList(4, msg);
        }

        @Override
        public void onOpen() {
            handleInterList(5, null);
        }
    };

    private void handleInterList(int kind, String data) {
        if (wsServiceInterfaceList != null) {
            synchronized (wsServiceInterfaceList) {
                for (int r = 0; r < wsServiceInterfaceList.size(); r++) {
                    WSServiceInterface wsServiceInterface = wsServiceInterfaceList.get(r);
                    if (wsServiceInterface == null) {
                        wsServiceInterfaceList.remove(r);
                    } else {
                        switch (kind) {
                            case 0:
                                wsServiceInterface.serviceConnect();
                                break;
                            case 1:
                                wsServiceInterface.serviceDisconnect();
                                break;
                            case 2:
                                wsServiceInterface.onClose();
                                break;
                            case 3:
                                wsServiceInterface.onError(data);
                                break;
                            case 4:
                                wsServiceInterface.onMessage(data);
                                break;
                            case 5:
                                wsServiceInterface.onOpen();
                                break;
                        }

                    }
                }
            }
        }
    }

    public boolean sendWsData(String data) {
        Log.e("out", data);
        if (wsService != null) {
            if (wsConnectState) {
                if (!Debug.debug) {
                    data = WsUtils.encryptAESData(data);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("data", data);
                        data = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                wsService.sendData(data);
                return true;
            }
        }
        return false;

    }

    public boolean stopWsService() {

        if (wsService != null) {
            if (wsConnectState) {
                wsService.closeConnext();
                context.unbindService(wSServiceConnection);
                return true;
            }
        }
        return false;
    }

    public static String encryptAESData(String data) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", DataUtils.getUserName());
            JSONObject jsonEn = new JSONObject();
            jsonEn.put("token", DataUtils.getToken());
            jsonEn.put("data", data);
            String dataBody = AESHelper.encryptByBase64(jsonEn.toString(), DataUtils.getAesNetKey());
            jsonObject.put("encryptdata", dataBody);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String decryptAESData(String data) {

        try {
            data = AESHelper.decryptByBase64(data, DataUtils.getAesNetKey());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
