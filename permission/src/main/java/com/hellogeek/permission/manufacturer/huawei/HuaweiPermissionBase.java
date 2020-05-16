package com.hellogeek.permission.manufacturer.huawei;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.PathEvent;

import org.greenrobot.eventbus.EventBus;

public class HuaweiPermissionBase extends AutoFixAction {
    protected HuaweiPermissionActionUtil huaweiPermissionActionUtil;
    public static int mVersionNum;
    public int huweiSystemVersion;

    public HuaweiPermissionBase(Context context) {
        huaweiPermissionActionUtil = new HuaweiPermissionActionUtil(context);
        huweiSystemVersion = getHuaweiSystemVersion(context);
    }

    public static int getHuaweiSystemVersion(Context context) {
        int version = 0;
        int versionNum = 0;
        int thirdPartFirtDigit = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.huawei.systemmanager", 0);
            String versionStr = info.versionName;
            String versionTmp[] = versionStr.split("\\.");
            if (versionTmp.length >= 2) {
                if (Integer.parseInt(versionTmp[0]) == 9) {
                    versionNum = 900;
                } else if (Integer.parseInt(versionTmp[0]) == 8) {
                    versionNum = 800;
                } else if (Integer.parseInt(versionTmp[0]) == 5) {
                    versionNum = 500;
                } else if (Integer.parseInt(versionTmp[0]) == 4) {
                    versionNum = Integer.parseInt(versionTmp[0] + versionTmp[1] + versionTmp[2]);
                } else {
                    versionNum = Integer.parseInt(versionTmp[0] + versionTmp[1]);
                }
            }
            if (versionTmp.length >= 3) {
                thirdPartFirtDigit = Integer.valueOf(versionTmp[2].substring(0, 1));
            }
        } catch (Exception e) {
        }
        mVersionNum = versionNum;
        if (versionNum >= 330) {
            if (versionNum >= 900) {
                version = 9;
            } else if (versionNum >= 800) {
                version = 9;
            } else if (versionNum >= 500) {
                version = 6;
            } else if (versionNum >= 400) {
                version = 5;
            } else if (versionNum >= 331) {
                version = 4;
            } else {
                version = (thirdPartFirtDigit == 6 || thirdPartFirtDigit == 4 || thirdPartFirtDigit == 2) ? 3 : 2;
            }
        } else if (versionNum != 0) {
            version = 1;
        }
        return version;
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        setEventType(Permission.SUSPENDEDTOAST);
    }

    @Override
    protected void actionSelfStarting() {
        super.actionSelfStarting();
        setEventType(Permission.SELFSTARTING);
    }

    @Override
    protected void actionRepLaceAcllpage() {
        super.actionRepLaceAcllpage();
        setEventType(Permission.REPLACEACLLPAGE);
    }

    @Override
    protected void actionPackageUsageStats() {
        super.actionPackageUsageStats();
        setEventType(Permission.PACKAGEUSAGESTATS);
    }

    @Override
    protected void actionSystemSetting() {
        super.actionSystemSetting();
        setEventType(Permission.SYSTEMSETTING);
    }

    @Override
    protected void actionNoticeOfTakeover() {
        super.actionNoticeOfTakeover();
        setEventType(Permission.NOTICEOFTAKEOVER);
    }

    @Override
    public void clearSNIGList(Permission permission) {
        super.clearSNIGList(permission);
        if (huaweiPermissionActionUtil != null) {
            huaweiPermissionActionUtil.clearList(permission);
        }
    }

    private Permission permission;

    private void setEventType(Permission permission) {
        this.permission = permission;
    }

    public Permission getEventType() {
        return permission;
    }


    public static boolean isHuaweiTAGAL00() {
        return android.os.Build.MODEL.contains(HuaweiModel.TAG_AL00) && Build.VERSION.SDK_INT == 22;
    }

    public static boolean isHuaweiP7L00SDK22() {
        return Build.MODEL.contains(HuaweiModel.P7_L00) && Build.VERSION.SDK_INT == 22;
    }

    public static boolean isHuaweiKIWAL10SDK23() {
        return android.os.Build.MODEL.contains(HuaweiModel.KIW_AL10) && Build.VERSION.SDK_INT == 23;
    }

    public static boolean isHuaweiSDK28() {
        return Build.VERSION.SDK_INT == 28;
    }

    public static boolean isHuaweiPLKAL10SDK23() {
        return android.os.Build.MODEL.contains(HuaweiModel.PLK_AL10) && Build.VERSION.SDK_INT == 23;
    }

    public static boolean isHuaweiFRDAL00() {
        return android.os.Build.MODEL.contains(HuaweiModel.FRD_AL00) && Build.VERSION.SDK_INT == 24;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void configAccessbility(AccessibilityService service) {
        if (service == null) return;
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.packageNames = new String[]{
                PermissionSystemPath.ANDROID_SETTINGS,
                PermissionSystemPath.ANDROID_PACKAGE_INSTALLER_NAME,
                PermissionSystemPath.HUAWEI_SETTING_MANAGE
        };
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = 1000;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        service.setServiceInfo(accessibilityServiceInfo);

    }

    public void getIntentFail(Context mContext, String status, Permission permission, Exception e) {
        PermissionProvider.save(mContext, status, true);
        EventBus.getDefault().post(new PathEvent(permission, true, true));
    }


}
