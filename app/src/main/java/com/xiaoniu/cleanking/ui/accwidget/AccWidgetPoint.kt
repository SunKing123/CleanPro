package com.xiaoniu.cleanking.ui.accwidget

import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils

/**
 * Created by xinxiaolong on 2020/8/22.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetPoint {

    companion object{

        fun shortcutAddWindConfirm(){
            StatisticsUtils.trackClick(Points.AccWidget.ShortcutCreate.ADD_CLICK_EVENT_CODE, Points.AccWidget.ShortcutCreate.ADD_CLICK_EVENT_NAME, "", Points.AccWidget.ShortcutCreate.PAGE)
        }

        fun shortcutAddWindCancel(){
            StatisticsUtils.trackClick(Points.AccWidget.ShortcutCreate.CANCEL_CLICK_EVENT_CODE, Points.AccWidget.ShortcutCreate.CANCEL_CLICK_EVENT_NAME, "", Points.AccWidget.ShortcutCreate.PAGE)
        }

        fun shortcutIconCreate(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.Shortcut.CREATED_EVENT_CODE, Points.AccWidget.Shortcut.CREATED_EVENT_NAME, "", Points.AccWidget.Shortcut.PAGE)
        }

        fun shortcutIconClick(){
            StatisticsUtils.trackClick(Points.AccWidget.Shortcut.CLICK_EVENT_CODE, Points.AccWidget.Shortcut.CLICK_EVENT_NAME, "", Points.AccWidget.Shortcut.PAGE)
        }

        fun finishPageCreate(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.AccFinish.PAGE_EVENT_CODE, Points.AccWidget.AccFinish.PAGE_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }

        fun finishAdvRequest(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.AccFinish.ADV_REQUEST_EVENT_CODE, Points.AccWidget.AccFinish.ADV_REQUEST_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }

        fun finishClickClean(){
            StatisticsUtils.trackClick(Points.AccWidget.AccFinish.CLEAN_CLICK_EVENT_CODE, Points.AccWidget.AccFinish.CLEAN_CLICK_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }
    }
}
