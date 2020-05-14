package com.hellogeek.permission.util;

import android.app.Activity;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.SuspendedToastPermission;
import com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.vivoHelp.VivoPermissionUtils;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_BACKSTAGEPOPUP;
import static com.hellogeek.permission.util.Constant.PROVIDER_LOCKDISPLAY;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;
import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;

public class AccessibilitUtil {

    private static final String TAG = AccessibilitUtil.class.getSimpleName();

    public static boolean isAccessibilitySettingsOn(Context mContext, String className) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + className;
        Log.i(TAG, "service:" + service);
        // formate: service:com.fadi.forestautoget/com.fadi.forestautoget.service.AccessibilityServiceMonitor
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    public static void showSettingsUI(Context mContext) {
        Intent mIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        mContext.startActivity(mIntent);
    }

    public static void showSettingsUIV2(Context context, int requestCode) {
        Intent mIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        ((Activity) context).startActivityForResult(mIntent, requestCode);
    }


    // 查找第一个 id 节点
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    // 查找第一个 文本 节点
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    public static boolean isClickable(AccessibilityNodeInfo nodeInfo) {
        return isNonNull(nodeInfo) && nodeInfo.isClickable();
    }

    public static boolean isScrollable(AccessibilityNodeInfo nodeInfo) {
        return isNonNull(nodeInfo) && nodeInfo.isScrollable();
    }

    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    public static boolean isNotEmpty(String s) {
        return !TextUtils.isEmpty(s);
    }

    public static boolean isNotEmpty(List list) {
        return isNonNull(list) && list.size() > 0;
    }

    public static boolean isEmpty(List list) {
        return !isNotEmpty(list);
    }

    public static boolean isNonNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isNull(final Object obj) {
        return obj == null;
    }

    public static boolean isNotNullClassName(AccessibilityNodeInfo nodeInfo) {
        return isNonNull(nodeInfo) && isNonNull(nodeInfo.getClassName());
    }


    public static boolean isOpenPermission(Context context, Permission permission) {
        if (permission == null) {
            return false;
        }
        switch (permission) {
            case SUSPENDEDTOAST:
                if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.VIVO) {
                    return SuspendedToastPermission.isFloatPermissionOpen(context);
                } else if (MIUIPermissionBase.isRedMi4ASDK23()) {
                    return PermissionProvider.getBoolen(context, PROVIDER_SUSPENDEDTOAST, false);
                } else {
                    boolean isOpen = false;
                    try {
                        if (Build.VERSION.SDK_INT >= 19) {
                            if (SuspendedToastPermission.checkFloatWindowPermission(context) == 2) {
                                isOpen = PermissionProvider.getBoolen(context, PROVIDER_SUSPENDEDTOAST, false);
                            } else if (SuspendedToastPermission.checkFloatWindowPermission(context) == 0) {
                                return true;
                            }
                        } else {
                            isOpen = PermissionProvider.getBoolen(context, PROVIDER_SUSPENDEDTOAST, false);
                        }
                    } catch (Exception e) {
                        isOpen = PermissionProvider.getBoolen(context, PROVIDER_SUSPENDEDTOAST, false);
                    }
                    return isOpen;
                }
            case SELFSTARTING:
                if (PhoneRomUtils.isVivo()) {
                    return VivoPermissionUtils.getvivobgStartUpAppsPermissionStatus(context) == 0;
                } else if (PermissionProvider.getBoolen(context, PROVIDER_SELFSTARTING, false)) {
                    return true;
                }
                break;
            case SYSTEMSETTING:
                boolean canWrite;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && !MIUIPermissionBase.isRedMi4ASDK23()) {
                    canWrite = Settings.System.canWrite(context);
                } else {
                    canWrite = PermissionProvider.getBoolen(context, PROVIDER_SYSTEMSETTING, false);
                }
                if (canWrite) {
                    return true;
                }
                break;
            case REPLACEACLLPAGE:
                if (SuspendedToastPermission.isDefaultPhoneCallApp(context)) {
                    return true;
                }
                break;
            case NOTIFICATIONBAR:
                if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.VIVO || PhoneRomUtils.getPhoneType() == PhoneRomUtils.XIAOMI) {
                    return SuspendedToastPermission.isNotificationListenerEnabled(context);
                } else if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.ONEPLUS) {
                    return SuspendedToastPermission.isNotificationListenerEnabled(context);
                } else {
                    //小米华为OPPO有效
                    NotificationManagerCompat manager = NotificationManagerCompat.from(context);
                    // areNotificationsEnabled方法的有效性官方只最低支持到API 19 (4.4)，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
                    return manager.areNotificationsEnabled();
                }
            case NOTICEOFTAKEOVER:
                if (PermissionProvider.getBoolen(context, PROVIDER_NOTICEOFTAKEOVER, false)) {
                    return true;
                }
                break;
            case LOCKDISPALY:
                if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.VIVO) {
                    return SuspendedToastPermission.getVivoLockedScreenPermissionStatus(context) == 0;
                } else {
                    return PermissionProvider.getBoolen(context, PROVIDER_LOCKDISPLAY, false);
                }
            case BACKSTAGEPOPUP:
                if (PhoneRomUtils.isVivo()) {
                    return VivoPermissionUtils.getvivoBgStartActivityPermissionStatus(context) == 0;
                } else {
                    return PermissionProvider.getBoolen(context, PROVIDER_BACKSTAGEPOPUP, false);
                }
            case PACKAGEUSAGESTATS:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   // 如果大于等于5.0 再做判断
                    long ts = System.currentTimeMillis();
                    UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);
                    List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
                    if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                        return false;
                    }
                }
                return true;
        }
        return false;
    }
}
