package com.hellogeek.permission.manufacturer.other;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.util.NotifyUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.BACKSTAGEPOPUP;
import static com.hellogeek.permission.Integrate.Permission.LOCKDISPALY;
import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.hellogeek.permission.Integrate.Permission.REPLACEACLLPAGE;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.PACKAGE_USAGE_STATS;
import static com.hellogeek.permission.util.Constant.PROVIDER_BACKSTAGEPOPUP;
import static com.hellogeek.permission.util.Constant.PROVIDER_LOCKDISPLAY;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;
import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;

public class OtherPermissionExecute extends OtherPermissionBase {
    private Context mContext;

    public OtherPermissionExecute(Context context) {
        super(context);
        mContext = context;
    }


    @SuppressLint("NewApi")
    @Override
    public void permissionHandlerEvent(AccessibilityService service, AccessibilityEvent event) {
        super.permissionHandlerEvent(service, event);
        AccessibilityNodeInfo nodeInfo = null;
        if (service != null) {
            nodeInfo = service.getRootInActiveWindow();
        }
        if (nodeInfo == null) {
            return;
        }
        Permission permission = getEventType();
        if (permission == null) {
            return;
        }
        switch (permission) {
            case SUSPENDEDTOAST:
                otherPermissionActionUtil.actionSuspendedToast(nodeInfo, service);
                break;
            case BACKSTAGEPOPUP:
                otherPermissionActionUtil.actionBackstatePopUp(nodeInfo, service);
                break;
            case LOCKDISPALY:
                otherPermissionActionUtil.actionLockDisplay(nodeInfo, service);
                break;
            case NOTICEOFTAKEOVER:
                otherPermissionActionUtil.actionNoticeOfTakeover(nodeInfo, service);
                break;
            case SELFSTARTING:
                otherPermissionActionUtil.actionSelfStaring(nodeInfo, service);
                break;
            case SYSTEMSETTING:
                otherPermissionActionUtil.actionSystemSetting(nodeInfo, service);
                break;
            case PACKAGEUSAGESTATS:
                otherPermissionActionUtil.actionPakageUsageStats(nodeInfo, service, null, null);
                break;
        }
    }

    protected void actionNotificationRead() {
        super.actionNotificationRead();
        NotifyUtils.openNotificationListenerSettings(mContext);
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        try {
            if (PhoneRomUtils.is360()) {
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startTOSysTemActivity(intent, SUSPENDEDTOAST);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                startTOSysTemActivity(intent, SUSPENDEDTOAST);
            }
        } catch (Exception e1) {
            getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e1);
        }
    }


    @Override
    protected void actionSystemSetting() {
        super.actionSystemSetting();
        jumpSystemSetting(mContext);
    }

    protected void actionPackageUsageStats() {
        super.actionPackageUsageStats();
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startTOSysTemActivity(intent, PACKAGEUSAGESTATS);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            getIntentFail(mContext, PACKAGE_USAGE_STATS, PACKAGEUSAGESTATS, e);
        }

    }


    @Override
    protected void actionNoticeOfTakeover() {
        super.actionNoticeOfTakeover();
        try {

            Intent intent = new Intent(PermissionSystemPath.HUAWEI_CALLPHONEDIANO_PERMISSION);
            startTOSysTemActivity(intent, NOTICEOFTAKEOVER);
        } catch (Exception e) {
            getIntentFail(mContext, PROVIDER_NOTICEOFTAKEOVER, NOTICEOFTAKEOVER, e);
        }
    }

    @Override
    public ArrayList<Permission> getPermissionList() {
        ArrayList<Permission> permissionArrayList = PermissionIntegrate.getPermission().getPermissionList();
        ArrayList<Permission> getlist = new ArrayList<>();
        for (Permission permission : permissionArrayList) {
            switch (permission) {
                case SUSPENDEDTOAST:
                    getlist.add(permission);
                    break;
                case SYSTEMSETTING:
                    getlist.add(permission);
                    break;
                case NOTICEOFTAKEOVER:
                    getlist.add(permission);
                    break;
                case PACKAGEUSAGESTATS:
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   // 如果大于等于5.0 有此权限
                        getlist.add(permission);
                    }
                    break;
                case NOTIFICATIONREAD:
                    if (Build.VERSION.SDK_INT >= 19) {
                        getlist.add(permission);
                    }
                    break;
            }
        }
        return getlist;
    }

    /***
     * 封装系统权限页跳转。如果以后有需要在跳转页加埋点可以在这加
     *
     * **/
    private void startTOSysTemActivity(Intent intent, Permission permission) {
        ((Activity) mContext).startActivityForResult(intent, permission.getRequestCode() != null ? permission.getRequestCode() : 0);
    }
}
