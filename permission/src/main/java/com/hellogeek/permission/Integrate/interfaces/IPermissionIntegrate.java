package com.hellogeek.permission.Integrate.interfaces;

import android.app.Activity;
import android.content.Context;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;


import java.util.ArrayList;
import java.util.Map;

public interface IPermissionIntegrate {

    IPermissionIntegrate setPermissionList(Permission... permission);

    ArrayList<Permission> getPermissionList();

    void start(Context context);

    void startForResult(Activity context);


    IPermissionIntegrate setPermissionCallBack(PermissionCallback callback);
    IPermissionIntegrate setPermissionRecordCallback(PermissionRecordCallback callback);
    IPermissionIntegrate setPermissionAddQQCallback(PermissionAddQQCallback callback);


    IPermissionIntegrate setPackName(String packName);

    IPermissionIntegrate setIsManual(boolean isManual);

    boolean getIsManual();


    //设置权限名称
    IPermissionIntegrate setSuspendedToastName(String title);

    IPermissionIntegrate setSelfStartingName(String title);

    IPermissionIntegrate setLockDispalyName(String title);

    IPermissionIntegrate setBackStagePopupName(String title);

    IPermissionIntegrate setSystemSettingName(String title);

    IPermissionIntegrate setReplaceacllPageName(String title);

    IPermissionIntegrate setNotifiCationBarName(String title);

    IPermissionIntegrate setNoticeOfTakeoverName(String title);

    String getSuspendedToastName();

    String getSelfStartingName();

    String getLockDispalyName();

    String getBackStagePopupName();

    String getSystemSettingName();

    String getReplaceacllPageName();

    String getNotifiCationBarName();

    String getNoticeOfTakeoverName();

    //设置权限图标
    int getSuspendedToastRes();

    PermissionIntegrate setSuspendedToastRes(int suspendedToastRes);

    int getSelfStartingRes();

    PermissionIntegrate setSelfStartingRes(int selfStartingRes);

    int getLockDispalyRes();

    PermissionIntegrate setLockDispalyRes(int lockDispalyRes);

    int getBackStagePopupRes();

    PermissionIntegrate setBackStagePopupRes(int backStagePopupRes);

    int getSystemSettingRes();

    PermissionIntegrate setSystemSettingRes(int systemSettingRes);

    int getReplaceacllPageRes();

    PermissionIntegrate setReplaceacllPageRes(int replaceacllPageRes);

    int getNotifiCationBarRes();

    PermissionIntegrate setNotifiCationBarRes(int notifiCationBarRes);

    int getNoticeOfTakeoverRes();

    PermissionIntegrate setNoticeOfTakeoverRes(int noticeOfTakeoverRes);

    //设置权限item默认颜色
    int getPermissionDefaultColor();

    PermissionIntegrate setPermissionDefaultColor(int noticeOfTakeoverRes);
    //设置权限item开启颜色
    int getPermissionOpenColor();

    PermissionIntegrate setPermissionOpenColor(int noticeOfTakeoverRes);


    PermissionIntegrate setPermissionSourcePage(String sourcePage);

    String getPermissionSourcePage();


    PermissionIntegrate setIsNecessary(boolean necessary);
    boolean getIsNecessary();
//    void usageRecord( int usageType,String currentPage, String sourcePage, String eventCode, String eventName, Map<String,String> extraMap);


}
