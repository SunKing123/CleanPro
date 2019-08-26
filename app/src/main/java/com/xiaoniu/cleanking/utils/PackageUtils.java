package com.xiaoniu.cleanking.utils;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;

import com.xiaoniu.cleanking.ui.main.bean.InstalledApp;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.SignatureUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PackageUtils {

    /**
     * 已安装应用
     */
    private static ConcurrentHashMap<String, InstalledApp> sInstalledApps = new ConcurrentHashMap<>();
    /**
     * 系统已安装应用
     */
    private static ConcurrentHashMap<String, InstalledApp> sSysInstalledApps = new ConcurrentHashMap<>();
    /**
     * 用户已安装应用
     */
    private static ConcurrentHashMap<String, InstalledApp> sUserInstalledApps = new ConcurrentHashMap<>();

    private static volatile HashSet<String> sSysSignatures;

    /**
     * 获取所有已安装应用列表
     */
    public static ConcurrentHashMap<String, InstalledApp> getInstalledApps() {
        if (sInstalledApps.isEmpty()) {
            loadInstalledApps();
        }
        return sInstalledApps;
    }


    /**
     * 获取系统预装应用列表
     */
    public static ConcurrentHashMap<String, InstalledApp> getSysInstalledApps() {
        if (sSysInstalledApps.isEmpty()) {
            loadInstalledApps();
        }
        return sSysInstalledApps;
    }

    /**
     * 获取用户安装的应用列表
     */
    public static ConcurrentHashMap<String, InstalledApp> getUserInstalledApps() {
        if (sUserInstalledApps.isEmpty()) {
            loadInstalledApps();
        }
        return sUserInstalledApps;
    }

    @SuppressLint("NewApi")
    private static void loadInstalledApps() {
        ConcurrentHashMap<String, InstalledApp> installed = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, InstalledApp> sysInstalled = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, InstalledApp> userInstalled = new ConcurrentHashMap<>();

        PackageManager packageManager = ContextUtils.getContext().getPackageManager();
        if (packageManager == null) {
            return;
        }

        List<PackageInfo> packages = AppUtils.getAllInstalledApps(ContextUtils.getContext());
        if (packages == null) {
            return;
        }

        for (PackageInfo packageInfo : packages) {
            if (packageInfo != null && packageInfo.applicationInfo != null) {
                InstalledApp appInfo = new InstalledApp();

                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                appInfo.uid = applicationInfo.uid;
                appInfo.appName = applicationInfo.loadLabel(packageManager).toString();

                appInfo.versionName = packageInfo.versionName != null ? packageInfo.versionName : "";

                appInfo.versionCode = packageInfo.versionCode;

                appInfo.packageName = packageInfo.packageName;

                appInfo.flags = applicationInfo.flags;

                appInfo.lastUpdateTime = packageInfo.lastUpdateTime;

                appInfo.signatures = new HashSet<>();

                if (packageInfo.signatures != null) {
                    for (int i = 0; i < packageInfo.signatures.length; i++) {
                        String str = SignatureUtils.parseSignature(packageInfo.signatures[i].toByteArray());
                        appInfo.signatures.add(str);
                    }
                }

                String dir = applicationInfo.sourceDir;
                if (dir != null) {
                    File sourceFile = new File(dir);
                    if (sourceFile.exists()) {
                        appInfo.totalFileSize = sourceFile.length();
                        appInfo.size = Formatter.formatFileSize(ContextUtils.getContext(), appInfo.totalFileSize);
                    }
                    appInfo.storeLocation = dir;
                    appInfo.store = (dir.contains("/system/app") || dir.contains("/data/app")) ? 0 : 1;
                }

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appInfo.flag = InstalledApp.APK_USER;
                    appInfo.isSignedWithSysSignature = isSystemApp(appInfo);
                    userInstalled.put(appInfo.packageName, appInfo);
                } else {
                    appInfo.flag = InstalledApp.APK_SYS;
                    sysInstalled.put(appInfo.packageName, appInfo);
                }
                installed.put(appInfo.packageName, appInfo);
            }
        }

        sUserInstalledApps = userInstalled;
        sSysInstalledApps = sysInstalled;
        sInstalledApps = installed;
    }

    private static boolean isSystemApp(InstalledApp app) {
        try {
            // Get signatures for system package
            Set<String> sysSignatures = getSysSignatures();
            if (sysSignatures != null) {
                for (String signature : app.signatures) {
                    if (sysSignatures.contains(signature)) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignore) {
        }
        return false;
    }

    private static Set<String> getSysSignatures() {
        try {
            if (sSysSignatures == null || sSysSignatures.isEmpty()) {
                if (sSysSignatures == null || sSysSignatures.isEmpty()) {
                    sSysSignatures = SignatureUtils.getSignature(ContextUtils.getContext(), "android");
                }
            }
        } catch (Throwable ignore) {
        }
        return sSysSignatures;
    }

}
