package com.xiaoniu.cleanking.ui.accwidget

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by xinxiaolong on 2020/8/21.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetUpdateDataService :Service(){

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
