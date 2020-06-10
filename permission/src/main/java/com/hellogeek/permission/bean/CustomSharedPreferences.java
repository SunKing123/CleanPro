package com.hellogeek.permission.bean;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPreferences {
    private static String shareName = "permission";
    public static String isManual = "manual";
    public static String isPermissionShow= "permissionShow";
    public static String isActivity= "autofixActivity";
    public static String inExecution= "inExecution";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences share(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static String getStringValue(Context context, String key) {
        return share(context).getString(key, "");
    }

    public static boolean getBooleanValue(Context context, String key) {
        return share(context).getBoolean(key, false);
    }

    public static int getIntValue(Context context, String key) {
        return share(context).getInt(key, 0);
    }

    public static boolean setValue(Context context, String key,String value) {
        SharedPreferences.Editor e = share(context).edit();
        e.putString(key,value);
        Boolean bool = e.commit();
        return bool;
    }

    public static boolean setValue(Context context, String key,boolean value) {
        SharedPreferences.Editor e = share(context).edit();
        e.putBoolean(key,value);
        Boolean bool = e.commit();
        return bool;
    }


}
