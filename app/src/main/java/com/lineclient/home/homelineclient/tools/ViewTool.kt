package com.lineclient.home.homelineclient.tools

import android.content.Context

object ViewTool {
    @JvmStatic fun getScreenWidth(context: Context): Int {
        val dm2 = context.resources.displayMetrics
        return dm2.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val dm2 = context.resources.displayMetrics
        return dm2.heightPixels
    }
}