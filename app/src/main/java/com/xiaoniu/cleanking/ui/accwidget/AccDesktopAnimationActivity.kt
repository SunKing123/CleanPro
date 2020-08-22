package com.xiaoniu.cleanking.ui.accwidget

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import kotlinx.android.synthetic.main.activity_widget_acc_animation_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccDesktopAnimationActivity : Activity() {

    var context: Context = this
    var configBean: InsertAdSwitchInfoList.DataBean? = null
    var animation_image_file = "images_widget_acc"
    var animation_json = "widget_acc.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent), true)
        } else {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent), false)
        }

        setContentView(R.layout.activity_widget_acc_animation_layout)
        configBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_WIDGET_ACC_FINISH)
        loadAnimationAndPlay()

        AccWidgetPoint.shortcutIconClick()
    }


    fun loadAnimationAndPlay() {
        lottie_animation.imageAssetsFolder = animation_image_file
        lottie_animation.setAnimation(animation_json)
        lottie_animation.playAnimation()

        lottie_animation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                toFinishActivity()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    fun toFinishActivity() {
        if (configBean != null && configBean!!.isOpen) {
            var intent = Intent()
            intent.setClass(this, AccDesktopCleanFinishActivity::class.java)
            startActivity(intent)
        }

        //过了冷却时间，进行刷新
        if (PreferenceUtil.getWidgetAccCleanTime()) {
            AccWidgetViewManager.updateAccCleanedProgress(this)
            PreferenceUtil.saveWidgetAccCleanTime()
        }
        finish()
    }
}
