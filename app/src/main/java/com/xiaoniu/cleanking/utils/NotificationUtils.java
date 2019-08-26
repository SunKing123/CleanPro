package com.xiaoniu.cleanking.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.ContextUtils;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;

public class NotificationUtils extends ContextWrapper {

    public static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "消息通知";
    private static final String CHANNEL_DESCRIPTION = "this is default channel!";
    private static final String FAKE_TITLE = "title";
    private static final String FAKE_CONTENT = "content";
    private static int sTitleColor = Color.TRANSPARENT;
    private static int sContentColor = Color.TRANSPARENT;
    private static boolean isBkgCloseToDark = true;

    private NotificationManager mManager;

    public NotificationUtils(Context base, boolean isUpdateAgent) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(isUpdateAgent);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(boolean isUpdateAgent) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, isUpdateAgent ? NotificationManager.IMPORTANCE_LOW :  NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//是否绕过请勿打扰模式
        channel.enableLights(true);//闪光灯
        channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
        channel.setLightColor(Color.RED);//闪关灯的灯光颜色
        channel.canShowBadge();//桌面launcher的消息角标
        channel.enableVibration(false);//是否允许震动
        channel.getAudioAttributes();//获取系统通知响铃声音的配置
        channel.getGroup();//获取通知取到组
        channel.setBypassDnd(true);//设置可绕过  请勿打扰模式
        channel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
        channel.shouldShowLights();//是否会有灯光
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public void sendNotification(String title, String content,PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = getNotification(title, content);
        builder.setContentIntent(pendingIntent);
        getManager().notify(3000, builder.build());
    }


    public NotificationCompat.Builder getNotification(String title, String content) {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setPriority(PRIORITY_DEFAULT);
        }
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.applogo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.applogo));
        //点击自动删除通知
        builder.setAutoCancel(true);
        return builder;
    }

    public void sendNotification(int notifyId, String title, String content) {
        NotificationCompat.Builder builder = getNotification(title, content);
        getManager().notify(notifyId, builder.build());
    }

    public static NotificationCompat.Builder createNotificationBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextUtils.getContext(), CHANNEL_ID);
                return builder;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return new NotificationCompat.Builder(ContextUtils.getContext());
    }

    public static boolean isBkgCloseToDark() {
        if (sTitleColor == Color.TRANSPARENT) {
            fetchNotificationColors();
        }
        return isBkgCloseToDark;
    }

    public static void fetchNotificationColors() {
        int[] colors = getColorsInternal();
        sTitleColor = colors[0];
        sContentColor = colors[1];
        if (sTitleColor != Color.TRANSPARENT) {
            isBkgCloseToDark = !isSimilar(Color.BLACK, sTitleColor);
        }
    }

    private static int[] getColorsInternal() {
        NotificationCompat.Builder builder = NotificationUtils.createNotificationBuilder();
        builder.setContentTitle(FAKE_TITLE);
        builder.setContentText(FAKE_CONTENT);
        Notification notification = builder.build();
        int[] colors = new int[]{Color.TRANSPARENT, Color.TRANSPARENT};

        RemoteViews remoteViews = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            remoteViews = new Notification.Builder(ContextUtils.getContext()).createContentView();
        } else {
            remoteViews = notification.contentView;
        }
        if (remoteViews == null) {
            return colors;
        }

        ViewGroup root = (ViewGroup) remoteViews.apply(ContextUtils.getContext(), new FrameLayout(ContextUtils.getContext()));
        TextView title = (TextView) root.findViewById(android.R.id.title);
        if (title != null) {
            colors[0] = title.getCurrentTextColor();
        }

        traverseView(root, colors);
        if (colors[0] != Color.TRANSPARENT && colors[1] == Color.TRANSPARENT) {
            colors[1] = colors[0];
        } else if (colors[0] == Color.TRANSPARENT && colors[1] != Color.TRANSPARENT) {
            colors[0] = colors[1];
        }

        return colors;
    }

    private static void traverseView(View view, int[] colors) {
        if (view == null || colors == null || colors.length != 2) {
            return;
        }

        if (view instanceof TextView) {
            String text = ((TextView) view).getText().toString();
            if (colors[0] == Color.TRANSPARENT && FAKE_TITLE.equals(text)) {
                colors[0] = ((TextView) view).getCurrentTextColor();
            } else if (colors[1] == Color.TRANSPARENT && FAKE_CONTENT.equals(text)) {
                colors[1] = ((TextView) view).getCurrentTextColor();
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                traverseView(parent.getChildAt(i), colors);
                if (colors[0] != Color.TRANSPARENT && colors[1] != Color.TRANSPARENT) {
                    break;
                }
            }
        }
    }

    private static boolean isSimilar(int refColor, int color) {
        final double COLOR_THRESHOLD = 180.0;
        int formatRefColor = refColor | 0xff000000;
        int formatColor = color | 0xff000000;
        int baseRed = Color.red(formatRefColor) - Color.red(formatColor);
        int baseGreen = Color.green(formatRefColor) - Color.green(formatColor);
        int baseBlue = Color.blue(formatRefColor) - Color.blue(formatColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < COLOR_THRESHOLD;
    }
}
