package com.lineclient.home.homelineclient.application

import android.app.Application
import android.content.Intent
import android.net.wifi.WifiInfo
import cn.jpush.android.api.JPushInterface
import com.lineclient.home.homelineclient.activity.LoginActivity
import com.lineclient.home.homelineclient.net.HttpConnectHelper
import com.lineclient.home.homelineclient.tools.ShaPreHelper
import com.lineclient.home.homelineclient.tools.WifiHelper
import com.lineclient.home.homelineclient.tools.WifiHelper.WifiSwitchInterface

//import cn.jpush.android.api.JPushInterface;
/**
 * Created by yangfengyuan on 2017/7/24.
 */
class MyApplication : Application() {
    private var wifiHelper: WifiHelper? = null
    override fun onCreate() {
        super.onCreate()
        HttpConnectHelper.init(this)
        JPushInterface.init(this)
        //VLCInit.init(this);
        initAutoExe()
    }

    private fun initAutoExe() {
        wifiHelper = WifiHelper()
        WifiHelper.readWifiInfoFromXml(this)
        if (WifiHelper.wifiUse(this)) {
            startWifiListen()
        }
    }

    fun startWifiListen() {
        wifiHelper?.listenWifiState(this,object: WifiSwitchInterface {
            override fun wifiSwitchState(wifiInfo: WifiInfo?) {
                if (WifiHelper.judgeWifiSame(wifiInfo)) {

                }
            }
        })
    }

    fun stopWifiListen() {
        wifiHelper?.unlistenWifiState()
    }

    fun loginOut() {
        ShaPreHelper.deleteAllFiles(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}