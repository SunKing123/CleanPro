package com.xiaoniu.cleanking.ui.newclean.activity

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.midas.AdRequestParams
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.mvp.BaseActivity
import com.xiaoniu.cleanking.ui.newclean.util.OutlineProvider
import com.xiaoniu.cleanking.utils.DimenUtils
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xnad.sdk.ad.listener.AbsAdCallBack
import kotlinx.android.synthetic.main.activity_finish_layout.btnLeft
import kotlinx.android.synthetic.main.activity_gold_coin_success.*


/**
 *  Created by zhaoyingtao
 *  Date: 2020/7/1
 *  Describe: 领取金币成功页面
 */
class GoldCoinSuccessActivity : BaseActivity() {


    private var coinNum: Int = 0

    companion object {
        const val COIN_NUM = "coin_num"
        const val AD_ID = "ad_id"
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_gold_coin_success)
    }

    override fun initViews() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ad_frameLayout.outlineProvider = OutlineProvider(DimenUtils.dp2px(context, 6f).toFloat())
            ad_frameLayout.clipToOutline = true
        }
    }

    override fun initData() {
        coinNum = intent.getIntExtra(COIN_NUM, 0)
        //广告ID，如果广告位没有打开的话，不传这个ID即可（在进入ACTIVITY前自行判断是否打开了配置）
        val adId = intent.getStringExtra(AD_ID)
        var text = "到账" + coinNum + "金币";

        var sp = SpannableString(text)
        var start = text.indexOf(coinNum.toString())
        var end = start + coinNum.toString().length;
        var size = AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.dp_24))
        sp.setSpan(size, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_coin_content.setText(sp)

        btnLeft.setOnClickListener { finish() }


        if (!TextUtils.isEmpty(adId)) {
            //显示广告
            val params = AdRequestParams.Builder().setAdId(adId).setViewContainer(ad_frameLayout)
                    .setActivity(this).build()
            MidasRequesCenter.requestAd(params, object : AbsAdCallBack() {

            })

        }


    }


}
