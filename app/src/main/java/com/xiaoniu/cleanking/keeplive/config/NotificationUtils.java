package com.xiaoniu.cleanking.keeplive.config;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.xiaoniu.cleanking.R;

public class NotificationUtils extends ContextWrapper {
    private NotificationManager manager;
    private String id;
    private String name;
    private Context mContext;
    private NotificationChannel channel;

    private NotificationUtils(Context context) {
        super(context);
        this.mContext = context;
        id = mContext.getPackageName();
        name = mContext.getPackageName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        if (channel == null) {
            channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            getManager().createNotificationChannel(channel);
        }
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content, int icon, Intent intent,String btnString) {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (TextUtils.isEmpty(title)) {
            title = mContext.getString(R.string.app_name);
        }
        if (TextUtils.isEmpty(content)) {
            content = mContext.getString(R.string.app_name);
        }
        if (icon == 0) {
            icon = R.drawable.ic_launcher;
        }
        Notification.Builder builder = new Notification.Builder(mContext, id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        //添加自定义视图  activity_notification
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.live_notification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.text,Html.fromHtml(content));
        //守护push没有不显示查看
        if (!TextUtils.isEmpty(title)&& title.contains(mContext.getString(R.string.push_content_default_title))){
            mRemoteViews.setViewVisibility(R.id.tv_look_btn, View.GONE);
        }else if(!TextUtils.isEmpty(btnString)){
            mRemoteViews.setTextViewText(R.id.tv_look_btn,btnString);
            mRemoteViews.setViewVisibility(R.id.tv_look_btn, View.VISIBLE);
        }
        builder.setContent(mRemoteViews);
        return builder;
    }

    public NotificationCompat.Builder getNotification_25(String title, String content, int icon, Intent intent) {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(mContext, id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(new long[]{0})
                .setContentIntent(pendingIntent);
    }

    public static void sendNotification(@NonNull Context context, @NonNull String title, @NonNull String content, @NonNull int icon, @NonNull Intent intent,String btn) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            notificationUtils.createNotificationChannel();
            notification = notificationUtils.getChannelNotification(title, content, icon, intent,btn).build();
        } else {
            notification = notificationUtils.getNotification_25(title, content, icon, intent).build();
        }
        notificationUtils.getManager().notify(new java.util.Random().nextInt(10000), notification);
    }

    public static Notification createNotification(@NonNull Context context, @NonNull String title, @NonNull String content, @NonNull int icon, @NonNull Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            notificationUtils.createNotificationChannel();
            notification = notificationUtils.getChannelNotification(title, content, icon, intent,"").build();
        } else {
            notification = notificationUtils.getNotification_25(title, content, icon, intent).build();
        }
        return notification;
    }
}
