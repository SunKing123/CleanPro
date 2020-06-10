package com.hellogeek.permission.manufacturer.vivo;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.BackstagePopupPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.LockDisPlayPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.NoticeOfTakeoverPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.NotifiCationBarPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.PakageUsageStatsPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.SelfStartingPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.SuspendedToastPermission;
import com.hellogeek.permission.manufacturer.vivo.permissionlist.SystemSettingPermission;
import com.hellogeek.permission.util.PhoneRomUtils;


public class VivoPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;
    private NotifiCationBarPermission notifiCationBarPermission;
    private BackstagePopupPermission backstagePopupPermission;
    private LockDisPlayPermission lockDisPlayPermission;
    private PakageUsageStatsPermission pakageUsageStatsPermission;

    public VivoPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
        notifiCationBarPermission = new NotifiCationBarPermission(mContext);
        backstagePopupPermission = new BackstagePopupPermission(mContext);
        lockDisPlayPermission = new LockDisPlayPermission(mContext);
        pakageUsageStatsPermission = new PakageUsageStatsPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version, String vivoOs) {
        switch (version) {
            case VERSION_3:
            case VERSION_3_2:
                if (Build.MODEL.contains("Y67") || (PhoneRomUtils.isVivoX7SDK22() && TextUtils.equals("3.0", vivoOs))) {
                    suspendedToastPermission.openSuspendedToastY67(mContext, info, service);
                } else {
                    suspendedToastPermission.openSuspendedToastV3_2(mContext, info, service);
                }
                break;
            case VERSION_4_1:
            case VERSION_4:
            case VERSION_4_1_8:
            case VERSION_4_2:
            case VERSION_4_4:
                if (PhoneRomUtils.isVivoY66SDK23()) {
                    suspendedToastPermission.openSuspendedToastV4_4Y66(mContext, info, service);
                    break;
                }
            case VERSION_5_2_0:
                suspendedToastPermission.openSuspendedToastVOther(mContext, info, service);
                break;
        }
    }

    /**
     * 锁屏权限
     */
    public void actionLockDisplay(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version) {
        switch (version) {
            case VERSION_3_2:
                lockDisPlayPermission.openLockDisPlayV67(mContext, info, service);
                break;
            case VERSION_4_4:
                if (PhoneRomUtils.isVivoY66SDK23()) {
                    lockDisPlayPermission.openLockDisPlayV4_4Y66(mContext, info, service);
                    break;
                }
            default:
                lockDisPlayPermission.openLockDisPlay(mContext, info, service);

        }
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
    public void actionSystemSetting(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version) {
        switch (version) {
            case VERSION_4_4:
                if (PhoneRomUtils.isVivoY66SDK23()) {
                    systemSettingPermission.openSystemSettingV4_4Y66(mContext, info, service);
                    break;
                }
            default:
                systemSettingPermission.openSystemSetting(mContext, info, service);
        }

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
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version) {
        switch (version) {
            case VERSION_3_2:
                //设置—-状态栏与通知
                notifiCationBarPermission.openNotifiCationBarV3_2(mContext, info, service);
                break;
            case VERSION_4:
            case VERSION_4_1_8:
            case VERSION_4_1:
            case VERSION_4_2:
            case VERSION_4_4:
            case VERSION_5_2_0:
                notifiCationBarPermission.openNotifiCationBarOther(mContext, info, service);
                break;
        }
    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service, VivoPermissionBase.VERSION version, String vivoOs) {
        try {
            switch (version) {
                case VERSION_1:
                case VERSION_2:
                case VERSION_3:
                case VERSION_3_2:
                    if (Build.MODEL.contains(VivoModel.Y67) || (PhoneRomUtils.isVivoX7SDK22() && TextUtils.equals("3.0", vivoOs))) {
                        selfStartingPermission.openSelfStartingY67(mContext, info, service);
                    } else {
                        selfStartingPermission.openSelfStartingV2V3(mContext, info, service);
                    }
                    break;
                case VERSION_4_1:
                case VERSION_4:
                case VERSION_4_1_8:
                case VERSION_4_2:
                case VERSION_4_4:
                case VERSION_5_2_0:
                    selfStartingPermission.openSelfStarting(mContext, info, service);
                    break;
            }

        } catch (Exception e) {
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
                notifiCationBarPermission.clearList();

                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }

}
