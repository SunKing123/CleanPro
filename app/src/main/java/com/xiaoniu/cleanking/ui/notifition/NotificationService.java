package com.xiaoniu.cleanking.ui.notifition;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.CleanBigFileActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneCoolingActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;

import static android.support.v4.app.NotificationCompat.*;

public class NotificationService extends Service {

    private final int NOTIFICATION_ID = 0xa01;
    private final int REQUEST_CODE = 0xb01;
    private Context context;
    private NotificationManager notificationManager;
    private RemoteViews contentView;
    private Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Builder mBuilder = new Builder(context);
        // 此处设置的图标仅用于显示新提醒时候出现在设备的通知栏
        mBuilder.setSmallIcon(R.mipmap.applogo);
        notification = mBuilder.build();

        // 当用户下来通知栏时候看到的就是RemoteViews中自定义的Notification布局
        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout);
        //如果版本号低于（3.0），那么不显示按钮
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

        } else {
            contentView.setOnClickPendingIntent(R.id.ll_clean, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, CleanBigFileActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.ll_speed, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneAccessActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.ll_temperature, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneCoolingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.ll_power, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneSuperPowerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.ll_notification, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, NotifyCleanGuideActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        notification.contentView = contentView;
        //设置常驻通知栏
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 发送到手机的通知栏
        notificationManager.notify(NOTIFICATION_ID, notification);
    }



}
