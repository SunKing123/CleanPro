package com.hellogeek.permission.util;


import android.text.TextUtils;

import com.hellogeek.permission.R;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;

/**
 * Desc:
 * <p>
 */
public class PermissionConvertUtils {
    public static int getRes(Permission permission) {
        int res = 0;
        switch (permission) {
            case SUSPENDEDTOAST:
                if (PermissionIntegrate.getPermission().getSuspendedToastRes() != 0) {
                    res = PermissionIntegrate.getPermission().getSuspendedToastRes();
                } else {
                    res = R.mipmap.wk_icon_fix_supendedtoast;
                }
                break;
            case LOCKDISPALY:
                if (PermissionIntegrate.getPermission().getLockDispalyRes() != 0) {
                    res = PermissionIntegrate.getPermission().getLockDispalyRes();
                } else {
                    res = R.mipmap.icon_fix_lock;
                }
                break;
            case SYSTEMSETTING:
                if (PermissionIntegrate.getPermission().getSystemSettingRes() != 0) {
                    res = PermissionIntegrate.getPermission().getSystemSettingRes();
                } else {
                    res = R.mipmap.icon_fix_systemsetting;
                }
                break;
            case SELFSTARTING:
                if (PermissionIntegrate.getPermission().getSelfStartingRes() != 0) {
                    res = PermissionIntegrate.getPermission().getSelfStartingRes();
                } else {
                    res = R.mipmap.wk_self_starting_icon;
                }
                break;
            case NOTIFICATIONBAR:
                if (PermissionIntegrate.getPermission().getNotifiCationBarRes() != 0) {
                    res = PermissionIntegrate.getPermission().getNotifiCationBarRes();
                } else {
                    res = R.mipmap.icon_notification;
                }
                break;
            case BACKSTAGEPOPUP:
                if (PermissionIntegrate.getPermission().getBackStagePopupRes() != 0) {
                    res = PermissionIntegrate.getPermission().getBackStagePopupRes();
                } else {
                    res = R.mipmap.icon_fix_backstagepopup;
                }
                break;
            case REPLACEACLLPAGE:
                if (PermissionIntegrate.getPermission().getReplaceacllPageRes() != 0) {
                    res = PermissionIntegrate.getPermission().getReplaceacllPageRes();
                } else {
                    res = R.mipmap.icon_fix_replaceacllpage;
                }
                break;
            case NOTICEOFTAKEOVER:
                if (PermissionIntegrate.getPermission().getNoticeOfTakeoverRes() != 0) {
                    res = PermissionIntegrate.getPermission().getNoticeOfTakeoverRes();
                } else {
                    res = R.mipmap.wk_icon_fix_noticeoftakeover;
                }
                break;
            case PACKAGEUSAGESTATS:
                if (PermissionIntegrate.getPermission().getNoticeOfTakeoverRes() != 0) {
                    res = PermissionIntegrate.getPermission().getNoticeOfTakeoverRes();
                } else {
                    res = R.mipmap.wk_icon_fix_noticeoftakeover;
                }
                break;
            default:
                res = -1;
                break;
        }
        return res;
    }

    public static String getTitleStr(Permission permission) {
        String title = null;
        switch (permission) {
            case SUSPENDEDTOAST:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getSuspendedToastName())) {
                    title = PermissionIntegrate.getPermission().getSuspendedToastName();
                } else {
                    title = permission.getPermissionDesc() != null ? permission.getPermissionDesc() : permission.getName();
                }
                break;
            case REPLACEACLLPAGE:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getReplaceacllPageName())) {
                    title = PermissionIntegrate.getPermission().getReplaceacllPageName();
                } else {
                    title = "替换来电页面";
                }
                break;
            case LOCKDISPALY:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getLockDispalyName())) {
                    title = PermissionIntegrate.getPermission().getLockDispalyName();
                } else {
                    title = "展示锁屏";
                }
                break;
            case SYSTEMSETTING:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getSystemSettingName())) {
                    title = PermissionIntegrate.getPermission().getSystemSettingName();
                } else {
                    title = "修改手机铃声";
                }
                break;
            case SELFSTARTING:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getSelfStartingName())) {
                    title = PermissionIntegrate.getPermission().getSelfStartingName();
                } else {
                    title = permission.getPermissionDesc() != null ? permission.getPermissionDesc() : permission.getName();
                }
                break;
            case NOTIFICATIONBAR:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getNotifiCationBarName())) {
                    title = PermissionIntegrate.getPermission().getNotifiCationBarName();
                } else {
                    title = permission.getPermissionDesc() != null ? permission.getPermissionDesc() : permission.getName();
                }
                break;
            case BACKSTAGEPOPUP:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getBackStagePopupName())) {
                    title = PermissionIntegrate.getPermission().getBackStagePopupName();
                } else {
                    title = "确保产品正常使用";
                }
                break;
            case NOTICEOFTAKEOVER:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getNoticeOfTakeoverName())) {
                    title = PermissionIntegrate.getPermission().getNoticeOfTakeoverName();
                } else {
                    title = "读取来电通知";
                }
                break;
            case PACKAGEUSAGESTATS:
                if (!TextUtils.isEmpty(PermissionIntegrate.getPermission().getNoticeOfTakeoverName())) {
                    title = PermissionIntegrate.getPermission().getNoticeOfTakeoverName();
                } else {
                    title = permission.getPermissionDesc() != null ? permission.getPermissionDesc() : permission.getName();
                }
                break;
            default:
                title = null;
                break;
        }
        return title;
    }
}
