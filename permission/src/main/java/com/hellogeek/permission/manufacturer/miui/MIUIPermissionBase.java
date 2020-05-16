package com.hellogeek.permission.manufacturer.miui;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;



import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

public class MIUIPermissionBase extends AutoFixAction {
   protected MiuiPermissionActionUtil miuiPermissionActionUtil;
    public VERSION miuiSystemVersion;
    private static String mMiuiVersion;
   public String extraPackname="extra_pkgname";

    public enum VERSION {
        COMMON,
        SPECIAL,
        SPECIAL_2,
        SPECIAL_9_1,
        SPECIAL_9_1_3,
        SPECIAL_9_2,
        SPECIAL_9_2_2,
        SPECIAL_9_5,
        SPECIAL_9_6
    }

    public MIUIPermissionBase(Context context) {
        miuiPermissionActionUtil = new MiuiPermissionActionUtil(context);
        miuiSystemVersion=getMiuiSystemVersion(context);
    }
    public static VERSION getMiuiSystemVersion(Context context) {
        VERSION version = null;
        mMiuiVersion = Build.VERSION.INCREMENTAL;
        if (mMiuiVersion.startsWith("V9.1.3")) {
            version = VERSION.SPECIAL_9_1_3;
        } else if (mMiuiVersion.startsWith("V9.2")) {
            if (mMiuiVersion.startsWith("V9.2.2")) {
                version = VERSION.SPECIAL_9_2_2;
                return version;
            }
            version = VERSION.SPECIAL_9_2;
        } else if (mMiuiVersion.startsWith("V9.5")) {
            version = VERSION.SPECIAL_9_5;
        } else if (mMiuiVersion.startsWith("9.1")) {
            version = VERSION.SPECIAL_9_1;
        } else if (mMiuiVersion.startsWith("V9.6")) {
            version = VERSION.SPECIAL_9_6;
        } else if (mMiuiVersion.startsWith("V8.1.6")) {
            version = VERSION.SPECIAL;
        }else if (mMiuiVersion.startsWith("V8")||mMiuiVersion.startsWith("V10")||mMiuiVersion.startsWith("V11") || mMiuiVersion.startsWith("V9") || mMiuiVersion.startsWith("9")) {
            version = VERSION.SPECIAL_9_1_3;
        } else {
            try {

                String versionName =    AppUtils.getVersionName(context,"com.miui.securitycenter");
                if (versionName.startsWith("2.0") || versionName.startsWith("2.1") || versionName.startsWith("2.2") ||
                        versionName.startsWith("2.3") || versionName.startsWith("2.5") || versionName.startsWith("2.6")||mMiuiVersion.startsWith("6.9.29")||mMiuiVersion.startsWith("V8.1.1") || mMiuiVersion.startsWith("V8.2") || mMiuiVersion.startsWith("7.1")) {
                    version = VERSION.SPECIAL_2;
                } else if (versionName.startsWith("1.9")||mMiuiVersion.startsWith("6.9") || mMiuiVersion.startsWith("V8.1.5") || mMiuiVersion.startsWith("V8.1.3")) {
                    version = VERSION.SPECIAL;
                } else {
                    version = VERSION.COMMON;
                }
            } catch (Exception e) {

            }
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
    protected void actionPackageUsageStats() {
        super.actionPackageUsageStats();
        setEventType(Permission.PACKAGEUSAGESTATS);
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
        if (miuiPermissionActionUtil != null) {
            miuiPermissionActionUtil.clearList(permission);
        }
    }


    private Permission permission;
    private void setEventType(Permission permission){
        this.permission=permission;
    }
    public Permission getEventType(){
        return permission;
    }





    public static boolean isRedMiNote4SDK23() {
        return android.os.Build.MODEL.contains(MiuiModel.REDMI_NOTE4) && Build.VERSION.SDK_INT == 23;
    }
    public static boolean isRedMi4ASDK23() {
        return android.os.Build.MODEL.contains(MiuiModel.REDMI_4A) && Build.VERSION.SDK_INT == 23;
    }
    public static boolean isXiaoMi8SESDK27() {
        return android.os.Build.MODEL.contains(MiuiModel.MI8_SE) && Build.VERSION.SDK_INT == 27;
    }
    public static boolean isXiaoMi6XSDK27() {
        return android.os.Build.MODEL.contains(MiuiModel.MI6X) && Build.VERSION.SDK_INT == 27;
    }
    public static boolean isMIUIV6(){
        return PhoneRomUtils.isXiaoMi()&& TextUtils.equals("V6",PhoneRomUtils.getMiuiVersion());
    }
    public static boolean isMIUIV7(){
        return PhoneRomUtils.isXiaoMi()&& TextUtils.equals("V7",PhoneRomUtils.getMiuiVersion());
    }
    public static boolean isMIUIV8(){
        return PhoneRomUtils.isXiaoMi()&& TextUtils.equals("V8",PhoneRomUtils.getMiuiVersion());
    }

    public static boolean isXiaomiMiui7Sdk19() {
        return isMIUIV7() && Build.VERSION.SDK_INT == 19;
    }

    public static boolean isXiaomiMiui6Sdk19() {
        return isMIUIV7() && Build.VERSION.SDK_INT == 19;
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void configAccessbility(AccessibilityService service) {
        if (service == null) return;
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.packageNames = new String[]{
                PermissionSystemPath.ANDROID_SETTINGS,
                PermissionSystemPath.ANDROID_PACKAGE_INSTALLER_NAME,
                PermissionSystemPath.MIUI_POWERKEEPER,
                PermissionSystemPath.MIUI_SECURITYCENTER
        };
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = 1000;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;

        service.setServiceInfo(accessibilityServiceInfo);

    }
    public void getIntentFail(Context mContext,String status,Permission permission,Exception e){
        PermissionProvider.save(mContext,status, true);
        EventBus.getDefault().post(new PathEvent(permission, true, true));
    }



}
