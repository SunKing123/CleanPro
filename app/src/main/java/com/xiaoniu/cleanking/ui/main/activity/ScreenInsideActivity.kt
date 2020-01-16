package com.xiaoniu.cleanking.ui.main.activity

import android.os.Handler
import android.util.Log
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.entity.AdInfo
import com.comm.jksdk.ad.listener.AdListener
import com.comm.jksdk.ad.listener.AdManager
import com.comm.jksdk.config.InitBaseConfig
import com.xiaoniu.cleanking.BuildConfig
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.Constant
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.common.utils.ToastUtils

/**
 * @author XiLei
 * @date 2019/12/2.
 * description：内部插屏广告
 */
class ScreenInsideActivity : BaseActivity<MainPresenter>() {

    private lateinit var mAdManager: AdManager
    private val TAG = "GeekSdk"
    override fun getLayoutId(): Int {
        return R.layout.activity_hot_redpacket
    }

    override fun inject(activityComponent: ActivityComponent?) {
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        mAdManager = GeekAdSdk.getAdsManger()
        StatisticsUtils.customTrackEvent("ad_vue_custom", "内部插屏广告vue创建", "hot_splash_page", "inside_advertising_ad_page")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isFinishing) {
            loadCustomInsertScreenAd()
        }
    }

    /**
     * 内部插屏广告
     */
    private fun loadCustomInsertScreenAd() {

        var adState = 0
        if (null == mAdManager) return
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "hot_splash_page", "inside_advertising_ad_page")
        mAdManager.loadCustomInsertScreenAd(this, "inside_advertising_ad", 3, object : AdListener {
            override fun adSuccess(info: AdInfo) {
                if (null == info) return
                Log.d(TAG, "adSuccess 内部插屏--" + info.toString())
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info!!.adId, info.adSource, "success", "hot_splash_page", "inside_advertising_ad_page")
            }

            override fun adExposed(info: AdInfo) {
                Log.d(TAG, "adExposed 内部插屏")
                adState = 1
                PreferenceUtil.saveShowAD(true)
                if (null == info) return
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info!!.adId, info.adSource, "hot_splash_page", "inside_advertising_ad_page", info.adTitle)
            }

            override fun adClicked(info: AdInfo) {
                Log.d(TAG, "adClicked 内部插屏")
                PreferenceUtil.saveShowAD(false)
                if (null == info) return
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info!!.adId, info.adSource, "hot_splash_page", "inside_advertising_ad_page", info.adTitle)
                finish()
            }

            override fun adClose(info: AdInfo?) {
                Log.d(TAG, "adClose 内部插屏")
                PreferenceUtil.saveShowAD(false)
                if (null != info) {
                    StatisticsUtils.clickAD("ad_close_click", "关闭点击", "1", info!!.adId, info.adSource, "hot_splash_page", "inside_advertising_ad_page", info.adTitle)
                }
                finish()
            }

            override fun adError(info: AdInfo?, errorCode: Int, errorMsg: String?) {
                Log.d(TAG, "adError 内部插屏 $errorCode-----$errorMsg")
                if (!BuildConfig.SYSTEM_EN.contains("prod")) {
                    ToastUtils.showShort("-----adError 内部插屏错误 $errorCode -----$errorMsg")
                }
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info!!.adId, info!!.adSource, "fail", "hot_splash_page", "inside_advertising_ad_page")
                finish()
            }
        }, "80")
        Handler().postDelayed( Runnable() {//在当前线程（也即主线程中）开启一个消息处理器，并在3秒后在主线程中执行，从而来更新UI
            if(adState ==0){
                if (!isFinishing) {
                    InitBaseConfig.getInstance().initChjAd(this, Constant.CSJ_AD_ID)
                    PreferenceUtil.saveShowAD(false)
                    finish()
                }
            }
        }, 2500);
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            PreferenceUtil.saveShowAD(false)
            finish()
        }
    }

    override fun netError() {
    }


}