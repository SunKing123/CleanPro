package com.xiaoniu.cleanking.keeplive.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.receive.NotificationClickReceiver;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;

import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;
/**
 * 隐藏前台服务通知
 */
public class HideForegroundService extends Service {
    private Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
                stopSelf();
            }
        }, 2000);
        return START_NOT_STICKY;
    }


    private void startForeground() {
        if (KeepAliveConfig.foregroundNotification != null) {
            Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
            intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Notification notification = NotificationUtils.createNotification(this,
                    SPUtils.getInstance(getApplicationContext(),SP_NAME).getString(KeepAliveConfig.TITLE),
                    SPUtils.getInstance(getApplicationContext(),SP_NAME).getString(KeepAliveConfig.CONTENT),
                    SPUtils.getInstance(getApplicationContext(),SP_NAME).getInt(KeepAliveConfig.RES_ICON),
                    intent
            );
            startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
