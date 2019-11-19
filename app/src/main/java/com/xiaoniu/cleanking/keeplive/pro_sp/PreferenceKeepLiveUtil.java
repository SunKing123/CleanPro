package com.xiaoniu.cleanking.keeplive.pro_sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.xiaoniu.cleanking.BuildConfig;

import androidx.annotation.NonNull;


public class PreferenceKeepLiveUtil {
    public static final String METHOD_CONTAIN_KEY = "method_contain_key";
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".preference";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_QUERY_VALUE = "method_query_value";
    public static final String METHOD_EIDIT_VALUE = "method_edit";
    public static final String METHOD_QUERY_PID = "method_query_pid";
    public static final String KEY_VALUES = "key_result";


    public static final Uri sContentCreate = Uri.withAppendedPath(URI, "create");

    public static final Uri sContentChanged = Uri.withAppendedPath(URI, "changed");

    public static SharedPreferences getSharedPreference(@NonNull Context ctx, String preferName) {
        return SharedPreferenceProxy.getSharedPreferences(ctx, preferName);
    }
}