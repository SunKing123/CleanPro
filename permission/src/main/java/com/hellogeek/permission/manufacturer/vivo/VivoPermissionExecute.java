package com.hellogeek.permission.manufacturer.vivo;

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
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.DialogUtil;
import com.hellogeek.permission.util.PermissionConvertUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.BACKSTAGEPOPUP;
import static com.hellogeek.permission.Integrate.Permission.LOCKDISPALY;
import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.NOTIFICATIONBAR;
import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.hellogeek.permission.Integrate.Permission.REPLACEACLLPAGE;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_1;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_2;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_3_2;
import static com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase.VERSION.VERSION_4_1;
import static com.hellogeek.permission.util.Constant.*;


public class VivoPermissionExecute extends VivoPermissionBase {
    private Context mContext;

    public VivoPermissionExecute(Context context) {
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
                vivoPermissionActionUtil.actionSuspendedToast(nodeInfo, service, vivoSystemVersion, vivoOs);
                break;
            case BACKSTAGEPOPUP:
                vivoPermissionActionUtil.actionBackstatePopUp(nodeInfo, service);
                break;
            case LOCKDISPALY:
                vivoPermissionActionUtil.actionLockDisplay(nodeInfo, service, vivoSystemVersion);
                break;
            case NOTICEOFTAKEOVER:
                vivoPermissionActionUtil.actionNoticeOfTakeover(nodeInfo, service);
                break;
            case SELFSTARTING:
                vivoPermissionActionUtil.actionSelfStaring(nodeInfo, service, vivoSystemVersion, vivoOs);
                break;
            case SYSTEMSETTING:
                vivoPermissionActionUtil.actionSystemSetting(nodeInfo, service, vivoSystemVersion);
                break;
            case NOTIFICATIONBAR:
                vivoPermissionActionUtil.actionNotifiCationBar(nodeInfo, service, vivoSystemVersion);
                break;
            case PACKAGEUSAGESTATS:
                vivoPermissionActionUtil.actionPakageUsageStats(nodeInfo, service, vivoSystemVersion, vivoOs);
                break;
        }
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        Intent intent = new Intent();
        try {
            switch (vivoSystemVersion) {
                case VERSION_3:
                case VERSION_3_2:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_MAIN_ACTIVITY);
                    break;
                case VERSION_4_1:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_SAFEGUARC_SOFTPERMISSIONDETAIL);
                    intent.setAction(PermissionSystemPath.VIVO_ACTION);
                    intent.putExtra("packagename", mContext.getPackageName());
                    break;
                case VERSION_4:
                case VERSION_4_1_8:
                case VERSION_4_2:
                case VERSION_4_4:
                case VERSION_5_2_0:
                    intent.setClassName(PermissionSystemPath.VIVO_PERMISSIONMANAGER, PermissionSystemPath.VIVO_SOFTPERMISSIONDETAIL);
                    intent.setAction(PermissionSystemPath.VIVO_ACTION);
                    intent.putExtra("packagename", mContext.getPackageName());
                    break;
            }
            if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase(VivoModel.Y21I) || PhoneRomUtils.getPhoneModel().equalsIgnoreCase(VivoModel.Y75)) {
                DialogUtil.showToastToolsDialog((Activity) mContext, new DialogUtil.CallToolsDialogDismissListener() {
                    @Override
                    public void open() {
                        startTOSysTemActivity(intent, SUSPENDEDTOAST);
                    }

                    @Override
                    public void dismiss() {
                    }
                });
            } else {
                startTOSysTemActivity(intent, SUSPENDEDTOAST);
            }
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e);
        } catch (Exception e) {
        }
    }

    @Override
    public void actionSelfStarting() {
        super.actionSelfStarting();
        Intent intent = new Intent();
        try {
            switch (vivoSystemVersion) {
                case VERSION_1:
                case VERSION_2:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_SECURE_MAINGUIDE);
                    startTOSysTemActivity(intent, SELFSTARTING);

                    break;
                case VERSION_3:
                case VERSION_3_2:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_MAIN_ACTIVITY);
                    startTOSysTemActivity(intent, SELFSTARTING);

                    break;
                case VERSION_4_1:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_SAFEGUARC_SOFTPERMISSIONDETAIL);
                    intent.setAction(PermissionSystemPath.VIVO_ACTION);
                    intent.putExtra("packagename", mContext.getPackageName());
                    startTOSysTemActivity(intent, SELFSTARTING);

                    break;
                case VERSION_4:
                case VERSION_4_1_8:
                case VERSION_4_2:
                case VERSION_4_4:
                case VERSION_5_2_0:
//                    if ((Build.MODEL.contains(VivoModel.Y85) && !Build.MODEL.contains(VivoModel.Y85A)) || Build.MODEL.contains(VivoModel.Y53L) || Build.MODEL.contains(VivoModel.X21)|| Build.MODEL.contains(VivoModel.X9s)) {
                    try {
                        intent.setClassName(PermissionSystemPath.VIVO_PERMISSIONMANAGER, PermissionSystemPath.VIVO_PURVIEWTAB);
                        intent.putExtra("packagename", mContext.getPackageName());
                        intent.putExtra("tabId", "1");
                        startTOSysTemActivity(intent, SELFSTARTING);
                    } catch (Exception e) {
//                    } else {
                        intent.setClassName(PermissionSystemPath.VIVO_PERMISSIONMANAGER, PermissionSystemPath.VIVO_SOFTPERMISSIONDETAIL);
                        intent.setAction(PermissionSystemPath.VIVO_ACTION);
                        intent.putExtra("packagename", mContext.getPackageName());
                        startTOSysTemActivity(intent, SELFSTARTING);

                    }
//                    }
                    break;
            }

        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_SELFSTARTING, SELFSTARTING, e);
        } catch (Exception e) {
        }


    }


    @Override
    protected void actionSystemSetting() {
        super.actionSystemSetting();
        jumpSystemSetting(mContext);
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
        Intent intent = new Intent();
        try {
            switch (vivoSystemVersion) {
                case VERSION_3_2:
                    //设置—-状态栏与通知
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setClassName(PermissionSystemPath.VIVO_SYSTEM_UI, PermissionSystemPath.VIVO_STATUSBARSETTING);
                    break;
                case VERSION_4:
                case VERSION_4_1_8:
                case VERSION_4_1:
                case VERSION_4_2:
                case VERSION_4_4:
                case VERSION_5_2_0:
                    intent.setAction(PermissionSystemPath.ANDROID_SETTINGS_NOTICE);
                    intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                    break;
            }
            startTOSysTemActivity(intent, NOTIFICATIONBAR);

        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_NOTIFICATIONBAR, NOTIFICATIONBAR, e);

        } catch (Exception e) {
        }
    }

    @Override
    protected void actionLockDisplay() {
        super.actionLockDisplay();
        Intent intent = new Intent();
        try {
            switch (vivoSystemVersion) {
                case VERSION_3_2:
                    intent.setClassName(PermissionSystemPath.VIVO_PHONE_MANAGER, PermissionSystemPath.VIVO_MAIN_ACTIVITY);
                    break;
                default:
                    intent.setClassName(PermissionSystemPath.VIVO_PERMISSIONMANAGER, PermissionSystemPath.VIVO_SOFTPERMISSIONDETAIL);
                    intent.setAction(PermissionSystemPath.VIVO_ACTION);
                    intent.putExtra("packagename", mContext.getPackageName());
            }
            startTOSysTemActivity(intent, LOCKDISPALY);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_LOCKDISPLAY, LOCKDISPALY, e);
        } catch (Exception e) {
        }


    }

    @Override
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
        Intent intent = new Intent();
        try {
            intent.setClassName(PermissionSystemPath.VIVO_PERMISSIONMANAGER, PermissionSystemPath.VIVO_SOFTPERMISSIONDETAIL);
            intent.setAction(PermissionSystemPath.VIVO_ACTION);
            intent.putExtra("packagename", mContext.getPackageName());
            startTOSysTemActivity(intent, BACKSTAGEPOPUP);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_BACKSTAGEPOPUP, BACKSTAGEPOPUP, e);
        } catch (Exception e) {
        }

    }

    @Override
    protected void actionNoticeOfTakeover() {
        super.actionNoticeOfTakeover();
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startTOSysTemActivity(intent, NOTICEOFTAKEOVER);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
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
                    if (vivoSystemVersion != VERSION_1 && vivoSystemVersion != VERSION_2) {
                        getlist.add(permission);
                    }
                    break;
                case LOCKDISPALY:
                    if (vivoSystemVersion != VERSION_2 && vivoSystemVersion != VERSION_1 && vivoSystemVersion != VERSION_4_1 && !(vivoSystemVersion == VERSION_3_2 && (Build.MODEL.contains(VivoModel.Y51A) || Build.MODEL.contains(VivoModel.X7)))) {
                        getlist.add(permission);
                    }
                    break;
                case SELFSTARTING:
                    if (!PermissionIntegrate.getPermission().getIsNecessary()) {
                        getlist.add(permission);
                    }
                    break;

                case NOTICEOFTAKEOVER:
                    getlist.add(permission);
                    break;
                case BACKSTAGEPOPUP:
                    if (isBACKSHOW()) {
                        getlist.add(permission);
                    }
                    break;
                case SYSTEMSETTING:
                    if (vivoSystemVersion != VERSION_2 && vivoSystemVersion != VERSION_1 && !PhoneRomUtils.isVivoX7SDK22()) {
                        getlist.add(permission);
                    }
                    break;
                case NOTIFICATIONBAR:
                    if (vivoSystemVersion != VERSION_2 && vivoSystemVersion != VERSION_1
                            && vivoSystemVersion != VERSION_3_2 && vivoSystemVersion != VERSION_4_1) {
                        getlist.add(permission);
                    }
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
        ((Activity) mContext).startActivityForResult(intent, permission.getRequestCode());
    }
}
