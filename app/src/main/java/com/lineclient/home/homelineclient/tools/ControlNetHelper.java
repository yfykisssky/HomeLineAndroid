package com.lineclient.home.homelineclient.tools;

import android.content.Context;

import com.lineclient.home.homelineclient.contants.KEYContants;
import com.lineclient.home.homelineclient.net.NetDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class ControlNetHelper {

    public interface ResponseBackInterface{
        void response(String data);
    }

    public static void getData(String data, Context context, final ResponseBackInterface responseBackInterface) throws Exception {

        final String aesKey=ShaPreHelper.readShaPreCrypt("AESNETKEY","key",context, KEYContants.AES_DATA_KEY);
        String token= ShaPreHelper.readShaPreCrypt("USER","token",context,KEYContants.AES_DATA_KEY);
        String userName=ShaPreHelper.readShaPreCrypt("USER","phonenum",context,KEYContants.AES_DATA_KEY);
        final JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("token",token);
            jsonObject.put("data",data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        String postData= AESHelper.encryptByBase64(jsonObject.toString(),aesKey);

        Request request = new Request.Builder().url(NetDataConstants.GETDATA+"?username="+userName).post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),postData)).build();
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String responseData=response.body().string();

                    try {
                        responseData=AESHelper.decryptByBase64(responseData,aesKey);
                        responseBackInterface.response(responseData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

    }

    public static void sendOrder(String data, Context context,final ResponseBackInterface responseBackInterface) throws Exception {

        final String aesKey=ShaPreHelper.readShaPreCrypt("AESNETKEY","key",context, KEYContants.AES_DATA_KEY);
        String token= ShaPreHelper.readShaPreCrypt("USER","token",context,KEYContants.AES_DATA_KEY);
        String userName=ShaPreHelper.readShaPreCrypt("USER","phonenum",context,KEYContants.AES_DATA_KEY);
        final JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("token",token);
            jsonObject.put("order",data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        String postData= AESHelper.encryptByBase64(jsonObject.toString(),aesKey);

        Request request = new Request.Builder().url(NetDataConstants.SEND_ORDER+"?username="+userName).post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),postData)).build();
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String responseData=response.body().string();

                    try {
                        responseData=AESHelper.decryptByBase64(responseData,aesKey);
                        responseBackInterface.response(responseData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

    }

}
