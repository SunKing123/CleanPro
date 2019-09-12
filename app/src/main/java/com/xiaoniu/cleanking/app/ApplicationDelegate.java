package com.xiaoniu.cleanking.app;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.bugly.Bugly;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.common.base.IApplicationDelegate;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.NiuDataAPI;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by admin on 2017/6/8.
 */

public class ApplicationDelegate implements IApplicationDelegate {

    private static final String TAG = "Tinker.ApplicationDelegate";

    @Override
    public void onCreate(Application application) {
        initNiuData(application);
        PlatformConfig.setWeixin("wx19414dec77020d03", "090f560fa82e0dfff2f0cb17e43747c2");
        PlatformConfig.setQQZone("1109516379", "SJUCaQdURyRd8Dfi");
        PlatformConfig.setSinaWeibo("1456333364", "bee74e1ccd541f657875803a7eb32b1b", "http://xiaoniu.com");
        Bugly.init(application, "bdd6fe23ab", false);

        UMShareAPI.get(application);

        initInjector(application);

        //初始化sdk
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
        JPushInterface.init(application);

        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(application);
        UMConfigure.init(application, "5d230f2f4ca357bdb700106d", AppUtils.getChannelId(), UMConfigure.DEVICE_TYPE_PHONE, "");
        NotificationUtils.createNotificationChannel();
        NotifyCleanManager.getInstance().sendRebindServiceMsg();
    }

    private static AppComponent mAppComponent;

    private void initInjector(Application application) {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModule(application))
                .build();
        mAppComponent.inject(application);
    }

    public void initNiuData(Application application) {
        //测试环境
        NiuDataAPI.init(application, new Configuration().serverUrl(AppConstants.BIGDATA_MD)
                //切换到sdk默认的测试环境地址
                .setHeartbeatMode(Configuration.HEARTBEAT_MODE_FOREGROUND)
                //打开sdk日志信息
                .logOpen()
                .channel(AppUtils.getChannelId())
        );
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

}
