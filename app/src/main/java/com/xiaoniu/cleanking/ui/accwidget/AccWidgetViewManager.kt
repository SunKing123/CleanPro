package com.xiaoniu.cleanking.ui.accwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.widget.RemoteViews
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.tool.notify.event.AccAnimationCompleteEvent
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


/**
 * Created by xinxiaolong on 2020/8/21.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetViewManager {
    companion object {
        /**
         * 更新桌面组件
         */
        fun updateAccCleanedProgress(context: Context) {
            var value = NumberUtils.mathRandomInt(20, 45)
            updateAccProgress(context, value)
        }

        fun updateAccNormalProgress(context: Context) {
            var value = NumberUtils.mathRandomInt(50, 85)
            updateAccProgress(context, value)
        }
        /**
         * 当添加新的桌面组件时，同步更新值
         */
        fun updateAddWidgetProgress(context: Context){
            val value=PreferenceUtil.getShortcutAccMemoryNum()
            updateAccProgress(context, value)
        }

        private fun updateAccProgress(context: Context, value: Int) {
            try {
                val remoteView = RemoteViews(context.packageName, R.layout.widget_view_acc_layout)
                val manager = AppWidgetManager.getInstance(context) //获得appwidget管理实例，用于管理appwidget以便进行更新操作
                remoteView.setProgressBar(R.id.widget_progress, 100, value, false)
                remoteView.setTextViewText(R.id.widget_label, value.toString() + "%的内存已使用")
                val componentName = ComponentName(context, AccWidgetProvider::class.java) //获得所有本程序创建的appwidget
                manager.updateAppWidget(componentName, remoteView)
                PreferenceUtil.saveShortcutAccMemoryNum(value)
            } catch (e: Exception) {

            }
        }
    }


}
