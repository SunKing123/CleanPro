package com.xiaoniu.cleanking.ui.finish

import android.app.Activity
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.midas.AdRequestParams
import com.xiaoniu.cleanking.midas.MidasConstants
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.finish.presenter.CleanFinishPlusPresenter
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.common.utils.DisplayUtils
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.common.utils.ToastUtils
import com.xnad.sdk.ad.entity.AdInfo
import com.xnad.sdk.ad.listener.AbsAdCallBack
import kotlinx.android.synthetic.main.activity_new_clean_finish_plus_layout.*
import com.xiaoniu.unitionadbase.model.AdInfoModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public class NewCleanFinishPlusActivity : BaseActivity<CleanFinishPlusPresenter>(), NewCleanFinishPlusContract.CleanFinishView {

    var titleName: String = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_new_clean_finish_plus_layout
    }

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        titleName = intent.getStringExtra("title")
        mPresenter.attachView(this)
        mPresenter.onCreate()
        //todo 这里替换成广告位容器布局
        mPresenter.loadOneAdv(FrameLayout(this))
        mPresenter.loadTwoAdv(FrameLayout(this))

        titleName = "手机清理"
        left_title.text = titleName
        left_title.setOnClickListener {
            onBackPressed()
        }
        when (titleName) {
            "建议清理" -> showSuggestClearView("886", "MB")
            "一键加速" -> show0neKeySpeedUp("24")
            "病毒查杀" -> showKillVirusView()
            "超强省电" -> showPowerSaving()
            "微信专理" -> showWeiXinClear()
            "手机降温" -> showPhoneCold("37", "60")
            "通知栏清理" -> showNotificationClear()
            "网络加速" -> showNetSpeedUp("80")
            "手机清理" -> showPhoneClear()
        }
    }

    //建议清理
    private fun showSuggestClearView(num: String, unit: String) {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        val content = num.plus(unit)
        val spannableString = SpannableString(content)
        val sizeSpan = AbsoluteSizeSpan(DisplayUtils.sp2px(this, 30F))
        spannableString.setSpan(sizeSpan, 0, num.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        function_sub_title.text = "垃圾已清理"
        function_sub_title.textSize = 10F
    }

    //一键加速
    private fun show0neKeySpeedUp(num: String) {
        function_icon.setImageResource(R.mipmap.finish_icon_speedup)
        val content = "运行速度已提升$num%"
        val spannableString = SpannableString(content)
        val styleSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpan, content.length - 1 - num.length, content.length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
    }

    //病毒查杀
    private fun showKillVirusView() {
        function_icon.setImageResource(R.mipmap.finish_icon_virus)
        function_sub_title.visibility = View.GONE
    }

    //超强省电
    private fun showPowerSaving() {
        function_icon.setImageResource(R.mipmap.finish_icon_power)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他功能"
    }

    //微信清理
    private fun showWeiXinClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_weixin)
        function_title.text = "已清理"
    }

    //手机降温
    private fun showPhoneCold(num: String, time: String) {
        function_icon.setImageResource(R.mipmap.finish_icon_cold)
        val content = "成功降温$num°C"
        val spannableString = SpannableString(content)
        val styleSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpan, content.indexOf(num), content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        val subContent = "${time}s后达到最佳降温效果"
        val subSpannableString = SpannableString(subContent)
        subSpannableString.setSpan(styleSpan, 0, subContent.indexOf("s"), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_sub_title.text = subSpannableString

    }

    //通知栏清理
    private fun showNotificationClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_notification)
        function_title.text = "通知栏很干净"
        function_sub_title.text = "快去体验其他清理功能"
    }

    //网络加速
    private fun showNetSpeedUp(num: String) {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        val content = num.plus("%")
        val spannableString = SpannableString(content)
        val sizeSpan = AbsoluteSizeSpan(DisplayUtils.sp2px(this, 30F))
        spannableString.setSpan(sizeSpan, 0, num.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        function_sub_title.text = "网络已提速"
        function_sub_title.textSize = 10F
    }

    //手机清理
    private fun showPhoneClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

    override fun netError() {

    }

    /**
     * 显示第一个推荐功能视图
     */
    override fun visibleRecommendViewFirst(item: RecmedItemModel) {

    }

    /**
     * 显示第二个推荐功能视图
     */
    override fun visibleRecommendViewSecond(item: RecmedItemModel) {

    }

    /**
     * 显示刮刮卡引导视图
     */
    override fun visibleScratchCardView() {

    }

    /**
     * 金币弹框
     */
    override fun showGoldCoinDialog(bubbleCollected: BubbleCollected) {
        val bean = GoldCoinDialogParameter()
        bean.dialogType = 3
        bean.obtainCoinCount = bubbleCollected.data.goldCount
        bean.totalCoinCount = bubbleCollected.data.totalGoldCount.toDouble()
        bean.adId = MidasConstants.FINISH_GET_GOLD_COIN
        bean.context = this
        bean.isRewardOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO)
//        bean.advCallBack = object : AbsAdCallBack() {}
        bean.closeClickListener = View.OnClickListener { view: View? -> StatisticsUtils.trackClick("close_click", "弹窗关闭点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson()) }
        bean.onDoubleClickListener = View.OnClickListener { v: View? ->
            if (AndroidUtil.isFastDoubleBtnClick(1000)) {
                return@OnClickListener
            }
            StatisticsUtils.trackClick("double_the_gold_coin_click", "金币翻倍按钮点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson())
            StatisticsUtils.customTrackEvent("ad_request_sdk_2", "功能完成页翻倍激励视频广告发起请求", "", "success_page_gold_coin_pop_up_window", getStatisticsMap())
            val viewGroup = getWindow().getDecorView() as ViewGroup
            val params = AdRequestParams.Builder().setActivity(this).setViewContainer(viewGroup).setAdId(MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON).build()

            MidasRequesCenter.requestAndShowAd(this,MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON,object : VideoAbsAdCallBack(){
                override fun onAdLoadError(errorCode: String?, errorMsg: String?) {
                    super.onAdLoadError(errorCode, errorMsg)
                    ToastUtils.showLong("网络异常")
                    GoldCoinDialog.dismiss()
                }

                override fun onAdClose(adInfo: AdInfoModel?, isComplete: Boolean) {
                    super.onAdClose(adInfo, isComplete)
                    StatisticsUtils.trackClick("incentive_video_ad_click", "功能完成页金币翻倍激励视频广告关闭点击", "", "success_page_gold_coin_pop_up_window_incentive_video_page", getStatisticsJson())
                    if (isComplete) {
                        //播放完成的话去翻倍
                        mPresenter.addDoubleGoldCoin(bubbleCollected)
                    } else {
                        //没有播放完成就关闭广告的话把弹窗关掉
                        GoldCoinDialog.dismiss()
                    }
                }

                override fun onAdVideoComplete(adInfoModel: AdInfoModel?) {
                    super.onAdVideoComplete(adInfoModel)
                }
            })
        }
        StatisticsUtils.customTrackEvent("success_page_gold_coin_pop_up_window_custom", "功能完成页金币领取弹窗曝光", "", "success_page_gold_coin_pop_up_window")
        StatisticsUtils.customTrackEvent("ad_request_sdk_1", "功能完成页金币领取弹窗上广告发起请求", "", "success_page_gold_coin_pop_up_window", getStatisticsMap())
        GoldCoinDialog.showGoldCoinDialog(bean)
    }

    override fun dismissGoldCoinDialog() {
        GoldCoinDialog.dismiss()
    }

    private fun getStatisticsMap(): Map<String, Any>? {
        val map: MutableMap<String, Any> = HashMap()
        map["position_id"] = 5
        map["function_name"] = titleName
        return map
    }

    private fun getStatisticsJson(): JSONObject? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("position_id", 5)
            jsonObject.put("function_name", titleName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getFunctionTitle(): String {
        return this.titleName
    }

    override fun onPostResume() {
        super.onPostResume()
        mPresenter.onPostResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
