package com.xiaoniu.cleanking.ui.accwidget

import android.content.Context
import android.os.Handler
import android.widget.RemoteViews
import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/8/21.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetViewManager {


    var handler = Handler()
    val remoteView: RemoteViews

    constructor(context: Context) {
        remoteView = RemoteViews(context.packageName, R.layout.widget_view_acc_layout)
    }

    fun startAccAnimation() {
      //  remoteView.setProgressBar(R.id.widget_progress, 100, 20)
    }



}
