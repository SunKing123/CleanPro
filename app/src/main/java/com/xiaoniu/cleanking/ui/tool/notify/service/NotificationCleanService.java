package com.xiaoniu.cleanking.ui.tool.notify.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.ui.main.bean.InstalledApp;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanDetailActivity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationCleanEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.ResidentUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.cleanking.utils.PackageUtils;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationCleanService extends NotificationListenerService {
    public static final String ACTION_CLEAN = "action.xiaoniu.notification_clean";
    public static final String ACTION_LIST = "action.xiaoniu.notification_list";
    public static final String ACTION_UPDATE_RERIDENT = "action.xiaoniu.notification_update";
    public static final String NOTIFICATION_BUNDLE_RANKER = "ranker_bundle";
    public static final String NOTIFICATION_GROUP_RANKER = "ranker_group";
    private static final int REFLECTION_ACTION_TAG = 2;
    private static boolean sIsServiceConnected = false;
    private static final int NOTIFICATION_TEST = 1;
    private static final int NOTIFICATION_RERIDENT = 2;
    private Handler mHandler = new Handler();
    public static boolean shasNotInterceptNotify = false;

    public static void updateReridentView(Context context) {
        if (context != null) {
            EventBus.getDefault().post(new ResidentUpdateEvent());
        }
    }

    @Subscribe
    public void onEventMainThread(ResidentUpdateEvent event) {
        Intent intent = new Intent(ACTION_UPDATE_RERIDENT);
        onStartCommand(intent, 0, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_CLEAN.equals(action)) {
//                NotifyCleanManager.getInstance().saveLastCleanCount();
//                cleanAllNotifications();
                /*显示清理动画*/
                SystemUtils.collapseStatusBar(this);
                String className = SystemUtils.getCurrentTopActivity(this);
                if (NotifyCleanGuideActivity.class.getName().equals(className)
                        || NotifyCleanDetailActivity.class.getName().equals(className)) {
                    return START_NOT_STICKY;
                }
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
            } else if (ACTION_LIST.equals(action)) {
                SystemUtils.collapseStatusBar(this);
                String className = SystemUtils.getCurrentTopActivity(this);
                if (NotifyCleanGuideActivity.class.getName().equals(className)
                        || NotifyCleanDetailActivity.class.getName().equals(className)) {
                    return START_NOT_STICKY;
                }
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
            } else if (ACTION_UPDATE_RERIDENT.equals(action)) {
                int count = NotifyCleanManager.getInstance().getAllNotifications().size();
                if (count > 0) {
                    Notification notification = NotifyCleanManager.getInstance().getResidentNotification(this);
                    if (notification != null) {
                        startForeground(NOTIFICATION_RERIDENT, notification);
                    }
                } else {
                    stopForeground(true);
                }
            }
        }
        return START_STICKY;
    }

    private void cleanAllNotifications() {
        int count = NotifyCleanManager.getInstance().getAllNotifications().size();
        if (count > 0) {
            NotifyCleanManager.getInstance().cleanAllNotification();
            stopForeground(true);
            NotificationCleanEvent event = new NotificationCleanEvent();
            event.cleanCount = count;
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sIsServiceConnected) {
            getAllNotifications();
            sIsServiceConnected = true;
        }
        NotificationInfo info = filterNotification(sbn);

        if (info != null) {
            NotifyCleanManager.getInstance().addNotification(info);
            Notification notification = NotifyCleanManager.getInstance().getResidentNotification(this);
            if (notification != null) {
                startForeground(NOTIFICATION_RERIDENT, notification);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn != null && BuildConfig.APPLICATION_ID.equals(sbn.getPackageName())) {
            if (sbn.getId() == NOTIFICATION_RERIDENT) {//部分手机常驻通知栏可以滑掉，相当于清除操作
                cleanAllNotifications();
            }
        }
    }

    //android4.4(20)及之前的版本无此方法
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onListenerConnected() {
        getAllNotifications();
        sIsServiceConnected = true;
    }

    public void getAllNotifications() {
        try {
            shasNotInterceptNotify = false;
            StatusBarNotification[] activeNotifications = getActiveNotifications();
            ArrayList<NotificationInfo> notifications = filterNotification(activeNotifications);
            NotifyCleanManager.getInstance().addAllNotification(notifications);
            if (NotifyCleanManager.getInstance().getAllNotifications().size() > 0) {
                Notification notification = NotifyCleanManager.getInstance().getResidentNotification(this);
                if (notification != null) {
                    startForeground(NOTIFICATION_RERIDENT, notification);
                }
            }

        } catch (Throwable e) {
        }
    }

    //android5.1(22)及之前的版本无此方法
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }

    /*onBind时获取不到所有消息，低版本调用getActiveNotifications还报错
     * 另外onBind时报错，以后都无法重新绑定，除非重启手机*/
    @Override
    public IBinder onBind(Intent intent) {
        NotifyCleanManager.getInstance().setServiceSuccess(true);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendTestNotification();
                    } catch (Throwable ignore) {
                    }
                }
            }, 1000);

        }
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sIsServiceConnected = false;
        NotifyCleanManager.getInstance().setServiceSuccess(false);
        NotifyCleanManager.getInstance().requestRebindService(this);
//      stopForeground(true);
//      NotifyCleanManager.getInstance().cleanAllNotification(this, false);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void cancelNotification(StatusBarNotification sbn) {
        if (sbn == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getKey());
            } else {
                cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
            }
        } catch (Throwable e) {
        }
    }

    private void sendTestNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建通知
        NotificationCompat.Builder builder = NotificationUtils.createNotificationBuilder();
        //设置通知标题
        builder.setContentTitle("Test notification")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true);
        //发送通知
        if (nm != null) {
            nm.notify(NOTIFICATION_TEST, builder.build());
        }
    }

    private ArrayList<NotificationInfo> filterNotification(StatusBarNotification... activeNotifications) {
        ArrayList<NotificationInfo> notifications = new ArrayList<>();
        if (activeNotifications != null) {
            for (int i = 0; i < activeNotifications.length; i++) {
                StatusBarNotification barNotification = activeNotifications[i];
                NotificationInfo info = filterNotification(barNotification);
                if (info != null) {
                    notifications.add(info);
                }
            }
        }
        return notifications;
    }

    private NotificationInfo filterNotification(StatusBarNotification sbn) {
        if (sbn != null && sbn.isClearable()) {
            if (!SPUtil.isCleanNotificationEnable()) {
                return null;
            }

            // 当 API > 18 时，使用 extras 获取通知的详细信息
            Notification notification = sbn.getNotification();
            if (notification == null) {
                return null;
            }

            Bundle extras = notification.extras;
            if (extras == null) {
                return null;
            }

            PendingIntent contentIntent = notification.contentIntent;
//            if (contentIntent == null) {
//                return null;
//            }

            String pkgName = sbn.getPackageName();
            ConcurrentHashMap<String, InstalledApp> allInstalledApps = PackageUtils.getInstalledApps();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String group = notification.getGroup();
                if (NOTIFICATION_GROUP_RANKER.equals(group)
                        || NOTIFICATION_BUNDLE_RANKER.equals(group)) {//解决VIVO重复拦截QQ消息的问题
                    return null;
                }
                // MIUI do this weird action
                if (AndroidUtil.isLegalPackageName(group) && !group.equals(pkgName)) {
                    Set<String> set = allInstalledApps.keySet();
                    if (set.contains(group)) {
                        pkgName = group;
                    }
                }
            }

            if (BuildConfig.APPLICATION_ID.equals(pkgName)) {//不拦截手助自己的通知
                if (sbn.getId() == NOTIFICATION_TEST) {
                    cancelNotification(sbn);
                }
                return null;
            }

            // 获取通知标题
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE, "");
            CharSequence content = extras.getCharSequence(Notification.EXTRA_TEXT, "");
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                ArrayList<String> textList = parseNotificationText(notification.contentView);
                if (textList != null && textList.size() >= 2) {
                    title = textList.get(0);
                    content = textList.get(1);
                }
            }

            String appName = "";
            try {
                PackageManager pm = getPackageManager();
                appName = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)).toString();
            } catch (Throwable ignore) {
            }

            if (TextUtils.isEmpty(title)) {
                title = appName;
                if (TextUtils.isEmpty(title)) {
                    return null;
                }
            }

            Set<String> actualWhitelist = SPUtil.getActualWhitelist();
            /*过滤用户安装白名单应用*/
            if (actualWhitelist != null && actualWhitelist.contains(pkgName)) {
                shasNotInterceptNotify = true;
                return null;
            }

            // 获取通知内容
            NotificationInfo info = new NotificationInfo();
            info.pkg = pkgName;
            info.title = title;
            info.content = content;
            info.intent = contentIntent;
            info.time = sbn.getPostTime();
            info.appName = appName;
            try {
                Bitmap largeIcon = null;
                Object largeIconObj = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
                if (largeIconObj instanceof Bitmap) {
                    largeIcon = (Bitmap) largeIconObj;
                }
                Bitmap appIcon = AppUtils.getAppIcon(this, info.pkg);
                if (appIcon != null) {
                    info.icon = appIcon;
                } else if (largeIcon != null) {
                    info.icon = largeIcon;
                } else {
                    int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON, 0);
                    info.icon = getSmallIcon(this, sbn.getPackageName(), iconId);
                }
            } catch (Throwable e) {
            }

            cancelNotification(sbn);
            return info;
        }
        return null;
    }

    private Bitmap getSmallIcon(Context context, String pkgName, int id) {
        Bitmap smallIcon = null;
        try {
            Context remotePkgContext = context.createPackageContext(pkgName, 0);
            Drawable drawable = remotePkgContext.getResources().getDrawable(id);
            if (drawable != null) {
                smallIcon = ((BitmapDrawable) drawable).getBitmap();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return smallIcon;
    }

    public ArrayList<String> parseNotificationText(RemoteViews contentView) {
        if (contentView == null) {
            return null;
        }
        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        ArrayList<String> textList = new ArrayList<>();
        try {
            Field field = contentView.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(contentView);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != REFLECTION_ACTION_TAG) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    // Store the actual string
                    String text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    textList.add(text);
                }
                parcel.recycle();
            }
        } catch (Throwable e) {
        }
        return textList;
    }
}
