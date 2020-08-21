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


/**
 * Created by xinxiaolong on 2020/8/21.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetViewManager {

    var handler = Handler()
    val remoteView: RemoteViews
    val manager:AppWidgetManager
    val mContext:Context
    constructor(context: Context) {
        mContext=context
        remoteView = RemoteViews(context.packageName, R.layout.widget_view_acc_layout)
        manager = AppWidgetManager.getInstance(context) //获得appwidget管理实例，用于管理appwidget以便进行更新操作
        EventBus.getDefault().register(this)
    }

    fun updateCleanedAccProgress() {
        if(PreferenceUtil.getWidgetAccCleanTime()){
            var memory = NumberUtils.mathRandomInt(20, 45)
            remoteView.setProgressBar(R.id.widget_progress, 100, memory, false)
            remoteView.setTextViewText(R.id.widget_label,memory.toString()+"%的内存已使用")
            val componentName = ComponentName(mContext, AccWidgetProvider::class.java) //获得所有本程序创建的appwidget
            manager.updateAppWidget(componentName,remoteView)
        }
    }

    fun onDestroy(){
        EventBus.getDefault().unregister(this)
    }

    //加速动画完成时发消息
    @Subscribe
    fun fromHomeCleanFinishEvent(event: AccAnimationCompleteEvent?) {
        updateCleanedAccProgress()
    }

}
