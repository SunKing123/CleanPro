package com.xiaoniu.cleanking.utils.geeksdk

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.entity.AdInfo
import com.comm.jksdk.ad.listener.AdPreloadingListener
import com.xiaoniu.cleanking.BuildConfig
import com.xiaoniu.cleanking.app.AppApplication

/**
 * @author XiLei
 * @date 2019/12/17.
 * description：广告SDK预加载工具类
 */

private val TAG: String? = "GeekSdk"

/**
 * 激励视频预加载
 */
fun preloadingSplashAd(activity: Activity, position: String, name: String) {
    if (null == activity || null == AppApplication.getInstance()) return
    GeekAdSdk.getAdsManger().preloadingRewardVideoAd(activity, position, "user123", 1, object : AdPreloadingListener {
        override fun adSuccess(info: AdInfo) {
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adSuccess ${name}预加载-----")
                Toast.makeText(AppApplication.getInstance(), "${name}预加载成功", Toast.LENGTH_LONG).show()
            }
        }

        override fun adError(info: AdInfo, errorCode: Int, errorMsg: String) {
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adError ${name}预加载-----$errorMsg")
                Toast.makeText(AppApplication.getInstance(), "${name}预加载失败", Toast.LENGTH_LONG).show()
            }
        }
    })
}

/**
 * 全屏视频
 */
fun preloadingSplashAd(activity: Activity, position: String) {
    if (null == activity || null == AppApplication.getInstance()) return
    GeekAdSdk.getAdsManger().preloadingVideoAd(activity, position, object : AdPreloadingListener {
        override fun adSuccess(info: AdInfo) {
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adSuccess 完成页前全屏视频预加载-----")
                Toast.makeText(AppApplication.getInstance(), "完成页前全屏视频 - 预加载成功", Toast.LENGTH_LONG).show()
            }
        }

        override fun adError(info: AdInfo, errorCode: Int, errorMsg: String) {
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adError 完成页前全屏视频预加载-----$errorMsg")
                Toast.makeText(AppApplication.getInstance(), "完成页前全屏视频 - 预加载失败", Toast.LENGTH_LONG).show()
            }
        }
    })
}

/**
 * 大图广告预加载
 */
fun preloadingAd(activity: Activity, position: String, name: String) {
    if (null == activity || null == AppApplication.getInstance()) return
    GeekAdSdk.getAdsManger().preloadingAd(activity, position, object : AdPreloadingListener {
        override fun adSuccess(info: AdInfo) {
            if (null == info) return
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adSuccess ${name}-----预加载")
                Toast.makeText(AppApplication.getInstance(), "${name}预加载成功", Toast.LENGTH_LONG).show()
            }
        }

        override fun adError(info: AdInfo, errorCode: Int, errorMsg: String) {
            if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                Log.d(TAG, "-----adError ${name}-----预加载$errorCode-----$errorMsg")
                Toast.makeText(AppApplication.getInstance(), "${name}预加载失败", Toast.LENGTH_LONG).show()
            }
        }
    })
}