package com.lineclient.home.homelineclient.fragment

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lineclient.home.homelineclient.R.layout
import com.lineclient.home.homelineclient.application.MyApplication
import com.lineclient.home.homelineclient.contants.XMLContants
import com.lineclient.home.homelineclient.contants.XMLContants.REFRESH_DATA
import com.lineclient.home.homelineclient.tools.ShaPreHelper
import com.lineclient.home.homelineclient.ws.WsUtils
import com.lineclient.home.homelineclient.ws.WsUtils.WSServiceInterface
import kotlinx.android.synthetic.main.fragment_main.refresh
import kotlinx.android.synthetic.main.fragment_main.refresh_all_layout
import kotlinx.android.synthetic.main.fragment_main.refresh_change
import kotlinx.android.synthetic.main.fragment_main.refresh_layout
import kotlinx.android.synthetic.main.fragment_main.refresh_state
import kotlinx.android.synthetic.main.fragment_main.refresh_time
import kotlinx.android.synthetic.main.fragment_main.refreshall_auto_connect
import kotlinx.android.synthetic.main.fragment_main.refreshall_connect
import kotlinx.android.synthetic.main.fragment_main.save_refresh_time
import kotlinx.android.synthetic.main.view_environment_board.envir_board
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * Created by yangfengyuan on 2017/7/25.
 */
class MainFragment : BaseFragment() {
    private var views: View? = null
    private var refurbishTime = 0
    private var isPause = false
    private var refurbishHandler: RefushHandler? = null
    private var refushRunnable: RefushRunnable? = null
    private var useRefreshAll = false
    private val RESRESH_TIME = "RESRESHTIME"
    private val RESRESH_ALL = "RESRESHALL"
    private val DEFAULT_REFRESH_TIME = 5
    private var myApplication: MyApplication? = null
    private var wsUtils: WsUtils? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(layout.fragment_main, container, false)
        refushRunnable = RefushRunnable(this)
        refurbishHandler = RefushHandler()
        myApplication = activity!!.application as MyApplication
        //showLoading();
        wsUtils = this.context?.let { WsUtils.getInstance(it) }
        wsUtils?.addListener(object : WSServiceInterface {
            override fun serviceConnect() {
                refresh_state.text = "绑定成功,连接中……"
            }

            override fun serviceDisconnect() {
                closeLoading()
                refresh_state.text = "解绑成功"
            }

            override fun onClose() {
                closeLoading()
                refresh_state.text = "连接关闭"
            }

            override fun onError(error: String?) {
                closeLoading()
                refresh_state.text = "错误:$error"
            }

            override fun onMessage(msg: String?) {
                closeLoading()
            }

            override fun onOpen() {
                closeLoading()
                refresh_state.text = "连接成功"
                val jsonObject = JSONObject()
                /*   try {
                    jsonObject.put("kind", "get");
                    jsonObject.put("vv", "vv");
                    wsUtils.sendWsData(WsUtils.encryptAESData(jsonObject.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        })
        initData()
        initView()
        return views
    }

    private fun initData() {
        val refreshkind = ShaPreHelper.readShaPre(
                XMLContants.REFRESH_XML, REFRESH_DATA.REFRESH_KIND, this.context
        )
        if (!TextUtils.isEmpty(refreshkind)) {
            useRefreshAll = true
            if (refreshkind == RESRESH_ALL) {
                startRefreshAll()
            }
        } else {
            useRefreshAll = false
            val time = ShaPreHelper.readShaPre(
                    XMLContants.REFRESH_XML, REFRESH_DATA.REFRESH_TIME,
                    this.context
            )
            refurbishTime = if (TextUtils.isEmpty(time)) {
                DEFAULT_REFRESH_TIME
            } else {
                time.toInt()
            }
            refushData()
            startRefreshTime()
        }
    }

    private fun startRefreshAll() {

        // wsUtils.startWsService();
    }

    private fun stopRefreshAll() {

        /*  if (wsUtils.stopWsService()) {
            refreshStateTex.setText("断开成功");
        } else {
            refreshStateTex.setText("尚未连接,不能断开");
        }*/
    }

    private fun initView() {
        envir_board.initView(this.context)
        refresh_change.setOnClickListener {
            val refreshKind: String
            if (useRefreshAll) {
                refreshKind = RESRESH_ALL
                stopRefreshTime()
                /*stopRefreshTime();
                startRefreshAll();*/
            } else {
                refreshKind = RESRESH_TIME
                stopRefreshAll()
                /* stopRefreshAll();
                startRefreshTime();*/
            }
            ShaPreHelper.writeShaPre(
                    XMLContants.REFRESH_XML, REFRESH_DATA.REFRESH_KIND, refreshKind,
                    this.context
            )
            updateRefreshChangeView()
        }

        save_refresh_time.setOnClickListener {
            val time = refresh_time.text
                    .toString()
            refurbishTime = if (!TextUtils.isEmpty(time)) {
                time.toInt()
            } else {
                showShortToast("输入不能为空")
                return@setOnClickListener
            }
            ShaPreHelper.writeShaPre(
                    XMLContants.REFRESH_XML, REFRESH_DATA.REFRESH_TIME, time,
                    this.context
            )
            stopRefreshTime()
            startRefreshTime()
        }
        refresh.setOnClickListener {
            refushData()
        }
        refreshall_auto_connect.setOnClickListener {

        }
        refreshall_connect.setOnClickListener {
            startRefreshAll()
        }
        refresh_time.setText(refurbishTime.toString())
        updateRefreshChangeView()
    }

    private fun startRefreshTime() {
        refurbishHandler!!.postDelayed(refushRunnable, refurbishTime.toLong())
    }

    private fun stopRefreshTime() {
        refurbishHandler!!.removeCallbacks(refushRunnable)
    }

    private fun updateRefreshChangeView() {
        if (useRefreshAll) {
            useRefreshAll = false
            refresh_layout.visibility = View.GONE
            refresh_all_layout.visibility = View.VISIBLE
        } else {
            useRefreshAll = true
            refresh_layout.visibility = View.VISIBLE
            refresh_all_layout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        refushData()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    private fun refushData() {}
    private class RefushHandler : Handler()
    private class RefushRunnable internal constructor(objects: MainFragment) : Runnable {
        var weakReference: WeakReference<MainFragment> = WeakReference(objects)
        override fun run() {
            val objects = weakReference.get()
            if (objects != null) {
                if (!objects.isPause) {
                    objects.refushData()
                    objects.refurbishHandler!!.postDelayed(
                            objects.refushRunnable, objects.refurbishTime.toLong()
                    )
                }
            }
        }

    }
}