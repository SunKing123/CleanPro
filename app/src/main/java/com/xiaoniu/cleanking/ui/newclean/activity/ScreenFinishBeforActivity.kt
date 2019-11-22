package com.xiaoniu.cleanking.ui.newclean.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.entity.AdInfo
import com.comm.jksdk.ad.listener.AdListener
import com.comm.jksdk.ad.listener.AdManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.ScreenFinishBeforPresenter
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager
import com.xiaoniu.cleanking.utils.FileQueryUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import java.util.*

/**
 * @author XiLei
 * @date 2019/11/22.
 * description：完成页之前的插屏广告
 */
class ScreenFinishBeforActivity : BaseActivity<ScreenFinishBeforPresenter>() {

    private lateinit var mAdManager: AdManager
    private lateinit var mFileQueryUtils: FileQueryUtils
    private var mRamScale = 0 //所有应用所占内存大小
    private var mPowerSize = 0 //耗电应用数
    private var mNotifySize = 0 //通知条数

    private var mTitle = ""
    private val TAG = "GeekSdk"

    override fun inject(activityComponent: ActivityComponent?) {
        activityComponent!!.inject(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_hot_redpacket
    }

    override fun initView() {
        mTitle = intent.getStringExtra("title")
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
        for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
            if (mTitle.equals(getString(R.string.virus_kill)) && PositionId.KEY_VIRUS_SCREEN == switchInfoList.configKey) {
                isOpen = switchInfoList.isOpen
            }
        }
        if (isOpen) {
            mAdManager = GeekAdSdk.getAdsManger()
            loadGeekAd()
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
        mAdManager.loadCustomInsertScreenAd(this, "cp_ad_1", 3, object : AdListener {
            //暂时这样
            override fun adSuccess(info: AdInfo) {
                Log.d(TAG, "-----adSuccess-----=" + info.adSource)
            }

            override fun adExposed(info: AdInfo) {
                Log.d(TAG, "-----adExposed-----")
            }

            override fun adClicked(info: AdInfo) {
                Log.d(TAG, "-----adClicked-----")
            }

            override fun adClose(info: AdInfo) {
                Log.d(TAG, "-----adClose-----")
                goFinishActivity()
            }

            override fun adError(errorCode: Int, errorMsg: String) {
                Log.d(TAG, "-----adError-----$errorMsg")
                goFinishActivity()
            }
        })
    }

    private fun goFinishActivity() {
        var isOpen = false
        if (null != AppHolder.getInstance().switchInfoList && null != AppHolder.getInstance().switchInfoList.data && AppHolder.getInstance().switchInfoList.data.size > 0) {
            for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                if (PositionId.KEY_VIRUS == switchInfoList.configKey && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                    isOpen = switchInfoList.isOpen
                }
            }
        }
        if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.virus_kill), mRamScale, mNotifySize, mPowerSize) < 3) {
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            startActivity(CleanFinishAdvertisementActivity::class.java, bundle)
            finish()
        } else {
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            startActivity(NewCleanFinishActivity::class.java, bundle)
            finish()
        }
    }

    override fun netError() {
    }


}