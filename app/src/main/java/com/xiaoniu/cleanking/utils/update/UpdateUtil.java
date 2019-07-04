/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoniu.cleanking.utils.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.utils.encypt.rsa.MD5Utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UpdateUtil {
    private static final String TAG = "ezy.update";
    private static final String PREFS = "ezy.update.prefs";
    private static final String KEY_IGNORE = "ezy.update.prefs.ignore";
    private static final String KEY_UPDATE = "ezy.update.prefs.update";

    static boolean DEBUG = true;

    public static void log(String content) {
        if (DEBUG) {
            Log.i(TAG, content);
        }
    }

    public static void clean(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, 0);
        File file = new File(context.getExternalCacheDir(), sp.getString(KEY_UPDATE, "") + ".apk");
        UpdateUtil.log("apk ==> " + file.toString());
        if (file.exists()) {
            file.delete();
        }
        sp.edit().clear().apply();
    }

    public static void setUpdate(Context context) {

        File oldFile = new File(getFilePath(context));
        if (oldFile.exists()) {
            oldFile.delete();
        }
        File file = new File(getFilePath(context));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setIgnore(Context context, String md5) {
        context.getSharedPreferences(PREFS, 0).edit().putString(KEY_IGNORE, md5).apply();
    }

    public static boolean isIgnore(Context context, String md5) {
        return !TextUtils.isEmpty(md5) && md5.equals(context.getSharedPreferences(PREFS, 0).getString(KEY_IGNORE, ""));
    }

    public static void install(Context context, boolean force) {
        String md5 = context.getSharedPreferences(PREFS, 0).getString(KEY_UPDATE, "");
        File apk = new File(getFilePath(context));

        install(context, apk, force);
    }

    public static void install(Context context, File file, boolean force) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        } else {
//            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".updatefileprovider", file);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < 24) {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            } else {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".updatefileprovider", file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (force) {
            ((Activity) context).finish();
        }
    }

    /**
     * 校验文件是否存在
     *
     * @param apk
     * @return
     */
    public static boolean verify(File apk) {
        boolean exists = apk.exists();
        long length = apk.length();
        return apk.exists() && apk.length() > 0;
    }

    /**
     * 获取文件路径名
     *
     * @param context
     * @return
     */
    public static String getFilePath(Context context) {
       /* return Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator + "flashloan" + File.separator + "xiaoniu_v" + getAppVersionName(context) + ".apk";*/
        File apk = new File(context.getExternalCacheDir(), "xiaoniu_v" + getAppVersionName(context) + ".apk");
        return apk.getAbsolutePath();
    }

    /**
     * 获取temp文件路径名
     *
     * @param context
     * @return
     */
    public static String getTempPath(Context context) {
        /*return Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator + "flashloan" + File.separator + "xiaoniu_v" + getAppVersionName(context);*/
        File apk = new File(context.getExternalCacheDir(), File.separator + "flashloan" + File.separator + "xiaoniu_v" + getAppVersionName(context));
        return apk.getAbsolutePath();
    }

    public static File makeFile(Context context) {
        File file = new File(context.getExternalCacheDir(), File.separator + "flashloan");
//        String fileDirs = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "flashloan";
//        File file = new File(fileDirs);
        if (!file.exists()) {
            file.mkdirs();
        }

        File newFile = new File(file, "xiaoniu_v" + getAppVersionName(context));
        if (newFile.exists()) {
            newFile.delete();
        }
        return new File(file, "xiaoniu_v" + getAppVersionName(context) + ".apk");
    }


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "0.0.0";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            // 这里的context.getPackageName()可以换成你要查看的程序的包名
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            //Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static boolean checkWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isDebuggable(Context context) {
        try {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static String readString(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();
        } finally {
            close(input);
            close(output);
        }
        return output.toString("UTF-8");
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface PatchCallback {
        public void downloadSuccess(String path);

        public void downloadError(String message);
    }

    public static boolean verify(File apk, String md5) {
        if (!apk.exists()) {
            return false;
        }
        String _md5 = MD5Utils.getMD5(apk);
        boolean result = !TextUtils.isEmpty(_md5) && _md5.equalsIgnoreCase(md5);
        if (!result) {
            apk.delete();
            return false;
        }

        return result;
    }
}