package com.comm.jksdk.http.utils;

import android.util.Log;

/**
 *
 * 日志工具类
 *
 * 作者：ahq on 2017/12/18 13:13
 * 邮箱：anhuiqing888@163.com
 */
public class LogUtils {
    public static boolean isDebug = true;

    public static final String TAGAN = "lpb";

    public static void info(String msg){
        if(isDebug) {
            Log.i(buildTAG(), msg == null ? "null" : msg);
        }
    }
    public static void info(String tag, String msg){
        if(isDebug) {
            Log.i(tag == null ? buildTAG() : tag, msg == null ? "null" : msg);
        }
    }

    private static String buildTAG() {
        StringBuilder buffer = new StringBuilder();
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        buffer.append("【");
        /*buffer.append(Thread.currentThread().getName());
        buffer.append("】【");*/
        buffer.append(stackTraceElement.getFileName());
        buffer.append("】【");
        buffer.append(stackTraceElement.getLineNumber());
        buffer.append("行】【");
        buffer.append(stackTraceElement.getMethodName());
        buffer.append("()】 ");
        return buffer.toString();

    }
    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAGAN, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAGAN, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAGAN, msg);
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(TAGAN, msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(TAGAN, msg);
        }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }
}
