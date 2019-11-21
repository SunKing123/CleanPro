package com.xiaoniu.cleanking.ui.main.activity

import android.os.Build
import android.util.Log
import android.view.View
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
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_hot_redpacket.*
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

    private lateinit var mAdManager: AdManager
    private var mIsAdSusscss = false

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

        if (null != AppHolder.getInstance().switchInfoList && null != AppHolder.getInstance().switchInfoList.data
                && AppHolder.getInstance().switchInfoList.data.size > 0) {
            for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                if (PositionId.KEY_RED_JILI == switchInfoList.configKey && switchInfoList.isOpen) {
                    initGeekAdSdk()
                }
            }
        }
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
        val count: Int
        if (AppHolder.getInstance().redPacketEntityList.data[0].showType == 1) { //循环
            if (PreferenceUtil.getRedPacketShowTrigger() != AppHolder.getInstance().redPacketEntityList.data[0].trigger) {
                PreferenceUtil.saveRedPacketForCount(0)
            }
            PreferenceUtil.saveRedPacketShowTrigger(AppHolder.getInstance().redPacketEntityList.data[0].trigger)
            count = PreferenceUtil.getRedPacketForCount()
            if (count >= AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1) {
                PreferenceUtil.saveRedPacketForCount(0)
            } else {
                PreferenceUtil.saveRedPacketForCount(PreferenceUtil.getRedPacketForCount() + 1)
            }
        } else { //随机
            if (AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size == 1) {
                count = 0
            } else {
                count = Random().nextInt(AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1)
            }
        }
        if (!isFinishing()) {
            WebDialogManager.getInstance().showWebDialog(this, AppHolder.getInstance().redPacketEntityList.data[0].htmlUrl + AppHolder.getInstance().redPacketEntityList.data[0].imgUrls[count])
            WebDialogManager.getInstance().setFinishInterface(this)
        }
    }

    /**
     * 初始化广告sdk
     */
    private fun initGeekAdSdk() {
        mAdManager = GeekAdSdk.getAdsManger()
        mFrameLayout.removeAllViews()
        mAdManager.loadRewardVideoAd(this, "click_virus_killing_ad", "user123", 1, object : VideoAdListener {
            override fun onVideoResume() {}
            override fun onVideoRewardVerify(rewardVerify: Boolean, rewardAmount: Int, rewardName: String) {
                Log.d(TAG, "rewardName + rewardAmount=" + rewardName + rewardAmount)
            }

            override fun adSuccess(info : AdInfo) {
                Log.d(TAG, "-----adSuccess-----")
                mIsAdSusscss = true
            }

            override fun adExposed(info : AdInfo) {
                Log.d(TAG, "-----adExposed-----")
            }

            override fun adClicked(info : AdInfo) {
                Log.d(TAG, "-----adClicked-----")
            }

            override fun adError(errorCode: Int, errorMsg: String) {
                Log.d(TAG, "-----adError-----$errorCode$errorMsg")
                mFrameLayout.removeAllViews()
            }
        })
    }

    /**
     * 红包点击 js回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Subscriber(mode = ThreadMode.MAIN)
    fun receiverMessage(baseEvent: BaseEventBus<String>) {
        when (baseEvent.getAction()) {
            BaseEventBusConstant.WEB_REDPACKET_AD -> {
                Log.d("XiLei", "开始了吗--------------")
                showGeekAdSdk()
            }
        }
    }

    /**
     * 展示广告sdk
     */
    private fun showGeekAdSdk() {
        if (mIsAdSusscss && null != mAdManager) {
            Log.d("XiLei", "showGeekAdSdk");
            mFrameLayout.visibility = View.VISIBLE
            mFrameLayout.addView(mAdManager.getAdView())
        } else {
            finish()
        }
    }

    override fun finishActivity() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun netError() {
    }


}