package com.lineclient.home.homelineclient.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import com.lineclient.home.homelineclient.R.id
import com.lineclient.home.homelineclient.R.layout
import com.lineclient.home.homelineclient.fragment.MainFragment
import com.lineclient.home.homelineclient.fragment.UserFragment
import com.lineclient.home.homelineclient.fragment.VideoFragment
import kotlinx.android.synthetic.main.activity_home.tab1_bnt
import kotlinx.android.synthetic.main.activity_home.tab1_text
import kotlinx.android.synthetic.main.activity_home.tab2_bnt
import kotlinx.android.synthetic.main.activity_home.tab2_text
import kotlinx.android.synthetic.main.activity_home.tab3_bnt
import kotlinx.android.synthetic.main.activity_home.tab3_text

class HomeActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_home)
        initView()
        initData()
    }

    private fun initData() {
/*
        try {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("test", "test");

            HttpConnectHelper.doAESPost(this, Contants.GETDATA, null, jsonobject.toString(), new HttpConnectHelper.ResponseCallBack() {

                @Override
                public void callBack(String body) {

                    Log.e("data", body);

                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        boolean flag = jsonObject.getBoolean("flag");
                        if (flag) {

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
        }*/
    }

    fun initView() {
        tab1_bnt.setOnClickListener{
            showPage(0)
        }
        tab2_bnt.setOnClickListener{
            showPage(1)
        }
        tab3_bnt.setOnClickListener{
            showPage(2)
        }
        showPage(0)
    }

    private fun showPage(position: Int) {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = MainFragment()
                tab1_text.setTextColor(Color.WHITE)
                tab2_text.setTextColor(Color.BLACK)
                tab3_text.setTextColor(Color.BLACK)
            }
            1 -> {
                fragment = VideoFragment()
                tab1_text.setTextColor(Color.BLACK)
                tab2_text.setTextColor(Color.WHITE)
                tab3_text.setTextColor(Color.BLACK)
            }
            2 -> {
                fragment = UserFragment()
                tab1_text.setTextColor(Color.BLACK)
                tab2_text.setTextColor(Color.BLACK)
                tab3_text.setTextColor(Color.WHITE)
            }
        }
        val fragTran = supportFragmentManager.beginTransaction()
        fragTran.replace(id.fragment_home, fragment!!)
        fragTran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragTran.commit()
    }
}