package com.xiaoniu.cleanking.ui.finish

import android.app.Activity
import android.view.View
import android.view.ViewGroup
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
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.ToastUtils
import com.xnad.sdk.ad.entity.AdInfo
import com.xnad.sdk.ad.listener.AbsAdCallBack
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public class NewCleanFinishPlusActivity : BaseActivity<CleanFinishPlusPresenter>(),NewCleanFinishPlusContract.CleanFinishView {

    var titleName:String=""

    override fun getLayoutId(): Int {
        return R.layout.activity_new_clean_finish_plus_layout
    }

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initView() {
        titleName= intent.getStringExtra("title")
        mPresenter.attachView(this)
        mPresenter.onCreate()
        //todo 这里替换成广告位容器布局
        mPresenter.loadOneAdv(LinearLayout(this))
        mPresenter.loadTwoAdv(LinearLayout(this))

        mPresenter.getGoldCoin()
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
        bean.advCallBack = object : AbsAdCallBack() {}
        bean.closeClickListener = View.OnClickListener { view: View? -> StatisticsUtils.trackClick("close_click", "弹窗关闭点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson()) }
        bean.onDoubleClickListener = View.OnClickListener { v: View? ->
            if (AndroidUtil.isFastDoubleBtnClick(1000)) {
                return@OnClickListener
            }
            StatisticsUtils.trackClick("double_the_gold_coin_click", "金币翻倍按钮点击", "", "success_page_gold_coin_pop_up_window", getStatisticsJson())
            StatisticsUtils.customTrackEvent("ad_request_sdk_2", "功能完成页翻倍激励视频广告发起请求", "", "success_page_gold_coin_pop_up_window", getStatisticsMap())
            val viewGroup = getWindow().getDecorView() as ViewGroup
            val params = AdRequestParams.Builder().setActivity(this).setViewContainer(viewGroup).setAdId(MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON).build()
            MidasRequesCenter.requestAd(params, object : VideoAbsAdCallBack() {
                override fun onShowError(i: Int, s: String) {
                    super.onShowError(i, s)
                    ToastUtils.showLong("网络异常")
                    GoldCoinDialog.dismiss()
                }

                override fun onAdError(adInfo: AdInfo, i: Int, s: String) {
                    super.onAdError(adInfo, i, s)
                    ToastUtils.showLong("网络异常")
                    GoldCoinDialog.dismiss()
                }

                override fun onAdClose(adInfo: AdInfo, isComplete: Boolean) {
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

                override fun onAdVideoComplete(adInfo: AdInfo) {
                    super.onAdVideoComplete(adInfo)
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
            jsonObject.put("function_name",titleName)
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
