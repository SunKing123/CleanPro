package com.hellogeek.permission.manufacturer.oppo.safe;

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
import com.hellogeek.permission.util.NotifyUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.hellogeek.permission.Integrate.Permission.*;

import static com.hellogeek.permission.util.Constant.*;

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
                oppoPermissionSafeActionUtil.actionSuspendedToast(nodeInfo, service, oppoSystemVersion);
                break;
            case BACKSTAGEPOPUP:
                break;
            case LOCKDISPALY:
                break;
            case NOTICEOFTAKEOVER:
                oppoPermissionSafeActionUtil.actionNoticeOfTakeover(nodeInfo, service, oppoSystemVersion);
                break;
            case SELFSTARTING:
                oppoPermissionSafeActionUtil.actionSelfStaring(nodeInfo, service, oppoSystemVersion);
                break;
            case SYSTEMSETTING:
                oppoPermissionSafeActionUtil.actionSystemSetting(nodeInfo, service);
                break;
            case NOTIFICATIONBAR:
                oppoPermissionSafeActionUtil.actionNotifiCationBar(nodeInfo, service);
                break;
            case REPLACEACLLPAGE:
                break;
            case PACKAGEUSAGESTATS:
                oppoPermissionColorActionUtil.actionPakageUsageStats(nodeInfo, service, null, null);
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
        Intent intent = new Intent();
        try {
            intent.setClassName(
                    PermissionSystemPath.OPPO_SAFE,
                    PermissionSystemPath.OPPO_COLORO_PERMISSIONTOP);
            startTOSysTemActivity(intent, SUSPENDEDTOAST);
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
            intent.setClassName(PermissionSystemPath.OPPO_SAFE, PermissionSystemPath.OPPO_COLORO_AUTO_PERMISSIONTOP);
            startTOSysTemActivity(intent, SELFSTARTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            getIntentFail(mContext, PROVIDER_SELFSTARTING, SELFSTARTING, e);
        } catch (SecurityException e2) {
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
        } catch (Exception e1) {
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
        } catch (Exception e1) {
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
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                startTOSysTemActivity(intent, BACKSTAGEPOPUP);
            }
        } catch (ActivityNotFoundException e) {
            getIntentFail(mContext, PROVIDER_BACKSTAGEPOPUP, BACKSTAGEPOPUP, e);
        } catch (Exception e1) {
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
        } catch (Exception e1) {
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
                case SYSTEMSETTING:
                    if (!PhoneRomUtils.isOppo1107()) {
                        getlist.add(permission);
                    }
                    break;
                case LOCKDISPALY://无

                    break;
                case SELFSTARTING:
                    getlist.add(permission);
                    break;
                case NOTICEOFTAKEOVER:
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
