package com.xiaoniu.cleanking.ui.accwidget

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xiaoniu.unitionadbase.model.AdInfoModel
import kotlinx.android.synthetic.main.activity_acc_widget_clean_finish_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetCleanFinishActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent), true)
        } else {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent), false)
        }
        setContentView(R.layout.activity_acc_widget_clean_finish_layout)
        initView()

        AccWidgetPoint.finishPageCreate()
    }

    fun initView() {
        if (PreferenceUtil.getWidgetAccCleanTime()) {
            var memoryLower = NumberUtils.mathRandomInt(10, 30)
            tv_cleaned_memory.text = "释放内存" + memoryLower + "%"
            tv_cleaned_memory_sub.text = "手机运行速度快如闪电"
        } else {
            tv_cleaned_memory.text = "已优化"
            tv_cleaned_memory_sub.text = "手机已加速"
        }

        var storageGarbage = NumberUtils.mathRandomInt(300, 800)
        tv_storage_garbage.text = storageGarbage.toString() + "MB"

        initEvent()
        loadAdv()

        if (!PreferenceUtil.getNowCleanTime()) {
            memory_view.visibility = View.GONE
            tv_goCleanStorage.visibility = View.GONE
        }
    }

    fun initEvent() {
        tv_goCleanStorage.setOnClickListener({ goToCleanStorage() })
        scene_close.setOnClickListener({ finish() })
    }

    fun goToCleanStorage() {
        var intent = Intent()
        intent.setClass(this, NowCleanActivity::class.java)
        startActivity(intent)

        AccWidgetPoint.finishClickClean()
        finish()
    }


    fun loadAdv() {
        var isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_WIDGET_ACC_FINISH, PositionId.DRAW_ONE_CODE)
        if (!isOpenOne) {
            return
        }
        AccWidgetPoint.finishAdvRequest()

        var adId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_WIDGET_ACC_FINISH, PositionId.DRAW_ONE_CODE)
        MidasRequesCenter.requestAndShowAd(this, adId, object : SimpleViewCallBack(findViewById(R.id.ad_container)) {
            override fun onAdLoaded(adInfoModel: AdInfoModel?) {
                super.onAdLoaded(adInfoModel)
                ad_separate.visibility = View.VISIBLE
            }

            override fun onAdLoadError(errorCode: String?, errorMsg: String?) {
                super.onAdLoadError(errorCode, errorMsg)
                LogUtils.e("====================:errorCode:${errorCode} message:$errorMsg")
            }
        })
    }

    override fun finish() {
        super.finish()
    }
}
