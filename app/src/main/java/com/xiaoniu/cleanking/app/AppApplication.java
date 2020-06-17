package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.baidu.mobstat.StatService;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.Integrate.interfaces.PermissionRecordCallback;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.AppProfile;
import com.xiaoniu.common.base.BaseApplication;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.SystemUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by admin on 2017/7/13.
 */

public class AppApplication extends BaseApplication {
    private static AppApplication sInstance;
    public static boolean isFromHome;
    public static String mapCoordinate;//坐标
    public static String provinceName;//省名
    public static String cityName;//市名
    public static String cityAreaName;//区（县）名
    public static Set<Integer> popSet = new HashSet<>();//运营弹窗是否一弹过位置统计
    public static String officialAccountContent;
    public static String officialAccountName;
    public static String AuditSwitch = "AuditSwitch";
    public static boolean isAudit;//是否市场审核中

    public AppApplication() {
        super();
        AppProfile.API_APPID = BuildConfig.API_APPID;
        AppProfile.API_APPSECRET = BuildConfig.API_APPSECRET;
        AppProfile.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        AppProfile.BUILD_TYPE = BuildConfig.BUILD_TYPE;
        AppProfile.DEBUG = BuildConfig.DEBUG;
        AppProfile.MESSAGE = BuildConfig.MESSAGE;
        AppProfile.PLATFORM = BuildConfig.PLATFORM;
        AppProfile.TINKER_ID = BuildConfig.TINKER_ID;
        AppProfile.VERSION_CODE = BuildConfig.VERSION_CODE;
        AppProfile.VERSION_NAME = BuildConfig.VERSION_NAME;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        ContextUtils.initApplication(this);
        //接入百度统计sdk
        StatService.setAppChannel(this,ChannelUtil.getChannel(),true);
        StatService.autoTrace(this);


        //权限相关初始化
        PermissionIntegrate.getInstance(this)
                .setPermissionList(Permission.SUSPENDEDTOAST, Permission.SELFSTARTING,
                        Permission.NOTIFICATIONREAD, Permission.PACKAGEUSAGESTATS)
                .setPermissionRecordCallback(new PermissionRecordCallback() {
                    @Override
                    public void usagePermissionRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap) {

                    }
                });

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("lifeCycle","onActivityCreated()"+activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("lifeCycle","onActivityDestroyed()"+activity.getLocalClassName());
            }
        });
    }

    /**
     * @return App 全局上下文
     */
    public static AppApplication getInstance() {
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.4以下手机启动失败
        MultiDex.install(base);
    }

}
