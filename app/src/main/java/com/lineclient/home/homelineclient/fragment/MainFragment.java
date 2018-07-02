package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.contants.XMLContants;
import com.lineclient.home.homelineclient.tools.ShaPreHelper;
import com.lineclient.home.homelineclient.view.EnviromentBoard;
import com.lineclient.home.homelineclient.ws.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private EditText editRefurbishTime;
    private int refurbishTime;
    private boolean isPause = false;
    private RefushHandler refurbishHandler;
    private RefushRunnable refushRunnable;
    private LinearLayout refreshLayout;
    private LinearLayout refreshAllLayout;
    private ImageView imageChange;
    private TextView refreshStateTex;
    private TextView refreshAllTex;
    private boolean useRefreshAll = false;
    private final String RESRESH_TIME = "RESRESHTIME";
    private final String RESRESH_ALL = "RESRESHALL";
    private final int DEFAULT_REFRESH_TIME = 5;
    private MyApplication myApplication;
    private EnviromentBoard enviromentBoard;
    private WsUtils wsUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        refushRunnable = new RefushRunnable(this);
        refurbishHandler = new RefushHandler();
        myApplication = (MyApplication) getActivity().getApplication();
        //showLoading();
        wsUtils=WsUtils.getInstance(this.getContext());
        wsUtils.addListener(new WsUtils.WSServiceInterface() {

            @Override
            public void serviceConnect() {
                refreshStateTex.setText("绑定成功,连接中……");
            }

            @Override
            public void serviceDisconnect() {
                closeLoading();
                refreshStateTex.setText("解绑成功");
            }

            @Override
            public void onClose() {
                closeLoading();
                refreshStateTex.setText("连接关闭");
            }

            @Override
            public void onError(String error) {
                closeLoading();
                refreshStateTex.setText("错误:" + error);
            }

            @Override
            public void onMessage(String msg) {
                closeLoading();
            }

            @Override
            public void onOpen() {
                closeLoading();
                refreshStateTex.setText("连接成功");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("kind", "get");
                    jsonObject.put("vv", "vv");
                    wsUtils.sendWsData(WsUtils.encryptAESData(jsonObject.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

        initData();

        initView();

        return view;

    }

    private void initData() {

        String refreshkind = ShaPreHelper.readShaPre(XMLContants.REFRESH_XML,XMLContants.REFRESH_DATA.REFRESH_KIND, this.getContext());

        if (!TextUtils.isEmpty(refreshkind)) {
            useRefreshAll = true;
            if (refreshkind.equals(RESRESH_ALL)) {
                startRefreshAll();
            }
        } else {

            useRefreshAll = false;

            String time = ShaPreHelper.readShaPre(XMLContants.REFRESH_XML,XMLContants.REFRESH_DATA.REFRESH_TIME, this.getContext());

            if (TextUtils.isEmpty(time)) {
                refurbishTime = DEFAULT_REFRESH_TIME;
            } else {
                refurbishTime = Integer.parseInt(time);
            }

            refushData();

            startRefreshTime();

        }

    }

    private void startRefreshAll() {

        wsUtils.startWsService();

    }

    private void stopRefreshAll() {

        if (wsUtils.stopWsService()) {
            refreshStateTex.setText("断开成功");
        } else {
            refreshStateTex.setText("尚未连接,不能断开");
        }

    }

    private void initView() {

        enviromentBoard = view.findViewById(R.id.envir_board);
        enviromentBoard.initView(this.getContext());

        imageChange = view.findViewById(R.id.refresh_change);

        imageChange.setOnClickListener(this);

        refreshStateTex = view.findViewById(R.id.refresh_state);

        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshAllLayout = view.findViewById(R.id.refresh_all_layout);

        view.findViewById(R.id.save_refresh_time).setOnClickListener(this);

        view.findViewById(R.id.refresh).setOnClickListener(this);

        view.findViewById(R.id.refreshall_auto_connect).setOnClickListener(this);

        view.findViewById(R.id.refreshall_connect).setOnClickListener(this);

        editRefurbishTime = view.findViewById(R.id.refresh_time);

        editRefurbishTime.setText(String.valueOf(refurbishTime));

        updateRefreshChangeView();

    }

    private void startRefreshTime() {
        refurbishHandler.postDelayed(refushRunnable, refurbishTime);
    }

    private void stopRefreshTime() {
        refurbishHandler.removeCallbacks(refushRunnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_refresh_time:
                String time = editRefurbishTime.getText().toString();
                if (!TextUtils.isEmpty(time)) {
                    refurbishTime = Integer.parseInt(time);
                } else {
                    showShortToast("输入不能为空");
                    return;
                }

                ShaPreHelper.writeShaPre(XMLContants.REFRESH_XML,XMLContants.REFRESH_DATA.REFRESH_TIME, time, this.getContext());
                stopRefreshTime();
                startRefreshTime();
                break;
            case R.id.refresh:
                refushData();
                break;
            case R.id.refreshall_connect:
                startRefreshAll();
                break;
            case R.id.refreshall_auto_connect:
                //refushData();
                break;
            case R.id.refresh_change:
                String refreshKind;
                if (useRefreshAll) {
                    refreshKind = RESRESH_ALL;
                    stopRefreshTime();
                    /*stopRefreshTime();
                    startRefreshAll();*/
                } else {
                    refreshKind = RESRESH_TIME;
                    stopRefreshAll();
                   /* stopRefreshAll();
                    startRefreshTime();*/
                }
                ShaPreHelper.writeShaPre(XMLContants.REFRESH_XML,XMLContants.REFRESH_DATA.REFRESH_KIND, refreshKind, this.getContext());
                updateRefreshChangeView();
                break;
        }
    }

    private void updateRefreshChangeView() {
        if (useRefreshAll) {
            useRefreshAll = false;
            refreshLayout.setVisibility(View.GONE);
            refreshAllLayout.setVisibility(View.VISIBLE);
        } else {
            useRefreshAll = true;
            refreshLayout.setVisibility(View.VISIBLE);
            refreshAllLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        refushData();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    private void refushData() {


    }

    private static class RefushHandler extends Handler {

    }

    private static class RefushRunnable implements Runnable {

        WeakReference<MainFragment> weakReference;

        RefushRunnable(MainFragment object) {
            weakReference = new WeakReference<>(object);
        }

        @Override
        public void run() {
            MainFragment object = weakReference.get();
            if (object != null) {
                if (!object.isPause) {
                    object.refushData();
                    object.refurbishHandler.postDelayed(object.refushRunnable, object.refurbishTime);
                }
            }
        }

    }

}
