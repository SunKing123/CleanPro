package com.hellogeek.permission.manufacturer.other;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NotifyUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

public class OtherPermissionBase extends AutoFixAction {
    protected OtherPermissionActionUtil otherPermissionActionUtil;
    private static String mMiuiVersion;
    public String extraPackname = "extra_pkgname";

    public OtherPermissionBase(Context context) {
        otherPermissionActionUtil = new OtherPermissionActionUtil(context);

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
        if (otherPermissionActionUtil != null) {
            otherPermissionActionUtil.clearList(permission);
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
                "com.meizu.safe",
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
