package com.lineclient.home.homelineclient.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lineclient.home.homelineclient.R;
import com.lineclient.home.homelineclient.tools.ViewTool;


/**
 * Created by yangfengyuan on 16/7/26.
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        initDialog(context);
    }

    private void initDialog(Context context) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RelativeLayout layout = new RelativeLayout(context);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(context);
        int screenWidth=ViewTool.getScreenWidth(this.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/3,screenWidth/3));

        layout.addView(imageView);
        this.setContentView(layout);

        imageView.setImageResource(R.drawable.loading);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);

        imageView.startAnimation(hyperspaceJumpAnimation);

        this.setCancelable(false);
    }


}
