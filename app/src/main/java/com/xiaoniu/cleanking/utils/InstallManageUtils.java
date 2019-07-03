package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lang.chen on 2019/7/2
 */
public class InstallManageUtils {


    /**
     * 获取apk信息
     *
     * @param path
     * @return
     */
    public static AppInfoBean getUninstallAPKInfo(Context context, String path) {
        AppInfoBean appInfoBean = new AppInfoBean();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo appinfo = packageInfo.applicationInfo;
            appInfoBean.versionName = packageInfo.versionName;
            appInfoBean.icon = pm.getApplicationIcon(appinfo);
            //appInfoBean.name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfoBean.name=getAppName(context,appinfo.packageName);
            appInfoBean.packageName = appinfo.packageName;
            appInfoBean.isInstall = isAppInstalled(context, appinfo.packageName);
            appInfoBean.installTime = packageInfo.firstInstallTime;
            File file = new File(path);
            appInfoBean.packageSize = file.length();
            appInfoBean.path = path;
            appInfoBean.installTime=file.lastModified();
        }
        return appInfoBean;
    }


    /**
     * 获取程序的名字
     *
     * @param context
     * @param packname
     * @return
     */
    public static String getAppName(Context context, String packname) {
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return packname;
    }


    /**
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {

            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;

        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }

        return installed;

    }


    /**
     * 获取已安装应用列表
     *
     * @param context
     * @return
     */
    public static List<AppInfoBean> getAllApk(Context context) {
        List<AppInfoBean> appBeanList = new ArrayList<>();
        AppInfoBean bean = null;
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : list) {
            bean = new AppInfoBean();
            bean.icon = (packageInfo.applicationInfo.loadIcon(packageManager));
            bean.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            bean.packageName = (packageInfo.applicationInfo.packageName);
            bean.path = (packageInfo.applicationInfo.sourceDir);
            File file = new File(packageInfo.applicationInfo.sourceDir);
            bean.packageSize = ((int) file.length());
            int flags = packageInfo.applicationInfo.flags;
            //判断是否是属于系统的apk
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appBeanList.add(bean);
            }
            return appBeanList;
        }
        return appBeanList;
    }


}
