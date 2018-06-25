package com.lineclient.home.homelineclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.contants.KEYContants;
import com.lineclient.home.homelineclient.contants.XMLContants;
import com.lineclient.home.homelineclient.net.HttpConnectHelper;
import com.lineclient.home.homelineclient.net.NetDataConstants;
import com.lineclient.home.homelineclient.tools.DataUtils;
import com.lineclient.home.homelineclient.tools.MD5Helper;
import com.lineclient.home.homelineclient.tools.RSAHelper;
import com.lineclient.home.homelineclient.tools.ShaPreHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangfengyuan on 2017/7/24.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneEdit;

    private EditText pswdEdit;

    private Button loginBnt;

    private Button verCodeBnt;

    private EditText verCodeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initData();

        initView();

    }

    private void initData() {

        setUseLock(false);

        String token = ShaPreHelper.readShaPreCrypt(XMLContants.USER_XML,  XMLContants.USER_KEY.TOKEN, LoginActivity.this, KEYContants.AES_DATA_KEY);

        if (!TextUtils.isEmpty(token)) {

            String aesKey=ShaPreHelper.readShaPreCrypt(XMLContants.USER_XML,  XMLContants.USER_KEY.AESKEY, LoginActivity.this, KEYContants.AES_DATA_KEY);

            String userName=ShaPreHelper.readShaPreCrypt(XMLContants.USER_XML,  XMLContants.USER_KEY.USER_NAME, LoginActivity.this, KEYContants.AES_DATA_KEY);

            initTemData(token,aesKey,userName);

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            finish();

        }

    }

    private void initTemData(String token,String aesKey,String userName){

        DataUtils.setToken(token);
        DataUtils.setAesNetKey(aesKey);
        DataUtils.setUserName(userName);

    }

    private void initView() {

        phoneEdit = findViewById(R.id.phonenum);

        pswdEdit = findViewById(R.id.pswd);

        loginBnt = findViewById(R.id.bnt_login);

        loginBnt.setOnClickListener(this);

        verCodeBnt = findViewById(R.id.vercodesend);

        verCodeEdit = findViewById(R.id.vercode);

        verCodeBnt.setOnClickListener(this);

    }


    private void getPublicKey(final String userName, final String password, final String verCode) {

        Map<String, String> map = new HashMap<>();

        map.put("username", userName);

        HttpConnectHelper.doGet(this, NetDataConstants.GETPUBKEY, map, new HttpConnectHelper.ResponseCallBack() {

            @Override
            public void callBack(String body) {

                try {
                    JSONObject jsonObject = new JSONObject(body);
                    boolean flag = jsonObject.getBoolean("flag");
                    if (flag) {
                        String pubKey = jsonObject.getString("data");
                        toLogin(userName, password, verCode, pubKey);
                    }
                    showShortToast(jsonObject.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void toLogin(final String userName, String password, String verCode, final String publicKey) {

        try {

            Map<String, Object> genKey = RSAHelper.RSAUtils.genKeyPair();
            String publicKeyLocal = RSAHelper.RSAUtils.getPublicKey(genKey);
            final String privateKeyLocal = RSAHelper.RSAUtils.getPrivateKey(genKey);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pswd", MD5Helper.stringToMD5(password));
            jsonObject.put("verfycode", verCode);
            jsonObject.put("pubkey", publicKeyLocal);

            String data = RSAHelper.RSAUtils.enPubData(jsonObject.toString(), publicKey);

            Map<String, String> map = new HashMap<>();

            map.put("username", userName);

            HttpConnectHelper.doPost(this, NetDataConstants.LOGIN, map, data, new HttpConnectHelper.ResponseCallBack() {

                @Override
                public void callBack(String body) {

                    try {

                        JSONObject jsonObject = new JSONObject(body);
                        boolean flag = jsonObject.getBoolean("flag");
                        if (flag) {
                            body = RSAHelper.RSAUtils.dePriData(jsonObject.getString("data"), privateKeyLocal);
                            jsonObject=new JSONObject(body);
                            String aesKey = jsonObject.getString("aeskey");
                            String token = jsonObject.getString("token");

                            ShaPreHelper.writeShaPreCrypt(XMLContants.USER_XML, XMLContants.USER_KEY.TOKEN, token, LoginActivity.this, KEYContants.AES_DATA_KEY);
                            ShaPreHelper.writeShaPreCrypt(XMLContants.USER_XML, XMLContants.USER_KEY.AESKEY, aesKey, LoginActivity.this, KEYContants.AES_DATA_KEY);
                            ShaPreHelper.writeShaPreCrypt(XMLContants.USER_XML, XMLContants.USER_KEY.USER_NAME, userName, LoginActivity.this, KEYContants.AES_DATA_KEY);

                            initTemData(token,aesKey,userName);

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                            finish();

                        }
                        showShortToast(jsonObject.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Handler timeHandler = new Handler();

    private Runnable timeRunnable = new Runnable() {

        int time = 60;

        @Override
        public void run() {

            verCodeBnt.setEnabled(false);
            verCodeBnt.setText("(" + String.valueOf(time) + ")秒后发送");

            if (time == 0) {
                time = 60;
                verCodeBnt.setEnabled(true);
                verCodeBnt.setText("发送验证码");
            } else {
                time--;
                timeHandler.postDelayed(timeRunnable, 1000);
            }
        }

    };

    private void sendCode(String phonenum) {

        Map<String, String> map = new HashMap<>();

        map.put("username", phonenum);

        HttpConnectHelper.doGet(this, NetDataConstants.SEND_CODE, map, new HttpConnectHelper.ResponseCallBack() {

            @Override
            public void callBack(String body) {

                try {
                    JSONObject jsonObject = new JSONObject(body);
                    boolean flag = jsonObject.getBoolean("flag");
                    if(flag){
                        timeHandler.post(timeRunnable);
                    }
                    showShortToast(jsonObject.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bnt_login:

                String phoneNum = phoneEdit.getText().toString();

                String pswdStr = pswdEdit.getText().toString();

                String verCodeStr = verCodeEdit.getText().toString();

                if (TextUtils.isEmpty(phoneNum)) {
                    showShortToast("手机号不能为空");
                    return;
                }

                if (TextUtils.isEmpty(pswdStr)) {
                    showShortToast("密码不能为空");
                    return;
                }

                if (TextUtils.isEmpty(verCodeStr)) {
                    showShortToast("验证码不能为空");
                    return;
                }

                getPublicKey(phoneNum, pswdStr, verCodeStr);

                break;
            case R.id.vercodesend:

                if (TextUtils.isEmpty(phoneEdit.getText().toString())) {
                    showShortToast("手机号不能为空");
                    return;
                }

                sendCode(phoneEdit.getText().toString());
                break;
        }
    }
}
