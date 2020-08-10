package com.xiaoniu.cleanking.ui.finish.model

import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by xinxiaolong on 2020/8/6.
 * email：xinxiaolong123@foxmail.com
 *
 * 文档: https://docs.qq.com/sheet/DZUx2WXlCcXpYSXFu?newPad=1&newPadType=clone&tab=2f6nzz
 *
 * 完成页面埋点统一封装
 *
 */
class CleanFinishPointer {

    var title: String
    lateinit var point: Points.CleanFinish.Point

    constructor(title: String) {
        this.title = title
        initPoint(title)
    }

    private fun initPoint(title: String) {
        when (title) {
            "建议清理", "立即清理", "一键清理" -> {
                point = Points.CleanFinish.Clean()
            }
            "通知栏清理" -> {
                point = Points.CleanFinish.Notify()
            }
            "微信专清" -> {
                point = Points.CleanFinish.WxClean()
            }
            "超强省电" -> {
                point = Points.CleanFinish.Power()
            }
            "一键加速" -> {
                point = Points.CleanFinish.Acc()
            }
            "网络加速" -> {
                point = Points.CleanFinish.NetWork()
            }
            "病毒查杀" -> {
                point = Points.CleanFinish.Virus()
            }
            "手机降温" -> {
                point = Points.CleanFinish.Cool()
            }
            else -> {
                point = Points.CleanFinish.Acc()
            }
        }
    }

    /**
     * 曝光埋点
     */
    fun exposurePoint() {
        StatisticsUtils.customTrackEvent(point.pageEventCode, point.pageEventName, "", point.page)
    }

    /**
     * 点击推荐埋点
     */
    fun recommendClickPoint(functionName: String) {
        StatisticsUtils.trackClick(Points.CleanFinish.RECOMMEND_CLICK_CODE, Points.CleanFinish.RECOMMEND_CLICK_NAME, "", point.page, getPositionTitleJson(functionName))
    }

    /**
     * 点击左上角返回埋点
     */
    fun returnPoint() {
        StatisticsUtils.trackClick(Points.RETURN_CLICK_EVENT_CODE, point.returnClickName, "", point.page)
    }

    /**
     * 系统返回埋点
     */
    fun systemReturnPoint() {
        StatisticsUtils.trackClick(Points.SYSTEM_RETURN_CLICK_EVENT_CODE, point.returnClickName, "", point.page)
    }

    /**
     * 金币弹框曝光
     */
    fun goldCoinDialogExposure() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.GoldCoin.PAGE_EVENT_CODE, Points.CleanFinish.GoldCoin.PAGE_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE)
    }

    /**
     * 金币点击翻倍
     */
    fun goldCoinDoubleClick() {
        StatisticsUtils.trackClick(Points.CleanFinish.GoldCoin.DOUBLE_CLICK_EVENT_CODE, Points.CleanFinish.GoldCoin.DOUBLE_CLICK_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE, getStatisticsJson())
    }

    /**
     * 金币弹框第一个广告位
     */
    fun goldCoinRequestAdv1() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.GoldCoin.REQUEST_ADV1_EVENT_CODE, Points.CleanFinish.GoldCoin.REQUEST_ADV1_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE, getStatisticsMap())
    }

    /**
     * 金币弹框激励视频广告
     */
    fun goldCoinRequestAdv2() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.GoldCoin.REQUEST_ADV2_EVENT_CODE, Points.CleanFinish.GoldCoin.REQUEST_ADV2_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE, getStatisticsMap())
    }

    /**
     * 信息流广告位1
     */
    fun requestFeedAdv1() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.FeedAdv.REQUEST_ADV1_EVENT_CODE, Points.CleanFinish.FeedAdv.REQUEST_ADV1_EVENT_NAME, "", Points.CleanFinish.FeedAdv.PAGE)
    }

    /**
     * 信息流广告位2
     */
    fun requestFeedAdv2() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.FeedAdv.REQUEST_ADV2_EVENT_CODE, Points.CleanFinish.FeedAdv.REQUEST_ADV2_EVENT_NAME, "", Points.CleanFinish.FeedAdv.PAGE)
    }

    /**
     * 插屏广告
     */
    fun insertAdvRequest4() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.Insert.REQUEST_ADV4_EVENT_CODE, Points.CleanFinish.Insert.REQUEST_ADV4_EVENT_NAME, "", Points.CleanFinish.Insert.PAGE)
    }

    /**
     * 首页插屏广告
     */
    fun insertAdvRequest5() {
        StatisticsUtils.customTrackEvent(Points.CleanFinish.Insert.REQUEST_ADV5_EVENT_CODE, Points.CleanFinish.Insert.REQUEST_ADV5_EVENT_NAME, "", Points.CleanFinish.Insert.PAGE)
    }

    /**
     * 金币弹框关闭
     */
    fun goldCoinClose() {
        StatisticsUtils.trackClick(Points.CleanFinish.GoldCoin.CLOSE_CLICK_EVENT_CODE, Points.CleanFinish.GoldCoin.CLOSE_CLICK_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE,  getStatisticsJson())
    }

    /**
     * 激励视频广告关闭
     */
    fun videoAdvClose() {
        StatisticsUtils.trackClick(Points.CleanFinish.VIDEO_CLOSE_EVENT_CODE, Points.CleanFinish.VIDEO_CLOSE_EVENT_NAME, "", Points.CleanFinish.VIDEO_PAGE, getStatisticsJson())
    }

    /**
     * 金币发放数量
     */
    fun goldNum(goldNum: String) {
        val map: MutableMap<String, Any> = getStatisticsMap() as MutableMap<String, Any>
        map["gold_number"] = goldNum
        StatisticsUtils.customTrackEvent(Points.CleanFinish.GoldCoin.NUMBER_OF_GOLD_COINS_EVENT_CODE, Points.CleanFinish.GoldCoin.NUMBER_OF_GOLD_COINS_EVENT_NAME, "", Points.CleanFinish.GoldCoin.PAGE, map)
    }

    private fun getStatisticsMap(): Map<String, Any>? {
        val map: MutableMap<String, Any> = java.util.HashMap()
        map["function_name"] = title
        return map
    }

    private fun getStatisticsJson(): JSONObject? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("function_name", title)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun getPositionTitleJson(functionName:String): JSONObject? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("position_title", functionName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}
