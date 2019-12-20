package com.xiaoniu.cleanking.ui.newclean.activity

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.entity.AdInfo
import com.comm.jksdk.ad.listener.AdManager
import com.comm.jksdk.ad.listener.VideoAdListener
import com.comm.jksdk.constant.Constants
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.ScreenFinishBeforPresenter
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.FileQueryUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.NetworkUtils
import com.xiaoniu.common.utils.StatisticsUtils
import kotlinx.android.synthetic.main.activity_finish_befor.*
import java.util.*

/**
 * @author XiLei
 * @date 2019/11/22.
 * description：完成页之前的全屏视频广告
 */
class ScreenFinishBeforActivity : BaseActivity<ScreenFinishBeforPresenter>() {

    private lateinit var mAdManager: AdManager
    private lateinit var mFileQueryUtils: FileQueryUtils
    private var mRamScale = 0 //所有应用所占内存大小
    private var mPowerSize = 0 //耗电应用数
    private var mNotifySize = 0 //通知条数

    private var mTitle = ""
    private var mNum = ""
    private var mUnit = ""
    private var mIsOpen = false //插屏广告开关（广告位3）
    var mSourcePage = ""
    var mCurrentPage = "insert_screen_ad_in_front_of_result_page"
    private val TAG = "GeekSdk"

    override fun inject(activityComponent: ActivityComponent?) {
        activityComponent!!.inject(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_finish_befor
    }

    override fun initView() {
        mSourcePage = AppHolder.getInstance().cleanFinishSourcePageId
        mTitle = intent.getStringExtra(ExtraConstant.TITLE)
        if (!TextUtils.isEmpty(intent.getStringExtra(ExtraConstant.NUM))) {
            mNum = intent.getStringExtra(ExtraConstant.NUM)
        }
        if (!TextUtils.isEmpty(intent.getStringExtra(ExtraConstant.UNIT))) {
            mUnit = intent.getStringExtra(ExtraConstant.UNIT)
        }
        initGeegAd()
        mFileQueryUtils = FileQueryUtils()
        mPowerSize = mFileQueryUtils.runningProcess.size
        mNotifySize = NotifyCleanManager.getInstance().allNotifications.size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mPresenter.getAccessListBelow()
        }
    }

    private fun initGeegAd() {
        var isOpen = false;
        if (null != AppHolder.getInstance().switchInfoList && null != AppHolder.getInstance().switchInfoList.data && AppHolder.getInstance().switchInfoList.data.size > 0) {
            for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                if (mTitle.equals(getString(R.string.tool_suggest_clean))) {
                    if (PositionId.KEY_CLEAN_ALL_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_CLEAN_ALL == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_phone_clean))) {
                    if (PositionId.KEY_PHONE_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_PHONE == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_one_key_speed))) {
                    if (PositionId.KEY_JIASU_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_JIASU == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_phone_temperature_low))) {
                    if (PositionId.KEY_COOL_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_COOL == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_chat_clear))) {
                    if (PositionId.KEY_WECHAT_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_WECHAT == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_notification_clean))) {
                    if (PositionId.KEY_NOTIFY_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_NOTIFY == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_super_power_saving))) {
                    if (PositionId.KEY_CQSD_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_CQSD == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.game_quicken))) {
                    if (PositionId.KEY_GAME_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_GAME == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.virus_kill))) {
                    if (PositionId.KEY_VIRUS_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_VIRUS == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.network_quicken))) {
                    if (PositionId.KEY_NET_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_NET == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                } else if (mTitle.equals(getString(R.string.tool_qq_clear))) {
                    if (PositionId.KEY_QQ_SCREEN == switchInfoList.configKey) {
                        isOpen = switchInfoList.isOpen
                    }
                    if (PositionId.KEY_QQ == switchInfoList.configKey
                            && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                        mIsOpen = switchInfoList.isOpen
                    }
                }
            }
        }
        if (isOpen) {
            if (NetworkUtils.isNetConnected()) {
                mAdManager = GeekAdSdk.getAdsManger()
                loadGeekAd()
            } else {
                goFinishActivity()
            }

        } else {
            goFinishActivity()
        }
    }


    //低于Android O
    fun getAccessListBelow(listInfo: ArrayList<FirstJunkInfo?>?) {
        if (null != listInfo && listInfo.size != 0 && null != mFileQueryUtils) {
            mRamScale = mFileQueryUtils.computeTotalSize(listInfo)
        }
    }

    /**
     * 全屏插屏广告
     */
    private fun loadGeekAd() {
        if (null == mAdManager) return
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", mSourcePage, mCurrentPage)
        mAdManager.loadVideoAd(this, PositionId.AD_FINISH_BEFOR, object : VideoAdListener {
            override fun onVideoResume(info: AdInfo) {

            }

            override fun onVideoRewardVerify(info: AdInfo, rewardVerify: Boolean, rewardAmount: Int, rewardName: String) {

            }

            override fun onVideoComplete(info: AdInfo) {

            }

            override fun adSuccess(info: AdInfo) {
                if (null == info) return
                Log.d(TAG, "adSuccess 完成页前全屏视频==" + info.toString())
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.adId, info.adSource, "success", mSourcePage, mCurrentPage)
                if (Constants.AdType.YouLiangHui.equals(info.adSource)) {
                    var view = info.getAdView()
                    if (null != lin_ad_container && null != view) {
                        lin_ad_container.removeAllViews()
                        lin_ad_container.addView(view)
                    } else {
                        goFinishActivity()
                    }
                } else if (Constants.AdType.ChuanShanJia.equals(info.adSource)) { //穿山甲没有view返回,不做异常处理

                }

            }

            override fun adExposed(info: AdInfo) {
                if (null == info) return
                Log.d(TAG, "adExposed 完成页前全屏视频")
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.adId, info.adSource, mSourcePage, mCurrentPage, info.adTitle)
                PreferenceUtil.saveShowAD(true)
            }

            override fun adClicked(info: AdInfo) {
                if (null == info) return
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.adId, info.adSource, mSourcePage, mCurrentPage, info.adTitle)
            }

            override fun adClose(info: AdInfo) {
                PreferenceUtil.saveShowAD(false)
                if (null != info) {
                    StatisticsUtils.clickAD("ad_close_click", "关闭点击", "1", info.adId, info.adSource, mSourcePage, mCurrentPage, info.adTitle)
                }
                goFinishActivity()

            }

            override fun adError(info: AdInfo?, errorCode: Int, errorMsg: String?) {
                if (null != info) {
                    Log.d(TAG, "adError 完成页前全屏视频")
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info!!.adId, info!!.adSource, "fail", mSourcePage, mCurrentPage)
                }
                goFinishActivity()
            }
        })
    }

    private fun goFinishActivity() {
        if (mIsOpen && PreferenceUtil.getShowCount(this, mTitle, mRamScale, mNotifySize, mPowerSize) < 3) {
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            bundle.putBoolean("main", getIntent().getBooleanExtra("main", false))
            startActivity(CleanFinishAdvertisementActivity::class.java, bundle)
        } else if (getIntent().hasExtra(ExtraConstant.ACTION_NAME) && !TextUtils.isEmpty(getIntent().getStringExtra(ExtraConstant.ACTION_NAME)) && getIntent().getStringExtra(ExtraConstant.ACTION_NAME).equals("lock")) {//新Task路径_跳转Ad3_锁屏跳转
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            bundle.putString("action", "lock")
            startActivity(CleanFinishAdvertisementActivity::class.java, bundle)
        } else {
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            bundle.putString("num", mNum)
            bundle.putString("unit", mUnit)
            bundle.putBoolean("main", getIntent().getBooleanExtra("main", false))
            startActivity(NewCleanFinishActivity::class.java, bundle)
        }
        finish()
    }

    override fun netError() {
    }


}