package com.xiaoniu.cleanking.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.xiaoniu.cleanking.app.AppApplication;

import java.io.File;

public class FileUtils {

    public static boolean isAppInstalled(String str) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean haveSDCard() {
        try {
            return Environment.getExternalStorageState().equals("mounted");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSystemApK(String str) {
        try {
            if ((AppApplication.getInstance().getPackageManager().getPackageInfo(str, 0).applicationInfo.flags & 1) > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void deleteFileAndFolder(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file2 : listFiles) {
                        if (file2 != null) {
                            if (file2.isDirectory()) {
                                deleteFileAndFolder(file2);
                            } else {
                                forceDelete(file2);
                            }
                        }
                    }
                }
            }
            forceDelete(file);
        }
    }

    public static void forceDelete(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
