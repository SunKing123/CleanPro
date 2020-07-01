package com.xiaoniu.cleanking.lifecyler;

import android.app.Application;

/**
 * @author: dukangkang
 * @date: 2019/4/26 15:08.
 * @description: todo ...
 */
public class LifecycleHelper {

    /**
     * 注册APP生命周期监测
     * @param application
     * @param listener
     */
    public static void registerActivityLifecycle(Application application, LifecycleListener listener) {
        ForegroundCallbacks.init(application);

        ForegroundCallbacks.get().addListener(listener);
    }

    /**
     * 判断是否是前台
     * @return
     */
    public static boolean isForeground() {
        return ForegroundCallbacks.get().isForeground();
    }

    /**
     * 判断是否是后台
     * @return
     */
    public static boolean isBackground() {
        return ForegroundCallbacks.get().isBackground();
    }
}
