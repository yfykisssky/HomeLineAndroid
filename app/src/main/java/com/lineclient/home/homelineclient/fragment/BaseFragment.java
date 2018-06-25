package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.lineclient.home.homelineclient.activity.BaseActivity;

public class BaseFragment extends Fragment {

    private BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) this.getActivity();
    }

    public void showLongToast(String text) {
        baseActivity.showLongToast(text);
    }

    public void showShortToast(String text) {
        baseActivity.showShortToast(text);
    }

    public void showLoading() {
        baseActivity.showLoading();
    }

    public void closeLoading() {
        baseActivity.closeLoading();
    }

}
