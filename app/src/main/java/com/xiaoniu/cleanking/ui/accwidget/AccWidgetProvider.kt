package com.xiaoniu.cleanking.ui.accwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.RemoteViews
import com.xiaoniu.cleanking.R
import java.util.*


/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
public class AccWidgetProvider : AppWidgetProvider() {

    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
    private val idsSet = HashSet<Int>()
    private var viewManager:AccWidgetViewManager? = null
    private val ACTION_VIEW_CLICK = "com.xiaoniu.widget.action.view.click"

    /**
     * 当Widget被添加或者被更新时会调用该方法
     *
     */
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        WidgetLog.log("===============onUpdate() start====================")

        if (appWidgetIds != null) {
            for (id in appWidgetIds) {
                idsSet.add(id)
            }
        }
        if (context != null) {
            if(viewManager==null)
            viewManager=AccWidgetViewManager(context)
            updateAllAppWidgets(context!!, AppWidgetManager.getInstance(context), idsSet)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        WidgetLog.log("===============onUpdate() end====================")
    }

    /**
     * 当接收到广播的时候会被调用。
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        WidgetLog.log("onReceive()  " + intent?.action)
        if (intent != null && intent?.action != null) {
            var action = intent.action
            if (action != null&&context!=null) {
                viewEventHandle(context,action)
            }
        }
        super.onReceive(context, intent)
    }

    /**
     * 处理view的点击事件
     */
    private fun viewEventHandle(context: Context,action: String) {
        when (action) {
            ACTION_VIEW_CLICK + ":" + R.id.widget_image.toString() -> {
                WidgetLog.log("===============click widget_image====================")
            }
            ACTION_VIEW_CLICK + ":" + R.id.widget_acc_container.toString() -> {
                WidgetLog.log("===============click widget_acc_container====================")
                var intent=Intent()
                intent.flags=FLAG_ACTIVITY_NEW_TASK
                intent.setClass(context, AccWidgetAnimationActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        WidgetLog.log("onRestored()")

    }

    /**
     * 这个方法会在用户首次添加Widget时调用。
     */
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        WidgetLog.log("onEnabled()")
    }

    /**
     *
     * 当最后一个Widget实例被移除的时候调用这个方法。在这个方法中我们可以做一些清除工作.
     */
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        if(viewManager!=null){
            viewManager!!.onDestroy()
        }
        WidgetLog.log("onDisabled()")
    }

    /**
     * 当控件被删除的时候调用该方法
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        WidgetLog.log("onDeleted()")

    }

    /**
     * 这个方法会在添加Widget或者改变Widget的大小时候被调用。在这个方法中我们还可以根据Widget的大小来选择性的显示或隐藏某些控件。
     */
    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        WidgetLog.log("onAppWidgetOptionsChanged()")
    }


    // 更新所有的 widget
    private fun updateAllAppWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        set: Set<*>
    ) {
        WidgetLog.log("updateAllAppWidgets")
        // widget 的id
        var appID: Int
        // 迭代器，用于遍历所有保存的widget的id
        val it = set.iterator()
        while (it.hasNext()) {
            appID = (it.next() as Int).toInt()
            // 获取 example_appwidget.xml 对应的RemoteViews
            val remoteView = RemoteViews(context.packageName, R.layout.widget_view_acc_layout)
            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(
                    R.id.widget_acc_container,
                getPendingIntent(context, R.id.widget_acc_container)
            )
            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView)
        }
    }

    private fun getPendingIntent(context: Context, buttonId: Int): PendingIntent? {
        val intent = Intent()
        intent.setClass(context, AccWidgetProvider::class.java)
        //这里值得注意的是，当需要监听不同view的点击事件时，action不可以一样，一致的话会被覆盖。
        //所以需要对action做差异id
        intent.action = ACTION_VIEW_CLICK + ":" + buttonId.toString()
        WidgetLog.log("===============getPendingIntent    " + buttonId.toString())
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }


}
