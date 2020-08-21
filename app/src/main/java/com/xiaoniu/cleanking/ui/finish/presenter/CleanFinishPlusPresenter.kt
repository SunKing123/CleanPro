package com.xiaoniu.cleanking.ui.finish.presenter

import android.view.View
import android.widget.FrameLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity
import com.xiaoniu.cleanking.ui.finish.base.CleanFinishLogger
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.CleanFinishPointer
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemDataStore
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.main.bean.*
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel
import com.xiaoniu.cleanking.ui.main.model.MainModel
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity.Companion.start
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil
import com.xiaoniu.cleanking.ui.tool.notify.event.LimitAwardRefEvent
import com.xiaoniu.cleanking.utils.DaliyTaskInstance
import com.xiaoniu.cleanking.utils.InsideScreenDialogUtil
import com.xiaoniu.cleanking.utils.net.Common3Subscriber
import com.xiaoniu.cleanking.utils.net.RxUtil
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.ToastUtils
import com.xiaoniu.unitionadbase.model.AdInfoModel
import org.greenrobot.eventbus.EventBus
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
    private lateinit var pointer: CleanFinishPointer

    @Inject
    public constructor() {

    }

    override fun onCreate() {
        loadAdSwitch()
    }

    override fun attachView(view: NewCleanFinishPlusActivity) {
        this.view = view
        this.itemDataStore = RecmedItemDataStore.getInstance()
        pointer = CleanFinishPointer(view.getFunctionTitle())
    }

    /**
     * 广告位开关
     */
    private fun loadAdSwitch() {
        isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE)
        isOpenTwo = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_TWO_CODE)

        CleanFinishLogger.log("============ 信息广告开关：======================")
        CleanFinishLogger.log("isOpenOne=" + isOpenOne)
        CleanFinishLogger.log("isOpenTwo=" + isOpenTwo)
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

        /**
         * 当第二个推荐功能为空时,过审开关没有打开
         * 展示刮刮卡引导图
         */
        if (secondModel == null && !AppHolder.getInstance().auditSwitch) {
            view.visibleScratchCardView()
        } else {
            view.goneScratchCardView()
        }
    }

    //显示内部插屏广告
    override fun loadInsideScreenDialog() {
        if (isDestroy()) {
            return
        }
        pointer.insertAdvRequest4()
        val utils = InsideScreenDialogUtil()
        utils.showInsideDialog(view, AppHolder.getInstance().getInsertAdMidasId(PositionId.KEY_FINISH_INSIDE_SCREEN))

    }

    override fun loadGoldCoinDialog() {
        if (isDestroy()) {
            return
        }
        getGoldCoin()
    }

    /**
     * 加载第一个广告位数据
     */
    override fun loadOneAdv(advContainer: FrameLayout) {
        if (!isOpenOne || isDestroy()) {
            advContainer.visibility = View.GONE
            return
        }
        pointer.requestFeedAdv1()
        MidasRequesCenter.requestAndShowAd(view.getActivity(), AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE), object : SimpleViewCallBack(advContainer) {

        })
    }

    /**
     * 加载第二个广告位数据
     */
    override fun loadTwoAdv(advContainer: FrameLayout) {
        if (!isOpenTwo || isDestroy()) {
            advContainer.visibility = View.GONE
            return
        }
        pointer.requestFeedAdv2()
        MidasRequesCenter.requestAndShowAd(view.getActivity(), AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_TWO_CODE), object : SimpleViewCallBack(advContainer) {

        })
    }

    /**
     * 获取可以加金币的数量
     */
    fun getGoldCoin() {
        if (isDestroy()) {
            return
        }
        CleanFinishLogger.log("============金币发放数正在加载...：======================")
        if(DaliyTaskInstance.getInstance().isExistTask){
            addGoldCoin(DaliyTaskInstance.getInstance().taskList.get(0).goldNum)//直接领取金币任务
        }else{
            mModel?.getGoleGonfigs(object : Common3Subscriber<BubbleConfig?>() {
                override fun showExtraOp(code: String, message: String) {  //关心错误码；
                    ToastUtils.showShort(message)
                }

                override fun getData(bubbleConfig: BubbleConfig?) {
                    if (bubbleConfig != null && bubbleConfig.data.size > 0) {
                        for (item in bubbleConfig.data) {
                            if (item.locationNum == 5) {
                                CleanFinishLogger.log("============金币发放数正在加载完成:" + item.goldCount)
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

    }

    /**
     * 根据添加数量，添加金币
     */
    private fun addGoldCoin(goldNum: Int) {
        if (goldNum == 0 || isDestroy()) {
            return
        }
        CleanFinishLogger.log("============调用添加金币数量接口...：======================")
        var commmsubscriber =object : Common3Subscriber<BubbleCollected?>() {
            override fun showExtraOp(code: String, message: String) {  //关心错误码；
                // ToastUtils.showShort(message);
            }

            override fun getData(bubbleConfig: BubbleCollected?) {
                //实时更新金币信息
                RequestUserInfoUtil.getUserCoinInfo()
                pointer.goldNum(goldNum.toString())
                if (bubbleConfig != null) {
                    //添加成功后，展示金币弹框
                    CleanFinishLogger.log("============调用添加金币数量接口成功，弹窗展示：======================")
                    //如果是任务领取，清除任务
                    if(DaliyTaskInstance.getInstance().isExistTask){
                        DaliyTaskInstance.getInstance().cleanTask()
                        view.showGoldCoinDialog(bubbleConfig,true)
                        EventBus.getDefault().post(LimitAwardRefEvent())
                    }else{
                        view.showGoldCoinDialog(bubbleConfig,false)
                    }
                }
            }
            override fun showExtraOp(message: String) {}
            override fun netConnectError() {
                ToastUtils.showShort(R.string.notwork_error)
            }
        }
        if(DaliyTaskInstance.getInstance().isExistTask){
            mModel?.daliyTasksCollect(commmsubscriber, RxUtil.rxSchedulerHelper<ImageAdEntity>(view), DaliyTaskInstance.getInstance().taskList.get(0).id)
        }else{
            mModel?.goleCollect(commmsubscriber, RxUtil.rxSchedulerHelper<ImageAdEntity>(view), 5)
        }

    }

    /**
     * 激励视频看完，进行金币翻倍
     */
    override fun addDoubleGoldCoin(bubbleCollected: BubbleCollected,isTask:Boolean) {
        if (isDestroy()) {
            return
        }
        CleanFinishLogger.log("============激励视频看完，进行翻倍接口请求======================")
        mModel?.goldDouble(object : Common3Subscriber<BubbleDouble?>() {
            override fun showExtraOp(code: String, message: String) {  //关心错误码；
                ToastUtils.showShort(message)
                view.dismissGoldCoinDialog()
            }

            override fun getData(bubbleDouble: BubbleDouble?) {
                var adId = ""
                if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
                    adId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS, PositionId.DRAW_THREE_CODE)
                }
                if (null != bubbleDouble) {
                    CleanFinishLogger.log("============激励视频看完，进行翻倍接口请求成功！======================")
                    startGoldSuccess(adId, bubbleDouble.data.goldCount, view.getFunctionTitle(),
                            bubbleCollected.data.doubledMagnification)
                } else {
                    CleanFinishLogger.log("============激励视频看完，进行翻倍接口请求失败！bubbleDouble==null======================")
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
                bubbleCollected.data.goldCount, bubbleCollected.data.doubledMagnification,isTask)
    }

    fun startGoldSuccess(adId: String, num: Int, functionName: String, doubledMagnification: Int) {
        val model = GoldCoinDoubleModel(adId, num, Points.FunctionGoldCoin.SUCCESS_PAGE, functionName, doubledMagnification)
        start(view.getActivity(), model)
    }

    override fun onPostResume() {

    }

    override fun onPause() {

    }

    override fun detachView() {

    }

    override fun loadVideoAdv(bubbleCollected: BubbleCollected,isTask:Boolean) {
        if (isDestroy()) {
            return
        }
        pointer.goldCoinRequestAdv2()
        var videoId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO, PositionId.DRAW_TWO_CODE)
        MidasRequesCenter.requestAndShowAd(view.getActivity(), videoId, object : VideoAbsAdCallBack() {
            override fun onAdLoadError(errorCode: String?, errorMsg: String?) {
                super.onAdLoadError(errorCode, errorMsg)
                ToastUtils.showLong("网络异常")
                view.dismissGoldCoinDialog()
            }

            override fun onAdClose(adInfo: AdInfoModel?, isComplete: Boolean) {
                super.onAdClose(adInfo, isComplete)
                pointer.videoAdvClose()
                if (isComplete) {
                    //播放完成的话去翻倍
                    addDoubleGoldCoin(bubbleCollected,isTask)
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

    fun isDestroy(): Boolean {
        return view == null || pointer == null || view.getActivity() == null
    }
}
