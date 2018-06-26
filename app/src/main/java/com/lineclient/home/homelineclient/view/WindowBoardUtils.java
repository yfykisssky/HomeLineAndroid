package com.lineclient.home.homelineclient.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.tools.ViewTool;

public class WindowBoardUtils {

    //要引用的布局文件.
    LinearLayout linearLayout;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;

    //状态栏高度.（接下来会用到）
    int statusBarHeight = -1;

    public void hideToRight(){

    }

    public void showFromRight(){

    }

    @SuppressLint("ClickableViewAccessibility")
    public void createAndShow(Context context) {

        context = context.getApplicationContext();
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = ViewTool.getScreenWidth(context) / 3;
        params.height = params.width * 3 / 2;

        linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.window_board, null);
        windowManager.addView(linearLayout, params);

        //主动计算出当前View的宽高信息.
        linearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        params.x = (int) event.getRawX() - params.width / 2;
                        //这就是状态栏偏移量用的地方
                        params.y = (int) event.getRawY() - params.height / 2 - statusBarHeight;
                        windowManager.updateViewLayout(linearLayout, params);
                        break;
                }
                return false;
            }
        });
    }

    void destoryWindow() {
        windowManager.removeView(linearLayout);
    }

}
