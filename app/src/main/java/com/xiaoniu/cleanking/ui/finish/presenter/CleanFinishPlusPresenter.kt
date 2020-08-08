package com.xiaoniu.cleanking.ui.finish.presenter

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.midas.AdRequestParams
import com.xiaoniu.cleanking.midas.MidasConstants
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemDataStore
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.main.bean.*
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel
import com.xiaoniu.cleanking.ui.main.model.MainModel
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity.Companion.start
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.net.Common3Subscriber
import com.xiaoniu.cleanking.utils.net.RxUtil
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.ToastUtils
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback
import com.xiaoniu.unitionadbase.model.AdInfoModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * Created by xinxiaolong on 2020/8/5.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanFinishPlusPresenter : NewCleanFinishPlusContract.CleanFinishPresenter<NewCleanFinishPlusActivity, MainModel> {

    @JvmField
    @Inject
    var mModel: MainModel? = null

    lateinit var view: NewCleanFinishPlusActivity
    private lateinit var itemDataStore: RecmedItemDataStore
    private var isOpenOne = false
    private var isOpenTwo = false
    private var isFirst = true

    @Inject
    public constructor() {

    }

    override fun onCreate() {
        loadAdSwitch()
    }

    override fun attachView(view: NewCleanFinishPlusActivity) {
        this.view = view
        this.itemDataStore = RecmedItemDataStore.getInstance()
    }

    /**
     * 广告位开关
     */
    private fun loadAdSwitch() {
        isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE)
        isOpenTwo = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_TWO_CODE)
    }

    /**
     * 装载推荐功能布局
     */
     override fun loadRecommendData() {
        itemDataStore.resetIndex()

        var firstModel: RecmedItemModel? = itemDataStore.popModel()

        if (firstModel != null) {
            view.visibleRecommendViewFirst(firstModel)
        }

        var secondModel: RecmedItemModel? = itemDataStore.popModel()
        if (secondModel != null) {
            view.visibleRecommendViewSecond(secondModel)
        }

        if (secondModel == null) {
            view.visibleScratchCardView()
        }else{
            view.goneScratchCardView()
        }
    }

    /**
     * 加载弹框
     */
    override fun loadPopView() {
        val config: InsertAdSwitchInfoList.DataBean? = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_FINISH_INSIDE_SCREEN)
        config?.let {
            if (it.isOpen) {
                loadInsideScreenDialog()
            } else {
                loadGoldCoinDialog()
            }
        }
    }

    //显示内部插屏广告
    fun loadInsideScreenDialog() {
        if (view.getActivity() == null) {
            return
        }
        StatisticsUtils.customTrackEvent(
                "ad_request_sdk_4",
                "功能完成页广告位4发起请求",
                "",
                "success_page"
        )
//        val params: AdRequestParams = AdRequestParams.Builder()
//                .setActivity(view.getActivity()).setAdId(MidasConstants.FINISH_INSIDE_SCREEN_ID).build()
        MidasRequesCenter.requestAndShowAd(view.getActivity(),MidasConstants.FINISH_INSIDE_SCREEN_ID,object : AbsAdBusinessCallback(){
            override fun onAdExposure(adInfoModel: AdInfoModel?) {
                super.onAdExposure(adInfoModel)
                LogUtils.e("====完成页内部插屏广告展出======")
            }
        })
    }

    fun loadGoldCoinDialog() {
        getGoldCoin()
    }

    /**
     * 加载第一个广告位数据
     */
    override fun loadOneAdv(advContainer: FrameLayout) {
        if (!isOpenOne) return
        StatisticsUtils.customTrackEvent("ad_request_sdk_1", "功能完成页广告位1发起请求", NewCleanFinishActivity.sourcePage, "success_page")
        MidasRequesCenter.requestAndShowAd(view.getActivity(),MidasConstants.FINISH01_TOP_FEEED_ID,object : SimpleViewCallBack(advContainer){

        })
    }

    /**
     * 加载第二个广告位数据
     *
     */
    override fun loadTwoAdv(advContainer: FrameLayout) {
        if (!isOpenTwo) return
        StatisticsUtils.customTrackEvent("ad_request_sdk_2", "功能完成页广告位2发起请求", NewCleanFinishActivity.sourcePage, "success_page")
        MidasRequesCenter.requestAndShowAd(view.getActivity(),MidasConstants.FINISH01_CENTER_FEEED_ID,object : SimpleViewCallBack(advContainer){

        })
    }

    /**
     * 获取可以加金币的数量
     */
    fun getGoldCoin() {
        mModel?.getGoleGonfigs(object : Common3Subscriber<BubbleConfig?>() {
            override fun showExtraOp(code: String, message: String) {  //关心错误码；
                ToastUtils.showShort(message)
            }

            override fun getData(bubbleConfig: BubbleConfig?) {
                if (bubbleConfig != null && bubbleConfig.data.size > 0) {
                    for (item in bubbleConfig.data) {
                        if (item.locationNum == 5) {
                            addGoldCoin(item.goldCount)
                            break
                        }
                    }
                }
            }

            override fun showExtraOp(message: String) {}
            override fun netConnectError() {
                ToastUtils.showShort(R.string.notwork_error)
            }
        }, RxUtil.rxSchedulerHelper<ImageAdEntity>(view))
    }

    /**
     * 根据添加数量，添加金币
     */
    private fun addGoldCoin(goldNum: Int) {
        if(goldNum==0){
            return
        }
        mModel?.goleCollect(object : Common3Subscriber<BubbleCollected?>() {
            override fun showExtraOp(code: String, message: String) {  //关心错误码；
                // ToastUtils.showShort(message);
            }

            override fun getData(bubbleConfig: BubbleCollected?) {
                //实时更新金币信息
                RequestUserInfoUtil.getUserCoinInfo()
                val map: MutableMap<String, Any> = getStatisticsMap() as MutableMap<String, Any>
                map["gold_number"] = goldNum
                StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", "功能完成页领取弹窗金币发放数", "", "success_page_gold_coin_pop_up_window", map)
                if (bubbleConfig != null) {
                    //添加成功后，展示金币弹框
                    view.showGoldCoinDialog(bubbleConfig)
                }
            }

            override fun showExtraOp(message: String) {}
            override fun netConnectError() {
                ToastUtils.showShort(R.string.notwork_error)
            }
        }, RxUtil.rxSchedulerHelper<ImageAdEntity>(view), 5)
    }

    /**
     * 激励视频看完，进行金币翻倍
     */
    override fun addDoubleGoldCoin(bubbleCollected: BubbleCollected) {
        mModel?.goldDouble(object : Common3Subscriber<BubbleDouble?>() {
            override fun showExtraOp(code: String, message: String) {  //关心错误码；
                ToastUtils.showShort(message)
                view.dismissGoldCoinDialog()
            }

            override fun getData(bubbleDouble: BubbleDouble?) {
                var adId = ""
                if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
                    adId = MidasConstants.GET_DOUBLE_GOLD_COIN_SUCCESS
                }
                if (null != bubbleDouble) {
                    startGoldSuccess(adId, bubbleDouble.data.goldCount, view.getFunctionTitle(),
                            bubbleCollected.data.doubledMagnification)
                }
                view.dismissGoldCoinDialog()
            }

            override fun showExtraOp(message: String) {
                ToastUtils.showShort(message)
                view.dismissGoldCoinDialog()
            }

            override fun netConnectError() {
                ToastUtils.showShort(R.string.notwork_error)
                view.dismissGoldCoinDialog()
            }
        }, RxUtil.rxSchedulerHelper<ImageAdEntity>(view), bubbleCollected.data.uuid, bubbleCollected.data.locationNum,
                bubbleCollected.data.goldCount, bubbleCollected.data.doubledMagnification)
    }


    fun startGoldSuccess(adId: String, num: Int, functionName: String, doubledMagnification: Int) {
        val model = GoldCoinDoubleModel(adId, num, Points.FunctionGoldCoin.SUCCESS_PAGE, functionName, doubledMagnification)
        start(view.getActivity(), model)
    }

    override fun onPostResume() {
    }

    override fun onPause() {

    }

    private fun getStatisticsMap(): Map<String, Any>? {
        val map: MutableMap<String, Any> = HashMap()
        map["position_id"] = 5
        map["function_name"] = view.getFunctionTitle()
        return map
    }

    override fun detachView() {

    }

    override fun loadVideoAdv(bubbleCollected: BubbleCollected) {
        MidasRequesCenter.requestAndShowAd(view.getActivity(),MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON,object : VideoAbsAdCallBack(){
            override fun onAdLoadError(errorCode: String?, errorMsg: String?) {
                super.onAdLoadError(errorCode, errorMsg)
                ToastUtils.showLong("网络异常")
                view.dismissGoldCoinDialog()
            }
            override fun onAdClose(adInfo: AdInfoModel?, isComplete: Boolean) {
                super.onAdClose(adInfo, isComplete)
                StatisticsUtils.trackClick("incentive_video_ad_click", "功能完成页金币翻倍激励视频广告关闭点击", "", "success_page_gold_coin_pop_up_window_incentive_video_page", getStatisticsJson())
                if (isComplete) {
                    //播放完成的话去翻倍
                    addDoubleGoldCoin(bubbleCollected)
                } else {
                    //没有播放完成就关闭广告的话把弹窗关掉
                    view.dismissGoldCoinDialog()
                }
            }
            override fun onAdVideoComplete(adInfoModel: AdInfoModel?) {
                super.onAdVideoComplete(adInfoModel)
            }
        })
    }

    private fun getStatisticsJson(): JSONObject? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("position_id", 5)
            jsonObject.put("function_name", view.titleName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}
