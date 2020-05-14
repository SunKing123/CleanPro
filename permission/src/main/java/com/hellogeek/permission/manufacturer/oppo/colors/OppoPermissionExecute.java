package com.hellogeek.permission.manufacturer.oppo.colors;

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
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.manufacturer.oppo.OppoModel;
import com.hellogeek.permission.manufacturer.oppo.OppoPermissionBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.BACKSTAGEPOPUP;
import static com.hellogeek.permission.Integrate.Permission.NOTICEOFTAKEOVER;
import static com.hellogeek.permission.Integrate.Permission.NOTIFICATIONBAR;
import static com.hellogeek.permission.Integrate.Permission.REPLACEACLLPAGE;
import static com.hellogeek.permission.Integrate.Permission.SELFSTARTING;
import static com.hellogeek.permission.Integrate.Permission.SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.PROVIDER_BACKSTAGEPOPUP;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;
import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;

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
                oppoPermissionActionUtil.actionSuspendedToast(nodeInfo, service, oppoSystemVersion);
                break;
            case BACKSTAGEPOPUP:
                break;
            case LOCKDISPALY:
                break;
            case NOTICEOFTAKEOVER:
                oppoPermissionActionUtil.actionNoticeOfTakeover(nodeInfo, service, oppoSystemVersion);
                break;
            case SELFSTARTING:
                oppoPermissionActionUtil.actionSelfStaring(nodeInfo, service, oppoSystemVersion);
                break;
            case SYSTEMSETTING:
                oppoPermissionActionUtil.actionSystemSetting(nodeInfo, service);
                break;
            case NOTIFICATIONBAR:
                oppoPermissionActionUtil.actionNotifiCationBar(nodeInfo, service);
                break;
            case REPLACEACLLPAGE:
                break;
        }
    }

    @Override
    protected void actionSuspendedToast() {
        super.actionSuspendedToast();
        Intent intent = new Intent();
        try {
            if (oppoSystemVersion == VERSION.V1) {
                intent.setClassName(
                        PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME,
                        PermissionSystemPath.OPPO_COLOROS_FLOATWINDOW);
            } else if (oppoSystemVersion == VERSION.V2 || oppoSystemVersion == VERSION.V3 || oppoSystemVersion == VERSION.V5) {
                intent.setClassName(PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOROS_SYSFLOATWINDOW);
            } else if (oppoSystemVersion == VERSION.V4 || oppoSystemVersion == VERSION.V6 || oppoSystemVersion == VERSION.V6_1 || oppoSystemVersion == VERSION.V6_2
                    || oppoSystemVersion == VERSION.V7 || oppoSystemVersion == VERSION.V7_1 || oppoSystemVersion == VERSION.V8) {
                intent.setClassName(PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOROS_PERMISSIONTOP);
            }
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
        } catch (Exception e3) {
            try {
                Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
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
            if (oppoSystemVersion == VERSION.V1) {
                intent.setClassName(
                        PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME,
                        PermissionSystemPath.OPPO_COLOROS_SSTARTAPPLIST);

            } else if (oppoSystemVersion == VERSION.V2 || oppoSystemVersion == VERSION.V3 || oppoSystemVersion == VERSION.V5) {
                intent.setClassName(PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOROS_STARTAPPLIST);
            } else if (oppoSystemVersion == VERSION.V4 || oppoSystemVersion == VERSION.V6 || oppoSystemVersion == VERSION.V6_1 || oppoSystemVersion == VERSION.V6_2
                    || oppoSystemVersion == VERSION.V7 || oppoSystemVersion == VERSION.V7_1 || oppoSystemVersion == VERSION.V8) {
                intent.setClassName(PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME, PermissionSystemPath.OPPO_COLOROS_PERMISSIONTOP);
            }
            startTOSysTemActivity(intent, SELFSTARTING);
        } catch (Exception e) {
            try {
                intent.setAction(PermissionSystemPath.ANDROID_SETTINGS_NOTICE);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                startTOSysTemActivity(intent, SELFSTARTING);
            } catch (ActivityNotFoundException e1) {
                getIntentFail(mContext, PROVIDER_SELFSTARTING, SELFSTARTING, e1);
            } catch (Exception e2) {
            }
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
            if ((oppoSystemVersion == VERSION.V7_1 && !TextUtils.equals(Build.MODEL, OppoModel.A37m) && !TextUtils.equals(Build.MODEL, OppoModel.R9s)) || oppoSystemVersion == VERSION.V6_1) {
                try {
                    intent.setClassName(PermissionSystemPath.ANDROID_SETTINGS, "com.android.settings.Settings$ConfigureNotificationSettingsActivity");
                } catch (Exception e) {
                }
            } else if (TextUtils.equals(Build.MODEL, OppoModel.A37m) || TextUtils.equals(Build.MODEL, OppoModel.R9s) || oppoSystemVersion == VERSION.V7 || oppoSystemVersion == VERSION.V1 || oppoSystemVersion == VERSION.V2) {
                try {
                    intent.setClassName(PermissionSystemPath.ANDROID_SETTINGS, "com.oppo.settings.SettingsActivity");
                } catch (Exception e) {

                }
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
    }

    @Override
    protected void actionBackstatePopUp() {
        super.actionBackstatePopUp();
        Intent intent = new Intent();
        try {

            if (oppoSystemVersion == VERSION.V7) {
//        PackageManager packageManager = mContext.getPackageManager();
                String packageName = mContext.getPackageName();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                startTOSysTemActivity(intent, BACKSTAGEPOPUP);
            }
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_BACKSTAGEPOPUP, BACKSTAGEPOPUP, e);
        } catch (Exception e) {
        }

    }

    @Override
    protected void actionNoticeOfTakeover() {
        super.actionNoticeOfTakeover();
        Intent intent = new Intent();
        try {
            if (oppoSystemVersion == VERSION.V2 || oppoSystemVersion == VERSION.V6_2
                    || oppoSystemVersion == VERSION.V7 || oppoSystemVersion == VERSION.V7_1) {
                intent.setClassName(PermissionSystemPath.ANDROID_SETTINGS, PermissionSystemPath.OPPO_COLOROS_SETTINGNOTIFICATION);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
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
                case LOCKDISPALY://无

                    break;
                case SELFSTARTING:
                    getlist.add(permission);
                    break;
                case SYSTEMSETTING:
                    if (!PhoneRomUtils.isOppoR9TmSDK22() && !PhoneRomUtils.isOppoA37m() && !PhoneRomUtils.isOppoR9m()) {
                        getlist.add(permission);
                    }
                    break;
                case NOTICEOFTAKEOVER:
                    getlist.add(permission);
                    break;
                case BACKSTAGEPOPUP://无
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
