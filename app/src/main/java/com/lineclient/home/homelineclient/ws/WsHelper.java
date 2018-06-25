package com.lineclient.home.homelineclient.ws;

import com.lineclient.home.homelineclient.tools.AESHelper;
import com.lineclient.home.homelineclient.tools.DataUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class WsHelper {

    public static final class ConnectKind{
        public static final String GET_DATA="GET_DATA";
    }

    public static String encryptAESData(String data){

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("username", DataUtils.getUserName());
            JSONObject jsonEn=new JSONObject();
            jsonEn.put("token",DataUtils.getToken());
            jsonEn.put("data",data);
            String dataBody = AESHelper.encryptByBase64(jsonEn.toString(),DataUtils.getAesNetKey());
            jsonObject.put("encryptdata",dataBody);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
