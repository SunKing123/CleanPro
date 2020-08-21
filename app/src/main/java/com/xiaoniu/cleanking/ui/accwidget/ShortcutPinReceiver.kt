package com.xiaoniu.cleanking.ui.accwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xiaoniu.cleanking.utils.LogUtils

/**
 * Created by xinxiaolong on 2020/8/21.
 * email：xinxiaolong123@foxmail.com
 */
public class ShortcutPinReceiver :BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.e("========ShortcutPinReceiver==============")
    }
}
