package com.lineclient.home.homelineclient.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lineclient.home.homelineclient.R;

public class EnviromentBoard extends LinearLayout {

    private TextView temTex;
    private TextView humiTex;
    private Context context;

    public EnviromentBoard(Context context) {
        super(context);
    }

    public EnviromentBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(Context context) {
        this.context = context;
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/LED.ttf");
        temTex = this.findViewById(R.id.temperatureTex);
        temTex.setTypeface(typeFace);
        humiTex = findViewById(R.id.humidityTex);
        humiTex.setTypeface(typeFace);
    }

}
