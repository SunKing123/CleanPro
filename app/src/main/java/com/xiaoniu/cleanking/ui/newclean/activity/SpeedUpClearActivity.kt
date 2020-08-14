package com.xiaoniu.cleanking.ui.newclean.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.AppLifecyclesImpl
import com.xiaoniu.cleanking.base.ScanDataHolder
import com.xiaoniu.cleanking.bean.JunkResultWrapper
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.base.BaseActivity
import com.xiaoniu.common.utils.AppUtils
import com.xiaoniu.common.utils.DisplayUtils
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_speedup_clear.*
import java.util.*

class SpeedUpClearActivity : BaseActivity() {
    override fun setListener() {
    }

    override fun getLayoutResId() = R.layout.activity_speedup_clear

    override fun loadData() {
    }

    override fun initVariable(intent: Intent?) {
    }

    companion object {
        const val SPEED_UP_NUM = "speedup_num"
    }


    private val mJunkResultWrapperList by lazy { LinkedList<JunkResultWrapper>() }

    private var mCurrentIndex = 0

    private var mSpeedUpNum = "20"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTransparentForWindow(this)
        toolBar.setBackgroundResource(R.color.color_02D086)
        mBtnLeft.setImageResource(0)
        StatisticsUtils.onPageStart("boost_animation_page_view_page", "加速动画页浏览")
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsUtils.onPageEnd("boost_animation_page_view_page", "加速动画页浏览", "boost_animation_page", "boost_animation_page")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        StatisticsUtils.trackClick("system_return_click", "加速动画页返回", "boost_animation_page", "boost_animation_page")
    }

    private var mTotalSize = 0

    override fun initViews(savedInstanceState: Bundle?) {
        mSpeedUpNum = intent.getStringExtra(SPEED_UP_NUM)
        top_animation.imageAssetsFolder = "images_speedup_clear"
        top_animation.setAnimation("data_speedup_clear.json")
        top_animation.playAnimation()
        top_animation.repeatCount = 2
        mJunkResultWrapperList.addAll(ScanDataHolder.getInstance().junkResultWrapperList)
        app_num.text = 1.toString()
        mTotalSize = mJunkResultWrapperList.size
        app_progress_label.text = "1/$mTotalSize"
        AppLifecyclesImpl.post {
            val bitmap = getNextImg()
            if (bitmap != null) {
                app_logo.setImageBitmap(bitmap)
                //动画总时长3秒钟，算出每个动画需要多长时间
                val itemTime = if (mJunkResultWrapperList.size < 3) 1000L else (3000 / mJunkResultWrapperList.size).toLong()
                playIconAnim1(app_logo, itemTime)
            }
        }
    }


    private fun playIconAnim1(ivIcon: ImageView, time: Long) {
        val distance = DisplayUtils.dip2px(165F).toFloat()
        val anim1 = ValueAnimator.ofFloat(0f, distance)
        anim1.duration = time
        ivIcon.translationY = 0f
        ivIcon.scaleX = 1f
        ivIcon.scaleY = 1f
        anim1.addUpdateListener { animation: ValueAnimator ->
            val currentValue = animation.animatedValue as Float
            val percent = currentValue / distance
            val scale = 1 - percent
            if (scale > 0.4) {
                ivIcon.scaleX = scale
                ivIcon.scaleY = scale
            }
            ivIcon.translationY = -currentValue
        }
        anim1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                val bitmap: Bitmap? = getNextImg()
                if (bitmap != null) {
                    app_progress_label.text = "$mCurrentIndex/$mTotalSize"
                    app_num.text = mCurrentIndex.toString()
                    ivIcon.setImageBitmap(bitmap)
                    playIconAnim1(app_logo, time)
                } else {
                    top_animation.cancelAnimation()
                    ivIcon.visibility = View.GONE
                    jumpFinish()

                }
            }
        })
        anim1.start()

    }


    private fun jumpFinish() {
        if (isFinishing) {
            return
        }
        PreferenceUtil.saveOneKeySpeedNum(mSpeedUpNum)
        NewCleanFinishPlusActivity.start(this,getString(R.string.tool_one_key_speed),true)
        finish()
    }

    private fun getNextImg(): Bitmap? {
        if (mCurrentIndex < mJunkResultWrapperList.size) {
            val item = mJunkResultWrapperList[mCurrentIndex]
            mCurrentIndex++
            var icon = AppUtils.getAppIcon(this, item.firstJunkInfo.appPackageName)
            if (icon == null) {
                icon = BitmapFactory.decodeResource(resources, R.drawable.clean_icon_apk)
            }
            return icon
        }
        return null
    }
}
