package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.os.Bundle
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.midas.AdRequestParams
import com.xiaoniu.cleanking.midas.MidasConstants
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.mvp.BaseActivity
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xnad.sdk.ad.listener.AbsAdCallBack
import kotlinx.android.synthetic.main.activity_finish_layout.btnLeft
import kotlinx.android.synthetic.main.activity_gold_coin_success.*
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

        //显示广告
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
            val params = AdRequestParams.Builder().setAdId(MidasConstants.GET_DOUBLE_GOLD_COIN_SUCCESS).setViewContainer(ad_frameLayout)
                    .setActivity(this).build()
            MidasRequesCenter.requestAd(params, object : AbsAdCallBack() {

            })
        }


    }

}