package com.hellogeek.permission.manufacturer.vivo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;
import com.hellogeek.permission.util.NotifyUtils;

import org.greenrobot.eventbus.EventBus;

import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_1;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_2;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_3_2;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_4_1;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_4_4;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_5_2_0;

public class VivoPermissionBase extends AutoFixAction {
    protected VivoPermissionActionUtil vivoPermissionActionUtil;
    public VERSION vivoSystemVersion;
    public String vivoOs;
    private static String mMiuiVersion;
    public String extraPackname = "extra_pkgname";

    public enum VERSION {
        VERSION_X_1, //一台比较旧的vivo Xplay 4.2.2大屏手机，从2013年开始就在黄页组名下;
        VERSION_1,
        VERSION_2,
        VERSION_3,
        VERSION_3_2,
        VERSION_3_4_4,
        VERSION_4,
        VERSION_4_1,
        VERSION_4_1_8,
        VERSION_4_2,
        VERSION_4_4,
        VERSION_4_4_4,
        VERSION_5_2_0, //vivo nex 5.2.0.2;
    }

    public VivoPermissionBase(Context context) {
        vivoPermissionActionUtil = new VivoPermissionActionUtil(context);
        vivoSystemVersion = getVivoSystemVersion(context);
        vivoOs = AppUtils.getVersionName(context, PermissionSystemPath.VIVO_SYSTEM_UI);
    }

    public static VERSION getVivoSystemVersion(Context context) {
        String versionName = AppUtils.getVersionName(context, PermissionSystemPath.VIVO_PHONE_MANAGER);
        if (versionName.indexOf("2014") > 0) {
            return VERSION.VERSION_X_1; //4.2.2-eng.compiler.20140625.105741
        }
        String[] versionTmp = versionName.split("\\.");
        int v;
        try {
            v = Integer.valueOf(versionTmp[0]);
        } catch (NumberFormatException e) {
            v = 3;
        }
        //TLog.i("ycsss", "version: " + versionName + "##" + v);
        int v2 = 0, v3 = 0;//对小的版本号进行判断
        int v4 = -1;
        if (versionTmp.length >= 3) {
            try {
                v2 = Integer.valueOf(versionTmp[1]);
                v3 = Integer.valueOf(versionTmp[2]);
            } catch (Exception e) {
            }
        }
        if (versionTmp.length >= 4) {
            try {
                v4 = Integer.valueOf(versionTmp[3]);
            } catch (Exception ignored) {
            }
        }
        if (v == 1) {
            return VERSION_1;
        } else if (v == 2) {
            return VERSION_2;
        } else if (v == 3) {
            if (v2 >= 2) {//对3.2.7以上的版本进行单独判断
                return VERSION_3_2;
            }
            return VERSION.VERSION_3;
        } else if (v == 5 || v == 6 || v == 7) {
            if (v2 == 2 && v3 == 0 && v4 == 2) {
                return VERSION_5_2_0;
            }
            return VERSION_4_4;
        } else {
            if (v2 == 1) {
                if (v3 > 4) {
                    return VERSION.VERSION_4_1_8;
                } else {
                    return VERSION_4_1;
                }
            } else if (v2 == 2) {
                return VERSION.VERSION_4_2;
            } else if (v2 == 4) {
                return VERSION_4_4;
            } else {
                return VERSION.VERSION_4;
            }
        }
    }

    protected void actionNotificationRead() {
        super.actionNotificationRead();
        setEventType(Permission.NOTIFICATIONREAD);
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
    protected void actionLockDisplay() {
        super.actionLockDisplay();
        setEventType(Permission.LOCKDISPALY);
    }

    @Override
    protected void actionNotifiCationBar() {
        super.actionNotifiCationBar();
        setEventType(Permission.NOTIFICATIONBAR);
    }


    @Override
    protected void actionPackageUsageStats() {
        super.actionPackageUsageStats();
        setEventType(Permission.PACKAGEUSAGESTATS);
    }


    @Override
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
        setEventType(Permission.BACKSTAGEPOPUP);
    }

    @Override
    public void clearSNIGList(Permission permission) {
        super.clearSNIGList(permission);
        if (vivoPermissionActionUtil != null) {
            vivoPermissionActionUtil.clearList(permission);
        }
    }


    private Permission permission;

    private void setEventType(Permission permission) {
        this.permission = permission;
    }

    public Permission getEventType() {
        return permission;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void configAccessbility(AccessibilityService service) {
        if (service == null) return;
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.packageNames = new String[]{
                PermissionSystemPath.ANDROID_SETTINGS,
                PermissionSystemPath.ANDROID_PACKAGE_INSTALLER_NAME,
                PermissionSystemPath.VIVO_PERMISSIONMANAGER,
                PermissionSystemPath.VIVO_ABE,
                PermissionSystemPath.VIVO_PHONE_MANAGER
        };
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = 1000;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        service.setServiceInfo(accessibilityServiceInfo);

    }

    public boolean isBACKSHOW() {
        int version;
        String model = android.os.Build.VERSION.RELEASE;
        version = Integer.valueOf(model.contains(".") ? model.substring(0, model.indexOf(".")) : model);
        if (vivoSystemVersion == VERSION_3_2 || vivoSystemVersion == VERSION_2 ||
                vivoSystemVersion == VERSION_1 || vivoSystemVersion == VERSION_4_1 ||
                ((vivoSystemVersion == VERSION_2 || vivoSystemVersion == VERSION_5_2_0) && Build.MODEL.contains(VivoModel.Y53L)) ||
                (vivoSystemVersion == VERSION_4_4 && ((!Build.MODEL.contains(VivoModel.X20A) && version < 7)))) {
            return false;
        }
        return true;
    }


    public void getIntentFail(Context mContext, String status, Permission permission, Exception e) {
        PermissionProvider.save(mContext, status, true);
        EventBus.getDefault().post(new PathEvent(permission, true, true));
    }

}
