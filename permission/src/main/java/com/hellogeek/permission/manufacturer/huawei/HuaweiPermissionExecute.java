package com.hellogeek.permission.manufacturer.huawei;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.NOTIFICATIONBAR;
import static com.hellogeek.permission.Integrate.Permission.PACKAGEUSAGESTATS;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;
import static com.hellogeek.permission.util.Constant.*;

public class HuaweiPermissionExecute extends HuaweiPermissionBase {
    private Context mContext;

    public HuaweiPermissionExecute(Context context) {
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
                huaweiPermissionActionUtil.actionSuspendedToast(nodeInfo, service);
                break;
            case BACKSTAGEPOPUP:
                break;
            case LOCKDISPALY:
                huaweiPermissionActionUtil.actionLockDisplay(nodeInfo, service);
                break;
            case NOTICEOFTAKEOVER:
                huaweiPermissionActionUtil.actionNoticeOfTakeover(nodeInfo, service);
                break;
            case REPLACEACLLPAGE:
                huaweiPermissionActionUtil.actionRepLaceAcllPage(nodeInfo, service);
                break;
            case SELFSTARTING:
                huaweiPermissionActionUtil.actionSelfStaring(nodeInfo, service);
                break;
            case SYSTEMSETTING:
                huaweiPermissionActionUtil.actionSystemSetting(nodeInfo, service);
                break;
            case PACKAGEUSAGESTATS:
                huaweiPermissionActionUtil.actionPakageUsageStats(nodeInfo, service, null, null);
                break;
        }
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        Intent intent = new Intent();
        try {
            if (isHuaweiTAGAL00() || isHuaweiP7L00SDK22()) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION);
            } else if (isHuaweiKIWAL10SDK23() || isHuaweiPLKAL10SDK23() || Build.VERSION.SDK_INT == 23) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION);
            } else if (Build.VERSION.SDK_INT == 24) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= 26) {
                intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            } else {
                if (huweiSystemVersion == 1) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION_19_C2D2_1);
                } else if (huweiSystemVersion == 3) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION_19_C2D2_3);
                } else if (mVersionNum == 900) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION_UI_MAIN);
                } else {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION);
                }
            }
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
        } catch (Exception e) {
            try {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION_SYS_MAIN);
                startTOSysTemActivity(intent, SUSPENDEDTOAST);
            } catch (ActivityNotFoundException e1) {
                getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e1);
            } catch (Exception e2) {
            }

        }
    }

    @Override
    public void actionSelfStarting() {
        super.actionSelfStarting();
        Intent intent = new Intent();
        try {
            if (isHuaweiTAGAL00() || isHuaweiP7L00SDK22()) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION);
                startTOSysTemActivity(intent, SELFSTARTING);
            } else if (isHuaweiKIWAL10SDK23() || isHuaweiPLKAL10SDK23() || Build.VERSION.SDK_INT == 23) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION24);
                startTOSysTemActivity(intent, SELFSTARTING);
            } else if (Build.VERSION.SDK_INT == 24) {
                intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                        PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION24);
                startTOSysTemActivity(intent, SELFSTARTING);
            } else if (Build.VERSION.SDK_INT >= 26) {
                try {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION26);
                    startTOSysTemActivity(intent, SELFSTARTING);
                } catch (Exception e) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION24);
                    startTOSysTemActivity(intent, SELFSTARTING);
                }
            } else {
                if (huweiSystemVersion == 4 || huweiSystemVersion == 5
                        || isHuaweiFRDAL00()/* mVersion == 6.*/) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION24);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else if (huweiSystemVersion == 6) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_MANAGEAPPLICATION_PERMISSION_UI_MAIN);
                } else if (huweiSystemVersion == 9) {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION26);
                } else {
                    intent.setClassName(PermissionSystemPath.HUAWEI_SETTING_MANAGE,
                            PermissionSystemPath.HUAWEI_SELF_STARTING_PERMISSION);
                }
                startTOSysTemActivity(intent, SELFSTARTING);
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
    public ArrayList<Permission> getPermissionList() {
        ArrayList<Permission> permissionArrayList = PermissionIntegrate.getPermission().getPermissionList();
        ArrayList<Permission> getlist = new ArrayList<>();
        for (Permission permission : permissionArrayList) {
            switch (permission) {
                case SUSPENDEDTOAST:
                    getlist.add(permission);
                    break;
                case SELFSTARTING:
                    if (!PermissionIntegrate.getPermission().getIsNecessary()) {
                        getlist.add(permission);
                    }
                    break;
                case SYSTEMSETTING:
                    if (!isHuaweiTAGAL00() && !isHuaweiP7L00SDK22() && !Build.MODEL.contains(HuaweiModel.CHE1_CL20)) {
                        getlist.add(permission);
                    }
                    break;
                case NOTICEOFTAKEOVER:
                    getlist.add(permission);
                    break;
                case REPLACEACLLPAGE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < 29) {
                        getlist.add(permission);
                    }
                    break;
                case PACKAGEUSAGESTATS:
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   // 如果大于等于5.0 有此权限
                        getlist.add(permission);
                    }
                    break;
            }
        }
        return getlist;
    }


    @Override
    protected void actionRepLaceAcllpage() {
        super.actionRepLaceAcllpage();

        Intent intent = new Intent();
        try {
            if (isHuaweiKIWAL10SDK23() || isHuaweiPLKAL10SDK23() || Build.VERSION.SDK_INT == 23 || Build.VERSION.SDK_INT == 24) {
                intent.setClassName(PermissionSystemPath.ANDROID_SETTINGS,
                        PermissionSystemPath.HUAWEI_SYSTEM_SETTING_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= 25) {
                intent.setClassName(PermissionSystemPath.ANDROID_SETTINGS,
                        PermissionSystemPath.HUAWEI_SYSTEM_SETTING_PERMISSION26);
            }
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_SUSPENDEDTOAST, SUSPENDEDTOAST, e);
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
            startTOSysTemActivity(intent, NOTIFICATIONBAR);
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_NOTIFICATIONBAR, NOTIFICATIONBAR, e);
        } catch (Exception e) {
        }
    }


    /***
     * 封装系统权限页跳转。如果以后有需要在跳转页加埋点可以在这加
     *
     * **/
    private void startTOSysTemActivity(Intent intent, Permission permission) {
        ((Activity) mContext).startActivityForResult(intent, permission.getRequestCode() != null ? permission.getRequestCode() : 0);
    }
}
