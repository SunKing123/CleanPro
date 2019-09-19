package com.xiaoniu.cleanking.jpush;

import android.content.Context;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class JPushNotificationManager {
    public static void customPushNotification(Context context, int number, int layoutId, int icon, int title, int text, int iconTipId, int iconShowId) {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(context, layoutId, icon, title, text);
        builder.statusBarDrawable = iconTipId;
        builder.layoutIconDrawable = iconShowId;
        JPushInterface.setDefaultPushNotificationBuilder(builder);
//        JPushInterface.setPushNotificationBuilder(number, builder);
    }
}
