package com.xiaoniu.cleanking.scheme.utils;

import android.app.Activity;
import android.os.Build;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * deprecation:管理所有的栈中的Activity
 * author:ayb
 * time:2017/7/17
 */
public class ActivityCollector {
    /**
     * 存放activity的列表
     */
    public static HashMap<Class<?>, Activity> activities = new LinkedHashMap<>();

    /**
     * 添加Activity
     *
     * @param activity
     */
    public static void addActivity(Activity activity, Class<?> clz) {
        activities.put(clz, activity);
    }

    /**
     * 判断一个Activity 是否存在
     *
     * @param clz
     * @return
     */
    public static <T extends Activity> boolean isActivityExist(Class<T> clz) {
        boolean res;
        Activity activity = getActivity(clz);
        if (activity == null) {
            res = false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    res = false;
                } else {
                    res = true;
                }
            } else {
                if (activity.isFinishing()) {
                    res = false;
                } else {
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * 判断多个Activity 是否至少一个存在
     *
     * @param clzlist
     * @return
     */
    public static <T extends Activity> boolean isListActivityExist(Class<T>... clzlist) {
        boolean res =false;
        for (Class<T> clz : clzlist) {
            Activity activity = getActivity(clz);
            if (activity == null) {
                res = false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        res = false;
                    } else {
                        res = true;
                        return res;
                    }
                } else {
                    if (activity.isFinishing()) {
                        res = false;
                    } else {
                        res = true;
                        return res;
                    }
                }
            }
        }
        return res;
    }

    /**
     * 获得指定activity实例
     *
     * @param clazz Activity 的类对象
     * @return
     */
    public static <T extends Activity> T getActivity(Class<T> clazz) {
        return (T) activities.get(clazz);
    }

    /**
     * 移除activity,代替finish
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        if (activities.containsValue(activity)) {
            activities.remove(activity.getClass());
        }
    }

    /**
     * 移除所有的Activity
     */
    public static void finishAllActivity() {
        if (activities != null && activities.size() > 0) {
            Set<Map.Entry<Class<?>, Activity>> sets = activities.entrySet();
            for (Map.Entry<Class<?>, Activity> s : sets) {
                if (!s.getValue().isFinishing()) {
                    s.getValue().finish();
                }
            }
            activities.clear();
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static <T extends Activity> T currentActivity() {
        T activity = (T) activities.get(activities.size() - 1);
        return activity;
    }

}
