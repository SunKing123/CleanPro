package com.hellogeek.permission.manufacturer.oppo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.manufacturer.oppo.colors.OppoPermissionActionUtil;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.NodeInfoUtil;
import com.hellogeek.permission.util.NotifyUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME;
import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME;
import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_SAFE;

public class OppoPermissionBase extends AutoFixAction {
    protected OppoPermissionActionUtil oppoPermissionActionUtil;
    protected com.hellogeek.permission.manufacturer.oppo.safe.OppoPermissionActionUtil oppoPermissionSafeActionUtil;
    protected com.hellogeek.permission.manufacturer.oppo.color.OppoPermissionActionUtil oppoPermissionColorActionUtil;
    public VERSION oppoSystemVersion;
    public String extraPackname = "extra_pkgname";

    public enum VERSION {
        V1,//colors start
        V2,
        V3,
        V4,
        V5,
        V6,
        V6_1,
        V6_2,
        V7,
        V7_1,
        V8,//colors end
        V16,//color start
        V17,
        V_UNDEFINED,//color end
    }

    public OppoPermissionBase(Context context) {
        if (PhoneRomUtils.isPackName(context, OPPO_COLOROS_PACKAGE_NAME)) {
            oppoSystemVersion = getOppoColorsSystemVersion(context);
            oppoPermissionActionUtil = new OppoPermissionActionUtil(context);

        } else if (PhoneRomUtils.isPackName(context, OPPO_COLORO_PACKAGE_NAME)) {
            oppoSystemVersion = getOppoColorSystemVersion(context);
            oppoPermissionColorActionUtil = new com.hellogeek.permission.manufacturer.oppo.color.OppoPermissionActionUtil(context);

        } else if (PhoneRomUtils.isPackName(context, OPPO_SAFE)) {
            oppoSystemVersion = getOppoSafeSystemVersion(context);
            oppoPermissionSafeActionUtil = new com.hellogeek.permission.manufacturer.oppo.safe.OppoPermissionActionUtil(context);
        }
    }

    private static VERSION getOppoColorsSystemVersion(Context context) {
        String displayVersion;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(OPPO_COLOROS_PACKAGE_NAME, 0);
            String versionStr = info.versionName; //3.0.2266;
            versionStr = versionStr.replace(".", "");
            int version = Integer.parseInt(versionStr);
            if (version <= 302266) {
                return VERSION.V1;
            } else {
                try {
                    /**update 17-4-19 By Junxiang Cheng
                     * v3_displayVersion:>=161228(before:170000)*/
                    String display = Build.DISPLAY;
                    String[] tmp = display.split("_");
                    displayVersion = tmp[tmp.length - 1];
                    if ((displayVersion.equals("A.25") || displayVersion.equals("A.09")) && version == 302268) {
                        return VERSION.V8;
                    }
                    if ((displayVersion.contains("A") || displayVersion.contains("C")) && version == 302268) {
                        return VERSION.V7_1;
                    }
                    //如果displayVersion包含字母要放在这个之前，要不会catch
                    if (PhoneRomUtils.isOppoR9Tm() || PhoneRomUtils.isOppoR9m() || PhoneRomUtils.isOppoA57() || PhoneRomUtils.isOppoA59()
                            || Integer.parseInt(displayVersion) == 181206 || Integer.parseInt(displayVersion) == 190116 || Integer.parseInt(displayVersion) == 181222 || Integer.parseInt(displayVersion) == 181227 || Integer.parseInt(displayVersion) == 190107 || Integer.parseInt(displayVersion) == 190102 || Integer.parseInt(displayVersion) == 190123 ||
                            Integer.parseInt(displayVersion) == 190110 || Integer.parseInt(displayVersion) == 190124) {
                        return VERSION.V7;
                    }

                    if (Integer.parseInt(displayVersion) >= 180912 || Integer.parseInt(displayVersion) == 180718) {
                        return VERSION.V7_1;
                    }
                    if (Integer.parseInt(displayVersion) >= 180418) {
                        return VERSION.V7;
                    }
                    if (Integer.parseInt(displayVersion) >= 180224) {
                        return VERSION.V6_2;
                    }
                    if (Integer.parseInt(displayVersion) >= 170713) {
                        return VERSION.V6_1;
                    }
                    if (Integer.parseInt(displayVersion) >= 170613) {
                        return VERSION.V6;
                    }
                    if (Integer.parseInt(displayVersion) >= 170603) {
                        return VERSION.V5;
                    }
                    if (Integer.parseInt(displayVersion) >= 170500) {
                        return VERSION.V4;
                    }
                    if (Integer.parseInt(displayVersion) >= 161228/*170000*/) {
                        return VERSION.V3;
                    }
                } catch (Exception e) {
                }
                return VERSION.V2;
            }
        } catch (Exception e) {
        }
        return VERSION.V1;
    }


    private static VERSION getOppoColorSystemVersion(Context context) {
        String display = Build.DISPLAY;
        String[] tmp = display.split("_");
        String displayVersion = tmp[tmp.length - 1];
        try {
            int v = Integer.parseInt(displayVersion);
            if (v == 160811) {
                return VERSION.V16;
            }
            if (v == 151204) {
                return VERSION.V17;
            }
        } catch (Exception ignored) {
        }
        return VERSION.V_UNDEFINED;
    }

    private static VERSION getOppoSafeSystemVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(OPPO_SAFE, 0);
            String versionStr = info.versionName;
            if (versionStr != null && versionStr.startsWith("2")) {
                return VERSION.V3;
            } else if (versionStr != null && versionStr.startsWith("V1.03")) {
                return VERSION.V2;
            } else {
                return VERSION.V1;
            }
        } catch (Exception e) {
        }
        return VERSION.V1;
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
    protected void actionPackageUsageStats() {
        super.actionPackageUsageStats();
        setEventType(Permission.PACKAGEUSAGESTATS);
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
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
        setEventType(Permission.BACKSTAGEPOPUP);
    }

    @Override
    public void clearSNIGList(Permission permission) {
        super.clearSNIGList(permission);
        if (oppoPermissionActionUtil != null) {
            oppoPermissionActionUtil.clearList(permission);
        } else if (oppoPermissionSafeActionUtil != null) {
            oppoPermissionSafeActionUtil.clearList(permission);
        } else if (oppoPermissionColorActionUtil != null) {
            oppoPermissionColorActionUtil.clearList(permission);
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
                PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME,
                PermissionSystemPath.ANDROID_PACKAGE_INSTALLER_NAME,
                PermissionSystemPath.ANDROID_SETTINGS,
                PermissionSystemPath.OPPO_COLOROS_NOTIFICATION_PACKAGER,
                PermissionSystemPath.OPPO_NOTIFICATION_PACKAGE,
                PermissionSystemPath.OPPO_LAUNCHER,
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
