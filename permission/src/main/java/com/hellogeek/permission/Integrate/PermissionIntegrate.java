package com.hellogeek.permission.Integrate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hellogeek.permission.Integrate.interfaces.PermissionAddQQCallback;
import com.hellogeek.permission.Integrate.interfaces.PermissionRecordCallback;
import com.hellogeek.permission.activity.PermissionAutoFixActivity;
import com.hellogeek.permission.Integrate.interfaces.IPermissionIntegrate;
import com.hellogeek.permission.Integrate.interfaces.PermissionCallback;
import com.hellogeek.permission.activity.WKPermissionAutoFixActivity;


import java.util.ArrayList;
import java.util.Map;

public class PermissionIntegrate implements IPermissionIntegrate {
    private static PermissionIntegrate instance;
    private static String appPackName;
    private static boolean isManual;
    private static Application mContext;
    private static PermissionCallback callback;
    private static PermissionRecordCallback recordCallback;
    private static PermissionAddQQCallback addQQCallback;

    private String suspendedToastName;
    private String selfStartingName;
    private String lockDispalyName;
    private String backStagePopupName;
    private String systemSettingName;
    private String replaceacllPageName;
    private String notifiCationBarName;
    private String noticeOfTakeoverName;

    private int suspendedToastRes;
    private int selfStartingRes;
    private int lockDispalyRes;
    private int backStagePopupRes;
    private int systemSettingRes;
    private int replaceacllPageRes;
    private int notifiCationBarRes;
    private int noticeOfTakeoverRes;

    private int permissionDefaultColor;
    private int permissionOpenColor;
    private String sourcePage;
    private boolean necessary;

    /**
     * 初始化
     */
    public static PermissionIntegrate getInstance(Application context) {
        if (instance == null) {
            synchronized (PermissionIntegrate.class) {
                if (instance == null) {
                    ARouter.init(context);
                    instance = new PermissionIntegrate(context);
                }
            }
        }
        return instance;
    }

    public PermissionIntegrate(Application context) {
        this.mContext = context;
    }

    public PermissionIntegrate() {
    }


    /**
     * 无参调用（不支持初始化操作）
     */
    public static PermissionIntegrate getPermission() {
        if (instance != null) {
            return instance;
        }
        return new PermissionIntegrate();
    }

    public String getAppPackName() {
        return appPackName;
    }

    private ArrayList<Permission> permissionList = new ArrayList<>();

    @Override
    public IPermissionIntegrate setPermissionList(Permission... mPermission) {
        if (permissionList != null && permissionList.size() > 0) {
            permissionList.clear();
        }
        if (mPermission != null) {
            for (Permission permission : mPermission) {
                permissionList.add(permission);
            }
        }
        if (permissionList.contains(Permission.REPLACEACLLPAGE)) {
            permissionList.remove(Permission.REPLACEACLLPAGE);
            permissionList.add(Permission.REPLACEACLLPAGE);
        }
        return this;
    }

    @Override
    public ArrayList<Permission> getPermissionList() {
        return permissionList;
    }

    @Override
    public void start(Context context) {
        context.startActivity(new Intent(context, PermissionAutoFixActivity.class));
    }

    public void startWK(Context context) {
        context.startActivity(new Intent(context, WKPermissionAutoFixActivity.class));
    }

    @Override
    public void startForResult(Activity activity) {
        activity.startActivityForResult(new Intent(activity, PermissionAutoFixActivity.class), 1001);
    }

    /**
     * 权限页面销毁后的回调
     */
    @Override
    public IPermissionIntegrate setPermissionCallBack(PermissionCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 加QQ点击反馈
     * 如果需要权限埋点（必传）
     */
    @Override
    public IPermissionIntegrate setPermissionRecordCallback(PermissionRecordCallback callback) {
        this.recordCallback = callback;
        return this;
    }

    /**
     * 权限埋点数据回传
     * 如果需要权限埋点（必传）
     */
    @Override
    public IPermissionIntegrate setPermissionAddQQCallback(PermissionAddQQCallback callback) {
        this.addQQCallback = callback;
        return this;
    }


    public PermissionCallback getPermissionCallBack() {
        return callback;
    }

    public PermissionAddQQCallback getPermissionAddQQCallback() {
        return addQQCallback;
    }

    public PermissionRecordCallback getPermissionRecordCallBack() {
        return recordCallback;
    }

    @Override
    public IPermissionIntegrate setPackName(String packName) {
        appPackName = packName;
        return this;
    }

    @Override
    public IPermissionIntegrate setIsManual(boolean isManual) {
        this.isManual = isManual;
        return this;
    }

    @Override
    public boolean getIsManual() {
        return isManual;
    }

    @Override
    public IPermissionIntegrate setSuspendedToastName(String title) {
        suspendedToastName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setSelfStartingName(String title) {
        selfStartingName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setLockDispalyName(String title) {
        lockDispalyName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setBackStagePopupName(String title) {
        backStagePopupName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setSystemSettingName(String title) {
        systemSettingName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setReplaceacllPageName(String title) {
        replaceacllPageName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setNotifiCationBarName(String title) {
        notifiCationBarName = title;
        return this;
    }

    @Override
    public IPermissionIntegrate setNoticeOfTakeoverName(String title) {
        noticeOfTakeoverName = title;
        return this;
    }


    @Override
    public String getSuspendedToastName() {
        return suspendedToastName;
    }

    @Override
    public String getSelfStartingName() {
        return selfStartingName;
    }

    @Override
    public String getLockDispalyName() {
        return lockDispalyName;
    }

    @Override
    public String getBackStagePopupName() {
        return backStagePopupName;
    }

    @Override
    public String getSystemSettingName() {
        return systemSettingName;
    }

    @Override
    public String getReplaceacllPageName() {
        return replaceacllPageName;
    }

    @Override
    public String getNotifiCationBarName() {
        return notifiCationBarName;
    }

    @Override
    public String getNoticeOfTakeoverName() {
        return noticeOfTakeoverName;
    }


    @Override
    public int getSuspendedToastRes() {
        return suspendedToastRes;
    }

    @Override
    public PermissionIntegrate setSuspendedToastRes(int suspendedToastRes) {
        this.suspendedToastRes = suspendedToastRes;
        return this;
    }

    @Override
    public int getSelfStartingRes() {
        return selfStartingRes;
    }

    @Override
    public PermissionIntegrate setSelfStartingRes(int selfStartingRes) {
        this.selfStartingRes = selfStartingRes;
        return this;
    }

    @Override
    public int getLockDispalyRes() {
        return lockDispalyRes;
    }

    @Override
    public PermissionIntegrate setLockDispalyRes(int lockDispalyRes) {
        this.lockDispalyRes = lockDispalyRes;
        return this;
    }

    @Override
    public int getBackStagePopupRes() {
        return backStagePopupRes;
    }

    @Override
    public PermissionIntegrate setBackStagePopupRes(int backStagePopupRes) {
        this.backStagePopupRes = backStagePopupRes;
        return this;
    }

    @Override
    public int getSystemSettingRes() {
        return systemSettingRes;
    }

    @Override
    public PermissionIntegrate setSystemSettingRes(int systemSettingRes) {
        this.systemSettingRes = systemSettingRes;
        return this;
    }

    @Override
    public int getReplaceacllPageRes() {
        return replaceacllPageRes;
    }

    @Override
    public PermissionIntegrate setReplaceacllPageRes(int replaceacllPageRes) {
        this.replaceacllPageRes = replaceacllPageRes;
        return this;
    }

    @Override
    public int getNotifiCationBarRes() {
        return notifiCationBarRes;
    }

    @Override
    public PermissionIntegrate setNotifiCationBarRes(int notifiCationBarRes) {
        this.notifiCationBarRes = notifiCationBarRes;
        return this;
    }

    @Override
    public int getNoticeOfTakeoverRes() {
        return noticeOfTakeoverRes;
    }

    @Override
    public PermissionIntegrate setNoticeOfTakeoverRes(int noticeOfTakeoverRes) {
        this.noticeOfTakeoverRes = noticeOfTakeoverRes;
        return this;
    }


    @Override
    public int getPermissionDefaultColor() {
        return permissionDefaultColor;
    }

    @Override
    public PermissionIntegrate setPermissionDefaultColor(int permissionDefaultColor) {
        this.permissionDefaultColor = permissionDefaultColor;
        return this;
    }

    @Override
    public int getPermissionOpenColor() {
        return permissionOpenColor;
    }

    @Override
    public PermissionIntegrate setPermissionOpenColor(int permissionOpenColor) {
        this.permissionOpenColor = permissionOpenColor;
        return this;
    }


    /**
     * 埋点sourcePage
     * 如果需要权限埋点（必传）
     */
    @Override
    public PermissionIntegrate setPermissionSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
        return this;
    }

    @Override
    public String getPermissionSourcePage() {
        return sourcePage;
    }

    @Override
    public PermissionIntegrate setIsNecessary(boolean necessary) {
        this.necessary = necessary;
        return this;
    }

    @Override
    public boolean getIsNecessary() {
        return necessary;
    }

//    @Override
//    public void usageRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap) {
//
//    }


    public Application getApplication() {
        return mContext;
    }


}
