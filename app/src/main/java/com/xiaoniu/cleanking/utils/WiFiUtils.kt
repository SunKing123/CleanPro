package com.xiaoniu.cleanking.utils

import android.content.Context
import android.net.wifi.WifiManager
import com.xiaoniu.clean.deviceinfo.EasyNetworkMod


/**
 * Desc: 获取WIFI相关参数的工具类
 * <p>
 * Date: 2020/8/14
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:
 *
 * @author ZengBo
 */
class WiFiUtils {

    /**
     * 检测wifi是否连接
     */
    fun isWifiConnected(context: Context): Boolean {
        val easyNetworkMod = EasyNetworkMod(context)
        return easyNetworkMod.isWifiEnabled
    }

    //获取WIFI网速
    fun getConnectWiFiSpeed(context: Context): String {
        val easyNetworkMod = EasyNetworkMod(context)
        return easyNetworkMod.wifiLinkSpeed
    }

    //获取当前WIFI连接的设备数


    //获取WIFI名称
    fun getConnectWifiName(context: Context): String {
        val easyNetworkMod = EasyNetworkMod(context)
        return easyNetworkMod.wifiSSID
    }


    //获取WIFI的信号强度
    fun getConnectWifiLevel(context: Context): WiFiLevel {
        val easyNetworkMod = EasyNetworkMod(context)
        return when (easyNetworkMod.checkWifiState()) {
            1 -> WiFiLevel.High
            2 -> WiFiLevel.Middle
            3 -> WiFiLevel.Low
            else -> WiFiLevel.Middle
        }
    }


    enum class WiFiLevel(value: Int) {
        High(1), Middle(2), Low(3)
    }
}
