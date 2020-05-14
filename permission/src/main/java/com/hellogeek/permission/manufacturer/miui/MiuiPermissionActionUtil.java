package com.hellogeek.permission.manufacturer.miui;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.BackstagePopupPermission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.LockDisPlayPermission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.NoticeOfTakeoverPermission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.SelfStartingPermission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.SuspendedToastPermission;
import com.hellogeek.permission.manufacturer.miui.permissionlist.SystemSettingPermission;
import com.hellogeek.permission.util.PhoneRomUtils;

import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMi4ASDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMiNote4SDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isXiaoMi6XSDK27;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isXiaoMi8SESDK27;


public class MiuiPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;
    private BackstagePopupPermission backstagePopupPermission;
    private LockDisPlayPermission lockDisPlayPermission;

    public MiuiPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
        backstagePopupPermission = new BackstagePopupPermission(mContext);
        lockDisPlayPermission = new LockDisPlayPermission(mContext);
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
        lockDisPlayPermission.openLockDisPlay(mContext, info, service);
    }


    /**
     * 后台弹出
     */
    public void actionBackstatePopUp(AccessibilityNodeInfo info, AccessibilityService service) {
        backstagePopupPermission.openBackstagePopup(mContext, info, service);
    }


    /**
     * 系统设置
     */
    public void actionSystemSetting(AccessibilityNodeInfo info, AccessibilityService service,boolean isToSystem) {
        if (isToSystem) {
            systemSettingPermission.openSystemSetting(mContext, info, service);
        } else {
            systemSettingPermission.openSystemSettingMIui(mContext, info, service);
        }
    }

    /**
     * 接管通知
     */
    public void actionNoticeOfTakeover(AccessibilityNodeInfo info, AccessibilityService service) {
        noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
    }



    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service) {
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
                lockDisPlayPermission.clearList();

                break;
            case BACKSTAGEPOPUP:
                backstagePopupPermission.clearList();

                break;
            case SYSTEMSETTING:
                systemSettingPermission.clearList();

                break;
            case REPLACEACLLPAGE:
                break;
            case NOTIFICATIONBAR:
                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }

}
