package com.xiaoniu.asmhelp.vivoHelp;

import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;

import com.xiaoniu.asmhelp.util.PhoneRomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class VivoPermissionUtils {

    /**
     * 后台启动Activity权限 1 关闭 0 开启
     *
     * @param context
     * @return
     */
    public static int getvivoBgStartActivityPermissionStatus(Context context) {
        String packageName = context.getPackageName();
//        String packageName = "com.xiaoniu.zuilaidian";
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        try {
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode;
                } else {
                    cursor.close();
                    return 1;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 1;
    }

    /**
     * 判断通话记录是否可读 1 关闭 0 开启
     *
     * @param context
     * @return
     */
    public static int checkCallRecords(Context context) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.getCount() >= 0) {
                cursor.close();
                return 0;
            } else {
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 自启动权限 1 关闭 0 开启
     *
     * @param context
     * @return
     */
    public static int getvivobgStartUpAppsPermissionStatus(Context context) {
        String packageName = context.getPackageName();
//        String packageName = "com.xiaoniu.zuilaidian";
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/bg_start_up_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor query = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        try {
            if (query != null) {
                if (query.moveToFirst()) {
                    int currentmode = query.getInt(query.getColumnIndex("currentstate"));
                    query.close();
                    return currentmode;
                } else {
                    query.close();
                    return 1;
                }
            } else {
                if (query == null) {
                    query = context.getContentResolver().query(Uri.parse("content://com.iqoo.secure.provider.secureprovider/forbidbgstartappslist"), null, "pkgname=?", new String[]{context.getPackageName()}, null);
                    if (checkOldVivo(context) && (query == null || query.getCount() == 0)) {
                        return 1;
                    }
                }
                if (query != null) {
                    if (query.getCount() != 0) {
                        query.moveToFirst();
                        if (query.getInt(0) == 0) {
                            return 0;
                        }

                    }
                }
                return 1;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();

        }
        return 1;
    }

    public static boolean checkOldVivo(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                Class cls3 = Class.forName("com.vivo.services.security.client.VivoPermissionInfo");
                Class cls4 = Class.forName("android.os.SystemProperties");
                Method method2 = cls4.getMethod("get", new Class[]{String.class, String.class});
                Field field = cls3.getField("DENIED");
                Field field2 = cls3.getField("WARNING");
                field.setAccessible(true);
                field2.setAccessible(true);
                String str = (String) method2.invoke(null, new Object[]{"ro.vivo.rom.version", null});
                if (!TextUtils.isEmpty(str) && Float.parseFloat(str.substring(4)) >= 2.5f) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 悬浮窗 1 关闭 0 开启
     *
     * @param context
     * @return
     */
//    public static int getVivofloatPermissionStatus(Context context) {
////        String packageName = context.getPackageName();
//        String packageName = "com.xiaoniu.zuilaidian";
////        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
//        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/allowfloatwindowapp");
//        String selection = "pkgname = ?";
//        String[] selectionArgs = new String[]{packageName};
//        try {
//            Cursor cursor = context
//                    .getContentResolver()
//                    .query(uri2, null, selection, selectionArgs, null);
//            if (cursor != null) {
//                if (cursor.moveToFirst()) {
//                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"));
//                    cursor.close();
//                    return currentmode;
//                } else {
//                    cursor.close();
//                    return 1;
//                }
//            }
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return 1;
//    }
    public static int getFloatPermissionStatusOld(Context context) {
        if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("vivo Y66")) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays1 = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                return (Boolean)canDrawOverlays1.invoke(null, context)? 0:1;
            } catch (Exception e) {
                return 1;
            }
        }else {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            String packageName = context.getPackageName();
            Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            try {
                Cursor cursor = context
                        .getContentResolver()
                        .query(uri, null, selection, selectionArgs, null);
                if (cursor != null) {
                    cursor.getColumnNames();
                    if (cursor.moveToFirst()) {
                        int currentmode = cursor.getInt(cursor.getColumnIndex("currentlmode"));
                        cursor.close();
                        return currentmode;
                    } else {
                        cursor.close();
                        return getFloatPermissionStatusNew(context);
                    }

                } else {
                    return getFloatPermissionStatusNew(context);
                }
            } catch (Exception e) {
                return getFloatPermissionStatusNew(context);
            }
        }
    }

    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private static int getFloatPermissionStatusNew(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                cursor.close();
                return currentmode;
            } else {
                cursor.close();
                return 1;
            }
        }
        return 1;
    }


    /**
     * 锁屏显示 1 关闭 0 开启
     *
     * @param context
     * @return
     */
    public static int getVivoLockedScreenPermissionStatus(Context context) {
        String packageName = context.getPackageName();
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/control_locked_screen_action");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        try {
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode;
                } else {
                    cursor.close();
                    return 1;
                }
            } else {
                return getVivoLockedScreenPermissionStatusOld(context);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 1;
    }

    public static int getVivoLockedScreenPermissionStatusOld(Context context) {
                String packageName = context.getPackageName();
//        String packageName = "com.xiaoniu.zuilaidian";
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/start_bg_activity");
        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/control_locked_screen_action");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        try {
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode;
                } else {
                    cursor.close();
                    return 1;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 1;
    }

    public static boolean isNotificationListenerEnabled(Context context) {
        try {
            String packageName = context.getPackageName();
            String str1 = Settings.Secure.getString(context.getContentResolver()
                    , "enabled_notification_listeners");
            if (!TextUtils.isEmpty(str1)) {
                String[] strings = str1.split(":");
                for (String string : strings) {
                    ComponentName localComponentName
                            = ComponentName.unflattenFromString(string);
                    if ((localComponentName == null)
                            || (!TextUtils.equals(packageName, localComponentName.getPackageName()))) {
                    } else {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static int getVivoApplistPermissionStatus(Context context) {
////        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/read_installed_apps");
//        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/read_installed_apps");
//        try {
//            Cursor cursor = context
//                    .getContentResolver()
//                    .query(uri2, null, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex("pkgname"));
//                    String newName = CryUtils.akt(name, "iqoo11-14");
//                    int currentmode = cursor.getInt(cursor.getColumnIndex("status"));
//                    Log.d("liwei", "name------>" + newName + "------status---->" + currentmode);
//                }
//            }
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return 1;
//    }

}
