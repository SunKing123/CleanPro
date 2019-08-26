package com.xiaoniu.cleanking.ui.tool.notify.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanDetailActivity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.service.NotificationCleanService;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.common.utils.ContextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by wangbz on 2019/5/20.
 */

public class NotifyCleanManager {
    private static final long CHECK_SERVICE_TIME = 8000;
    private static volatile NotifyCleanManager sInstance = null;
    private ArrayList<NotificationInfo> mNotificationList = new ArrayList();
    private Handler mHandler = new Handler();
    private boolean mServiceSuccess = false;
    private int mLastCleanCount = 0;
    private static final String QQ_PKG_NAME = "com.tencent.mobileqq";

    private NotifyCleanManager() {
    }

    public static NotifyCleanManager getInstance() {
        if (sInstance == null) {
            synchronized (NotifyCleanManager.class) {
                if (sInstance == null) {
                    sInstance = new NotifyCleanManager();
                }
            }
        }
        return sInstance;
    }

    public ArrayList<NotificationInfo> getAllNotifications() {
        return mNotificationList;
    }

    public void saveLastCleanCount() {
        mLastCleanCount = mNotificationList.size();
    }

    public int getLastCleanCount() {
        return mLastCleanCount;
    }

    public void addAllNotification(ArrayList<NotificationInfo> notifications) {
        if (notifications != null && notifications.size() > 0) {
            mNotificationList.addAll(notifications);
            EventBus.getDefault().post(new NotificationUpdateEvent());
        }
    }

    public void addNotification(NotificationInfo notification) {
        if (notification != null) {
            mNotificationList.add(0, notification);
            handleQQMsg(notification);
            EventBus.getDefault().post(new NotificationUpdateEvent());
        }
    }

    /**
     * 同一个好友发的消息，新消息会把旧消息的PendingIntent取消导致点击无法跳转
     * ，这里是过滤同一个人发的消息更新其PendingIntent为最新的
     */
    private void handleQQMsg(NotificationInfo info) {
        if (info == null || info.intent == null || info.title == null) {
            return;
        }
        if (!QQ_PKG_NAME.equals(info.pkg)) {
            return;
        }

        String targetTitle = subQQTitle(info.title.toString());
        if (TextUtils.isEmpty(targetTitle)) {
            return;
        }
        PendingIntent targetIntent = info.intent;

        for (int i = 0; i < mNotificationList.size(); i++) {
            info = mNotificationList.get(i);
            if (info != null && QQ_PKG_NAME.equals(info.pkg)) {
                if (!TextUtils.isEmpty(info.title)) {
                    String subTitle = subQQTitle(info.title.toString());
                    if (targetTitle.equals(subTitle)) {
                        info.intent = targetIntent;
                    }
                }
            }
        }
    }

    private String subQQTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return "";
        }
        int index = title.indexOf('(');
        if (index > 0) {
            title = title.substring(0, index);
        }
        return title.trim();
    }

    public void removeNotification(NotificationInfo notification) {
        if (notification != null) {
            mNotificationList.remove(notification);
            SPUtil.addNotifyCleanCount(1);
            NotificationUpdateEvent event = new NotificationUpdateEvent();
            event.cleanCount = 1;
            EventBus.getDefault().post(event);
            NotificationCleanService.updateReridentView(ContextUtils.getContext());
        }
    }

    public void removeNotification(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            int cleanCount = 0;
            for (int i = mNotificationList.size() - 1; i >= 0; i--) {
                NotificationInfo info = mNotificationList.get(i);
                if (info != null && pkgName.equals(info.pkg)) {
                    mNotificationList.remove(i);
                    cleanCount++;
                }
            }
            if (cleanCount > 0) {
                SPUtil.addNotifyCleanCount(cleanCount);
                NotificationUpdateEvent event = new NotificationUpdateEvent();
                event.cleanCount = cleanCount;
                EventBus.getDefault().post(event);
                NotificationCleanService.updateReridentView(ContextUtils.getContext());
            }
        }
    }

    public void cleanAllNotification(Context context) {
        int count = mNotificationList.size();
        if (count > 0) {
            mNotificationList.clear();
            SPUtil.addNotifyCleanCount(count);
        }
    }

    public void setServiceSuccess(boolean isbind) {
        mServiceSuccess = isbind;
    }

    public void sendRebindServiceMsg() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        boolean enable = NotifyUtils.isNotificationListenerEnabled();
        if (!enable) {
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!mServiceSuccess) {
                    requestRebindService(ContextUtils.getContext());
                }
            }
        }, CHECK_SERVICE_TIME);
    }

    /*绑定上后如果主动又onUnBind，以下方法有可能无效*/
    public void requestRebindService(Context context) {
        if (context == null || !NotifyUtils.isNotificationListenerEnabled()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                NotificationListenerService.requestRebind(new ComponentName(context, NotificationCleanService.class));
            } catch (Throwable e) {
            }
        }
        try {
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(context, NotificationCleanService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(context, NotificationCleanService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } catch (Throwable e) {
        }
    }

    public Notification getResidentNotification(Context context) {
        if (context == null) {
            return null;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int allCount = mNotificationList.size();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification_fast_clean);
        String cleanCountDesc = context.getString(R.string.notification_clean_count, allCount);
        remoteViews.setTextViewText(R.id.tvCleanCount, Html.fromHtml(cleanCountDesc));

        if (NotificationUtils.isBkgCloseToDark()) {
            remoteViews.setTextColor(R.id.tvCleanCount, Color.WHITE);
            remoteViews.setTextColor(R.id.tvNotifyDesc, Color.WHITE);
        }


        ArrayList<NotificationInfo> showNotifyList = new ArrayList();
        for (int i = 0; i < allCount; i++) {
            NotificationInfo info = mNotificationList.get(i);
            if (info == null) {
                continue;
            }
            String pkg = info.pkg;
            boolean canShow = true;
            if (!TextUtils.isEmpty(pkg)) {
                for (int j = 0; j < showNotifyList.size(); j++) {
                    NotificationInfo showInfo = showNotifyList.get(j);
                    if (showInfo != null && pkg.equals(showInfo.pkg)) {
                        canShow = false;
                        break;
                    }
                }
            } else {
                canShow = false;
            }
            if (canShow) {
                showNotifyList.add(info);
            }
        }

        int showCount = showNotifyList.size();
        if (showCount > 5) {
            showCount = 5;
            remoteViews.setViewVisibility(R.id.tvNotifyDesc, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.tvNotifyDesc, View.GONE);
        }

        int[] iconIds = new int[]{R.id.ivIcon1, R.id.ivIcon2, R.id.ivIcon3, R.id.ivIcon4, R.id.ivIcon5};
        remoteViews.setViewVisibility(iconIds[0], View.GONE);
        remoteViews.setViewVisibility(iconIds[1], View.GONE);
        remoteViews.setViewVisibility(iconIds[2], View.GONE);
        remoteViews.setViewVisibility(iconIds[3], View.GONE);
        remoteViews.setViewVisibility(iconIds[4], View.GONE);
        for (int i = 0; i < showCount; i++) {
            NotificationInfo info = showNotifyList.get(i);
            if (info != null && info.icon != null) {
                remoteViews.setImageViewBitmap(iconIds[i], info.icon);
                remoteViews.setViewVisibility(iconIds[i], View.VISIBLE);
            }
        }

        Intent cleanIntent = new Intent(context, NotificationCleanService.class);
        cleanIntent.setAction(NotificationCleanService.ACTION_CLEAN);
        PendingIntent pendingCleanIntent = PendingIntent.getService(context, 0, cleanIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnClean, pendingCleanIntent);

        Intent listIntent = new Intent(context, NotificationCleanService.class);
        listIntent.setAction(NotificationCleanService.ACTION_LIST);
        PendingIntent pendingListIntent = PendingIntent.getService(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.layoutNotifyClean, pendingListIntent);

        NotificationCompat.Builder builder = NotificationUtils.createNotificationBuilder();
        builder.setContent(remoteViews)
                .setWhen(System.currentTimeMillis());
//                .setSmallIcon(R.drawable.notificationbar_icon_clean);
        Notification notification = builder.build();
        return notification;
    }

    public static void startNotificationCleanActivity(Context context, int type) {
        if (!NotifyUtils.isNotificationListenerEnabled() || !SPUtil.isCleanNotificationEnable()) {
            NotifyCleanGuideActivity.startNotificationGuideActivity(context);
        } else {
            NotifyCleanDetailActivity.startNotificationCleanActivity(context);
        }
    }
}
