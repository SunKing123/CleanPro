package com.xiaoniu.cleanking.ui.notifition;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;

public class NotificationService extends Service {
    private static final String ONCLICK = "com.app.onclick";
    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //显示通知栏
        Notification notification = new Notification(R.drawable.ic_launcher, "JcMan", System.currentTimeMillis());
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification_bar_layout);
        notification.contentView = view;
//        IntentFilter filter_click = new IntentFilter();
//        filter_click.addAction(ONCLICK);
        //注册广播
//        registerReceiver(receiver_onclick, filter_click);
        Intent Intent_pre = new Intent(ONCLICK);
        //得到PendingIntent
        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);
        //设置监听
        notification.contentView.setOnClickPendingIntent(R.id.btn_download5, pendIntent_click);
        //前台运行
        startForeground(1, notification);
    }
}
