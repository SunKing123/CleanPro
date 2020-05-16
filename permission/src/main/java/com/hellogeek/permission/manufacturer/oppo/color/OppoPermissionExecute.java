package com.hellogeek.permission.manufacturer.oppo.color;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.manufacturer.oppo.OppoPermissionBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.hellogeek.permission.Integrate.Permission.REPLACEACLLPAGE;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.Integrate.Permission.SYSTEMSETTING;
import static com.hellogeek.permission.util.Constant.*;
import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;

public class OppoPermissionExecute extends OppoPermissionBase {
    private Context mContext;

    public OppoPermissionExecute(Context context) {
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
                oppoPermissionColorActionUtil.actionSuspendedToast(nodeInfo, service, oppoSystemVersion);
                break;
            case BACKSTAGEPOPUP:
                break;
            case LOCKDISPALY:
                break;
            case NOTICEOFTAKEOVER:
                oppoPermissionColorActionUtil.actionNoticeOfTakeover(nodeInfo, service, oppoSystemVersion);
                break;
            case SELFSTARTING:
                oppoPermissionColorActionUtil.actionSelfStaring(nodeInfo, service, oppoSystemVersion);
                break;
            case SYSTEMSETTING:
                oppoPermissionColorActionUtil.actionSystemSetting(nodeInfo, service);
                break;
            case NOTIFICATIONBAR:
                oppoPermissionColorActionUtil.actionNotifiCationBar(nodeInfo, service);
                break;
            case REPLACEACLLPAGE:
                break;
            case PACKAGEUSAGESTATS:
                oppoPermissionColorActionUtil.actionPakageUsageStats(nodeInfo, service, null, null);
                break;
        }
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        Intent intent = new Intent();
        try {
            if (oppoSystemVersion == VERSION.V16) {
                Intent sysIntent = new Intent();
                sysIntent.setClassName(PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOR_PERMISSIONMANAGER);
            } else if (oppoSystemVersion == VERSION.V_UNDEFINED) {
                Intent sysIntent = new Intent();
                sysIntent.setClassName(PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOR_PERMISSIONMANAGER);
            } else {
                Intent sysIntent = new Intent();
                sysIntent.setClassName(PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME, PermissionSystemPath.OPPO_TOAST_ACTIVITY_NAMES);
            }
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void actionSelfStarting() {
        super.actionSelfStarting();
        Intent intent = new Intent();
        try {
            Intent sysIntent = new Intent();
            sysIntent.setClassName(PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME, PermissionSystemPath.OPPO_AUTO_ACTIVITY_NAMES);
            startTOSysTemActivity(intent, SELFSTARTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            getIntentFail(mContext, PROVIDER_SELFSTARTING, SELFSTARTING, e);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }


    @Override
    protected void actionSystemSetting() {
        super.actionSystemSetting();
        try {
            jumpOppoSystemSetting(mContext);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_SYSTEMSETTING, SYSTEMSETTING, e);
        } catch (Exception e) {

        }
    }


    @Override
    protected void actionRepLaceAcllpage() {
        super.actionRepLaceAcllpage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Intent intent = new Intent();
            intent.setAction(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, mContext.getPackageName());
            startTOSysTemActivity(intent, REPLACEACLLPAGE);
        }
    }

    @Override
    protected void actionNotifiCationBar() {
        super.actionNotifiCationBar();
    }

    @Override
    protected void actionLockDisplay() {
        super.actionLockDisplay();
    }

    @Override
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
    }

    @Override
    protected void actionNoticeOfTakeover() {
        super.actionNoticeOfTakeover();
        try {
            Intent intent = new Intent(PermissionSystemPath.HUAWEI_CALLPHONEDIANO_PERMISSION);
            startTOSysTemActivity(intent, NOTICEOFTAKEOVER);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_NOTICEOFTAKEOVER, NOTICEOFTAKEOVER, e);
        } catch (Exception e) {

        }
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
    public ArrayList<Permission> getPermissionList() {
        ArrayList<Permission> permissionArrayList = PermissionIntegrate.getPermission().getPermissionList();
        ArrayList<Permission> getlist = new ArrayList<>();
        for (Permission permission : permissionArrayList) {
            switch (permission) {
                case SUSPENDEDTOAST:
                    if (oppoSystemVersion != VERSION.V17) {
                        getlist.add(permission);
                    }
                    break;
                case NOTICEOFTAKEOVER:
                    if (oppoSystemVersion == VERSION.V17 || oppoSystemVersion == VERSION.V_UNDEFINED) {
                        getlist.add(permission);
                    }
                    break;
                case SYSTEMSETTING:
                    if (!PhoneRomUtils.isOppoA33SDK22() && !PhoneRomUtils.isOppoR7()) {
                        getlist.add(permission);
                    }
                    break;
                case SELFSTARTING:
                    getlist.add(permission);
                    break;
                case LOCKDISPALY://无
                    break;
                case BACKSTAGEPOPUP://无
                    break;

                case PACKAGEUSAGESTATS:
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   // 如果大于等于5.0 有此权限
                        getlist.add(permission);
                    }
                    break;
//                case REPLACEACLLPAGE:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        getlist.add(permission);
//                    }
//                    break;
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
