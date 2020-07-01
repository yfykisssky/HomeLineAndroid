package com.lineclient.home.homelineclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lineclient.home.homelineclient.R.layout
import kotlinx.android.synthetic.main.activity_main.sample_text

class MainActivity : AppCompatActivity() {

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        sample_text.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun stringFromJNI(): String?
}