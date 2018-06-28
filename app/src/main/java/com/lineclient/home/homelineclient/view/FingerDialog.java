package com.lineclient.home.homelineclient.view;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lineclient.home.homelineclient.R;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class FingerDialog extends BaseDialog {

    @Override
    int getContentViewId() {
        return R.layout.dialog_finger;
    }

    public FingerDialog(@NonNull Context context) {
        super(context,2.0/3.0,2.0/3.0);
        initView();
    }

    private void initView() {


    }

}
