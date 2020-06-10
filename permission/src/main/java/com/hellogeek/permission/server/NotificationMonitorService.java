package com.hellogeek.permission.server;

import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationMonitorService extends NotificationListenerService  {

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
    }
    
    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}