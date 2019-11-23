package com.xiaoniu.cleanking.ui.main.activity

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.entity.AdInfo
import com.comm.jksdk.ad.listener.AdManager
import com.comm.jksdk.ad.listener.VideoAdListener
import com.geek.webpage.eventbus.BaseEventBus
import com.geek.webpage.eventbus.BaseEventBusConstant
import com.geek.webpage.web.model.WebDialogManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.statistic.NiuDataAPI
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber
import org.simple.eventbus.ThreadMode
import java.util.*

/**
 * @author XiLei
 * @date 2019/11/19.
 * description：热启动红包
 */
class RedPacketHotActivity : BaseActivity<MainPresenter>(), WebDialogManager.FinishInterface {

    private var mCount = 0
    private lateinit var mAdManager: AdManager

    private val TAG = "GeekSdk"
    override fun getLayoutId(): Int {
        return R.layout.activity_hot_redpacket
    }

    override fun inject(activityComponent: ActivityComponent?) {
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        EventBus.getDefault().register(this)
        showRedPacket()
    }

    /**
     * 展示红包
     */
    private fun showRedPacket() {
        if (null == AppHolder.getInstance() || null == AppHolder.getInstance().redPacketEntityList
                || null == AppHolder.getInstance().redPacketEntityList.data
                || AppHolder.getInstance().redPacketEntityList.data.size <= 0
                || null == AppHolder.getInstance().redPacketEntityList.data[0].imgUrls
                || AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size <= 0)
            return
        if (AppHolder.getInstance().redPacketEntityList.data[0].showType == 1) { //循环
            if (PreferenceUtil.getRedPacketShowTrigger() != AppHolder.getInstance().redPacketEntityList.data[0].trigger) {
                PreferenceUtil.saveRedPacketForCount(0)
            }
            PreferenceUtil.saveRedPacketShowTrigger(AppHolder.getInstance().redPacketEntityList.data[0].trigger)
            mCount = PreferenceUtil.getRedPacketForCount()
            if (mCount >= AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1) {
                PreferenceUtil.saveRedPacketForCount(0)
            } else {
                PreferenceUtil.saveRedPacketForCount(PreferenceUtil.getRedPacketForCount() + 1)
            }
        } else { //随机
            if (AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size == 1) {
                mCount = 0
            } else {
                mCount = Random().nextInt(AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1)
            }
        }
        if (!isFinishing()) {
            NiuDataAPI.onPageStart("red_envelopes_page_view_page", "红包弹窗浏览")
            WebDialogManager.getInstance().showWebDialog(this, AppHolder.getInstance().redPacketEntityList.data[0].htmlUrl + AppHolder.getInstance().redPacketEntityList.data[0].imgUrls[mCount])
            WebDialogManager.getInstance().setFinishInterface(this)
        }
    }

    /**
     * 红包点击 js回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Subscriber(mode = ThreadMode.MAIN)
    fun receiverMessage(baseEvent: BaseEventBus<String>) {
        when (baseEvent.getAction()) {
            BaseEventBusConstant.WEB_REDPACKET_AD -> {
                if (null != AppHolder.getInstance().switchInfoList && null != AppHolder.getInstance().switchInfoList.data
                        && AppHolder.getInstance().switchInfoList.data.size > 0) {
                    for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                        if (PositionId.KEY_RED_JILI == switchInfoList.configKey && switchInfoList.isOpen) {
                            NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page", "red_envelopes_page_view_page", "红包弹窗浏览")
                            StatisticsUtils.trackClick("red_envelopes_click", "红包弹窗点击", "hot_splash_page", "red_envelopes_page")
                            WebDialogManager.getInstance().dismissDialog()
                            initGeekAdSdk()
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化广告sdk
     */
    private fun initGeekAdSdk() {
        if (null == mAdManager) return
        NiuDataAPI.onPageStart("red_envelopes_page_video_view_page", "红包弹窗激励视频页浏览")
        NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page_video_page", "red_envelopes_page_video_view_page", "红包弹窗激励视频页浏览")
        mAdManager = GeekAdSdk.getAdsManger()
        //暂时这样
        mAdManager.loadRewardVideoAd(this, "red_envelopes_ad", "user123", 1, object : VideoAdListener {

            override fun adSuccess(info: AdInfo?) {
                Log.d(TAG, "-----adSuccess-----")
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info!!.adId, info.adSource, "success", "hot_splash_page", "red_envelopes_page_video_page")
            }

            override fun adExposed(info: AdInfo?) {
                Log.d(TAG, "-----adExposed-----")
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info!!.adId, info.adSource, "hot_splash_page", "red_envelopes_page_video_page", " ")
            }

            override fun onVideoResume(info: AdInfo?) {

            }

            override fun adClicked(info: AdInfo?) {
                Log.d(TAG, "-----adClicked-----")
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info!!.adId, info.adSource, "hot_splash_page", "red_envelopes_page_video_page", " ")
            }

            override fun adClose(info: AdInfo?) {
                Log.d(TAG, "-----adClose-----")
                StatisticsUtils.clickAD("close_click", "红包弹窗激励视频结束页关闭点击", "1", info!!.adId, info.adSource, "hot_splash_page", "red_envelopes_page_video_end_page", " ")
                if (!isFinishing()) {
                    AppHolder.getInstance().cleanFinishSourcePageId = "red_envelopes_page_video_end_page"
                    startActivity(Intent(this@RedPacketHotActivity, AgentWebViewActivity::class.java)
                            .putExtra(ExtraConstant.WEB_URL, AppHolder.getInstance().redPacketEntityList.data[0].jumpUrls[mCount]))
                    finish()
                }
            }

            override fun adError(errorCode: Int, errorMsg: String) {
                Log.d(TAG, "-----adError-----$errorMsg")
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "fail", "hot_splash_page", "red_envelopes_page_video_page")
            }

            override fun onVideoRewardVerify(info: AdInfo?, rewardVerify: Boolean, rewardAmount: Int, rewardName: String?) {

            }

            override fun onVideoComplete(info: AdInfo?) {

            }
        })
    }

    override fun finishActivity() {
        finish()
        NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page", "red_envelopes_page_view_page", "红包弹窗浏览")
        StatisticsUtils.trackClick("close_click", "红包弹窗按钮关闭", "hot_splash_page", "red_envelopes_page")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun netError() {
    }


}