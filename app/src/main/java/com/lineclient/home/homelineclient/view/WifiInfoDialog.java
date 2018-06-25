package com.lineclient.home.homelineclient.view;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.tools.WifiHelper;

public class WifiInfoDialog extends BaseDialog implements View.OnClickListener {

    private TextView wifiNameTex;

    private TextView wifiIPTex;

    private TextView wifiMACTex;

    private TextView wifiBSSIDTex;

    private TextView connectWifiStateTex;

    private Button confirmBnt;

    private WifiInfo wifiInfo;

    @Override
    int getContentViewId() {
        return R.layout.dialog_wifiinfo;
    }

    public WifiInfoDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {

        wifiNameTex = findViewById(R.id.wifi_name);

        wifiIPTex = findViewById(R.id.wifi_ip);

        wifiMACTex = findViewById(R.id.wifi_mac);

        wifiBSSIDTex = findViewById(R.id.wifi_bssid);

        confirmBnt = findViewById(R.id.confirm);

        connectWifiStateTex = findViewById(R.id.connect_wifi_state);

        confirmBnt.setOnClickListener(this);

        if (WifiHelper.isWifi(this.getContext())) {

            connectWifiStateTex.setText("wifi已经连接");

            wifiInfo = WifiHelper.getConnectWifi(this.getContext());

            wifiNameTex.setText(wifiInfo.getSSID());

            wifiIPTex.setText(String.valueOf(wifiInfo.getIpAddress()));

            wifiMACTex.setText(wifiInfo.getMacAddress());

            wifiBSSIDTex.setText(wifiInfo.getBSSID());

        } else {
            connectWifiStateTex.setText("wifi未连接");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                WifiHelper.saveWifiInfo(wifiInfo,getContext());
                dismiss();
                break;
        }
    }

}
