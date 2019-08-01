package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;

public class Logger {
    public static String TAG = "GGTag";
    public static String ZYTAG = "zytag";
    private static boolean isSave2File = true;
    private static String logFileName = "CMLog.txt";
    private static String loggerSavePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static File mLoggerSaveFile = new File(loggerSavePath + logFileName);

    public static void d(String str, String str2, String str3) {
        i(str, str2, str3);
    }

    public static void e(String str, String str2, String str3) {
        i(str, str2, str3);
    }

    public static void i(String str, String str2, String str3) {
        if (!Constants.IS_LOG_CONTROLER) {
            return;
        }
        if (Constants.PRIVATE_LOG_CONTROLER) {
            Log.i(str2, str3);
        } else {
            Log.i(str, str3);
        }
    }

    public static void iCatch(String str, String str2, String str3, Exception exc) {
        if (!Constants.IS_LOG_CONTROLER) {
            return;
        }
        if (Constants.PRIVATE_LOG_CONTROLER) {
            Log.i(ZYTAG, "Logger---iCatch ---- " + exc.toString());
            Log.i(str2, str3 + "   " + getStackTraceText(exc));
            return;
        }
        Log.i(str, str3 + "   " + getStackTraceText(exc));
    }

    public static void iCatch(String str, String str2, String str3, Throwable th) {
        if (!Constants.IS_LOG_CONTROLER) {
            return;
        }
        if (Constants.PRIVATE_LOG_CONTROLER) {
            Log.i(ZYTAG, "Logger---iCatch ---- " + th.toString());
            Log.i(str2, str3 + "   " + getStackTraceText(th));
            return;
        }
        Log.i(str, str3 + "   " + getStackTraceText(th));
    }


    private static void LogSave2File(String str) {
        try {
            if (!mLoggerSaveFile.exists()) {
                mLoggerSaveFile.createNewFile();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(mLoggerSaveFile, "rw");
            randomAccessFile.seek(mLoggerSaveFile.length());
            randomAccessFile.write(str.getBytes());
            randomAccessFile.write("\r\n".getBytes());
            randomAccessFile.close();
        } catch (Exception e) {
        }
    }

    public static String getStackTraceText(Exception exc) {
        try {
            return getStackTraceText(exc.getCause());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getStackTraceText(Throwable th) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            th.printStackTrace(printWriter);
            printWriter.close();

            return stringWriter.toString();
        } catch (Exception e) {
            return "";
        }
    }
}