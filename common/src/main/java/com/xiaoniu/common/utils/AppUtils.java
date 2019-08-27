package com.xiaoniu.common.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.leon.channel.helper.ChannelReaderUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.Config.RGB_565;

public class AppUtils {

    public static Drawable getApkIconFromPath(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return null;
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (packageArchiveInfo != null) {
                ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                applicationInfo.sourceDir = filePath;
                applicationInfo.publicSourceDir = filePath;
                return applicationInfo.loadIcon(packageManager);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getApkNameFromPath(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (packageArchiveInfo != null) {
                ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                applicationInfo.sourceDir = filePath;
                applicationInfo.publicSourceDir = filePath;
                return (String) applicationInfo.loadLabel(packageManager);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap getAppIcon(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return null;
        }
        Bitmap bit = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            Drawable draw = packageManager.getApplicationIcon(packageName);
            int rawWidth = draw.getIntrinsicWidth();
            int rawHeight = draw.getIntrinsicHeight();
            bit = Bitmap.createBitmap(rawWidth, rawHeight, draw.getOpacity() != PixelFormat.OPAQUE ? ARGB_8888 : RGB_565);
            draw.setBounds(0, 0, rawWidth, rawHeight);
            Canvas can = new Canvas(bit);
            draw.draw(can);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bit;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return applicationInfo.loadLabel(packageManager).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * [获取应用程序版本名称信息]
     */
    public static String getVersionName(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * [获取应用程序版本号信息]
     */
    public static int getVersionCode(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return 0;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    packageName, 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取市场渠道名
     *
     * @return channel
     */
    public static String getChannelId() {
        String channel = "";
        try {
            Log.i("123", "getChannelId" + ContextUtils.getContext());
            channel = ChannelReaderUtil.getChannel(ContextUtils.getContext());
        } catch (Exception e) {
        }
        return !TextUtils.isEmpty(channel) ? channel : "official";
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        if (context != null && !TextUtils.isEmpty(pkgName)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
                if (packageInfo != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isSystemApp(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean installNormal(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        try {
            String[] args1 = {"chmod", "771", file.getPath().substring(0, file.getPath().lastIndexOf("/"))};
            Process p1 = Runtime.getRuntime().exec(args1);
            p1.waitFor();
            p1.destroy();
            String[] args2 = {"chmod", "777", file.getPath()};
            Process p2 = Runtime.getRuntime().exec(args2);
            p2.waitFor();
            p2.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(FileUriUtils.getUriForFile(context, file), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(i);
        return true;
    }

    public static boolean uninstallNormal(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }


    @SuppressLint("NewApi")
    public static synchronized List<PackageInfo> getAllInstalledApps(Context context) {
        if (context == null) {
            return null;
        }

        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return null;
        }
        List<PackageInfo> packages = new LinkedList<>();
        int sizeBefore = 0;
        try {
            packages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
            sizeBefore = packages.size();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (sizeBefore <= 10) {
            List<PackageInfo> list = getInstalledPackages(packageManager);
            if (list.size() > sizeBefore) {
                packages = list;
            }
        }

        return packages;

    }

    private static List<PackageInfo> getInstalledPackages(PackageManager pm) {
        Process process = null;
        BufferedReader br = null;
        List<PackageInfo> list = new LinkedList<>();
        try {
            process = Runtime.getRuntime().exec("pm list packages");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String pkgName;
            while ((pkgName = br.readLine()) != null) {
                pkgName = pkgName.substring(pkgName.indexOf(':') + 1);
                list.add(pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES));
            }
            process.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }

        return list;
    }

}
