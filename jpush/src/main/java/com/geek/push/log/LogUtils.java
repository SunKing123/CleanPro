package com.geek.push.log;

import android.util.Log;

/**
 * 日志调试工具<p>
 *
 * @author zixuefei
 * @since 2019/5/15 14:54
 */

public class LogUtils {

    public static final String TAG = "pushLog";
    private static boolean sDebug = true;

    public static void i(String log) {
        if (sDebug) {
            Log.i(TAG, log);
        }
    }

    public static void i(String tag, String log) {
        if (sDebug) {
            Log.i(tag, log);
        }
    }

    public static void d(String log) {
        if (sDebug) {
            Log.d(TAG, log);
        }
    }

    public static void d(String tag, String log) {
        if (sDebug) {
            Log.d(tag, log);
        }
    }

    public static void e(String tag, String log) {
        Log.e(tag, log);
    }

    public static void e(String log) {
        Log.e(TAG, log);
    }

    public static void e(String log, Throwable throwable) {
        Log.e(TAG, log, throwable);
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static void setDebug(boolean isDebug) {
        sDebug = isDebug;
    }

}
