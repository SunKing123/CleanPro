package com.xiaoniu.cleanking.scheme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * deprecation:Activity工具类
 * author:ayb
 * time:2017-6-8
 */
public class ActivityUtils {
    public static final int REQUEST_CODE_OPEN_BROWSER = 0xf0;// 打开浏览器
    public static final int REQUEST_CODE_FROM_BROWSER = 0xf1;//从浏览器打开

    private static ArrayList<Activity> sActivityArray = new ArrayList<>();
    /**
     * 检测是否有Intent对应的Activity
     * @param context   上下文
     * @param intent    意图
     * @return
     */
    public static boolean queryActivityIntent(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        return queryIntentActivities != null && queryIntentActivities.size() > 0;
    }

    /**
     * Activity加入队列
     * @param activity
     */
    public static void addActivity(Activity activity) {
        sActivityArray.add(activity);
    }

    /**
     *Activity移除队列
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        sActivityArray.remove(activity);
    }

}
