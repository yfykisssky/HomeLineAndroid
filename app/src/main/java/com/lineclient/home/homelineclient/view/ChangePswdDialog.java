package com.lineclient.home.homelineclient.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.net.HttpConnectHelper;
import com.lineclient.home.homelineclient.net.NetDataConstants;
import com.lineclient.home.homelineclient.net.ViewInterface;
import com.lineclient.home.homelineclient.tools.MD5Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class ChangePswdDialog extends BaseDialog implements View.OnClickListener {

    private TextView phoneTex;

    private EditText pswdEdit;

    private EditText codeEdit;

    private Button sendCodeBnt;

    private Button confirmBnt;

    @Override
    int getContentViewId() {
        return R.layout.dialog_changepswd;
    }

    public ChangePswdDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {

        phoneTex = findViewById(R.id.phonenum);

        pswdEdit = findViewById(R.id.pswd);

        codeEdit = findViewById(R.id.code);

        sendCodeBnt = findViewById(R.id.sendcode);

        sendCodeBnt.setOnClickListener(this);

        confirmBnt = findViewById(R.id.confirm);

        confirmBnt.setOnClickListener(this);

    }

    public void setData(String phoneNum) {
        phoneTex.setText(phoneNum);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                String pswd = pswdEdit.getText().toString();
                String code = codeEdit.getText().toString();

                if (TextUtils.isEmpty(pswd)) {
                    Toast.makeText(this.getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this.getContext(), "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    changePswd(phoneTex.getText().toString(), pswd, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sendcode:

                timeHandler.post(timeRunnable);

                sendCodeBnt.setEnabled(false);
                sendCodeBnt.setText("重新发送(" + String.valueOf(intTime) + ")");
                try {
                    sendCode(phoneTex.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private int intTime = 30;

    private Handler timeHandler = new Handler();

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {

            intTime--;

            if (intTime > 0) {
                sendCodeBnt.setText("重新发送(" + String.valueOf(intTime) + ")");
                timeHandler.postDelayed(timeRunnable, 1000);
            } else {
                sendCodeBnt.setEnabled(true);
                sendCodeBnt.setText("发送");
                intTime = 30;
            }

        }
    };

    private void sendCode(String phonenum) {

        timeHandler.post(timeRunnable);

        Map<String, String> map = new HashMap<>();

        map.put("username", phonenum);

        HttpConnectHelper.doGet((ViewInterface) this.getContext(), NetDataConstants.SEND_CODE, map, new HttpConnectHelper.ResponseCallBack() {

            @Override
            public void callBack(String body) {

                try {
                    JSONObject jsonObject = new JSONObject(body);
                    jsonObject.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void changePswd(String userName, String pswd, String code){

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("password", MD5Helper.stringToMD5(pswd));
            jsonObject.put("verifycode", code);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        HttpConnectHelper.doAESPost((ViewInterface) this.getContext(), NetDataConstants.CHANGE_PASSWORD, null, jsonObject.toString(), new HttpConnectHelper.ResponseCallBack() {
            @Override
            public void callBack(String body) {

            }
        });

    }

}
