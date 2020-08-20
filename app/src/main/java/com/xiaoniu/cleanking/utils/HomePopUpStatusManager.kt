package com.xiaoniu.cleanking.utils

/**
 * Created by xinxiaolong on 2020/8/20.
 * email：xinxiaolong123@foxmail.com
 * 弹框状态工作栈
 */
public class HomePopUpStatusManager {

    val upgradePopIndex = 0
    val homeGuideIndex = 1
    val innerInsertIndex = 2
    val shortcutIndex = 3
    val redPacketIndex = 4

    var arrStatus = arrayOf(INIT, INIT, INIT, INIT, INIT)

    companion object {
        //初始状态
        val INIT = 0

        //正在消费
        val CONSUME_ING = 1

        //已消费
        val CONSUMEED = 2

        @Volatile
        private var instance: HomePopUpStatusManager? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance
                            ?: HomePopUpStatusManager().also { instance = it }
                }
    }

    private constructor() {
        logAllInfo("constructor")
    }

    /***************************************************************************************************************************************
     *********************************************************首页升级弹框********************************************************************
     ***************************************************************************************************************************************
     */
    fun setUpgradePopConsuming() {
        arrStatus[upgradePopIndex] = CONSUME_ING
        logAllInfo("setUpgradePopConsuming")
    }

    fun setUpgradePopConsumed() {
        arrStatus[upgradePopIndex] = CONSUMEED
        logAllInfo("setUpgradePopConsumed")
    }

    fun isUpgradePopCanPop(): Boolean {
        return checkPreTaskAllConsume(homeGuideIndex)
    }


    /***************************************************************************************************************************************
     *********************************************************首页引导视图********************************************************************
     ***************************************************************************************************************************************
     */
    fun setHomeGuideConsuming() {
        arrStatus[homeGuideIndex] = CONSUME_ING
        logAllInfo("setHomeGuideConsuming")
    }

    fun setHomeGuideConsumed() {
        arrStatus[homeGuideIndex] = CONSUMEED
        logAllInfo("setHomeGuideConsumed")
    }

    fun isHomeGuideCanPop(): Boolean {
        return checkPreTaskAllConsume(homeGuideIndex)
    }

    /***************************************************************************************************************************************
     *********************************************************首页内部插屏********************************************************************
     ***************************************************************************************************************************************
     */
    fun setInnerInsertConsuming() {
        arrStatus[innerInsertIndex] = CONSUME_ING
        logAllInfo("setInnerInsertConsuming")
    }

    fun setInnerInsertConsumed() {
        arrStatus[innerInsertIndex] = CONSUMEED
        logAllInfo("setInnerInsertConsumed")
    }


    fun isInnerInsertCanPop(): Boolean {
        return checkPreTaskAllConsume(innerInsertIndex)
    }

    /***************************************************************************************************************************************
     *********************************************************首页快捷方式********************************************************************
     ***************************************************************************************************************************************
     */
    fun setShortcutConsuming() {
        arrStatus[shortcutIndex] = CONSUME_ING
        logAllInfo("setShortcutConsuming")
    }

    fun setShortcutConsumed() {
        arrStatus[shortcutIndex] = CONSUMEED
        logAllInfo("setShortcutConsumed")
    }

    fun isShortcutCanPop(): Boolean {
        return checkPreTaskAllConsume(shortcutIndex)
    }

    /***************************************************************************************************************************************
     *********************************************************首页红包弹窗********************************************************************
     ***************************************************************************************************************************************
     */
    fun setRedPacketConsuming() {
        arrStatus[redPacketIndex] = CONSUME_ING
        logAllInfo("setRedPacketConsuming")
    }

    fun setRedPacketConsumed() {
        arrStatus[redPacketIndex] = CONSUMEED
        logAllInfo("setRedPacketConsumed")
    }

    fun isRedPacketCanPop(): Boolean {
        return checkPreTaskAllConsume(redPacketIndex)
    }


    /***************************************************************************************************************************************
     *********************************************************checker***********************************************************************
     ***************************************************************************************************************************************
     */

    /**
     * 这里需要 从优先级由低到高进行check。
     *
     * 比如 内部插屏＞添加加速图标＞运营弹窗（红包弹窗、挽留弹窗）
     * 当【添加加速图标】已弹过，其状态为 CONSUMEED。在有些实际出发了【内部插屏】的弹出 其状态处于 CONSUME_ING
     * 这个时候不能弹出【运营弹窗】 了。所以需要遍历整个队列。
     *
     */
    private fun checkPreTaskAllConsume(endIndex: Int): Boolean {
        var index = 0
        for (status in arrStatus) {
            if (index >= endIndex) {
                return true
            }
            if (!isConsumed(index)) {
                return false
            }
            index++
        }
        return false
    }

    private fun getStatus(index: Int): Int {
        if (index < 0 || index >= arrStatus.size) {
            return INIT
        }
        return arrStatus[index]
    }

    private fun isConsumed(index: Int): Boolean {
        return getStatus(index) == CONSUMEED
    }


    /***************************************************************************************************************************************
     *********************************************************log**************************************************************************
     ***************************************************************************************************************************************
     */
    private fun logAllInfo(method: String) {
        var index = 0
        for (status in arrStatus) {
            log("in the" + method + "         " + getPopName(index) + " 当前状态" + getStatusName(status))
            index++
        }
    }

    private fun log(text: String) {
        LogUtils.e("===============PopUpWorkingStatusStack:  " + text)
    }

    private fun getPopName(index: Int): String {
        var popName = ""
        when (index) {
            upgradePopIndex -> popName = "升级弹框"
            homeGuideIndex -> popName = "引导视频"
            innerInsertIndex -> popName = "内部插屏"
            shortcutIndex -> popName = "快捷方式"
            redPacketIndex -> popName = "红包运营"
        }
        return popName
    }

    private fun getStatusName(status: Int): String {
        var statusName = ""
        when (status) {
            INIT -> statusName = "初始状态"
            CONSUME_ING -> statusName = "正在消费"
            CONSUMEED -> statusName = "消费完成"
        }
        return statusName
    }

}
