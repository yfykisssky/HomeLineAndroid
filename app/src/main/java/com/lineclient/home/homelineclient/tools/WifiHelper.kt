package com.lineclient.home.homelineclient.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.NetworkInfo.State.CONNECTED
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lineclient.home.homelineclient.contants.KEYContants
import com.lineclient.home.homelineclient.contants.XMLContants
import com.lineclient.home.homelineclient.contants.XMLContants.AUTO_EXECUTE_KEY
import com.lineclient.home.homelineclient.contants.XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO

class WifiHelper {
    interface WifiSwitchInterface {
        fun wifiSwitchState(wifiInfo: WifiInfo?)
    }

    private var receiver: Receiver? = null
    private var mInterface: WifiSwitchInterface? = null
    private var context: Context? = null
    fun listenWifiState(
        context: Context,
        mInterface: WifiSwitchInterface
    ) {
        if (receiver == null) {
            this.mInterface = mInterface
            this.context = context
            val filter = IntentFilter()
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            receiver = Receiver()
            context.registerReceiver(receiver, filter)
        }
    }

    fun unlistenWifiState() {
        context!!.unregisterReceiver(receiver)
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION == action) {
                val parcelableExtra =
                    intent.getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
                if (parcelableExtra != null) {
                    val networkInfo = parcelableExtra as NetworkInfo
                    val state = networkInfo.state
                    if (state == CONNECTED) {
                        if (mInterface != null) {
                            val wifiInfo =
                                intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO)
                            mInterface!!.wifiSwitchState(wifiInfo)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic var savedWifiInfo: WIFI_INFO? = null
            private set

        fun judgeWifiSame(wifiInfo: WifiInfo?): Boolean {
            if (wifiInfo == null) {
                return false
            }
            return savedWifiInfo?.wifI_BSSID == wifiInfo.bssid && savedWifiInfo?.wifI_MAC == wifiInfo.macAddress
        }

        @JvmStatic fun isWifi(mContext: Context): Boolean {
            val connectivityManager = mContext
                    .getSystemService(
                            Context.CONNECTIVITY_SERVICE
                    ) as ConnectivityManager
            val info = connectivityManager.activeNetworkInfo
            return (info != null
                    && info.type == ConnectivityManager.TYPE_WIFI)
        }

        @JvmStatic fun getConnectWifi(context: Context): WifiInfo {
            val wifiManager =
                context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return wifiManager.connectionInfo
        }

        @JvmStatic fun saveWifiInfo(
            wifiInfo: WifiInfo,
            context: Context?
        ) {
            val wifi_info = WIFI_INFO()
            wifi_info.wifI_BSSID = wifiInfo.bssid
            wifi_info.wifI_IP = wifiInfo.ipAddress
                    .toString()
            wifi_info.wifI_MAC = wifiInfo.macAddress
            wifi_info.wifI_SSID = wifiInfo.ssid
            ShaPreHelper.writeShaPreCrypt(
                    XMLContants.AUTO_EXECUTE, WIFI_INFO,
                    Gson().toJson(wifi_info), context, KEYContants.AES_DATA_KEY
            )
            savedWifiInfo = wifi_info
        }

        fun readWifiInfoFromXml(context: Context?) {
            val data = ShaPreHelper.readShaPreCrypt(
                    XMLContants.AUTO_EXECUTE, WIFI_INFO, context,
                    KEYContants.AES_DATA_KEY
            )
            if (!TextUtils.isEmpty(data)) {
                savedWifiInfo =
                    Gson().fromJson(data, object : TypeToken<WIFI_INFO?>() {}.type)
            }
        }

        @JvmStatic fun wifiUse(context: Context?): Boolean {
            return !TextUtils.isEmpty(
                            ShaPreHelper.readShaPreCrypt(
                                    XMLContants.AUTO_EXECUTE,
                                    AUTO_EXECUTE_KEY.RANGE_WIFI, context,
                                    KEYContants.AES_DATA_KEY
                            )
                    )
        }

        @JvmStatic fun saveWifiUse(
            context: Context?,
            use: Boolean
        ) {
            if (use) {
                ShaPreHelper.writeShaPreCrypt(
                        XMLContants.AUTO_EXECUTE, AUTO_EXECUTE_KEY.RANGE_WIFI,
                        AUTO_EXECUTE_KEY.RANGE_WIFI, context, KEYContants.AES_DATA_KEY
                )
            } else {
                ShaPreHelper.writeShaPreCrypt(
                        XMLContants.AUTO_EXECUTE, AUTO_EXECUTE_KEY.RANGE_WIFI, null,
                        context, KEYContants.AES_DATA_KEY
                )
            }
        }
    }
}