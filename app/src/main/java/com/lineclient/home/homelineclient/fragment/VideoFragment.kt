package com.lineclient.home.homelineclient.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import com.lineclient.home.homelineclient.R
import com.lineclient.home.homelineclient.R.layout
import com.lineclient.home.homelineclient.net.HttpConnectHelper
import com.lineclient.home.homelineclient.net.NetDataConstants
import com.lineclient.home.homelineclient.net.ViewInterface
import com.lineclient.home.homelineclient.tools.PlatformUtils
import com.lineclient.home.homelineclient.view.PlayFrame
import com.lineclient.home.homelineclient.view.RockerView
import com.lineclient.home.homelineclient.view.RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE
import com.lineclient.home.homelineclient.view.RockerView.Direction
import com.lineclient.home.homelineclient.view.RockerView.DirectionMode.DIRECTION_8
import com.lineclient.home.homelineclient.view.RockerView.OnShakeListener
import com.lineclient.home.homelineclient.ws.WsUtils
import com.lineclient.home.homelineclient.ws.WsUtils.WSServiceInterface
import kotlinx.android.synthetic.main.fragment_video.player_root
import kotlinx.android.synthetic.main.fragment_video.video_connect
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Created by yangfengyuan on 2017/7/25.
 */
class VideoFragment : BaseFragment(){
    private var views: View? = null
    private var wsUtils: WsUtils? = null
    private var platformUtils: PlatformUtils? = null
    private var directionState: Direction? = null
    //private var videoPlayerView: PlayFrame? = null
    private val directionHandler = Handler()
    private val directionRunnable = DirectionRunnable(this)
    private var viewConnect: Button? = null

    private class DirectionRunnable internal constructor(objects: VideoFragment) : Runnable {
        var weakReference: WeakReference<VideoFragment> = WeakReference(objects)
        override fun run() {
            val objects = weakReference.get()
            if (objects != null) {
                if (objects.directionState != null) {
                    objects.platformUtils!!.handleRockerViewDirection(objects.directionState)
                }
                objects.directionHandler.postDelayed(objects.directionRunnable, 100)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(layout.fragment_video, container, false)
        wsUtils = this.context?.let { WsUtils.getInstance(it) }
        platformUtils = PlatformUtils.getInstance(wsUtils)
        initView()
        initData()
        return views
    }

    private fun initView() {
        initVideo()
        initControl()
    }

    private fun initData() {
        wsUtils?.addListener(object : WSServiceInterface {
            override fun serviceConnect() {}
            override fun serviceDisconnect() {}
            override fun onClose() {}
            override fun onError(error: String?) {}
            override fun onMessage(msg: String?) {}
            override fun onOpen() {}
        })
        wsUtils!!.startWsService()
        directionHandler.postDelayed(directionRunnable, 200)
    }

    private fun initVideo() {
        video_connect.setOnClickListener{
            viewConnect!!.isEnabled = false
            videoUrl
        }
        player_root.init(this.context)
        player_root.setPath("rtmp://media3.sinovision.net:1935/live/livestream")
        try {
            player_root.start()
        } catch (e: IOException) {
            // Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
            e.printStackTrace()
        }
    }

    fun initControl() {
        val rockerView: RockerView = view!!.findViewById(R.id.rockerview)
        if (rockerView != null) {
            rockerView.setCallBackMode(CALL_BACK_MODE_STATE_CHANGE)
            rockerView.setOnShakeListener(
                    DIRECTION_8, object : OnShakeListener {
                override fun onStart() {}
                override fun direction(direction: Direction) {
                    directionState = direction
                }

                override fun onFinish() {
                    directionState = null
                }
            })
        }
    }

    // Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
    private val videoUrl: Unit
        get() {
            HttpConnectHelper.doAESPost(
                    (this.activity as ViewInterface?)!!, NetDataConstants.GET_VIDEO_URL, null, null
            ) { body ->
                try {
                    val jsonObject = JSONObject(body)
                    if (jsonObject.getBoolean("success")) {
                        viewConnect!!.visibility = View.GONE
                        val url = jsonObject.getString("url")
                        player_root.setPath(url)
                        try {
                            player_root.start()
                        } catch (e: IOException) {
                            // Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
                            e.printStackTrace()
                        }
                    } else {
                        viewConnect!!.isEnabled = true
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
}