package com.lineclient.home.homelineclient.tools;

import android.content.Context;
import android.util.DisplayMetrics;

public class ViewTool {

    public static int getScreenWidth(Context context){
        DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
        return dm2.widthPixels;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
        return dm2.heightPixels;
    }

}
