package com.lineclient.home.homelineclient.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.application.MyApplication;
import com.lineclient.home.homelineclient.net.HttpConnectHelper;
import com.lineclient.home.homelineclient.net.NetDataConstants;
import com.lineclient.home.homelineclient.net.ViewInterface;
import com.lineclient.home.homelineclient.tools.DataUtils;
import com.lineclient.home.homelineclient.view.AutoExeDialog;
import com.lineclient.home.homelineclient.view.ChangePswdDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 2017/7/25.
 */

public class UserFragment extends Fragment implements View.OnClickListener {

    private View view;

    private TextView nameTex;

    private TextView phoneNumTex;

    private Button loginOutBnt;

    private Button changePswdBnt;

    private Button autoExecBnt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        initView();

        return view;

    }

    private void initView() {

        nameTex = view.findViewById(R.id.name);

        nameTex.setText(DataUtils.getUserName());

        phoneNumTex = view.findViewById(R.id.phonenum);

        phoneNumTex.setText(DataUtils.getUserName());

        loginOutBnt = view.findViewById(R.id.loginout);

        loginOutBnt.setOnClickListener(this);

        changePswdBnt = view.findViewById(R.id.changepswd);

        changePswdBnt.setOnClickListener(this);

        autoExecBnt=view.findViewById(R.id.auto_choice);

        autoExecBnt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginout:

                loginOut(DataUtils.getUserName());

                break;
            case R.id.changepswd:

                ChangePswdDialog dialog = new ChangePswdDialog(this.getContext());

                dialog.setData(phoneNumTex.getText().toString());

                dialog.show();

                break;
            case R.id.auto_choice:

                AutoExeDialog autoExeDialog=new AutoExeDialog(getContext());
                autoExeDialog.show();

                break;
        }
    }

    private void loginOut(String userName) {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",userName);
            HttpConnectHelper.doAESPost((ViewInterface)this.getActivity(), NetDataConstants.LOGIN_OUT, null, jsonObject.toString(), new HttpConnectHelper.ResponseCallBack() {
                @Override
                public void callBack(String body) {
                    MyApplication myApplication = (MyApplication)getContext().getApplicationContext();
                    myApplication.loginOut();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
