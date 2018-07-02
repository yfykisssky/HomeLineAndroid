package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.tools.PlatformUtils;
import com.lineclient.home.homelineclient.view.RockerView;
import com.lineclient.home.homelineclient.ws.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {

    private View view;
    private WsUtils wsUtils;
    private PlatformUtils platformUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video, container, false);
        wsUtils=WsUtils.getInstance(this.getContext());
        platformUtils=PlatformUtils.getInstance(wsUtils);

        initView();

        initData();

        return view;

    }

    private void initData() {

        wsUtils.addListener(new WsUtils.WSServiceInterface() {
            @Override
            public void serviceConnect() {

            }

            @Override
            public void serviceDisconnect() {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onMessage(String msg) {

            }

            @Override
            public void onOpen() {

            }
        });

    }

    private void initView() {

        RockerView rockerView = view.findViewById(R.id.rockerview);
        if (rockerView != null) {
            rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
            rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void direction(RockerView.Direction direction) {
                    platformUtils.handleRockerViewDirection(direction);
                }

                @Override
                public void onFinish() {

                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }
}
