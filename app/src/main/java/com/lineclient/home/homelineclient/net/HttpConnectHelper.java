package com.lineclient.home.homelineclient.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.tools.AESHelper;
import com.lineclient.home.homelineclient.tools.DataUtils;
import com.lineclient.home.homelineclient.tools.Debug;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangfengyuan on 2018/2/5.
 */

public class HttpConnectHelper {

    private static Context applicationContext;

    public static void init(Context context) {
        applicationContext = context;
    }

    public interface ResponseCallBack {
        void callBack(String body);
    }

    public static String mapToUrl(Map<String, String> map) {
        String formData = new String();

        if (map != null) {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key;
                String value;
                key = it.next().toString();
                value = map.get(key);
                formData += key + "=" + value + "&";
            }
        }

        if (formData.length() > 0) {
            formData = "?" + formData.substring(0, formData.length() - 1);
        }

        return formData;
    }

    public static void doGet(final ViewInterface viewInterface, String url, Map<String, String> params, final ResponseCallBack responseCallBack) {

        url = url + mapToUrl(params);

        viewInterface.showLoading();

        Request request = new Request.Builder().url(url).addHeader("Connection", "close").build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                viewInterface.closeLoading();
                viewInterface.showErrorMsg(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                viewInterface.closeLoading();
                if (response.isSuccessful()) {

                    String res = response.body().string();

                    responseCallBack.callBack(res);

                }
            }

        });

    }

    public static void doPost(final ViewInterface viewInterface, String url, Map<String, String> params, String postBody, final ResponseCallBack responseCallBack) {

        viewInterface.showLoading();

        url = url + mapToUrl(params);

        try {

            Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), postBody)).addHeader("Connection", "close").build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    viewInterface.closeLoading();
                    viewInterface.showErrorMsg(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    viewInterface.closeLoading();
                    if (response.isSuccessful()) {

                        String res = response.body().string();

                        try {
                            res = AESHelper.decryptByBase64(res, DataUtils.getAesNetKey());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        responseCallBack.callBack(res);

                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            viewInterface.closeLoading();
            viewInterface.showErrorMsg(e.getMessage());
        }


    }

    public static void doAESPost(@NonNull final ViewInterface viewInterface, String url, Map<String, String> params, String postBody, final ResponseCallBack responseCallBack) {

        viewInterface.showLoading();

        if (params == null) {
            params = new HashMap<>();
        }

        params.put("username", DataUtils.getUserName());

        url = url + mapToUrl(params);

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("data", postBody);

            if (!Debug.debug) {
                jsonObject.put("token", DataUtils.getToken());
                postBody = jsonObject.toString();
                postBody = AESHelper.encryptByBase64(postBody, DataUtils.getAesNetKey());
            }

            Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), postBody)).addHeader("Connection", "close").build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    viewInterface.closeLoading();
                    viewInterface.showErrorMsg(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    viewInterface.closeLoading();
                    if (response.isSuccessful()) {

                        try {

                            String res = response.body().string();

                            if (!Debug.debug) {

                                if (!TextUtils.isEmpty(res)) {

                                }

                                res = AESHelper.encryptByBase64(response.body().string(), DataUtils.getAesNetKey());
                                JSONObject jsonRes = new JSONObject(res);

                                if (jsonRes.getBoolean("tokenstate")) {
                                    if (jsonRes.getBoolean("flag")) {
                                        responseCallBack.callBack(res);
                                        viewInterface.showMsg(jsonRes.getString("msg"));
                                    } else {
                                        viewInterface.showErrorMsg(jsonRes.getString("msg"));
                                    }

                                } else {
                                    viewInterface.showErrorMsg("过期,请重新登录");
                                    MyApplication myApplication = (MyApplication) applicationContext.getApplicationContext();
                                    myApplication.loginOut();
                                }
                            } else {
                                responseCallBack.callBack(res);
                            }

                        } catch (Exception e) {
                            viewInterface.showErrorMsg(e.getMessage());
                        }

                    }
                }

            });

        } catch (Exception e) {
            viewInterface.closeLoading();
            viewInterface.showErrorMsg(e.getMessage());
        }
    }


}
