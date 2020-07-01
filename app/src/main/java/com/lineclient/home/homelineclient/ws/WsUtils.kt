package com.lineclient.home.homelineclient.ws

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.lineclient.home.homelineclient.tools.AESHelper
import com.lineclient.home.homelineclient.tools.DataUtils
import com.lineclient.home.homelineclient.tools.Debug
import com.lineclient.home.homelineclient.ws.WsService.LocalBinder
import com.lineclient.home.homelineclient.ws.WsService.WsGetDataInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class WsUtils(context: Context) {
    interface WSServiceInterface : WsGetDataInterface {
        fun serviceConnect()
        fun serviceDisconnect()
    }

    private val wsServiceInterfaceList: MutableList<WSServiceInterface>? = ArrayList()
    private var wsService: WsService? = null
    private var wSServiceConnection: ServiceConnection? = null
    private var wsConnectState = false
    private val context: Context = context.applicationContext
    fun addListener(wsServiceInterface: WSServiceInterface) {
        synchronized(wsServiceInterfaceList!!) { wsServiceInterfaceList.add(wsServiceInterface) }
    }

    fun startWsService() {
        if (!wsConnectState) {
            wSServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(
                    name: ComponentName,
                    service: IBinder
                ) {
                    wsService = (service as LocalBinder).service
                    wsService!!.setWsGetDataInterface(wsServiceInterface)
                    wsServiceInterface.serviceConnect()
                    wsConnectState = true
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    wsServiceInterface.serviceDisconnect()
                    wsService = null
                    wsConnectState = false
                }
            }
            val intent = Intent(context, WsService::class.java)
            wSServiceConnection?.let {
                context.bindService(
                        intent,it, Context.BIND_AUTO_CREATE
                )
            }

        }
    }

    private val wsServiceInterface: WSServiceInterface = object : WSServiceInterface {
        override fun serviceConnect() {
            handleInterList(0, null)
        }

        override fun serviceDisconnect() {
            handleInterList(1, null)
        }

        override fun onClose() {
            handleInterList(2, null)
        }

        override fun onError(error: String?) {
            handleInterList(3, error)
        }

        override fun onMessage(msg: String?) {
            var msg = msg
            if (!Debug.debug) {
                msg = decryptAESData(msg)
            }
            handleInterList(4, msg)
        }

        override fun onOpen() {
            handleInterList(5, null)
        }
    }

    private fun handleInterList(
        kind: Int,
        data: String?
    ) {
        if (wsServiceInterfaceList != null) {
            synchronized(wsServiceInterfaceList) {
                for (r in wsServiceInterfaceList.indices) {
                    val wsServiceInterface = wsServiceInterfaceList[r]
                    if (wsServiceInterface == null) {
                        wsServiceInterfaceList.removeAt(r)
                    } else {
                        when (kind) {
                            0 -> wsServiceInterface.serviceConnect()
                            1 -> wsServiceInterface.serviceDisconnect()
                            2 -> wsServiceInterface.onClose()
                            3 -> wsServiceInterface.onError(data)
                            4 -> wsServiceInterface.onMessage(data)
                            5 -> wsServiceInterface.onOpen()
                        }
                    }
                }
            }
        }
    }

    fun sendWsData(data: String?): Boolean {
        var data = data
        Log.e("out", data)
        if (wsService != null) {
            if (wsConnectState) {
                if (!Debug.debug) {
                    data = encryptAESData(data)
                } else {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("data", data)
                        data = jsonObject.toString()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                wsService!!.sendData(data)
                return true
            }
        }
        return false
    }

    fun stopWsService(): Boolean {
        if (wsService != null) {
            if (wsConnectState) {
                wsService!!.closeConnext()
                context.unbindService(wSServiceConnection!!)
                return true
            }
        }
        return false
    }

    companion object {
        private var instance: WsUtils? = null
        fun getInstance(context: Context): WsUtils? {
            if (instance == null) {
                synchronized(WsUtils::class.java) {
                    if (instance == null) {
                        instance = WsUtils(context)
                    }
                }
            }
            return instance
        }

        fun encryptAESData(data: String?): String? {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("username", DataUtils.userName)
                val jsonEn = JSONObject()
                jsonEn.put("token", DataUtils.token)
                jsonEn.put("data", data)
                val dataBody =
                    AESHelper.encryptByBase64(jsonEn.toString(), DataUtils.aesNetKey)
                jsonObject.put("encryptdata", dataBody)
                return jsonObject.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun decryptAESData(data: String?): String? {
            var data = data
            try {
                data = AESHelper.decryptByBase64(data, DataUtils.aesNetKey)
                return data
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}