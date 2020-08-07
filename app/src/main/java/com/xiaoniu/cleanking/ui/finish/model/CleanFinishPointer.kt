package com.xiaoniu.cleanking.ui.finish.model

import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils

/**
 * Created by xinxiaolong on 2020/8/6.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanFinishPointer {
    
    var title: String
    lateinit var point: Points.CleanFinish.Point

    constructor(title: String) {
        this.title = title
        initPoint(title)
    }

    private fun initPoint(title: String) {
        when (title) {
            "建议清理","立即清理","一键清理" -> {
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
            "游戏加速" -> {

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
        var extParam = HashMap<String, Any>()
        extParam.put("position_title", functionName)
        StatisticsUtils.customTrackEvent(Points.CleanFinish.RECOMMEND_CLICK_CODE, Points.CleanFinish.RECOMMEND_CLICK_NAME, "", point.page, extParam)
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
}
