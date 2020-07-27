package com.xiaoniu.cleanking.keeplive.receive

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel
import com.xiaoniu.cleanking.utils.AppLifecycleUtil
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.DateUtils
import org.greenrobot.eventbus.EventBus

class NetworkCallbackImpl constructor(context: Context) : ConnectivityManager.NetworkCallback() {

    val mContext = context

    //网络连接时调用
    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        LogUtils.e("=======onAvailable: 网络已连接")
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
                LogUtils.e("======onCapabilitiesChanged: 网络类型为wifi")
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
        //应用在前台不弹出
        if (AppLifecycleUtil.isAppOnForeground(mContext)) {
            LogUtils.e("======APP在前台")
            return
        }
        //判断WIFI插屏是否打开
        val configBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_WIFI_EXTERNAL_SCREEN)
        LogUtils.e("=========wifi config:${Gson().toJson(configBean)}")
        //1.先判断是否打开了WIFI弹窗的开关
        if (configBean != null && configBean.isOpen) {
            val currentTime = System.currentTimeMillis()
            val wifiEntity = PreferenceUtil.getPopupWifi()
            LogUtils.e("=========wifi entity:${Gson().toJson(wifiEntity)}")
            //2.判断是否同是一天
            if (DateUtils.isSameDay(wifiEntity.popupTime, currentTime)) {
                //判断当前时间是否满足上次一次的弹窗间隔
                val elapseUsedTime = DateUtils.getMinuteBetweenTimestamp(currentTime, wifiEntity.popupTime)
                if (elapseUsedTime < configBean.displayTime) {
                    LogUtils.e("==========不满足wifi展示的间隔时间")
                    return
                }
                //判断已经展示的交数是否超过最大次数
                if (wifiEntity.popupCount >= configBean.showRate) {
                    LogUtils.e("==========不满足wifi展示的总次数")
                    return
                }
                PreferenceUtil.updatePopupWifi(false)
                EventBus.getDefault().post(PopEventModel("wifi"))
            } else {
                //不是同一天，重置数据
                PreferenceUtil.updatePopupWifi(true)
                EventBus.getDefault().post(PopEventModel("wifi"))
            }


        }
    }

}