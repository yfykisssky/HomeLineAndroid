package com.lineclient.home.homelineclient.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yangfengyuan on 2017/7/21.
 */

public class Tools {

    public static JSONObject Map2JsonObject(Map<String,String> map){

        JSONObject jsonObject=new JSONObject();

        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
            String key;
            String value;
            key=it.next().toString();
            value=(String) map.get(key);
            try {
                jsonObject.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }

}
