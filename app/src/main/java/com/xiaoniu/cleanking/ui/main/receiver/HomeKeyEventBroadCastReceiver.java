package com.xiaoniu.cleanking.ui.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoniu.cleanking.ui.usercenter.service.FloatingImageDisplayService;

/**
 * Home键是一个系统的按钮，我们无法通过onKeyDown进行拦截，它是拦截不
 * 到的，我们只能得到他在什么时候被按下了。就是通过广播接收者,此广播也不需要任何权限
 */
public class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
    static final String SYSTEM_REASON = "reason";
    static final String SYSTEM_HOME_KEY = "homekey";
    static final String SYSTEM_RECENT_APPS = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (reason != null) {
                if (reason.equals(SYSTEM_HOME_KEY)) {
                    // home key处理点
                    context.stopService(new Intent(context, FloatingImageDisplayService.class));
                } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    // long home key处理点
                    context.stopService(new Intent(context, FloatingImageDisplayService.class));
                }
            }
        }
    }

}
