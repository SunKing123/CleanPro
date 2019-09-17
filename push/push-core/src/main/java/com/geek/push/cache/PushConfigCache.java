package com.geek.push.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存消息推送注册的时候，返回的唯一标示
 * Created by pyt on 2017/5/16.
 */

public class PushConfigCache {

    private static final String FILE_ONE_PUSH_CACHE = "push_cache";
    private static final String KEY_RID = "rid";
    private static final String KEY_PLATFORM = "platform";

    public static void putRid(Context context, String token) {
        getSharedPreferences(context).edit().putString(KEY_RID, token).commit();
    }

    public static String getRid(Context context) {
        return getSharedPreferences(context).getString(KEY_RID, "");
    }

    public static void delRid(Context context) {
        getSharedPreferences(context).edit().remove(KEY_RID).commit();
    }

    public static void putPlatform(Context context, String platform) {
        getSharedPreferences(context).edit().putString(KEY_PLATFORM, platform).commit();
    }

    public static String getPlatform(Context context) {
        return getSharedPreferences(context).getString(KEY_PLATFORM, "");
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_ONE_PUSH_CACHE, Context.MODE_PRIVATE);
    }

    public static void delPlatform(Context context) {
        getSharedPreferences(context).edit().remove(KEY_PLATFORM).commit();
    }
}
