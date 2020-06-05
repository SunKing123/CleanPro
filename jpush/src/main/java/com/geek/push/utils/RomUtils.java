package com.geek.push.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 判断ROM类型工具<p>
 *
 * @author zixuefei
 * @since 2019/5/15 10:24
 */

public class RomUtils {

    private static final String TAG = "RomUtils";
    private static final String VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String VERSION_EMUI = "ro.build.version.emui";
    private static final String VERSION_OPPO = "ro.build.version.opporom";
    private static final String VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String VERSION_VIVO = "ro.vivo.os.version";

    private static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }


    /**
     * 判断是否为华为UI
     */
    public static boolean isHuaweiRom() {
        String manufacturer = Build.MANUFACTURER;
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("HUAWEI");
    }

    /**
     * 判断是否为小米UI
     */
    public static boolean isMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty(VERSION_MIUI));
    }


    /**
     * "ro.build.user" -> "flyme"
     * "persist.sys.use.flyme.icon" -> "true"
     * "ro.flyme.published" -> "true"
     * "ro.build.display.id" -> "Flyme OS 5.1.2.0U"
     * "ro.meizu.setupwizard.flyme" -> "true"
     * <p>
     * 判断是否为魅族UI
     *
     * @return
     */
    public static boolean isFlymeRom() {
        return "flyme".equalsIgnoreCase(getSystemProperty("ro.build.user"));
    }


    public static boolean isOppoRom() {
        return !TextUtils.isEmpty(getSystemProperty(VERSION_OPPO));
    }


    public static boolean isVivoRom() {
        return !TextUtils.isEmpty(getSystemProperty(VERSION_VIVO));
    }

    /**
     * 获取当前进程名称
     *
     * @return processName
     */
    public static String getCurrentProcessName(Context context) {
        int currentProcessId = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.pid == currentProcessId) {
                return runningAppProcess.processName;
            }
        }
        return null;
    }

}
