package com.xiaoniu.common.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.jess.arms.utils.ArmsUtils;
import com.xiaoniu.common.BuildConfig;

import java.lang.reflect.Method;
import java.util.List;

public class SystemUtils {
    /**
     * 回到home，后台运行
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * 得到现在运行activityd 类名
     *
     * @param context
     * @return
     */
    public static String getCurrentTopActivity(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName() + cn.getShortClassName();
        } catch (Exception e) {
            return "";
        }
    }

    //关闭statusbar，自定义notification按钮时，很多机器在点击按钮时statusbar不会关闭，需强制关闭
    @SuppressLint("WrongConstant")
    public static void collapseStatusBar(Context context) {
        if (context == null) {
            return;
        }
        Object sbservice = context.getSystemService("statusbar");
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        try {
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method collapse;
            if (currentApiVersion <= 16) {
                collapse = statusBarManager.getMethod("collapse");
            } else {
                collapse = statusBarManager.getMethod("collapsePanels");
            }
            collapse.setAccessible(true);
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //使用记录读取权限
    public static boolean isUsageAccessAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                AppOpsManager manager = ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE));
                if (manager == null) return false;
                int mode = manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Throwable ignored) {
            }
            return false;
        }
        return true;
    }



    //获取当前进程名称
    public static String getProcessName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            //处理友盟bug,能不能try住？
            // https://mobile.umeng.com/platform/5dcb9de5570df3121b000fbe/error_analysis/list/detail/3317258220190
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> list=manager.getRunningAppProcesses();
            if(null!=list&&list.size()>0){
                for (ActivityManager.RunningAppProcessInfo processInfo : list) {
                    if (processInfo.pid == android.os.Process.myPid()) {
                        return processInfo.processName;
                    }
                }
            }
        }catch (Exception e){

        }
        return "";
    }


    /**
     * 根据包名打开应用
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    //判断Service是否运行
    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取安装包更新时间
     */
    public static long getAppLastUpdateTime(Context context){
        long updateTime = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            updateTime = pi.lastUpdateTime / 1000;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return updateTime;
    }

    /**
     * 是否第一次安装
     * @param context
     */
    public static boolean isFirstInstall(Context context) {
        boolean isFirst = false;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            isFirst = pi.firstInstallTime == pi.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isFirst;

    }





}
