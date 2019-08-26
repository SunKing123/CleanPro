package com.xiaoniu.cleanking.ui.tool.notify.utils;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;

import com.xiaoniu.common.utils.ContextUtils;

public class NotifyUtils {
    /**
     * 说明：是否已获得通知栏访问权限
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isNotificationListenerEnabled() {
        try {
            return NotificationManagerCompat.getEnabledListenerPackages(ContextUtils.getContext())
                    .contains(ContextUtils.getContext().getPackageName());
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 说明：在通知监听权限设置页面存在的前提下，打开该设置页面。
     * <br>作者：huyang
     * <br>添加时间：2019/5/17 13:47
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void openNotificationListenerSettings(Context context) {
        if (context == null) {
            return;
        }
        try {
            if (!isNotificationListenerEnabled()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
