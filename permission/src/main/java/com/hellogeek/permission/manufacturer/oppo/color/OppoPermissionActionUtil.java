package com.hellogeek.permission.manufacturer.oppo.color;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.oppo.OppoPermissionBase;
import com.hellogeek.permission.manufacturer.oppo.color.permissionlist.*;
import com.hellogeek.permission.manufacturer.vivo.VivoPermissionBase;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.PakageUsageStatsPermission;


public class OppoPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;
    private NotifiCationBarPermission notifiCationBarPermission;
    private PakageUsageStatsPermission pakageUsageStatsPermission;

    public OppoPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
        notifiCationBarPermission = new NotifiCationBarPermission(mContext);
        pakageUsageStatsPermission = new PakageUsageStatsPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        if (mVersion == OppoPermissionBase.VERSION.V16 || mVersion == OppoPermissionBase.VERSION.V_UNDEFINED) {
            suspendedToastPermission.openSuspendedToast(mContext, info, service);
        } else {
            suspendedToastPermission.openSuspendedToastOther(mContext, info, service);
        }

    }

    /**
     * 锁屏权限
     */
    public void actionLockDisplay(AccessibilityNodeInfo info, AccessibilityService service) {
    }


    /**
     * 后台弹出
     */
    public void actionBackstatePopUp(AccessibilityNodeInfo info, AccessibilityService service) {
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
    public void actionNoticeOfTakeover(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
    }

    /**
     * 通知栏
     */
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service) {
        notifiCationBarPermission.openNotifiCationBar(mContext, info, service);
    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        selfStartingPermission.openSelfStarting(mContext, info, service);
    }

    /**
     * 查看应用使用情况
     *
     * @param info
     * @param service
     * @param version
     * @param vivoOs
     */
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
                break;
            case NOTIFICATIONBAR:
                notifiCationBarPermission.clearList();

                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }
}
