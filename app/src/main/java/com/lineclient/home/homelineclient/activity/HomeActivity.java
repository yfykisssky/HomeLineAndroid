package com.lineclient.home.homelineclient.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.fragment.MainFragment;
import com.lineclient.home.homelineclient.fragment.UserFragment;
import com.lineclient.home.homelineclient.fragment.VideoFragment;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private FragmentManager fragMent;

    private TextView tabTex1;

    private TextView tabTex2;

    private TextView tabTex3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

        initData();

    }

    private void initData() {
/*
        try {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("test", "test");

            HttpConnectHelper.doAESPost(this, Contants.GETDATA, null, jsonobject.toString(), new HttpConnectHelper.ResponseCallBack() {

                @Override
                public void callBack(String body) {

                    Log.e("data", body);

                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        boolean flag = jsonObject.getBoolean("flag");
                        if (flag) {

                        }
                        showShortToast(jsonObject.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }*/


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab1_bnt:
                showPage(0);
                break;
            case R.id.tab2_bnt:
                showPage(1);
                break;
            case R.id.tab3_bnt:
                showPage(2);
                break;
        }
    }

    void initView() {

        findViewById(R.id.tab1_bnt).setOnClickListener(this);

        findViewById(R.id.tab2_bnt).setOnClickListener(this);

        findViewById(R.id.tab3_bnt).setOnClickListener(this);

        tabTex1 = findViewById(R.id.tab1_text);

        tabTex2 = findViewById(R.id.tab2_text);

        tabTex3 = findViewById(R.id.tab3_text);

        fragMent = getSupportFragmentManager();

        showPage(0);

    }

    void showPage(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new MainFragment();
                tabTex1.setTextColor(Color.WHITE);
                tabTex2.setTextColor(Color.BLACK);
                tabTex3.setTextColor(Color.BLACK);
                break;
            case 1:
                fragment = new VideoFragment();
                tabTex1.setTextColor(Color.BLACK);
                tabTex2.setTextColor(Color.WHITE);
                tabTex3.setTextColor(Color.BLACK);
                break;
            case 2:
                fragment = new UserFragment();
                tabTex1.setTextColor(Color.BLACK);
                tabTex2.setTextColor(Color.BLACK);
                tabTex3.setTextColor(Color.WHITE);
                break;
        }

        FragmentTransaction fragTran = fragMent.beginTransaction();
        fragTran.replace(R.id.fragment_home, fragment);
        fragTran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragTran.commit();
    }
}
