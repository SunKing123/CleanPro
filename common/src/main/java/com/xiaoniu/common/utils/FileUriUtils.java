package com.xiaoniu.common.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;

import java.io.File;

public class FileUriUtils {
    public static final String SHARE_FILE_AUTHORITY_SUFFIX = ".share_file";

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            return null;
        }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, getShareFileAuthority(context), file);
        }
        return Uri.fromFile(file);
    }

    public static Uri getUriForFile(Context context, String filePath) {
        try {
            return getUriForFile(context, new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getShareFileAuthority(Context context) {
        if (context != null) {
            return context.getPackageName() + SHARE_FILE_AUTHORITY_SUFFIX;
        }
        return "";
    }
}
