package com.hellogeek.permission.manufacturer.miui;

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

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.BACKSTAGEPOPUP;
import static com.hellogeek.permission.Integrate.Permission.LOCKDISPALY;
import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.hellogeek.permission.Integrate.Permission.REPLACEACLLPAGE;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.*;

public class MiuiPermissionExecute extends MIUIPermissionBase {
    private Context mContext;

    public MiuiPermissionExecute(Context context) {
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
                miuiPermissionActionUtil.actionSuspendedToast(nodeInfo, service);
                break;
            case BACKSTAGEPOPUP:
                miuiPermissionActionUtil.actionBackstatePopUp(nodeInfo, service);
                break;
            case LOCKDISPALY:
                miuiPermissionActionUtil.actionLockDisplay(nodeInfo, service);
                break;
            case NOTICEOFTAKEOVER:
                miuiPermissionActionUtil.actionNoticeOfTakeover(nodeInfo, service);
                break;
            case SELFSTARTING:
                miuiPermissionActionUtil.actionSelfStaring(nodeInfo, service);
                break;
            case SYSTEMSETTING:
                miuiPermissionActionUtil.actionSystemSetting(nodeInfo, service, isToSystem);
                break;
            case PACKAGEUSAGESTATS:
                miuiPermissionActionUtil.actionPakageUsageStats(nodeInfo, service, null, null);
                break;
        }
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        Intent intent = new Intent();
        try {
            if (miuiSystemVersion == VERSION.SPECIAL_2 || miuiSystemVersion == VERSION.SPECIAL_9_1_3 || miuiSystemVersion == VERSION.SPECIAL_9_2 || miuiSystemVersion == VERSION.SPECIAL_9_2_2 || miuiSystemVersion == VERSION.SPECIAL_9_5 || miuiSystemVersion == VERSION.SPECIAL_9_6 || miuiSystemVersion == VERSION.SPECIAL_9_1) {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            } else {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
            }
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
        } catch (ActivityNotFoundException e) {
            try {

                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
                startTOSysTemActivity(intent, SUSPENDEDTOAST);
            } catch (ActivityNotFoundException e1) {
                getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e1);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void actionSelfStarting() {
        super.actionSelfStarting();
        Intent intent = new Intent();
        try {
            intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_AUTOSTARTMANAGEMENT);
            startTOSysTemActivity(intent, SELFSTARTING);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_SELFSTARTING, SELFSTARTING, e);
        } catch (Exception e) {
        }

    }

    private boolean isToSystem = false;


    @Override
    protected void actionSystemSetting() {
        super.actionSystemSetting();
        Intent intent = new Intent();
        try {
            isToSystem = false;
            intent.putExtra("extra_pkgname", mContext.getPackageName());
            intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            mContext.startActivity(intent);
        } catch (Exception e) {
            isToSystem = true;
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivity(intent);
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
        Intent intent = new Intent();
        try {
            if (miuiSystemVersion == VERSION.SPECIAL_2 || miuiSystemVersion == VERSION.SPECIAL_9_1_3 || miuiSystemVersion == VERSION.SPECIAL_9_2 || miuiSystemVersion == VERSION.SPECIAL_9_2_2 || miuiSystemVersion == VERSION.SPECIAL_9_5 || miuiSystemVersion == VERSION.SPECIAL_9_6 || miuiSystemVersion == VERSION.SPECIAL_9_1) {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            } else {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
            }
            startTOSysTemActivity(intent, LOCKDISPALY);
        } catch (ActivityNotFoundException e) {
            try {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
                startTOSysTemActivity(intent, LOCKDISPALY);
            } catch (ActivityNotFoundException e1) {
                getIntentFail(mContext, PROVIDER_LOCKDISPLAY, LOCKDISPALY, e1);
            }
        } catch (Exception e) {
        }


    }

    @Override
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
        Intent intent = new Intent();
        try {
            if (miuiSystemVersion == VERSION.SPECIAL_2 || miuiSystemVersion == VERSION.SPECIAL_9_1_3 || miuiSystemVersion == VERSION.SPECIAL_9_2 || miuiSystemVersion == VERSION.SPECIAL_9_2_2 || miuiSystemVersion == VERSION.SPECIAL_9_5 || miuiSystemVersion == VERSION.SPECIAL_9_6 || miuiSystemVersion == VERSION.SPECIAL_9_1) {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            } else {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
            }
            startTOSysTemActivity(intent, BACKSTAGEPOPUP);
        } catch (ActivityNotFoundException e) {
            try {
                intent.putExtra(extraPackname, mContext.getPackageName());
                intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_APPPERMISSIONEDITORACTIVITY);
                startTOSysTemActivity(intent, BACKSTAGEPOPUP);
            } catch (ActivityNotFoundException e1) {
                getIntentFail(mContext, PROVIDER_BACKSTAGEPOPUP, BACKSTAGEPOPUP, e1);
            }
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

    @Override
    public ArrayList<Permission> getPermissionList() {
        ArrayList<Permission> permissionArrayList = PermissionIntegrate.getPermission().getPermissionList();
        ArrayList<Permission> getlist = new ArrayList<>();
        for (Permission permission : permissionArrayList) {
            switch (permission) {
                case SUSPENDEDTOAST:
                    getlist.add(permission);
                    break;
                case LOCKDISPALY:
                    if (!isXiaomiMiui7Sdk19() && !isXiaomiMiui6Sdk19()) {
                        getlist.add(permission);
                    }
                    break;
                case SELFSTARTING:
                    if (!PermissionIntegrate.getPermission().getIsNecessary()) {
                        getlist.add(permission);
                    }
                    break;
                case SYSTEMSETTING:
                    if (!isXiaomiMiui7Sdk19() && !isXiaomiMiui6Sdk19()) {
                        getlist.add(permission);
                    }
                    break;
                case NOTICEOFTAKEOVER:
                    getlist.add(permission);
                    break;
                case BACKSTAGEPOPUP:
                    getlist.add(permission);
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
