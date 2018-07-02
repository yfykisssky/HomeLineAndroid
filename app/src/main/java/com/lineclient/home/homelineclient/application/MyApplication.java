package com.lineclient.home.homelineclient.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.os.IBinder;

import com.lineclient.home.homelineclient.activity.LoginActivity;
import com.lineclient.home.homelineclient.activity.test;
import com.lineclient.home.homelineclient.net.HttpConnectHelper;
import com.lineclient.home.homelineclient.tools.ShaPreHelper;
import com.lineclient.home.homelineclient.tools.WifiHelper;
import com.lineclient.home.homelineclient.ws.WsService;

import java.util.List;

//import cn.jpush.android.api.JPushInterface;

/**
 * Created by yangfengyuan on 2017/7/24.
 */

public class MyApplication extends Application {

    private WifiHelper wifiHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        test.main();

        HttpConnectHelper.init(this);

        //JPushInterface.setDebugMode(true);
        //JPushInterface.init(this);

        initAutoExe();

    }

    private void initAutoExe() {

        wifiHelper = new WifiHelper();
        wifiHelper.readWifiInfoFromXml(this);
        if(WifiHelper.wifiUse(this)){
            startWifiListen();
        }

    }

    public void startWifiListen() {
        wifiHelper.listenWifiState(this, new WifiHelper.WifiSwitchInterface() {
            @Override
            public void wifiSwitchState(WifiInfo wifiInfo) {
                if(WifiHelper.judgeWifiSame(wifiInfo)){

                }
            }
        });
    }

    public void stopWifiListen() {
        wifiHelper.unlistenWifiState();
    }

    public void loginOut() {

        ShaPreHelper.deleteAllFiles(this);

        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
