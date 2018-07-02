package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.view.RockerView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {

    private View view;
    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video, container, false);
        myApplication = (MyApplication) getActivity().getApplication();

        initView();

        initData();

        return view;

    }

    private void initData() {

        myApplication.startWsService(new MyApplication.WSServiceInterface() {
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
                    handleDirection(direction);
                }

                @Override
                public void onFinish() {

                }
            });
        }
    }


    private void handleDirection(RockerView.Direction direction) {
        switch (direction) {
            case DIRECTION_LEFT:
                myApplication.sendWsData(stepLeft());
                break;
            case DIRECTION_RIGHT:
                myApplication.sendWsData(stepRight());
                break;
            case DIRECTION_UP:
                myApplication.sendWsData(orderUp());
                break;
            case DIRECTION_DOWN:
                myApplication.sendWsData(orderDown());
                break;
            case DIRECTION_UP_LEFT:
                myApplication.sendWsData(stepLeft());
                myApplication.sendWsData(orderUp());
                break;
            case DIRECTION_UP_RIGHT:
                myApplication.sendWsData(stepRight());
                myApplication.sendWsData(orderUp());
                break;
            case DIRECTION_DOWN_LEFT:
                myApplication.sendWsData(stepLeft());
                myApplication.sendWsData(orderDown());
                break;
            case DIRECTION_DOWN_RIGHT:
                myApplication.sendWsData(stepRight());
                myApplication.sendWsData(orderDown());
                break;
            default:
                break;
        }
    }

    private String orderUp() {
        try {
            JSONObject json = new JSONObject();
            json.put("camera_up", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String orderDown() {
        try {
            JSONObject json = new JSONObject();
            json.put("camera_down", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String stepLeft() {
        try {
            JSONObject json = new JSONObject();
            json.put("camera_left", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String stepRight() {
        try {
            JSONObject json = new JSONObject();
            json.put("camera_right", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }
}
