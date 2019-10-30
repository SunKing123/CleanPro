package com.xiaoniu.clean.function_lib.function;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/10/29 14
 * @mail：zhengzhihao@hellogeek.com
 */
public class SystemCommon {

    public static List<PackageInfo> installedPackages;
    public static List<ApplicationInfo> installedAppList;
    private static Application mApplication;

    public SystemCommon(Application application) {
        mApplication = application;
    }

    /**
     * 获取已安装包的信息
     *
     * @return
     */

    public static List<PackageInfo> getInstalledList() {
//        if(mApplication==null){
//            mApplication = ContextUtils.getApplication();
//        }
        PackageManager pm = mApplication.getPackageManager();
        installedPackages = pm.getInstalledPackages(0);
        return installedPackages;
    }

    public static ApplicationInfo getApplicationInfo(String str) {
        if (str == null) {
            return null;
        }
        for (ApplicationInfo applicationInfo : installedAppList) {
            if (str.equals(applicationInfo.processName)) {
                return applicationInfo;
            }
        }
        return null;
    }


    public static void  test() {
//        if(mApplication==null){
//            mApplication = ContextUtils.getApplication();
//        }
        Log.i("aaa","ssss");
    }


}
