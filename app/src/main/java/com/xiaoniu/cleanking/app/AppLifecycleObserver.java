package com.xiaoniu.cleanking.app;

import android.app.ActivityManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author XiLei
 * @date 2019/10/15.
 * description：监听Application生命周期
 */
public class AppLifecycleObserver implements LifecycleObserver {
    private Context mContext;
    private boolean isBack; //isBack = true 记录当前已经进入后台

    public AppLifecycleObserver(Context context) {
        this.mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onEnterForeground() {
        if (isBack && PreferenceUtil.getHomeBackTime()) {
            if (AppHolder.getInstance().isOpen(PositionId.SPLASH_ID, PositionId.HOT_CODE) && mContext != null) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(mContext, SplashADHotActivity.class);
                mContext.startActivity(intent);
                isBack = false;
            }
        }

        EventBus.getDefault().post(new LifecycEvent(true));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onEnterBackground() {
//        if (!isAppOnForeground()) {   在低版本有兼用型问题，导致不能热启动  JKWKQL-515 【悟空清理-v2.0】【vivo-X9s】APP冷启动后，置于后台，2分钟后，热启动广告不展示
            //app 进入后台
            isBack = true;
            PreferenceUtil.saveHomeBackTime();
//        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = mContext.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
