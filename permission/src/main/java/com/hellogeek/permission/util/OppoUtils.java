package com.hellogeek.permission.util;

import android.content.Context;
import android.os.Binder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OppoUtils {
    /**
     * 现在只对于oppo a59m 22
     *
     * @return
     */
    public static boolean isOppoNotificationOpen(Context context) {
        try {
            Class cls = Class.forName("android.content.Context");
            Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(cls);
            if (!(obj instanceof String)) {
                return false;
            }
            String str2 = (String) obj;
            obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
            cls = Class.forName("android.app.AppOpsManager");
            Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
            declaredField2.setAccessible(true);
            Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
            int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
            return result == declaredField2.getInt(cls);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
