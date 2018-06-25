package com.lineclient.home.homelineclient.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.tools.WifiHelper;

public class AutoExeDialog extends BaseDialog implements View.OnClickListener {

    private CheckBox wifiChe;

    private CheckBox gpsChe;

    private MyApplication myApplication;

    @Override
    int getContentViewId() {
        return R.layout.dialog_autoexe;
    }

    public AutoExeDialog(@NonNull Context context) {
        super(context, 1, 1.2);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomDialog);
        myApplication = (MyApplication) getContext().getApplicationContext();
        initView();
    }

    private void initView() {

        wifiChe = findViewById(R.id.wifi_check);
        (findViewById(R.id.wifi_change)).setOnClickListener(this);
        gpsChe = findViewById(R.id.gps_check);
        (findViewById(R.id.gps_change)).setOnClickListener(this);
        (findViewById(R.id.exit)).setOnClickListener(this);
        (findViewById(R.id.confirm)).setOnClickListener(this);
        if (WifiHelper.wifiUse(getContext())) {
            wifiChe.setChecked(true);
        }

    }

    private void changeWifiInFo() {

        WifiInfoDialog wifiInfoDialog = new WifiInfoDialog(this.getContext());
        wifiInfoDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                if (wifiChe.isChecked()) {
                    if (WifiHelper.getSavedWifiInfo() == null) {
                        return;
                    } else {
                        myApplication.startWifiListen();
                        WifiHelper.saveWifiUse(getContext(), true);
                    }
                } else {
                    WifiHelper.saveWifiUse(getContext(), false);
                }

                if (gpsChe.isChecked()) {

                }
                dismiss();
                break;
            case R.id.exit:
                dismiss();
                break;
            case R.id.wifi_change:
                changeWifiInFo();
                break;
            case R.id.gps_change:
                break;
        }
    }

}
