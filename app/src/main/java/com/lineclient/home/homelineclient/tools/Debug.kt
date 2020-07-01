package com.lineclient.home.homelineclient.tools

object Debug {
    var debug = true

    init {
        if (debug) {
            DataUtils.userName = "15921316046"
        }
    }
}