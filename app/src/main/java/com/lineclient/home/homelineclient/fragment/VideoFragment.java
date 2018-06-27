package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.view.RockerView;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class VideoFragment extends Fragment implements View.OnClickListener{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_video, container, false);

        initView();

        return view;

    }

    private void initView() {

        RockerView rockerView = view.findViewById(R.id.rockerview);
        if (rockerView!= null) {
            rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
            rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void direction(RockerView.Direction direction) {
                   // mLogLeft.setText("摇动方向 : " + getDirection(direction));
                }

                @Override
                public void onFinish() {
                   // mLogLeft.setText(null);
                }
            });
            rockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
                @Override
                public void onStart() {
                    //mLogRight.setText(null);
                }

                @Override
                public void angle(double angle) {
                    //mLogRight.setText("摇动角度 : " + angle);
                }

                @Override
                public void onFinish() {
                   // mLogRight.setText(null);
                }
            });
        }
    }


    private void handleDirection(RockerView.Direction direction) {
        switch (direction) {
            case DIRECTION_LEFT:

                break;
            case DIRECTION_RIGHT:

                break;
            case DIRECTION_UP:

                break;
            case DIRECTION_DOWN:

                break;
            case DIRECTION_UP_LEFT:

                break;
            case DIRECTION_UP_RIGHT:

                break;
            case DIRECTION_DOWN_LEFT:

                break;
            case DIRECTION_DOWN_RIGHT:

                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case 0:
                break;
        }
    }
}
