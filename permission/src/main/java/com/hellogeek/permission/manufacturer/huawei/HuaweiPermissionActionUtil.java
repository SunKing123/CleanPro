package com.hellogeek.permission.manufacturer.huawei;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.NoticeOfTakeoverPermission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.RepLaceAcllPagePermission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.SelfStartingPermission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.SuspendedToastPermission;
import com.hellogeek.permission.manufacturer.huawei.permissionlist.SystemSettingPermission;
import com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.PakageUsageStatsPermission;

import static com.hellogeek.permission.manufacturer.huawei.HuaweiPermissionBase.isHuaweiKIWAL10SDK23;
import static com.hellogeek.permission.manufacturer.huawei.HuaweiPermissionBase.isHuaweiPLKAL10SDK23;

public class HuaweiPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;
    private RepLaceAcllPagePermission repLaceAcllPagePermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;
    private PakageUsageStatsPermission pakageUsageStatsPermission;

    public HuaweiPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        repLaceAcllPagePermission = new RepLaceAcllPagePermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
        pakageUsageStatsPermission = new PakageUsageStatsPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service) {
        if (Build.VERSION.SDK_INT >= 26) {
            suspendedToastPermission.openSuspendedToast26(mContext, info, service);
        } else {
            suspendedToastPermission.openSuspendedToastOther(mContext, info, service);
        }
    }

    /**
     * 锁屏权限--华为无此权限
     */
    public void actionLockDisplay(AccessibilityNodeInfo info, AccessibilityService service) {

    }

    /**
     * 锁屏权限--华为无此权限
     */
    public void actionRepLaceAcllPage(AccessibilityNodeInfo info, AccessibilityService service) {
        if (isHuaweiKIWAL10SDK23() || isHuaweiPLKAL10SDK23() || Build.VERSION.SDK_INT == 23 || Build.VERSION.SDK_INT == 24) {
            repLaceAcllPagePermission.openRepLaceAcllPage(mContext, info, service);
        } else if (Build.VERSION.SDK_INT >= 29) {
            repLaceAcllPagePermission.openRepLaceAcllPage29(mContext, info, service);
        } else {
            repLaceAcllPagePermission.openRepLaceAcllPage26(mContext, info, service);
        }
    }


    /**
     * 系统设置
     */
    public void actionSystemSetting(AccessibilityNodeInfo info, AccessibilityService service) {
        systemSettingPermission.openSystemSetting(mContext, info, service);
    }

    /**
     * 接管通知
     */
    public void actionNoticeOfTakeover(AccessibilityNodeInfo info, AccessibilityService service) {
        noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
    }

    /**
     * 通知栏
     */
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service) {

    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service) {
        if (Build.VERSION.SDK_INT == 24) {
            selfStartingPermission.openSelfStarting24(mContext, info, service);
        } else if (isHuaweiKIWAL10SDK23() || isHuaweiPLKAL10SDK23() || Build.VERSION.SDK_INT == 23) {
            selfStartingPermission.openSelfStarting23(mContext, info, service);
        } else {
            selfStartingPermission.openSelfStarting(mContext, info, service);
        }
    }


    public void actionPakageUsageStats(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version, String vivoOs) {
        pakageUsageStatsPermission.openPakageUsageStats(mContext, info, service);
    }

    public void clearList(Permission permission) {
        switch (permission) {
            case SUSPENDEDTOAST:
                suspendedToastPermission.clearList();
                break;
            case SELFSTARTING:
                selfStartingPermission.clearList();
                break;
            case LOCKDISPALY:
                break;
            case BACKSTAGEPOPUP:
                break;
            case SYSTEMSETTING:
                systemSettingPermission.clearList();
                break;
            case REPLACEACLLPAGE:
                repLaceAcllPagePermission.clearList();
                break;
            case NOTIFICATIONBAR:
                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }

}
