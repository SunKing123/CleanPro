package com.xiaoniu.cleanking.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.xiaoniu.cleanking.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by fengpeihao on 2017/6/30.
 */

public class JumpPermissionManager {
    /**
     * adb shell dumpsys activity activities
     */
    private static JumpPermissionManager mManager;

    private static final String MANUFACTURER_HUAWEI = "HUAWEI";//华为

    private static final String MANUFACTURER_MEIZU = "Meizu";//魅族

    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米

    private static final String MANUFACTURER_SONY = "Sony";//索尼

    private static final String MANUFACTURER_OPPO = "OPPO";

    private static final String MANUFACTURER_LG = "LG";

    private static final String MANUFACTURER_VIVO = "vivo";

    private static final String MANUFACTURER_SAMSUNG = "samsung";//三星

    private static final String MANUFACTURER_LETV = "Letv";//乐视

    private static final String MANUFACTURER_ZTE = "ZTE";//中兴

    private static final String MANUFACTURER_YULONG = "YuLong";//酷派

    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    public static JumpPermissionManager with() {
        if (mManager == null) {
            synchronized (JumpPermissionManager.class) {
                mManager = new JumpPermissionManager();
            }
        }
        return mManager;
    }


    /**
     * 此函数可以自己定义
     *
     * @param activity
     */
    public void GoToSetting(Activity activity) {
        switch (Build.MANUFACTURER) {
            case MANUFACTURER_HUAWEI:
                Huawei(activity);
                break;
//            case MANUFACTURER_MEIZU:
//                Meizu(activity);
//                break;
            case MANUFACTURER_XIAOMI:
                Xiaomi(activity);
                break;
            case MANUFACTURER_SONY:
                Sony(activity);
                break;
            case MANUFACTURER_OPPO:
                OPPO(activity);
                break;
            case MANUFACTURER_LG:
                LG(activity);
                break;
            case MANUFACTURER_LETV:
                Letv(activity);
                break;
            case MANUFACTURER_VIVO:
                Vivo(activity);
                break;
            default:
                ApplicationInfo(activity);
                break;
        }
    }

    private void Huawei(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MAIN_ACTIVITY");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }

    }

    private void Vivo(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    private void Meizu(Activity activity) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    private void Xiaomi(Activity activity) {
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//        intent.setComponent(componentName);
//        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
//        activity.startActivity(intent);

//        String rom = getMiuiVersion();
//        Intent intent = null;
//        if ("V5".equals(rom)) {
//            Uri packageURI = Uri.parse("package:" + activity.getApplicationInfo().packageName);
//            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
//            activity.startActivity(intent);
//        } else if ("V6".equals(rom) || "V7".equals(rom)) {
//            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//            intent.putExtra("extra_pkgname", activity.getPackageName());
//            activity.startActivity(intent);
//        } else if ("V8".equals(rom)) {
//            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsTabActivity");
//            intent.putExtra("extra_pkgname", activity.getPackageName());
//            activity.startActivity(intent);
//        } else {
        ApplicationInfo(activity);
//        }

    }

    private void Sony(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    private void OPPO(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    private void LG(Activity activity) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }


    private void Letv(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    /**
     * 只能打开到自带安全软件
     *
     * @param activity
     */
    private void _360(Activity activity) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            ApplicationInfo(activity);
        }
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    private void ApplicationInfo(Activity activity) {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
            }
            activity.startActivity(localIntent);
        } catch (Exception e) {
            SystemConfig(activity);
        }
    }


    /**
     * 系统设置界面
     *
     * @param activity
     */
    private void SystemConfig(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);
    }

    //获取MIUI系统版本
    public String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
