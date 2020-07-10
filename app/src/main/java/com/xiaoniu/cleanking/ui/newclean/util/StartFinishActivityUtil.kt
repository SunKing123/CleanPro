package com.xiaoniu.cleanking.ui.newclean.util

import android.content.Context
import android.content.Intent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity

/**
 * @author XiLei
 * @date 2019/11/22.
 * description：完成页之前的全屏视频广告
 */
class StartFinishActivityUtil {

    companion object {
        var isOneOpen = false
        var isTwoOpen = false
        var isThreeOpen = false
        var hasInit = false;

        fun gotoFinish(context: Context, intent: Intent) {
            initOnOff();
            if (isThreeOpen) {
                intent.setClass(context, CleanFinishAdvertisementActivity::class.java);
                context.startActivity(intent)
            } else {
                intent.setClass(context, NewCleanFinishActivity::class.java);
                context.startActivity(intent)
            }
        }

        fun initOnOff() {
            if (hasInit) {
                return
            }
            hasInit = true
            isOneOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE)
            isTwoOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_TWO_CODE)
            isThreeOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_THREE_CODE)
        }
    }
}
