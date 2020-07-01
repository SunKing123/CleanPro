package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.os.Bundle
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.mvp.BaseActivity
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import kotlinx.android.synthetic.main.activity_finish_layout.btnLeft
import org.jetbrains.anko.intentFor


/**
 *  Created by zhaoyingtao
 *  Date: 2020/7/1
 *  Describe: 领取金币成功页面
 */
class GoldCoinSuccessActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(context.intentFor<GoldCoinSuccessActivity>())
        }
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_gold_coin_success)
    }

    override fun initViews() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true)
    }

    override fun initData() {
        btnLeft.setOnClickListener { finish() }
//        ad_frameLayout.addView(View(this))
    }

}