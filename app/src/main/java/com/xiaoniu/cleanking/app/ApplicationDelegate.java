package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.apkfuns.jsbridge.JsBridgeConfig;
import com.bun.miitmdid.core.JLibrary;
import com.comm.jksdk.GeekAdSdk;
import com.geek.push.GeekPush;
import com.geek.push.core.PushConstants;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.jpush.JPushNotificationManager;
import com.xiaoniu.cleanking.jsbridge.module.JsBridgeModule;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.keeplive.utils.HomeWatcher;
import com.xiaoniu.cleanking.keeplive.utils.OnHomePressedListener;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.lifecyler.LifecycleHelper;
import com.xiaoniu.cleanking.lifecyler.LifecycleListener;
import com.xiaoniu.cleanking.room.AppDataBase;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.IApplicationDelegate;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.MiitHelper;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.statistic.NiuDataTrackEventCallBack;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.room.Room;

/**
 * Created by admin on 2017/6/8.
 */

public class ApplicationDelegate implements IApplicationDelegate {

    private static final String TAG = "Tinker.ApplicationDelegate";
    private static AppDataBase mAppDatabase;


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
        UMConfigure.init(application, "5dcb9de5570df3121b000fbe", ChannelUtil.getChannel(), UMConfigure.DEVICE_TYPE_PHONE, "");
        NotificationUtils.createNotificationChannel();
        NotifyCleanManager.getInstance().sendRebindServiceMsg();

        initRoom(application);
        initNiuData(application);
        initOaid(application);
        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
//        TTAdManagerHolder.init(application);
        //商业sdk初始化
        GeekAdSdk.init(application, Constant.GEEK_ADSDK_PRODUCT_NAME, Constant.CSJ_AD_ID, ChannelUtil.getChannel(), BuildConfig.SYSTEM_EN.contains("prod"));
        initJsBridge();
        homeCatch(application);
        initLifecycle(application);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        String rootDir = MMKV.initialize(application);
//        Logger.i("zz---"+rootDir);
    }


    private static AppComponent mAppComponent;

    private void initInjector(Application application) {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModule(application))
                .build();
        mAppComponent.inject(application);

    }

    /**
     * js回调
     */
    private void initJsBridge() {
        JsBridgeConfig.getSetting().setProtocol("JWTJSBridge").registerDefaultModule(JsBridgeModule.class).debugMode(true);
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
        mAppDatabase = Room.databaseBuilder(application.getApplicationContext(), AppDataBase.class, "guanjia_cleanking.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
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
    public void initNiuData(Application application) {
        //测试环境
        NiuDataAPI.init(application, new Configuration()
                //切换到sdk默认的测试环境地址
                .serverUrl(AppConstants.BIGDATA_MD)
                .setHeartbeatUrl(AppConstants.BIGDATA_MD)
                //打开sdk日志信息
                .logOpen()
                .setHeartbeatInterval(5000)
                .channel(ChannelUtil.getChannel())
        );

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });

    }

    private String oaId = "";

    public void initOaid(Application application) {
        //判断是否为当前主进程
        String processName = SystemUtils.getProcessName(application);
        if (processName.equals(application.getPackageName())) {
            //设置oaid到埋点公共参数
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) { //6.0以上版本oaid
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

    //home键监听
    private long mLastClickTime = 0;

    public void homeCatch(Application application) {
        HomeWatcher mHomeWatcher = new HomeWatcher(application);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(application, LocalService.class);
                i.putExtra("action", "home");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    application.startForegroundService(i);
                } else {
                    application.startService(i);
                }
            }

            @Override
            public void onHomeLongPressed() {  //部分手机不走 onHomePressed();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(application, LocalService.class);
                i.putExtra("action", "home");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    application.startForegroundService(i);
                } else {
                    application.startService(i);
                }
            }
        });
        mHomeWatcher.startWatch();
    }


    private boolean mIsBack; //mIsBack = true 记录当前已经进入后台

    public void initLifecycle(Application application) {
        LifecycleHelper.registerActivityLifecycle(application, new LifecycleListener() {
            @Override
            public void onBecameForeground(Activity activity) {
                if (SystemUtils.getProcessName(application).equals(BuildConfig.APPLICATION_ID)) {
                    MmkvUtil.saveInt("isback",0);
                } else {//非当前主进程
                    return;
                }
                if (null == application || !mIsBack || ActivityCollector.isActivityExist(LockActivity.class) || ActivityCollector.isActivityExist(PopLayerActivity.class)
                        || ActivityCollector.isActivityExist(SplashADActivity.class) || ActivityCollector.isActivityExist(SplashADHotActivity.class) || activity.getLocalClassName().contains(".other") || activity.getLocalClassName().contains("FullPopLayerActivity")
                        || !PreferenceUtil.isNotFirstOpenApp())
                    return;
                if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                        && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                    for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
//                      if (PreferenceUtil.getHomeBackTime() && PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                        if (PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen() && !PreferenceUtil.isShowAD()) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(application.getApplicationContext(), SplashADHotActivity.class);
                            application.getApplicationContext().startActivity(intent);
                            mIsBack = false;
                        }
                    }
                }
                EventBus.getDefault().post(new LifecycEvent(true));
            }

            @Override
            public void onBecameBackground(Activity activity) {
                SPUtils.getInstance(application, "Lifecycle").put("acitivity_name", activity.getLocalClassName());
                if (!AppLifecycleUtil.isAppOnForeground(application)) {
                    //app 进入后台
                    mIsBack = true;
                    MmkvUtil.saveInt("isback",1);
                    PreferenceUtil.saveHomeBackTime();
                }
            }
        });
    }


}
