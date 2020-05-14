package com.hellogeek.permission.manufacturer.other;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.other.permissionlist.NoticeOfTakeoverPermission;
import com.hellogeek.permission.manufacturer.other.permissionlist.SuspendedToastPermission;
import com.hellogeek.permission.manufacturer.other.permissionlist.SystemSettingPermission;

import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMi4ASDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMiNote4SDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isXiaoMi8SESDK27;


public class OtherPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SystemSettingPermission systemSettingPermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;


    public OtherPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service) {
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
    public void actionNoticeOfTakeover(AccessibilityNodeInfo info, AccessibilityService service) {
        noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
    }

    /**
     * 通知栏
     */
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service) {
        noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service) {

    }
    public void clearList(Permission permission) {
        switch (permission) {
            case SUSPENDEDTOAST:
                suspendedToastPermission.clearList();
                break;
            case SYSTEMSETTING:
                systemSettingPermission.clearList();
                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }

}
