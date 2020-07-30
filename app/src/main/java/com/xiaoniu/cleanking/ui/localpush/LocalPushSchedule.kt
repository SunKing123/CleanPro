package com.xiaoniu.cleanking.ui.localpush

import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.rxjava.RxTimer
import com.xiaoniu.cleanking.utils.update.MmkvUtil
import org.greenrobot.eventbus.EventBus

class LocalPushSchedule {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LocalPushSchedule()
        }
    }


    private val rxTimer by lazy {
        RxTimer()
    }

    /**
     * myAppPress 是否是清理APP处于前台时按下的Home键
     */
    fun popPush(myAppPress: Boolean) {

        if (!myAppPress) {
            //当应用在后台时捕获到home键后10秒弹出插屏广告
            rxTimer.timer(10 * 1000) {
                //卓面弹窗广告
                EventBus.getDefault().post(PopEventModel("desktopPop"))
            }
            //判断是否满足本地弹窗的条件,若满足延迟3秒弹出本地弹窗
            val atLastFromClearAppToLauncher = MmkvUtil.getLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, 0L)
            val current = System.currentTimeMillis()
            val period = current / 1000 - atLastFromClearAppToLauncher / 1000
            if (period < 10 * 60) {
                LogUtils.e("====距离上次清理APP触发Home键过了" + period + "秒小于限制时间，直接返回")
                return
            }
            rxTimer.timer(3000) {
                EventBus.getDefault().post(PopEventModel("localPush"))
            }

        } else {
            LogUtils.e("========保存由清理按home键的时间")
            //保存最后一次清理在前台时按home键回桌面的时间
            MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, System.currentTimeMillis())
        }


    }


}