package com.lineclient.home.homelineclient.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.net.ViewInterface;
import com.lineclient.home.homelineclient.tools.FingerHelper;
import com.lineclient.home.homelineclient.view.FingerDialog;
import com.lineclient.home.homelineclient.view.LoadingDialog;

import java.lang.ref.WeakReference;

/**
 * Created by yangfengyuan on 2017/7/24.
 */

public class BaseActivity extends FragmentActivity implements ViewInterface {

    private boolean isPause = false;
    private boolean isUseLock = true;
    private MyApplication myApplication;
    private ViewHandler viewHandler;
    private LoadingDialog loadingDialog;

    private static class ViewHandler extends Handler {

        WeakReference<BaseActivity> weakReference;

        ViewHandler(BaseActivity object) {
            weakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BaseActivity object = weakReference.get();
            if (object != null) {
                if (!object.isPause) {
                    switch (msg.what) {
                        case 0:
                            Toast.makeText(object, (String) msg.obj, Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(object, (String) msg.obj, Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            object.loadingDialog.show();
                            break;
                        case 4:
                            object.loadingDialog.dismiss();
                            break;
                    }
                }
            }

        }

    }

    @Override
    public void showLoading() {
        Message msg = new Message();
        msg.what = 3;
        viewHandler.sendEmptyMessage(3);
    }

    @Override
    public void closeLoading() {
        Message msg = new Message();
        msg.what = 3;
        viewHandler.sendEmptyMessage(4);
    }

    @Override
    public void showErrorMsg(String msg) {
        showLongToast(msg);
    }

    @Override
    public void showMsg(String msg) {
        showShortToast(msg);
    }

    public void setUseLock(boolean useLock) {
        isUseLock = useLock;
    }

    public void showLongToast(String text) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        viewHandler.sendMessage(msg);
    }

    public void showShortToast(String text) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = text;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication) this.getApplication();
        viewHandler = new ViewHandler(this);
        initView();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUseLock) {
            if (isPause) {

                if (FingerHelper.checkUsed(this)) {

                    final FingerDialog fingerDialog = new FingerDialog(this);
                    fingerDialog.show();

                    FingerHelper.startCheckWithRadom(this.getApplicationContext(), new FingerHelper.FingerHelperInterfaceUnLock() {
                        @Override
                        public void success() {
                            fingerDialog.dismiss();
                        }

                        @Override
                        public void failed() {
                            fingerDialog.dismiss();
                        }

                        @Override
                        public void error(String msg) {
                            fingerDialog.dismiss();
                        }
                    });
                }

            }
        }
        isPause = false;
    }
}
