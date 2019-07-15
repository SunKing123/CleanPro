package com.xiaoniu.cleanking.hotfix;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.hotfix.log.MyLogImp;
import com.xiaoniu.cleanking.hotfix.utils.ApplicationContext;
import com.xiaoniu.cleanking.hotfix.utils.TinkerManager;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.tencent.bugly.Bugly;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.NiuDataAPI;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by admin on 2017/6/8.
 */

public class ApplicationDelegate extends DefaultApplicationLike {

    private static final String TAG = "Tinker.ApplicationDelegate";
    private static ApplicationDelegate sInstance;

//    private Set<Activity> allActivities;

    public ApplicationDelegate(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                               long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        sInstance = this;
        MultiDex.install(base);
        ApplicationContext.application = getApplication();
        ApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNiuData();
        PlatformConfig.setWeixin("wx19414dec77020d03", "090f560fa82e0dfff2f0cb17e43747c2");
        PlatformConfig.setQQZone("1106787504", "KjjMEbU64j1qFg1u");
        PlatformConfig.setSinaWeibo("2480041639", "fc45c092d152a6382b0d84d1868a5d21", "");
        Bugly.init(getApplication(), "bdd6fe23ab", false);

        UMShareAPI.get(getApplication());

        initInjector();

        //初始化sdk
        JPushInterface.setDebugMode(false);//正式版的时候设置false，关闭调试
        JPushInterface.init(getApplication());
        JPushInterface.setAlias(getApplication(), AndroidUtil.getPhoneNum(), null);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(getApplication());
        UMConfigure.init(getApplication(), "5d230f2f4ca357bdb700106d", AndroidUtil.getMarketId(), UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    private static AppComponent mAppComponent;

    private void initInjector() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .apiModule(new ApiModule(getApplication()))
                .build();
        mAppComponent.inject(getApplication());
    }

    public void initNiuData(){
        //测试环境
        NiuDataAPI.init(AppApplication.getInstance(), new Configuration().serverUrl(AppConstants.BIGDATA_MD)
                //.debugOn() //切换到sdk默认的测试环境地址
                .logClose()//打开sdk日志信息
        );
    }
    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * @return App 全局上下文
     */
    public static ApplicationDelegate getInstance() {
        return sInstance;
    }
}
