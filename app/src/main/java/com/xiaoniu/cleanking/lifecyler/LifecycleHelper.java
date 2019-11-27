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

//        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//                sTopActivity = activity;
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//                sTopActivity = activity;
//                Log.w("dkk", "sTopActivity = " + sTopActivity);
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//
//            }
//        });
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
