package com.xiaoniu.cleanking.app;

import android.app.Application;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bun.miitmdid.core.JLibrary;
import com.geek.push.GeekPush;
import com.geek.push.core.PushConstants;
import com.qq.e.comm.managers.GDTADManager;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.jpush.JPushNotificationManager;
import com.xiaoniu.cleanking.room.AppDataBase;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.common.base.IApplicationDelegate;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.MiitHelper;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.statistic.NiuDataTrackEventCallBack;

import org.json.JSONException;
import org.json.JSONObject;

//import com.tencent.bugly.Bugly;

/**
 * Created by admin on 2017/6/8.
 */

public class ApplicationDelegate implements IApplicationDelegate {

    private static AppDataBase mAppDatabase;
    private String oaId = "";

    @Override
    public void onCreate(Application application) {

        PlatformConfig.setWeixin("wx19414dec77020d03", "090f560fa82e0dfff2f0cb17e43747c2");
        PlatformConfig.setQQZone("1109516379", "SJUCaQdURyRd8Dfi");
        PlatformConfig.setSinaWeibo("1456333364", "bee74e1ccd541f657875803a7eb32b1b", "http://xiaoniu.com");
        UMShareAPI.get(application);
        initInjector(application);

        //初始化sdk
        initGeekPush(application);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(application);
        UMConfigure.init(application, "5d230f2f4ca357bdb700106d", ChannelUtil.getChannel(), UMConfigure.DEVICE_TYPE_PHONE, "");
        NotificationUtils.createNotificationChannel();
        NotifyCleanManager.getInstance().sendRebindServiceMsg();


        initRoom(application);
        initNiuData(application);
        initOaid(application);
        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(application);
        // 通过调用此方法初始化 SDK。如果需要在多个进程拉取广告，每个进程都需要初始化 SDK。
        GDTADManager.getInstance().initWith(application, PositionId.APPID);
    }


    private static AppComponent mAppComponent;

    private void initInjector(Application application) {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModule(application))
                .build();
        mAppComponent.inject(application);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver(application.getApplicationContext()));
    }

    private void initGeekPush(Application application) {
        GeekPush.setDebug(false);

        GeekPush.init(application, ((platformCode, platformName) -> {
            boolean result = (platformCode == PushConstants.PLATFORMCODE_JPUSH);
            return result;
        }));
        GeekPush.register();
        JPushNotificationManager.customPushNotification(application, 1, R.layout.layout_notivition, R.id.image, R.id.title, R.id.text, R.mipmap.applogo, R.mipmap.applogo);
    }


    private void initRoom(Application application) {
        mAppDatabase = RoomDataBaseUtils.init(application);
    }


    public static AppDataBase getAppDatabase() {
        return mAppDatabase;
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

    //埋点初始化
    private void initNiuData(Application application) {
        //测试环境
        Configuration configuration = new Configuration()
                //切换到sdk默认的测试环境地址
                .serverUrl(BuildConfig.STATISTICS_URL)
                .setHeartbeatUrl(BuildConfig.STATISTICS_URL)
                .setHeartbeatInterval(5000)
                .channel(ChannelUtil.getChannel());
        //事件上报策略（批量 或 单条）（默认是批量，可不设置）
        if (BuildConfig.DEBUG) {
            configuration.setTimelyReport(true);
            //log
            configuration.logOpen();
        } else {
            configuration.logClose();
        }
        //测试环境
        NiuDataAPI.init(application, configuration);

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });

    }

    private void initOaid(Application application) {
        //设置oaid到埋点公共参数
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) { //4.4以上版本oaid
            try {
                JLibrary.InitEntry(application);
                //获取oaid
                new MiitHelper(new MiitHelper.AppIdsUpdater() {
                    @Override
                    public void OnIdsAvalid(@NonNull String mOaid) {
                        oaId = mOaid;
                        NiuDataAPI.setOaid(oaId);
                        NiuDataAPI.setTrackEventCallback(new NiuDataTrackEventCallBack() {
                            //添加到默认事件
                            @Override
                            public void onTrackAutoCollectEvent(String eventCode, JSONObject eventProperties) {
                                try {
                                    eventProperties.put("oaid", oaId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            //添加到其他事件
                            @Override
                            public void onTrackEvent(String eventCode, JSONObject eventProperties) {
                                try {
                                    eventProperties.put("oaid", oaId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).getDeviceIds(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
