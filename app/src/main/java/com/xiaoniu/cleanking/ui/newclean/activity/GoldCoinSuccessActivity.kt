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
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
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
    private lateinit var point: GoldCoinDoubleModel
    private lateinit var extParam: HashMap<String, Any>

    companion object {
        const val COIN_NUM = "coin_num"
        const val AD_ID = "ad_id"

        fun start(context: Context, model: GoldCoinDoubleModel) {
            var intent = Intent(context, GoldCoinSuccessActivity::class.java);
            intent.putExtra("model", model)
            context.startActivity(intent)
        }
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_gold_coin_success)
    }

    override fun initViews() {
        exposurePoint()
        StatusBarCompat.translucentStatusBarForImage(this, true, true)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ad_frameLayout.outlineProvider = OutlineProvider(DimenUtils.dp2px(context, 6f).toFloat())
            ad_frameLayout.clipToOutline = true
        }
    }

    override fun initData() {
        point = intent.getParcelableExtra("model")
        coinNum = point.goldCoinsNum
        //广告ID，如果广告位没有打开的话，不传这个ID即可（在进入ACTIVITY前自行判断是否打开了配置）
        val adId = intent.getStringExtra(AD_ID)

        initCoinView();

        btnLeft.setOnClickListener { finish() }

        if (!TextUtils.isEmpty(adId)) {
            //显示广告
            val params = AdRequestParams.Builder().setAdId(adId).setViewContainer(ad_frameLayout)
                    .setActivity(this).build()
            adRequestPoint()
            MidasRequesCenter.requestAd(params, object : AbsAdCallBack() {

            })
        }
        goldCoinsNumPoint()
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
        when (point.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                adRequestName = Points.ScratchCard.SUCCESS_AD_REQUEST_SDK_NAME;
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                adRequestName = Points.FunctionGoldCoin.SUCCESS_AD_REQUEST_SDK_NAME;
            Points.MainGoldCoin.SUCCESS_PAGE ->
                adRequestName = Points.MainGoldCoin.SUCCESS_AD_REQUEST_SDK_NAME;
        }
        StatisticsUtils.customTrackEvent("ad_request_sdk", adRequestName, "", point.currentPage, extParam())
    }

    //曝光埋点
    fun exposurePoint() {
        when (point.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.ScratchCard.SUCCESS_EXPOSURE_CODE, Points.ScratchCard.SUCCESS_EXPOSURE_NAME, "", point.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.FunctionGoldCoin.SUCCESS_EXPOSURE_CODE, Points.FunctionGoldCoin.SUCCESS_EXPOSURE_NAME, "", point.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent(Points.MainGoldCoin.SUCCESS_EXPOSURE_CODE, Points.MainGoldCoin.SUCCESS_EXPOSURE_NAME, "", point.currentPage, extParam())

        }
    }

    //金币翻倍数量埋点
    fun goldCoinsNumPoint() {
        when (point.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.ScratchCard.SUCCESS_NUMBER_OF_GOLD_NAME, "", point.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.FunctionGoldCoin.SUCCESS_NUMBER_OF_GOLD_NAME, "", point.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.MainGoldCoin.SUCCESS_NUMBER_OF_GOLD_NAME, "", point.currentPage, extParam())

        }
    }

    //返回事件埋点
    fun returnBackPoint() {
        when (point.currentPage) {
            Points.ScratchCard.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.ScratchCard.SUCCESS_RETURN_CLICK_NAME, "", point.currentPage, extParam())
            Points.FunctionGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.FunctionGoldCoin.SUCCESS_RETURN_CLICK_NAME, "", point.currentPage, extParam())
            Points.MainGoldCoin.SUCCESS_PAGE ->
                StatisticsUtils.trackClickNew("return_click", Points.MainGoldCoin.SUCCESS_RETURN_CLICK_NAME, "", point.currentPage, extParam())

        }
    }

    fun extParam(): HashMap<String, Any> {
        if (extParam != null) {
            return extParam
        }
        extParam = HashMap();
        extParam.put("position_id", point.position)
        extParam.put("gold_number", point.goldCoinsNum)
        extParam.put("function_name", point.functionName)
        return extParam;
    }
}
