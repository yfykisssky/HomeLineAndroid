package com.lineclient.home.homelineclient.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.lineclient.home.homelineclient.tools.ViewTool;

public abstract class BaseDialog extends Dialog {

    abstract int getContentViewId();

    public BaseDialog(@NonNull Context context) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewId());
        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewTool.getScreenWidth(this.getContext()) * 4 / 5;
        window.setAttributes(wlp);

    }

    public BaseDialog(@NonNull Context context, double widthScale, double heightScaleWithWidth) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewId());

        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        wlp.width = (int) (ViewTool.getScreenWidth(this.getContext()) * widthScale);
        wlp.height = (int) (ViewTool.getScreenWidth(this.getContext()) * heightScaleWithWidth);
        window.setAttributes(wlp);

    }

}
