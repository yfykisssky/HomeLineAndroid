package com.lineclient.home.homelineclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.lineclient.home.homelineclient.R
import com.lineclient.home.homelineclient.R.layout
import com.lineclient.home.homelineclient.application.MyApplication
import com.lineclient.home.homelineclient.net.HttpConnectHelper
import com.lineclient.home.homelineclient.net.NetDataConstants
import com.lineclient.home.homelineclient.net.ViewInterface
import com.lineclient.home.homelineclient.tools.DataUtils
import com.lineclient.home.homelineclient.view.AutoExeDialog
import com.lineclient.home.homelineclient.view.ChangePswdDialog
import kotlinx.android.synthetic.main.fragment_user.auto_choice
import kotlinx.android.synthetic.main.fragment_user.changepswd
import kotlinx.android.synthetic.main.fragment_user.loginout
import kotlinx.android.synthetic.main.fragment_user.name
import kotlinx.android.synthetic.main.fragment_user.phonenum
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by yangfengyuan on 2017/7/25.
 */
class UserFragment : Fragment(){
    private var views: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(layout.fragment_user, container, false)
        initView()
        return views
    }

    private fun initView() {
        name.text = DataUtils.userName
        phonenum.text = DataUtils.userName
        loginout.setOnClickListener{
            DataUtils.userName?.let { it1 -> loginOut(it1) }
        }
        changepswd.setOnClickListener{
            val dialog = ChangePswdDialog(this.context!!)
            dialog.setData(
                    phonenum.text
                            .toString()
            )
            dialog.show()
        }
        auto_choice.setOnClickListener{
            val autoExeDialog = AutoExeDialog(context!!)
            autoExeDialog.show()
        }
    }

    private fun loginOut(userName: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", userName)
            HttpConnectHelper.doAESPost(
                    (this.activity as ViewInterface?)!!, NetDataConstants.LOGIN_OUT, null,
                    jsonObject.toString()
            ) {
                val myApplication =
                    context!!.applicationContext as MyApplication
                myApplication.loginOut()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}