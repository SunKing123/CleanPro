package com.xiaoniu.cleanking.ui.main.activity

import android.content.Intent
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.geek.webpage.eventbus.BaseEventBus
import com.geek.webpage.eventbus.BaseEventBusConstant
import com.geek.webpage.web.model.WebDialogManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.bean.PopupWindowType
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.HomePopUpStatusManager
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.statistic.NiuDataAPI
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback
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
        if (AppHolder.getInstance() == null || AppHolder.getInstance().popupDataEntity == null) {
            finish()
            return
        }
        val redPacketData = AppHolder.getInstance().getPopupDataFromListByType(AppHolder.getInstance().popupDataEntity, PopupWindowType.POPUP_RED_PACKET)
        if (null == redPacketData || null == redPacketData.imgUrls || redPacketData.imgUrls.size <= 0) {
            finish()
            return
        }
        if (redPacketData.showType == 1) { //循环
            if (PreferenceUtil.getRedPacketShowTrigger() != redPacketData.trigger) {
                PreferenceUtil.saveRedPacketForCount(0)
            }
            PreferenceUtil.saveRedPacketShowTrigger(redPacketData.trigger)
            mCount = PreferenceUtil.getRedPacketForCount()
            if (mCount >= redPacketData.imgUrls.size - 1) {
                PreferenceUtil.saveRedPacketForCount(0)
            } else {
                PreferenceUtil.saveRedPacketForCount(PreferenceUtil.getRedPacketForCount() + 1)
            }
        } else { //随机
            if (redPacketData.imgUrls.size == 1) {
                mCount = 0
            } else {
                mCount = Random().nextInt(redPacketData.imgUrls.size - 1)
            }
        }
        mCount = 1
        if (!isFinishing()) {
//            preloadingSplashAd(this, PositionId.AD_RED_PACKET, getString(R.string.redpack))
            NiuDataAPI.onPageStart("red_envelopes_page_view_page", "红包弹窗浏览")
            //val url="https://wkqlapph5.wukongclean.com/popBox.html?url=https://getvideo-test.oss-cn-shanghai.aliyuncs.com/clean/banner/image/6fa19961d83d4a199e52383576c22003.png"
            WebDialogManager.getInstance().showWebDialog(this, this, redPacketData.htmlUrl + redPacketData.imgUrls[mCount])
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
            BaseEventBusConstant.WEB_REDPACKET_CLOSE -> { //红包点击x按钮关闭
                WebDialogManager.getInstance().dismissDialog()
                if (!isFinishing) {
                    finish()
                }
                NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page", "red_envelopes_page_view_page", "红包弹窗浏览")
                StatisticsUtils.trackClick("close_click", "红包弹窗按钮关闭", "hot_splash_page", "red_envelopes_page")
            }
            BaseEventBusConstant.WEB_REDPACKET_AD -> {
                if (null != AppHolder.getInstance().switchInfoList && null != AppHolder.getInstance().switchInfoList.data
                        && AppHolder.getInstance().switchInfoList.data.size > 0) {
                    for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                        if (PositionId.KEY_RED_JILI == switchInfoList.configKey) {
                            if (switchInfoList.isOpen) {
                                NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page", "red_envelopes_page_view_page", "红包弹窗浏览")
                                StatisticsUtils.trackClick("red_envelopes_click", "红包弹窗点击", "hot_splash_page", "red_envelopes_page")
                                WebDialogManager.getInstance().dismissDialog()
                                initAd()
                            } else {
                                showWebView()
                            }
                        }
                    }
                } else {
                    showWebView()
                }
            }
        }
    }

    /**
     * 初始化广告sdk
     */
    private fun initAd() {
        NiuDataAPI.onPageStart("red_envelopes_page_video_view_page", "红包弹窗激励视频页浏览")
        NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page_video_page", "red_envelopes_page_video_view_page", "红包弹窗激励视频页浏览")
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "hot_splash_page", "red_envelopes_page_video_page")

        val viewGroup: ViewGroup = window.decorView as ViewGroup
//        val params = AdRequestParams.Builder().setAdId(MidasConstants.RED_PACKET)
//                .setViewContainer(viewGroup).setActivity(this).build()
        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_RED_JILI, PositionId.DRAW_ONE_CODE), object : AbsAdBusinessCallback() {})
    }

    private fun showWebView() {
        if (null == AppHolder.getInstance() || null == AppHolder.getInstance().popupDataEntity) {
            return;
        }
        val redPacketData = AppHolder.getInstance().getPopupDataFromListByType(AppHolder.getInstance().popupDataEntity, PopupWindowType.POPUP_RED_PACKET)

        if (redPacketData == null || null == redPacketData.imgUrls || redPacketData.imgUrls.size <= 0) {
            finish()
            return
        }
        if (redPacketData.jumpUrls[mCount].contains("http")) {
            AppHolder.getInstance().cleanFinishSourcePageId = "red_envelopes_page_video_end_page"
            startActivity(Intent(this@RedPacketHotActivity, AgentWebViewActivity::class.java)
                    .putExtra(ExtraConstant.WEB_URL, redPacketData.jumpUrls[mCount]))
        }
        if (!isFinishing) {
            finish()
        }
    }

    override fun finishActivity() {
        finish()
        NiuDataAPIUtil.onPageEnd("hot_splash_page", "red_envelopes_page", "red_envelopes_page_view_page", "红包弹窗浏览")
        StatisticsUtils.trackClick("close_click", "红包弹窗按钮关闭", "hot_splash_page", "red_envelopes_page")
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            WebDialogManager.getInstance().dismissDialog()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HomePopUpStatusManager.getInstance().setRedPacketDismiss()
        EventBus.getDefault().unregister(this)
    }

    override fun netError() {
    }


}
