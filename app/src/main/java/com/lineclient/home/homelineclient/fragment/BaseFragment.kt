package com.lineclient.home.homelineclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.lineclient.home.homelineclient.activity.BaseActivity

open class BaseFragment : Fragment() {
    private var baseActivity: BaseActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = this.activity as BaseActivity?
    }

    fun showLongToast(text: String?) {
        baseActivity?.showLongToast(text)
    }

    fun showShortToast(text: String?) {
        baseActivity?.showShortToast(text)
    }

    fun showLoading() {
        baseActivity?.showLoading()
    }

    fun closeLoading() {
        baseActivity?.closeLoading()
    }
}