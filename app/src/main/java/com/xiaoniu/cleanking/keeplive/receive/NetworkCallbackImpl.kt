package com.xiaoniu.cleanking.keeplive.receive

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel
import com.xiaoniu.cleanking.utils.AppLifecycleUtil
import com.xiaoniu.cleanking.utils.LogUtils
import org.greenrobot.eventbus.EventBus

class NetworkCallbackImpl constructor(context: Context) : ConnectivityManager.NetworkCallback() {

    val mContext = context

    //网络连接时调用
    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        LogUtils.e("=======onAvailable: 网络已连接");
    }

    //网络即将断开时调用
    override fun onLosing(network: Network?, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)

    }


    //网络断开时调用
    override fun onLost(network: Network?) {
        super.onLost(network)
        LogUtils.e("=======onLost: 网络已断开")
    }

    //网络缺失network时调用
    override fun onUnavailable() {
        super.onUnavailable()
    }

    //网络功能更改但任满足需求时调用
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                LogUtils.e("======onCapabilitiesChanged: 网络类型为wifi");
                startExternalSceneActivity()
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                LogUtils.e("======onCapabilitiesChanged: 蜂窝网络");
            } else {
                LogUtils.e("======onCapabilitiesChanged: 其他网络");
            }
        }

    }

    //网络连接属性修改时调用
    override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
        super.onLinkPropertiesChanged(network, linkProperties)
    }

    private fun startExternalSceneActivity() {
        if (AppLifecycleUtil.isAppOnForeground(mContext)) {
            LogUtils.e("==========应用在前台，不弹出wifi插屏")
            return
        }
        LogUtils.e("=====应用在后台====，弹出wifi插屏")
        EventBus.getDefault().post(PopEventModel("wifi"))

    }

}