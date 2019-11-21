package com.xiaoniu.cleanking.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.room.Room;

import com.alibaba.android.arouter.launcher.ARouter;
import com.apkfuns.jsbridge.JsBridgeConfig;
import com.bun.miitmdid.core.JLibrary;
import com.comm.jksdk.GeekAdSdk;
import com.geek.push.GeekPush;
import com.geek.push.core.PushConstants;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.jpush.JPushNotificationManager;
import com.xiaoniu.cleanking.jsbridge.module.JsBridgeModule;
import com.xiaoniu.cleanking.room.AppDataBase;
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
        TTAdManagerHolder.init(application);
        initProcess(application);
        //商业sdk初始化
        /*this, "18", "5036430", "jinritoutiao",   false*/
//        Context context, String productName, String csjAppId, String channel, boolean isFormal
        //todo_zzh
        GeekAdSdk.init(application, Constant.GEEK_ADSDK_PRODUCT_NAME,"5036430", ChannelUtil.getChannel(),  BuildConfig.SYSTEM_EN.contains("prod"));
        initJsBridge();
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

    /*Andriod P多进程访问webviwe;
     在对应的WebView数据目录后缀*/
    private void initProcess(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(application);
            if (!(BuildConfig.APPLICATION_ID + ":pushcore").equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
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
                .setHeartbeatMode(Configuration.HEARTBEAT_MODE_FOREGROUND)
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

    //获取当前进程名称
    public String getProcessName(Context context) {
        if (context == null) {
            return "";
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return "";
    }

}
