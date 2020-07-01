package com.lineclient.home.homelineclient.ws

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import com.lineclient.home.homelineclient.net.NetDataConstants
import com.lineclient.home.homelineclient.ws.WsService
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.lang.ref.WeakReference
import java.net.URI
import java.net.URISyntaxException

/**
 * Created by yangfengyuan on 16/7/28.
 */
class WsService : Service() {
    var isToTryAuto = true
    private var wsGetDataInterface: WsGetDataInterface? = null
    private var serviceUrl: URI? = null
    private var webSocketWorker: WebSocketWorker? = null
    var isConnect = false
        private set
    private val msgHandler = MsgHandler(this)

    interface WsGetDataInterface {
        fun onClose()
        fun onError(error: String?)
        fun onMessage(msg: String?)
        fun onOpen()
    }

    inner class LocalBinder : Binder() {
        val service: WsService
            get() = this@WsService
    }

    fun setWsGetDataInterface(wsGetDataInterface: WsGetDataInterface?) {
        this.wsGetDataInterface = wsGetDataInterface
    }

    override fun onBind(intent: Intent): IBinder? {
        try {
            serviceUrl = URI(NetDataConstants.WS_URL)
            startWs()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return LocalBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    private fun startWs() {
        Thread(Runnable { connectWSService() })
                .start()
    }

    fun retryConnect(): Boolean {
        try {
            if (webSocketWorker?.connectBlocking() == false) {
                webSocketWorker?.connect()
            } else {
                return false
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun connectWSService() {
        webSocketWorker =
            WebSocketWorker(serviceUrl, Draft_17())
        webSocketWorker?.connect()
    }

    fun closeConnext() {
        webSocketWorker?.close()
    }

    fun sendData(msg: String?) {
        if (isConnect) {
            webSocketWorker?.send(msg)
        }
    }

    private class MsgHandler internal constructor(objects: WsService) : Handler() {
        var weakReference: WeakReference<WsService> = WeakReference(objects)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val objects = weakReference.get()
            when (msg.what) {
                0 -> objects?.wsGetDataInterface?.onClose()
                1 -> objects?.wsGetDataInterface?.onOpen()
                2 -> objects?.wsGetDataInterface?.onError(msg.obj as String)
                3 -> {
                    val data = msg.obj as String
                    objects?.wsGetDataInterface?.onMessage(data)
                }
            }
        }

    }

    private inner class WebSocketWorker(
        serverUri: URI?,
        draft: Draft?
    ) : WebSocketClient(serverUri, draft) {
        override fun onClose(
            arg0: Int,
            arg1: String,
            arg2: Boolean
        ) {
            msgHandler.sendEmptyMessage(0)
            isConnect = false
        }

        override fun onError(error: Exception) {
            isConnect = false
            var errorMsg = error.message
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = "未知连接错误"
            }
            val msg = Message()
            msg.what = 2
            msg.obj = errorMsg
            msgHandler.sendMessage(msg)
            if (isToTryAuto) {
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (!isConnect) {
                    connectWSService()
                }
            }
        }

        override fun onMessage(data: String) {
            val msg = Message()
            msg.what = 3
            msg.obj = data
            msgHandler.sendMessage(msg)
        }

        override fun onOpen(arg0: ServerHandshake) {
            msgHandler.sendEmptyMessage(1)
            isConnect = true
        }
    }
}