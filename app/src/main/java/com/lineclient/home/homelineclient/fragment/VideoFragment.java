package com.lineclient.home.homelineclient.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.net.HttpConnectHelper;
import com.lineclient.home.homelineclient.net.NetDataConstants;
import com.lineclient.home.homelineclient.net.ViewInterface;
import com.lineclient.home.homelineclient.tools.PlatformUtils;
import com.lineclient.home.homelineclient.view.PlayFrame;
import com.lineclient.home.homelineclient.view.RockerView;
import com.lineclient.home.homelineclient.ws.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private WsUtils wsUtils;
    private PlatformUtils platformUtils;
    private RockerView.Direction directionState;
    private PlayFrame videoPlayerView;
    private Handler directionHandler = new Handler();
    private DirectionRunnable directionRunnable = new DirectionRunnable(this);
    private Button viewConnect;

    private static class DirectionRunnable implements Runnable {

        WeakReference<VideoFragment> weakReference;

        DirectionRunnable(VideoFragment object) {
            weakReference = new WeakReference<>(object);
        }

        @Override
        public void run() {
            VideoFragment object = weakReference.get();
            if (object != null) {
                if (object.directionState != null) {
                    object.platformUtils.handleRockerViewDirection(object.directionState);
                }
                object.directionHandler.postDelayed(object.directionRunnable, 100);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video, container, false);
        wsUtils = WsUtils.getInstance(this.getContext());
        platformUtils = PlatformUtils.getInstance(wsUtils);

        initView();

        initData();

        return view;

    }

    private void initView() {

        initVideo();
        initControl();

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
        wsUtils.startWsService();

        directionHandler.postDelayed(directionRunnable, 200);

    }

    void initVideo() {
        viewConnect = view.findViewById(R.id.video_connect);
        viewConnect.setOnClickListener(this);
        videoPlayerView = view.findViewById(R.id.player_root);
        videoPlayerView.init(this.getContext());

        videoPlayerView.setPath("rtmp://media3.sinovision.net:1935/live/livestream");
        try {
            videoPlayerView.start();
        } catch (IOException e) {
            // Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
            e.printStackTrace();
        }


    }

    void initControl() {
        RockerView rockerView = view.findViewById(R.id.rockerview);
        if (rockerView != null) {
            rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
            rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void direction(RockerView.Direction direction) {
                    directionState = direction;
                }

                @Override
                public void onFinish() {
                    directionState = null;
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_connect:
                viewConnect.setEnabled(false);
                getVideoUrl();
                break;
        }
    }

    public void getVideoUrl() {
        HttpConnectHelper.doAESPost((ViewInterface) this.getActivity(), NetDataConstants.GET_VIDEO_URL, null, null, new HttpConnectHelper.ResponseCallBack() {
            @Override
            public void callBack(String body) {
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.getBoolean("success")) {
                        viewConnect.setVisibility(View.GONE);
                        String url = jsonObject.getString("url");
                        videoPlayerView.setPath(url);
                        try {
                            videoPlayerView.start();
                        } catch (IOException e) {
                           // Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                    } else {
                        viewConnect.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
