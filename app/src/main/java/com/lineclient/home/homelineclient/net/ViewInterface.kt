package com.lineclient.home.homelineclient.net

interface ViewInterface {
    fun showLoading()
    fun closeLoading()
    fun showErrorMsg(msg: String?)
    fun showMsg(msg: String?)
}