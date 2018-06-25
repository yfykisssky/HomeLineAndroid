package com.lineclient.home.homelineclient.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lineclient.home.homelineclient.contants.KEYContants;
import com.lineclient.home.homelineclient.contants.XMLContants;

import static android.content.Context.WIFI_SERVICE;

public class WifiHelper {

    public interface WifiSwitchInterface {
        void wifiSwitchState(WifiInfo wifiInfo);
    }

    private Receiver receiver;
    private WifiSwitchInterface mInterface;
    private Context context;
    private static XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO savedWifiInfo;

    public void listenWifiState(Context context, WifiSwitchInterface mInterface) {
        if (receiver == null) {
            this.mInterface = mInterface;
            this.context = context;
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            receiver = new Receiver();
            context.registerReceiver(receiver, filter);
        }
    }

    public void unlistenWifiState() {
        context.unregisterReceiver(receiver);
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        if (mInterface != null) {
                            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            mInterface.wifiSwitchState(wifiInfo);
                        }
                    }
                }

            }
        }
    }

    public static boolean judgeWifiSame(WifiInfo wifiInfo) {

        if (wifiInfo == null) {
            return false;
        }

        if (savedWifiInfo.getWIFI_BSSID().equals(wifiInfo.getBSSID())
                && savedWifiInfo.getWIFI_MAC().equals(wifiInfo.getMacAddress())) {
            return true;
        }

        return false;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null
                && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static WifiInfo getConnectWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    public static void saveWifiInfo(WifiInfo wifiInfo, Context context) {
        XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO wifi_info = new XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO();
        wifi_info.setWIFI_BSSID(wifiInfo.getBSSID());
        wifi_info.setWIFI_IP(String.valueOf(wifiInfo.getIpAddress()));
        wifi_info.setWIFI_MAC(wifiInfo.getMacAddress());
        wifi_info.setWIFI_SSID(wifiInfo.getSSID());

        ShaPreHelper.writeShaPreCrypt(XMLContants.AUTO_EXECUTE, XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO, new Gson().toJson(wifi_info), context, KEYContants.AES_DATA_KEY);
        savedWifiInfo = wifi_info;
    }

    public static void readWifiInfoFromXml(Context context) {

        String data = ShaPreHelper.readShaPreCrypt(XMLContants.AUTO_EXECUTE, XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO, context, KEYContants.AES_DATA_KEY);

        if (!TextUtils.isEmpty(data)) {
            savedWifiInfo = new Gson().fromJson(data, new TypeToken<XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO>() {
            }.getType());
        }

    }

    public static XMLContants.AUTO_EXECUTE_KEY.WIFI_INFO getSavedWifiInfo() {
        return savedWifiInfo;
    }

    public static boolean wifiUse(Context context) {

        if (!TextUtils.isEmpty(ShaPreHelper.readShaPreCrypt(XMLContants.AUTO_EXECUTE, XMLContants.AUTO_EXECUTE_KEY.RANGE_WIFI, context, KEYContants.AES_DATA_KEY))) {
            return true;
        }
        return false;

    }

    public static void saveWifiUse(Context context, boolean use) {
        if (use) {
            ShaPreHelper.writeShaPreCrypt(XMLContants.AUTO_EXECUTE, XMLContants.AUTO_EXECUTE_KEY.RANGE_WIFI, XMLContants.AUTO_EXECUTE_KEY.RANGE_WIFI, context, KEYContants.AES_DATA_KEY);
        } else {
            ShaPreHelper.writeShaPreCrypt(XMLContants.AUTO_EXECUTE, XMLContants.AUTO_EXECUTE_KEY.RANGE_WIFI, null, context, KEYContants.AES_DATA_KEY);
        }
    }


}
