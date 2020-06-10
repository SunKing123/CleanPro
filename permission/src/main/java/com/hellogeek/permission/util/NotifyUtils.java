package com.hellogeek.permission.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import com.hellogeek.permission.Integrate.Permission;

public class NotifyUtils {   
    /**
     * 说明：是否已获得通知栏访问权限
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isNotificationListenerEnabled(Context context) {
        try {
            return NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(context.getPackageName());
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
    public static boolean openNotificationListenerSettings(Context context) {
        if (context == null) {
            return false;
        }
        try {
            if (!isNotificationListenerEnabled(context)) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                ((Activity) context).startActivityForResult(intent, Permission.NOTIFICATIONREAD.getRequestCode());
                return true;
            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                ((Activity) context).startActivityForResult(intent, Permission.NOTIFICATIONREAD.getRequestCode());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return false;
    }
}
