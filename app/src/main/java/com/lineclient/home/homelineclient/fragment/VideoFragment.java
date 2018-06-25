package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lineclient.home.homelineclient.R;

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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case 0:
                break;
        }
    }
}
