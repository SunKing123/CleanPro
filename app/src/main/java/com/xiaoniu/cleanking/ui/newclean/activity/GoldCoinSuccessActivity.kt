package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.content.Intent
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
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel
import com.xiaoniu.cleanking.ui.newclean.util.OutlineProvider
import com.xiaoniu.cleanking.utils.DimenUtils
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
import com.xnad.sdk.ad.entity.AdInfo
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
    private lateinit var model: GoldCoinDoubleModel
    private  var extParam = HashMap<String, Any>()

    companion object {
        fun start(context: Context, model: GoldCoinDoubleModel) {
            var intent = Intent(context, GoldCoinSuccessActivity::class.java);
            intent.putExtra("model", model)
            context.startActivity(intent)
        }
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_gold_coin_success)
        model = intent.getParcelableExtra("model")
        coinNum = model.goldCoinsNum
        exposurePoint()
        goldCoinsNumPoint()
    }

    override fun initViews() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ad_frameLayout.outlineProvider = OutlineProvider(DimenUtils.dp2px(context, 6f).toFloat())
            ad_frameLayout.clipToOutline = true
        }
    }

    override fun initData() {
        initCoinView();
        btnLeft.setOnClickListener { finish() }
        loadAd()
    }

    fun loadAd(){
        //广告ID，如果广告位没有打开的话，不传这个ID即可（在进入ACTIVITY前自行判断是否打开了配置）
        if (!TextUtils.isEmpty(model.adId)) {
            val params = AdRequestParams.Builder().setAdId(model.adId).setViewContainer(ad_frameLayout)
                    .setActivity(this).build()
            adRequestPoint()
            MidasRequesCenter.requestAd(params, object : AbsAdCallBack() {
                override fun onAdClose(p0: AdInfo?) {
                    super.onAdClose(p0)
                    loadAd()
                }

                override fun onAdError(p0: AdInfo?, p1: Int, p2: String?) {
                    super.onAdError(p0, p1, p2)
                    LogUtils.d("=====================goldCoinSuccess onAdError()"+p2)
                }
            })
        }
    }
    override fun finish() {
        super.finish()
        returnBackPoint()
    }

    fun initCoinView() {
        var text = "到账" + coinNum + "金币";
        var sp = SpannableString(text)
        var start = text.indexOf(coinNum.toString())
        var end = start + coinNum.toString().length;
        var size = AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.dp_24))
        sp.setSpan(size, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_coin_content.setText(sp)
    }

    //广告请求埋点
    fun adRequestPoint() {
        var adRequestName = ""
        when (model.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                adRequestName = Points.ScratchCard.SUCCESS_AD_REQUEST_SDK_NAME;
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                adRequestName = Points.FunctionGoldCoin.SUCCESS_AD_REQUEST_SDK_NAME;
            Points.MainGoldCoin.SUCCESS_PAGE ->
                adRequestName = Points.MainGoldCoin.SUCCESS_AD_REQUEST_SDK_NAME;
        }
        StatisticsUtils.customTrackEvent("ad_request_sdk", adRequestName, "", model.currentPage, extParam())
    }

    //曝光埋点
    fun exposurePoint() {
        when (model.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.ScratchCard.SUCCESS_EXPOSURE_CODE, Points.ScratchCard.SUCCESS_EXPOSURE_NAME, "", model.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.FunctionGoldCoin.SUCCESS_EXPOSURE_CODE, Points.FunctionGoldCoin.SUCCESS_EXPOSURE_NAME, "", model.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.MainGoldCoin.SUCCESS_EXPOSURE_CODE, Points.MainGoldCoin.SUCCESS_EXPOSURE_NAME, "", model.currentPage, extParam())

        }
    }

    //金币翻倍数量埋点
    fun goldCoinsNumPoint() {
        when (model.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.ScratchCard.SUCCESS_NUMBER_OF_GOLD_NAME, "", model.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.FunctionGoldCoin.SUCCESS_NUMBER_OF_GOLD_NAME, "", model.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.MainGoldCoin.SUCCESS_NUMBER_OF_GOLD_NAME, "", model.currentPage, extParam())

        }
    }

    //返回事件埋点
    fun returnBackPoint() {
        when (model.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.ScratchCard.SUCCESS_RETURN_CLICK_NAME, "", model.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.FunctionGoldCoin.SUCCESS_RETURN_CLICK_NAME, "", model.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.MainGoldCoin.SUCCESS_RETURN_CLICK_NAME, "", model.currentPage, extParam())

        }
    }

    fun extParam(): HashMap<String, Any> {
        if (extParam.containsKey("position_id")) {
            return extParam
        }
        extParam.put("position_id", model.position)
        extParam.put("gold_number", model.goldCoinsNum)
        extParam.put("function_name", model.functionName)
        return extParam;
    }
}
