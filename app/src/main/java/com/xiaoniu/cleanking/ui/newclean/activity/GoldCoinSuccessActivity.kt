package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.widget.ImageView
import android.widget.TextView
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
import java.lang.StringBuilder


/**
 *  Created by zhaoyingtao
 *  Date: 2020/7/1
 *  Describe: 领取金币成功页面
 */
class GoldCoinSuccessActivity : BaseActivity() {



    private var coinNum: Int = 0

    companion object {
        const val COIN_NUM="coin_num"
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_gold_coin_success)
    }

    override fun initViews() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true)
    }

    override fun initData() {
        coinNum = intent.getIntExtra(COIN_NUM, 0);
        var text="到账"+coinNum+"金币";

        var sp=SpannableString(text)
        var start=text.indexOf(coinNum.toString())
        var end=start+coinNum.toString().length;
        var size= AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.dp_24))
        sp.setSpan(size,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_coin_content.setText(sp)

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
