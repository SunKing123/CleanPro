package com.xiaoniu.cleanking.lifecyler;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.LinkedList;

/**
 * Created by iCenler on 2016/1/28.
 * Description: 全局生命周期监听
 */
public class ApplicationLifecycleListener implements Application.ActivityLifecycleCallbacks {

    /**
     * 后台切换广播
     */
    public static final String ACTION_BACKGROUND_CHANGED = "app.intent.action.BACKGROUND_CHANGED";

    /**
     * 前台切换广播
     */
    public static final String ACTION_FOREGROUND_CHANGED = "app.intent.action.FOREGROUND_CHANGED";

    /**
     * 可见 Activity 数量，用于识别前后台切换
     */
    private int activeCount = 0;

    /**
     * 应用是否处于后台
     */
    private boolean isBackground = true;

    private Activity preActivity;

    private Activity currActivity;

    private LinkedList<Activity> activityStack = new LinkedList<>();

    /**
     * @return 应用前后台状态
     */
    public boolean isBackground() {
        return isBackground;
    }

    /**
     * @return 当前任务栈栈顶下的 Activity 索引
     */
    @Nullable
    public Activity getPreActivity() {
        return preActivity;
    }

    /**
     * @return 当前任务栈栈顶 Activity 索引
     */
    @Nullable
    public Activity getTopActivity() {
        return currActivity;
    }

    /**
     * @return 获取 Activity 任务栈
     */
    public LinkedList<Activity> getActivityStack() {
        return new LinkedList<>(activityStack);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (isBackground) {
            isBackground = false;
            foreground(activity);
        }

        currActivity = activityStack.peekLast();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        preActivity = activity;
        if (activity.isFinishing()) {// 移除正在关闭 Activity, 处理延迟销毁问题
            activityStack.pollLast();
            if (activityStack.size() > 1) {
                preActivity = activityStack.get(activityStack.size() - 2);
            } else {
                preActivity = null;
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount == 0) {
            isBackground = true;
            background(activity);

            if (activityStack.size() > 1) {// 切换到后台, 重新调整 preActivity 索引
                preActivity = activityStack.get(activityStack.size() - 2);
            } else {
                preActivity = null;
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        activityStack.remove(activity);
        if (activity == preActivity) preActivity = null;
        if (activity == currActivity) currActivity = null;

    }

    /**
     * 切换后台调用
     */
    private void background(Activity activity) {
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_BACKGROUND_CHANGED));
    }

    /**
     * 切换前台调用
     */
    private void foreground(Activity activity) {
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FOREGROUND_CHANGED));
    }

}
