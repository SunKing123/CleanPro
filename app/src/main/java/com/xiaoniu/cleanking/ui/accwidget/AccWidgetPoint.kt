package com.xiaoniu.cleanking.ui.accwidget

import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils

/**
 * Created by xinxiaolong on 2020/8/22.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetPoint {

    companion object{

        /**
         * 快捷方式添加弹窗点击确认
         */
        fun shortcutAddWindConfirm(){
            StatisticsUtils.trackClick(Points.AccWidget.ShortcutCreate.ADD_CLICK_EVENT_CODE, Points.AccWidget.ShortcutCreate.ADD_CLICK_EVENT_NAME, "", Points.AccWidget.ShortcutCreate.PAGE)
        }

        /**
         * 快捷方式添加弹窗点击取消
         */
        fun shortcutAddWindCancel(){
            StatisticsUtils.trackClick(Points.AccWidget.ShortcutCreate.CANCEL_CLICK_EVENT_CODE, Points.AccWidget.ShortcutCreate.CANCEL_CLICK_EVENT_NAME, "", Points.AccWidget.ShortcutCreate.PAGE)
        }

        /**
         * 快捷图标创建
         */
        fun shortcutIconCreate(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.Shortcut.CREATED_EVENT_CODE, Points.AccWidget.Shortcut.CREATED_EVENT_NAME, "", Points.AccWidget.Shortcut.PAGE)
        }

        /**
         * 快捷图标点击
         */
        fun shortcutIconClick(){
            StatisticsUtils.trackClick(Points.AccWidget.Shortcut.CLICK_EVENT_CODE, Points.AccWidget.Shortcut.CLICK_EVENT_NAME, "", Points.AccWidget.Shortcut.PAGE)
        }

        /**
         * 快捷加速完成页曝光
         */
        fun finishPageCreate(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.AccFinish.PAGE_EVENT_CODE, Points.AccWidget.AccFinish.PAGE_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }

        /**
         * 快捷加速完成页广告请求
         */
        fun finishAdvRequest(){
            StatisticsUtils.customTrackEvent(Points.AccWidget.AccFinish.ADV_REQUEST_EVENT_CODE, Points.AccWidget.AccFinish.ADV_REQUEST_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }

        /**
         * 快捷加速完成页点击清理
         */
        fun finishClickClean(){
            StatisticsUtils.trackClick(Points.AccWidget.AccFinish.CLEAN_CLICK_EVENT_CODE, Points.AccWidget.AccFinish.CLEAN_CLICK_EVENT_NAME, "", Points.AccWidget.AccFinish.PAGE)
        }
    }
}
