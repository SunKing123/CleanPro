package com.xiaoniu.cleanking.ui.accwidget

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList
import com.xiaoniu.cleanking.ui.main.config.PositionId
import kotlinx.android.synthetic.main.activity_widget_acc_animation_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetAnimationActivity : Activity() {

    var context: Context = this
    var configBean: InsertAdSwitchInfoList.DataBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_acc_animation_layout)
        configBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_WIDGET_ACC_FINISH)
        loadAnimationAndPlay()
    }

    fun loadAnimationAndPlay() {
        lottie_animation.imageAssetsFolder = "images_widget_acc"
        lottie_animation.setAnimation("widget_acc.json")
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
       if(configBean!=null&&configBean!!.isOpen){
            var intent = Intent()
            intent.setClass(this, AccWidgetCleanFinishActivity::class.java)
            startActivity(intent)
      }
        finish()
    }
}
