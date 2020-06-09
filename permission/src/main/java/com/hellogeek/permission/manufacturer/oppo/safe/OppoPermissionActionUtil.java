package com.hellogeek.permission.manufacturer.oppo.safe;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.oppo.OppoPermissionBase;
import com.hellogeek.permission.manufacturer.oppo.safe.permissionlist.*;


public class OppoPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;


    public OppoPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
            suspendedToastPermission.openSuspendedToast(mContext, info, service);
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

    }

    /**
     * 通知栏
     */
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service) {
//        notifiCationBarPermission.openNotifiCationBar(mContext,info,service);
    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
            selfStartingPermission.openSelfStarting(mContext, info, service);
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
                break;
            case NOTICEOFTAKEOVER:
                break;


        }
    }

}
