package com.xiaoniu.cleanking.app;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.Integrate.interfaces.PermissionRecordCallback;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.common.AppProfile;
import com.xiaoniu.common.base.BaseApplication;
import com.xiaoniu.common.utils.ContextUtils;

import java.util.Map;


/**
 * Created by admin on 2017/7/13.
 */

public class AppApplication extends BaseApplication {
    private static AppApplication sInstance;
    public static String AuditSwitch = "AuditSwitch";

    public AppApplication() {
        super();
        AppProfile.API_APPID = BuildConfig.API_APPID;
        AppProfile.API_APPSECRET = BuildConfig.API_APPSECRET;
        AppProfile.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        AppProfile.BUILD_TYPE = BuildConfig.BUILD_TYPE;
        AppProfile.DEBUG = BuildConfig.DEBUG;
        AppProfile.PLATFORM = BuildConfig.PLATFORM;
        AppProfile.VERSION_CODE = BuildConfig.VERSION_CODE;
        AppProfile.VERSION_NAME = BuildConfig.VERSION_NAME;


    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        ContextUtils.initApplication(this);

//        PermissionIntegrate.getInstance(this)
//                .setPermissionList(Permission.SUSPENDEDTOAST,Permission.BACKSTAGEPOPUP,Permission.SELFSTARTING,Permission.LOCKDISPALY,Permission.NOTIFICATIONBAR,Permission.REPLACEACLLPAGE,Permission.SYSTEMSETTING)
//                .setPermissionRecordCallback(new PermissionRecordCallback() {
//                    @Override
//                    public void usagePermissionRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap) {
//
//                    }
//                });
//
        PermissionIntegrate.getInstance(this)
                .setPermissionList(Permission.SUSPENDEDTOAST, Permission.SELFSTARTING,
                        Permission.NOTIFICATIONBAR, Permission.PACKAGEUSAGESTATS)
                .setPermissionRecordCallback(new PermissionRecordCallback() {
                    @Override
                    public void usagePermissionRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap) {

                    }
                });
    }

    /**
     * @return App 全局上下文
     */
    public static AppApplication getInstance() {
        return sInstance;
    }
}
