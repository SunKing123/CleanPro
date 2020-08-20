package com.xiaoniu.cleanking.ui.accwidget

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation.AnimationListener
import com.airbnb.lottie.LottieAnimationView
import com.xiaoniu.cleanking.R
import kotlinx.android.synthetic.main.activity_widget_acc_animation_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetAnimationActivity : Activity() {

    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_acc_animation_layout)
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
        var intent = Intent()
        intent.setClass(this, AccWidgetCleanFinishActivity::class.java)
        startActivity(intent)

        finish()
    }
}
