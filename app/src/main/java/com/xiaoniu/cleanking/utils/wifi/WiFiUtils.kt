package com.xiaoniu.cleanking.utils.wifi

import android.content.Context
import android.net.TrafficStats
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.xiaoniu.clean.deviceinfo.EasyNetworkMod
import com.xiaoniu.clean.deviceinfo.LogUtils
import java.text.DecimalFormat


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

    //获取WIFI链接速度，单位MB/S
    fun getConnectWiFiSpeed(context: Context): String {
        val easyNetworkMod = EasyNetworkMod(context)
        val speed = easyNetworkMod.wifiLinkSpeed
        if (speed.endsWith(WifiInfo.LINK_SPEED_UNITS)) {
            val sp = speed.subSequence(0, speed.length - 4).toString()
            return (sp.toFloat() / 8).toString().plus("MB/S")
        }
        return easyNetworkMod.wifiLinkSpeed
    }


    private var rxTxTotal: Long = 0

    private val showFloatFormat = DecimalFormat("0.00")

    private fun formatSpeed(speed: Double): String {
        return if (speed >= 1048576.0) {
            showFloatFormat.format(speed / 1048576.0).toString() + "MB/s"
        } else {
            showFloatFormat.format(speed / 1024.0).toString() + "KB/s"
        }
    }


    //获取当前设备的网速
    fun getDeviceNetSpeed(context: Context): String {
        val tempSum = (TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes())
        val rxTxLast: Long = tempSum - rxTxTotal
        val totalSpeed = rxTxLast * 1000 / 2000.0
        rxTxTotal = tempSum
        return formatSpeed(totalSpeed)
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

    //网络延迟 NetPingManager


    private fun getSecurity(config: WifiConfiguration): WIFISecurity {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WIFISecurity.SECURITY_PSK
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return WIFISecurity.SECURITY_EAP
        }
        return if (config.wepKeys[0] != null) WIFISecurity.SECURITY_WEP else WIFISecurity.SECURITY_NONE
    }

    //加密方式
    fun getConnectWifiEncrypt(context: Context): WIFISecurity {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val wifiConfigList = wifiManager.configuredNetworks
        LogUtils.e("=======================current:" + wifiInfo.ssid + " " + wifiInfo.networkId.toString())
        wifiConfigList.forEach {
            val configSSid = it.SSID

            LogUtils.e("=======================forEach:" + configSSid + " " + it.networkId.toString())

            configSSid.replace("\"", "")
            val currentSSid = wifiInfo.ssid
            currentSSid.replace("\"", "")
            if (currentSSid == configSSid && wifiInfo.networkId == it.networkId) {
                return getSecurity(it)
            }
        }
        return WIFISecurity.UNKNOW
    }

    //当前的IP地址
    fun getConnectWifiIp(context: Context): String {
        val easyNetworkMod = EasyNetworkMod(context)
        return easyNetworkMod.iPv4Address
    }

    //当前的MAC地址
    fun getConnectWifiMac(context: Context): String {
        val easyNetworkMod = EasyNetworkMod(context)
        return easyNetworkMod.wifiMAC
    }


    enum class WiFiLevel(value: Int) {
        High(1), Middle(2), Low(3)
    }

    enum class WIFISecurity(value: Int) {
        SECURITY_NONE(0), SECURITY_WEP(1), SECURITY_PSK(2), SECURITY_EAP(3), UNKNOW(4)
    }

}
