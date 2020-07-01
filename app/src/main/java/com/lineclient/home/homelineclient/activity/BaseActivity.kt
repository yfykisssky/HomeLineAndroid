package com.lineclient.home.homelineclient.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.lineclient.home.homelineclient.application.MyApplication
import com.lineclient.home.homelineclient.net.ViewInterface
import com.lineclient.home.homelineclient.tools.FingerHelper
import com.lineclient.home.homelineclient.tools.FingerHelper.FingerHelperInterfaceUnLock
import com.lineclient.home.homelineclient.view.FingerDialog
import com.lineclient.home.homelineclient.view.LoadingDialog
import java.lang.ref.WeakReference

/**
 * Created by yangfengyuan on 2017/7/24.
 */
open class BaseActivity : FragmentActivity(), ViewInterface {
    private var isPause = false
    private var isUseLock = true
    private var myApplication: MyApplication? = null
    private var viewHandler: ViewHandler? = null
    private var loadingDialog: LoadingDialog? = null

    private class ViewHandler internal constructor(objects: BaseActivity) : Handler() {
        var weakReference: WeakReference<BaseActivity> = WeakReference(objects)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val objects = weakReference.get()
            if (objects?.isPause==false) {
                when (msg.what) {
                    0 -> Toast.makeText(objects, msg.obj as String, Toast.LENGTH_LONG)
                            .show()
                    1 -> Toast.makeText(objects, msg.obj as String, Toast.LENGTH_SHORT)
                            .show()
                    3 -> objects.loadingDialog?.show()
                    4 -> objects.loadingDialog?.dismiss()
                }
            }
        }

    }

    override fun showLoading() {
        val msg = Message()
        msg.what = 3
        viewHandler?.sendEmptyMessage(3)
    }

    override fun closeLoading() {
        val msg = Message()
        msg.what = 3
        viewHandler?.sendEmptyMessage(4)
    }

    override fun showErrorMsg(msg: String?) {
        showLongToast(msg)
    }

    override fun showMsg(msg: String?) {
        showShortToast(msg)
    }

    fun setUseLock(useLock: Boolean) {
        isUseLock = useLock
    }

    fun showLongToast(text: String?) {
        val msg = Message()
        msg.what = 0
        msg.obj = text
        viewHandler?.sendMessage(msg)
    }

    fun showShortToast(text: String?) {
        val msg = Message()
        msg.what = 1
        msg.obj = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication = this.application as? MyApplication
        viewHandler = ViewHandler(this)
        initView()
    }

    private fun initView() {
        loadingDialog = LoadingDialog(this)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        if (isUseLock) {
            if (isPause) {
                if (FingerHelper.checkUsed(this)) {
                    val fingerDialog = FingerDialog(this)
                    fingerDialog.show()
                    FingerHelper.startCheckWithRadom(
                            this.applicationContext, object : FingerHelperInterfaceUnLock {
                        override fun success() {
                            fingerDialog.dismiss()
                        }

                        override fun failed() {
                            fingerDialog.dismiss()
                        }

                        override fun error(msg: String) {
                            fingerDialog.dismiss()
                        }
                    })
                }
            }
        }
        isPause = false
    }
}