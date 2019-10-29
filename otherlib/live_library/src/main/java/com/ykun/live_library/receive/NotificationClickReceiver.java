package com.ykun.live_library.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ykun.live_library.config.KeepAliveConfig;
import com.ykun.live_library.utils.ToActivity;

public final class NotificationClickReceiver extends BroadcastReceiver {
    public final static String CLICK_NOTIFICATION = "CLICK_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (NotificationClickReceiver.CLICK_NOTIFICATION.equals(action)) {
            if (KeepAliveConfig.foregroundNotification != null) {
                if (KeepAliveConfig.foregroundNotification.getForegroundNotificationClickListener() != null) {
                    KeepAliveConfig.foregroundNotification.getForegroundNotificationClickListener().foregroundNotificationClick(context, intent);
                }
            }
            ToActivity.toActivity(context, "com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity");
        }
    }
}
