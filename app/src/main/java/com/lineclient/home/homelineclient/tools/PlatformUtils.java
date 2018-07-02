package com.lineclient.home.homelineclient.tools;

import android.content.Context;

import com.lineclient.home.homelineclient.view.RockerView;
import com.lineclient.home.homelineclient.ws.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PlatformUtils {

    private static PlatformUtils instance;
    private WsUtils wsUtils;

    public PlatformUtils(WsUtils wsUtils) {
        this.wsUtils = wsUtils;
    }

    public static PlatformUtils getInstance(WsUtils wsUtils) {

        if (instance == null) {
            synchronized (PlatformUtils.class) {
                if (instance == null) {
                    instance = new PlatformUtils(wsUtils);
                }
            }
        }
        return instance;
    }

    public void handleRockerViewDirection(RockerView.Direction direction) {
        switch (direction) {
            case DIRECTION_LEFT:
                wsUtils.sendWsData(stepLeft());
                break;
            case DIRECTION_RIGHT:
                wsUtils.sendWsData(stepRight());
                break;
            case DIRECTION_UP:
                wsUtils.sendWsData(orderUp());
                break;
            case DIRECTION_DOWN:
                wsUtils.sendWsData(orderDown());
                break;
            case DIRECTION_UP_LEFT:
                wsUtils.sendWsData(stepLeft());
                wsUtils.sendWsData(orderUp());
                break;
            case DIRECTION_UP_RIGHT:
                wsUtils.sendWsData(stepRight());
                wsUtils.sendWsData(orderUp());
                break;
            case DIRECTION_DOWN_LEFT:
                wsUtils.sendWsData(stepLeft());
                wsUtils.sendWsData(orderDown());
                break;
            case DIRECTION_DOWN_RIGHT:
                wsUtils.sendWsData(stepRight());
                wsUtils.sendWsData(orderDown());
                break;
            default:
                break;
        }
    }

    private String orderUp() {
        JSONObject json = new JSONObject();
        try {
            json.put("camera_up", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private String orderDown() {
        JSONObject json = new JSONObject();
        try {
            json.put("camera_down", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private String stepLeft() {
        JSONObject json = new JSONObject();
        try {
            json.put("camera_left", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private String stepRight() {
        JSONObject json = new JSONObject();
        try {
            json.put("camera_right", String.valueOf(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


}
