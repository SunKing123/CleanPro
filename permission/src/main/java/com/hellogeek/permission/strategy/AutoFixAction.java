package com.hellogeek.permission.strategy;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.manufacturer.PermissionSystemPath;
import com.hellogeek.permission.util.NotifyUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import java.util.ArrayList;

import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMi4ASDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isRedMiNote4SDK23;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isXiaoMi6XSDK27;
import static com.hellogeek.permission.manufacturer.miui.MIUIPermissionBase.isXiaoMi8SESDK27;

public class AutoFixAction {
    private static AutoFixAction instance;

    // 单例模式
    public static AutoFixAction getInstance() {
        if (null == instance) {
            synchronized (AutoFixAction.class) {
                instance = new AutoFixAction();
            }
        }
        return instance;
    }

    public void permissionHandlerEvent(AccessibilityService service, AccessibilityEvent event) {

    }

    public void permissionAction(Permission permission) {
        switch (permission) {
            case SUSPENDEDTOAST:
                actionSuspendedToast();
                break;
            case SELFSTARTING:
                actionSelfStarting();
                break;
            case LOCKDISPALY:
                actionLockDisplay();
                break;
            case BACKSTAGEPOPUP:
                actionBackstatePopUp();
                break;
            case SYSTEMSETTING:
                actionSystemSetting();
                break;
            case REPLACEACLLPAGE:
                actionRepLaceAcllpage();
                break;
            case NOTIFICATIONBAR:
                actionNotifiCationBar();
                break;
            case NOTICEOFTAKEOVER:
                actionNoticeOfTakeover();
                break;
            case PACKAGEUSAGESTATS:
                actionPackageUsageStats();
                break;
            case NOTIFICATIONREAD:
                actionNotificationRead();
                break;

        }
    }

    protected void actionNotificationRead() { // 通知读取权限
        clearSNIGList(Permission.NOTIFICATIONREAD);
    }


    protected void actionSuspendedToast() {//悬浮框
        clearSNIGList(Permission.SUSPENDEDTOAST);
    }

    ;

    protected void actionSelfStarting() {//自启动
        clearSNIGList(Permission.SELFSTARTING);

    }

    ;

    protected void actionLockDisplay() { //锁屏
        clearSNIGList(Permission.LOCKDISPALY);

    }

    ;

    protected void actionSystemSetting() {//修改系统设置
        clearSNIGList(Permission.SYSTEMSETTING);

    }


    protected void actionBackstatePopUp() {//后台弹出界面
        clearSNIGList(Permission.BACKSTAGEPOPUP);
    }

    ;

    protected void actionRepLaceAcllpage() {//替换来电页面
        clearSNIGList(Permission.REPLACEACLLPAGE);

    }

    ;;


    protected void actionNotifiCationBar() {//允许通知
        clearSNIGList(Permission.NOTIFICATIONBAR);

    }

    protected void actionNoticeOfTakeover() {// 接管通知
        clearSNIGList(Permission.NOTICEOFTAKEOVER);

    }

    protected void actionPackageUsageStats() {// 查看应用使用情况
        clearSNIGList(Permission.PACKAGEUSAGESTATS);
    }

    public void configAccessbility(AccessibilityService sevice) {

    }

    public void clearSNIGList(Permission permission) {

    }

    public ArrayList<Permission> getPermissionList() {
        // TODO: 2019/1/23 exchange the two lines
//        return PermissionListUtil.getIntersectionPermissionNameList(supportedPermissionList());
        return PermissionIntegrate.getPermission().getPermissionList();
    }


    /**
     * 修改系统设置页面
     */
    public static void jumpSystemSetting(Context mContext) {
        Intent intent = null;
        if (isRedMiNote4SDK23() || isRedMi4ASDK23()
                || isXiaoMi8SESDK27() || isXiaoMi6XSDK27() || PhoneRomUtils.isXiaoV11()) {
            intent = new Intent();
            intent.putExtra("extra_pkgname", mContext.getPackageName());
            intent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            mContext.startActivity(intent);
        } else {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        }
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.XIAOMI) {
                final Intent intentL = new Intent();
                intentL.putExtra("extra_pkgname", mContext.getPackageName());
                intentL.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            }
        }

    }


    /**
     * 修改系统设置页面
     */
    public static Intent jumpOppoSystemSetting(Context mContext) {
        Intent intent = null;
        if (isRedMiNote4SDK23() || isRedMi4ASDK23()
                || isXiaoMi8SESDK27()) {
            Intent sysIntent = new Intent();
            sysIntent.putExtra("extra_pkgname", mContext.getPackageName());
            sysIntent.setClassName(PermissionSystemPath.MIUI_SECURITYCENTER, PermissionSystemPath.MIUI_PERMISSIONEDITORACTIVITY);
            mContext.startActivity(sysIntent);
        } else {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        }
        return intent;

    }
}
