package com.lineclient.home.homelineclient.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.lineclient.home.homelineclient.R.layout
import com.lineclient.home.homelineclient.contants.KEYContants
import com.lineclient.home.homelineclient.contants.XMLContants
import com.lineclient.home.homelineclient.contants.XMLContants.USER_KEY
import com.lineclient.home.homelineclient.net.HttpConnectHelper
import com.lineclient.home.homelineclient.net.NetDataConstants
import com.lineclient.home.homelineclient.tools.DataUtils
import com.lineclient.home.homelineclient.tools.MD5Helper
import com.lineclient.home.homelineclient.tools.RSAHelper.RSAUtils
import com.lineclient.home.homelineclient.tools.ShaPreHelper
import kotlinx.android.synthetic.main.activity_login.bnt_login
import kotlinx.android.synthetic.main.activity_login.phonenum
import kotlinx.android.synthetic.main.activity_login.pswd
import kotlinx.android.synthetic.main.activity_login.vercode
import kotlinx.android.synthetic.main.activity_login.vercodesend
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by yangfengyuan on 2017/7/24.
 */
class LoginActivity : BaseActivity() {
    //private var phoneEdit: EditText? = null
    //private var pswdEdit: EditText? = null
    //private var loginBnt: Button? = null
    //private var verCodeBnt: Button? = null
    //private var verCodeEdit: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)
        initData()
        initView()
    }

    private fun initData() {
        setUseLock(false)
        val token = ShaPreHelper.readShaPreCrypt(
                XMLContants.USER_XML, USER_KEY.TOKEN, this@LoginActivity,
                KEYContants.AES_DATA_KEY
        )
        if (!TextUtils.isEmpty(token)) {
            val aesKey = ShaPreHelper.readShaPreCrypt(
                    XMLContants.USER_XML, USER_KEY.AESKEY, this@LoginActivity,
                    KEYContants.AES_DATA_KEY
            )
            val userName = ShaPreHelper.readShaPreCrypt(
                    XMLContants.USER_XML, USER_KEY.USER_NAME, this@LoginActivity,
                    KEYContants.AES_DATA_KEY
            )
            initTemData(token, aesKey, userName)
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun initTemData(
        token: String,
        aesKey: String,
        userName: String
    ) {
        DataUtils.token = token
        DataUtils.aesNetKey = aesKey
        DataUtils.userName = userName
    }

    private fun initView() {
        bnt_login.setOnClickListener {
            val phoneNum = phonenum.text
                    .toString()
            val pswdStr = pswd.text
                    .toString()
            val verCodeStr = vercode.text
                    .toString()
            if (TextUtils.isEmpty(phoneNum)) {
                showShortToast("手机号不能为空")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pswdStr)) {
                showShortToast("密码不能为空")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(verCodeStr)) {
                showShortToast("验证码不能为空")
                return@setOnClickListener
            }
            getPublicKey(phoneNum, pswdStr, verCodeStr)
        }
        vercodesend.setOnClickListener {
            if (TextUtils.isEmpty(
                            phonenum.text
                                    .toString()
                    )
            ) {
                showShortToast("手机号不能为空")
                return@setOnClickListener
            }
            sendCode(
                    phonenum.text
                            .toString()
            )
        }
    }

    private fun getPublicKey(
        userName: String,
        password: String,
        verCode: String
    ) {
        val map: MutableMap<String, String> =
            HashMap()
        map["username"] = userName
        HttpConnectHelper.doGet(this, NetDataConstants.GETPUBKEY, map) { body ->
            try {
                val jsonObject = JSONObject(body)
                val flag = jsonObject.getBoolean("flag")
                if (flag) {
                    val pubKey = jsonObject.getString("data")
                    toLogin(userName, password, verCode, pubKey)
                }
                showShortToast(jsonObject.getString("msg"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun toLogin(
        userName: String,
        password: String,
        verCode: String,
        publicKey: String
    ) {
        try {
            val genKey =
                RSAUtils.genKeyPair()
            val publicKeyLocal = RSAUtils.getPublicKey(genKey)
            val privateKeyLocal = RSAUtils.getPrivateKey(genKey)
            val jsonObject = JSONObject()
            jsonObject.put("pswd", MD5Helper.stringToMD5(password))
            jsonObject.put("verfycode", verCode)
            jsonObject.put("pubkey", publicKeyLocal)
            val data = RSAUtils.enPubData(jsonObject.toString(), publicKey)
            val map: MutableMap<String, String> =
                HashMap()
            map["username"] = userName
            HttpConnectHelper.doPost(
                    this, NetDataConstants.LOGIN, map, data
            ) { body ->
                var body = body
                try {
                    var jsonObject = JSONObject(body)
                    val flag = jsonObject.getBoolean("flag")
                    if (flag) {
                        body = RSAUtils.dePriData(
                                jsonObject.getString("data"), privateKeyLocal
                        )
                        jsonObject = JSONObject(body)
                        val aesKey = jsonObject.getString("aeskey")
                        val token = jsonObject.getString("token")
                        ShaPreHelper.writeShaPreCrypt(
                                XMLContants.USER_XML, USER_KEY.TOKEN, token,
                                this@LoginActivity, KEYContants.AES_DATA_KEY
                        )
                        ShaPreHelper.writeShaPreCrypt(
                                XMLContants.USER_XML, USER_KEY.AESKEY, aesKey,
                                this@LoginActivity, KEYContants.AES_DATA_KEY
                        )
                        ShaPreHelper.writeShaPreCrypt(
                                XMLContants.USER_XML, USER_KEY.USER_NAME, userName,
                                this@LoginActivity, KEYContants.AES_DATA_KEY
                        )
                        initTemData(token, aesKey, userName)
                        startActivity(
                                Intent(this@LoginActivity, HomeActivity::class.java)
                        )
                        finish()
                    }
                    showShortToast(jsonObject.getString("msg"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val timeHandler = Handler()
    private val timeRunnable: Runnable = object : Runnable {
        var time = 60
        override fun run() {
            vercodesend.isEnabled = false
            vercodesend.text = "($time)秒后发送"
            if (time == 0) {
                time = 60
                vercodesend.isEnabled = true
                vercodesend.text = "发送验证码"
            } else {
                time--
                timeHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun sendCode(phonenum: String) {
        val map: MutableMap<String, String> =
            HashMap()
        map["username"] = phonenum
        HttpConnectHelper.doGet(this, NetDataConstants.SEND_CODE, map) { body ->
            try {
                val jsonObject = JSONObject(body)
                val flag = jsonObject.getBoolean("flag")
                if (flag) {
                    timeHandler.post(timeRunnable)
                }
                showShortToast(jsonObject.getString("msg"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /*  override fun onClick(view: View) {
          when (view.id) {
              id.bnt_login -> {

              }
              id.vercodesend -> {

              }
          }
      }*/
}