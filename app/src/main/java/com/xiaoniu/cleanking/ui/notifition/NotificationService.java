package com.xiaoniu.cleanking.ui.notifition;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneCoolingActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 常住通知栏服务
 */
public class NotificationService extends Service {

    private final int NOTIFICATION_ID = 0xa01;
    private final int REQUEST_CODE = 0xb01;
    private Context context;
    private NotificationManager notificationManager;
    private RemoteViews contentView;
    private Notification notification;
    private Notification.Builder mBuilder = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NiuDataAPI.onPageStart("toggle_page_view_page", "常驻通知栏成功创建");
        EventBus.getDefault().register(this);
        context = this;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context);
        // 此处设置的图标仅用于显示新提醒时候出现在设备的通知栏
        mBuilder.setSmallIcon(R.mipmap.applogo);
        // 当用户下来通知栏时候看到的就是RemoteViews中自定义的Notification布局
        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout);

        //如果版本号低于（3.0），那么不显示按钮
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //TODO
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8.0 适配    需要配置 通知渠道NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel("1", "推送通知", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLightColor(context.getColor(R.color.color_D8D8D8));//指定闪光的灯光颜色
            notificationChannel.enableLights(true);//闪光
            notificationChannel.enableVibration(true);//震动
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//锁屏显示通知
            notificationManager.createNotificationChannel(notificationChannel);
            mBuilder.setChannelId("1");
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4版本兼容点击跳转

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout);
            //logo
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("NotificationService", "home");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.iv_app_icon, PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));

            //清理
            Intent intentClean = new Intent(context, NowCleanActivity.class);
            intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentClean.putExtra("NotificationService", "clean");
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.ll_clean, PendingIntent.getActivity(context, REQUEST_CODE, intentClean, PendingIntent.FLAG_UPDATE_CURRENT));

            //加速
            Intent phoneAccessIntent = new Intent(context, PhoneAccessActivity.class);
            intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            phoneAccessIntent.putExtra("NotificationService", "clean");
            phoneAccessIntent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.ll_speed, PendingIntent.getActivity(context, REQUEST_CODE, phoneAccessIntent, PendingIntent.FLAG_UPDATE_CURRENT));

            //降温
            Intent phoneCoolingIntent = new Intent(context, PhoneCoolingActivity.class);
            intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            phoneCoolingIntent.putExtra("NotificationService", "clean");
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.ll_temperature, PendingIntent.getActivity(context, REQUEST_CODE, phoneCoolingIntent, PendingIntent.FLAG_UPDATE_CURRENT));

            //省电
            Intent phoneSuperPowerIntent = new Intent(context, PhoneSuperPowerActivity.class);
            intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            phoneSuperPowerIntent.putExtra("NotificationService", "clean");
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.ll_power, PendingIntent.getActivity(context, REQUEST_CODE, phoneSuperPowerIntent, PendingIntent.FLAG_UPDATE_CURRENT));

            //通知栏清理
            Intent notifyCleanGuideIntent = new Intent(context, NotifyCleanGuideActivity.class);
            intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notifyCleanGuideIntent.putExtra("NotificationService", "clean");
            new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout).setOnClickPendingIntent(R.id.ll_notification, PendingIntent.getActivity(context, REQUEST_CODE, notifyCleanGuideIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }


        //logo
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("NotificationService", "home");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contentView.setOnClickPendingIntent(R.id.iv_app_icon, PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        //清理
        Intent intentClean = new Intent(context, NowCleanActivity.class);
        intentClean.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentClean.putExtra("NotificationService", "clean");
        contentView.setOnClickPendingIntent(R.id.ll_clean, PendingIntent.getActivity(context, REQUEST_CODE, intentClean, PendingIntent.FLAG_UPDATE_CURRENT));

        //加速
        Intent phoneAccessIntent = new Intent(context, PhoneAccessActivity.class);
        phoneAccessIntent.putExtra("NotificationService", "clean");
        phoneAccessIntent.putExtra(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
        contentView.setOnClickPendingIntent(R.id.ll_speed, PendingIntent.getActivity(context, REQUEST_CODE, phoneAccessIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //降温
        Intent phoneCoolingIntent = new Intent(context, PhoneCoolingActivity.class);
        phoneCoolingIntent.putExtra("NotificationService", "clean");
        contentView.setOnClickPendingIntent(R.id.ll_temperature, PendingIntent.getActivity(context, REQUEST_CODE, phoneCoolingIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //省电
        Intent phoneSuperPowerIntent = new Intent(context, PhoneSuperPowerActivity.class);
        phoneSuperPowerIntent.putExtra("NotificationService", "clean");
        contentView.setOnClickPendingIntent(R.id.ll_power, PendingIntent.getActivity(context, REQUEST_CODE, phoneSuperPowerIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //通知栏清理
        Intent notifyCleanGuideIntent = new Intent(context, NotifyCleanGuideActivity.class);
        notifyCleanGuideIntent.putExtra("NotificationService", "clean");
        contentView.setOnClickPendingIntent(R.id.ll_notification, PendingIntent.getActivity(context, REQUEST_CODE, notifyCleanGuideIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        mBuilder.setContent(contentView);
        mBuilder.setSmallIcon(R.mipmap.applogo);
        mBuilder.setOngoing(true);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.applogo));
        mBuilder.setAutoCancel(true);
        notification = mBuilder.build();
        // 发送到手机的通知栏
        notificationManager.notify(NOTIFICATION_ID, notification);
        NiuDataAPI.onPageEnd("toggle_page_view_page", "常驻通知栏成功创建");

        //8.0以上版本或者未获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || PermissionUtils.isUsageAccessAllowed(context)) {
            TypedArray speedlist = context.getResources().obtainTypedArray(R.array.acess_drawale_array);
            int index = NumberUtils.mathRandomInt(6,9);//中间随机数
            int id = speedlist.getResourceId(index,-1);
            contentView.setImageViewResource(R.id.iv_speed, id);
        }
    }

    /**
     * 更新图标的颜色 EventBus
     *
     * @param notificationEvent
     */
    @Subscribe
    public void cleanFinish(NotificationEvent notificationEvent) {
        Log.d("cleanFinish", "cleanFinish: " + notificationEvent);
        if ("clean".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_clean, R.drawable.icon_notifi_clean_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_clean, R.drawable.icon_notifi_clean_pre2);
            }
        } else if ("cooling".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_temperature, R.drawable.icon_notifi_temperature_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_temperature, R.drawable.icon_notifi_temperature_pre2);
            }
        } else if ("speed".equals(notificationEvent.getType())) {//一键加速
            int addValue = notificationEvent.getAppendValue();
            int imgState = (addValue / 10 + 1) >= 9 ? 9 : addValue / 10;
            TypedArray speedlist = context.getResources().obtainTypedArray(R.array.acess_drawale_array);
            int id = speedlist.getResourceId(imgState,-1);
            contentView.setImageViewResource(R.id.iv_speed, id);
        } else if ("power".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_power, R.drawable.icon_notifi_power_no);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_power, R.drawable.icon_notifi_power_pre2);
            }
        } else if ("notification".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_notification, R.drawable.icon_notifi_notification_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_notification, R.drawable.icon_notifi_notification_pre2);
            }
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
