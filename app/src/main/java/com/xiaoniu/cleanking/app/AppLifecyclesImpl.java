/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoniu.cleanking.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.room.Room;

import com.alibaba.android.arouter.launcher.ARouter;
import com.apkfuns.jsbridge.JsBridgeConfig;
import com.blankj.utilcode.util.Utils;
import com.bun.miitmdid.core.JLibrary;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.InitListener;
import com.comm.jksdk.http.utils.LogUtils;
import com.geek.push.GeekPush;
import com.geek.push.core.PushConstants;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.Integrate.interfaces.PermissionRecordCallback;
import com.jess.arms.base.delegate.AppLifecycles;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.umeng.commonsdk.UMConfigure;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.jpush.JPushNotificationManager;
import com.xiaoniu.cleanking.jsbridge.module.JsBridgeModule;
import com.xiaoniu.cleanking.keeplive.receive.NetworkCallbackImpl;
import com.xiaoniu.cleanking.keeplive.service.LocalService;
import com.xiaoniu.cleanking.keeplive.utils.HomeWatcher;
import com.xiaoniu.cleanking.keeplive.utils.OnHomePressedListener;
import com.xiaoniu.cleanking.lifecyler.LifecycleHelper;
import com.xiaoniu.cleanking.lifecyler.LifecycleListener;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.room.AppDataBase;
import com.xiaoniu.cleanking.room.clean.AppPathDataBase;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.deskpop.PowerStatePopChecker;
import com.xiaoniu.cleanking.ui.external.PhoneStatePopChecker;
import com.xiaoniu.cleanking.ui.external.PhoneStateSwitch;
import com.xiaoniu.cleanking.ui.localpush.LocalPushService;
import com.xiaoniu.cleanking.ui.localpush.PopPushActivity;
import com.xiaoniu.cleanking.ui.localpush.RomUtils;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.ui.newclean.activity.ExternalSceneActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.MiitHelper;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundPulseTimer;
import com.xiaoniu.cleanking.utils.rxjava.RxTimer;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.payshare.PayShareApplication;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.statistic.NiuDataTrackEventCallBack;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

import static com.xiaoniu.cleanking.constant.Constant.QQ_APPID;
import static com.xiaoniu.cleanking.constant.Constant.QQ_APPKEY;
import static com.xiaoniu.cleanking.constant.Constant.SHANYAN_APPID;
import static com.xiaoniu.cleanking.constant.Constant.SINA_APPID;
import static com.xiaoniu.cleanking.constant.Constant.SINA_APPSECRET;
import static com.xiaoniu.cleanking.constant.Constant.UMENG_APPKEY;
import static com.xiaoniu.cleanking.constant.Constant.WEICHAT_APPID;
import static com.xiaoniu.cleanking.constant.Constant.WEICHAT_APPSECRET;

/**
 * ================================================
 * 展示 {@link AppLifecycles} 的用法
 * ================================================
 */
public class AppLifecyclesImpl implements AppLifecycles {

    private static AppDataBase mAppDatabase;
    private static AppPathDataBase mAppPathDataBase;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static AppComponent mAppComponent;

    @Override
    public void attachBaseContext(@NonNull Context base) {
        //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
        MultiDex.install(base);


    }

    @Override
    public void onCreate(@NonNull Application application) {
        ContextUtils.initApplication(application);
        logConfig();
        PayShareApplication.getInstance().initPayShare(application, UMENG_APPKEY, ChannelUtil.getChannel(), UMConfigure.DEVICE_TYPE_PHONE, "")
                .setWeixin(WEICHAT_APPID, WEICHAT_APPSECRET)
                .setQQZone(QQ_APPID, QQ_APPKEY)
                .setSinaWeibo(SINA_APPID, SINA_APPSECRET, "http://xiaoniu.com");
//        PlatformConfig.setWeixin("wx19414dec77020d03", "090f560fa82e0dfff2f0cb17e43747c2");
//        PlatformConfig.setQQZone("1109516379", "SJUCaQdURyRd8Dfi");
//        PlatformConfig.setSinaWeibo("1456333364", "bee74e1ccd541f657875803a7eb32b1b", "http://xiaoniu.com");
//        UMShareAPI.get(application);
        initInjector(application);

        //初始化sdk
        initGeekPush(application);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(application);
//        UMConfigure.init(application, "5dcb9de5570df3121b000fbe", ChannelUtil.getChannel(), UMConfigure.DEVICE_TYPE_PHONE, "");
        NotificationUtils.createNotificationChannel();
        NotifyCleanManager.getInstance().sendRebindServiceMsg();
        setErrorHander();
        initRoom(application);
//        initNiuData(application);


        String processName = SystemUtils.getProcessName(application);
        if (processName.equals(application.getPackageName())) {
            //商业化初始化
            MidasRequesCenter.init(application);
            initOaid(application);
        }


//        initOaid(application);
        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
//        TTAdManagerHolder.init(application);
//        LogUtils.i("GeekSdk--"+SystemUtils.getProcessName(application));
//        initAdSdk(application);
        initJsBridge();
        homeCatch(application);
        /* if (RomUtils.checkIsHuaWeiRom()) {
            homeCatch(application);
        } else if (isMainProcess(application)) {
            homeCatchOtherDevice(application);
        }*/
        initLifecycle(application);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        String rootDir = MMKV.initialize(application);

        initPermission(application);
        initShanYan(application);

        //初始化utilCode
        Utils.init(application);

        registerWifiConnect(application);

    }


    private void registerWifiConnect(Application application) {
        String processName = SystemUtils.getProcessName(application);
        if (processName.equals(application.getPackageName())) {
            NetworkCallbackImpl networkCallback = new NetworkCallbackImpl(application);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager connMgr = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                connMgr.registerNetworkCallback(request, networkCallback);
            }
        }

    }

    /**
     * 初始化闪验一键登录
     */
    private void initShanYan(Application application) {
        OneKeyLoginManager.getInstance().init(application, SHANYAN_APPID, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                LogUtils.e("闪验初始化结果: code==" + code + "==result==" + result);
            }
        });
    }


    private void initInjector(Application application) {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModule(application))
                .build();
        mAppComponent.inject(application);

    }


    public void initPermission(Application application) {
        //权限相关初始化
        PermissionIntegrate.getInstance(application)
                .setPermissionList(Permission.SUSPENDEDTOAST, Permission.SELFSTARTING,
                        Permission.NOTIFICATIONREAD, Permission.PACKAGEUSAGESTATS)
                .setPermissionRecordCallback(new PermissionRecordCallback() {
                    @Override
                    public void usagePermissionRecord(int usageType, String currentPage, String sourcePage, String eventCode, String eventName, Map<String, String> extraMap) {

                    }
                });
    }

    //商业sdk初始化
//    public void initAdSdk(Application application) {
//        String processName = SystemUtils.getProcessName(application);
//        if (!processName.equals(application.getPackageName()))
//            return;
//        GeekAdSdk.init(application, Constant.GEEK_ADSDK_PRODUCT_NAME, Constant.CSJ_AD_ID, Constant.YLH_AD_ID, ChannelUtil.getChannel(), BuildConfig.SYSTEM_EN);
//        //广告sdk_Bid只设置一次
//        if (GeekAdSdk.getBid() < 0) {
//            GeekAdSdk.setBid(NumberUtils.mathRandomInt(0, 99));
//        }
//        ContextUtils.initAdBid(GeekAdSdk.getBid());
//    }


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

    //room初始化
    private void initRoom(Application application) {
        try {
            mAppDatabase = Room.databaseBuilder(application.getApplicationContext(), AppDataBase.class, "guanjia_cleanking.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

            mAppPathDataBase = Room.databaseBuilder(application.getApplicationContext(), AppPathDataBase.class, "convert0617.db")
                    .createFromAsset("databases/convert0617.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 全局神值rxjava下游取消订阅后抛出异常统一处理
     */
    private void setErrorHander() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("e: " + throwable.getMessage());
            }
        });
    }


    public static AppDataBase getAppDatabase() {
        return mAppDatabase;
    }

    public static AppPathDataBase getAppPathDatabase() {
        return mAppPathDataBase;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onTerminate(@NonNull Application application) {
        // Do nothing because of nothing
    }

 /*
    //商业化sdk已经完成初始化；
    埋点初始化
    public void initNiuData(Application application) {
        //测试环境
        NiuDataAPI.init(application, new Configuration()
                //切换到sdk默认的测试环境地址
                .serverUrl(BuildConfig.BIGDATA_MD)
                .setHeartbeatUrl(BuildConfig.BIGDATA_MD)
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

    }*/

    private static String oaId = "";
    private static boolean isSupportOaid = true;

    public static String getOaid() {
        return oaId;
    }

    public static boolean isSupportOaid() {
        return isSupportOaid;
    }

    public static void setIsSupportOaid(boolean isSupportOaid) {
        AppLifecyclesImpl.isSupportOaid = isSupportOaid;
    }

    public void initOaid(Application application) {
        //判断是否为当前主进程
        String processName = SystemUtils.getProcessName(application);
        if (processName.equals(application.getPackageName())) {
            //设置oaid到埋点公共参数
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) { //9.0以上版本oaid
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
    private long mLastClickTime = 0;  //只在local 进程中生效

    public void homeCatch(Application application) {
        if (!SystemUtils.getProcessName(application).contains("local")) {  //只在local进程中监听home按键，避免重复调用
            return;
        }
        HomeWatcher mHomeWatcher = new HomeWatcher(application);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                LogUtils.e("====onHomePressed键被触发====");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                long currentTimestamp = System.currentTimeMillis();
                if (AppLifecycleUtil.isAppOnForeground(application)) {
                    MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, currentTimestamp);
                } else {
                    MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, 0L);
                }
                Intent i = new Intent(application, LocalService.class);
                i.putExtra("action", "home");
                i.putExtra("homePressed", currentTimestamp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    application.startForegroundService(i);
                } else {
                    application.startService(i);
                }
            }

            @Override
            public void onHomeLongPressed() {  //部分手机不走 onHomePressed();
                LogUtils.e("=====onHomeLongPressed键被触发====");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                long currentTimestamp = System.currentTimeMillis();
                if (AppLifecycleUtil.isAppOnForeground(application)) {
                    MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, currentTimestamp);
                }
                Intent i = new Intent(application, LocalService.class);
                i.putExtra("action", "home");
                i.putExtra("homePressed", currentTimestamp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    application.startForegroundService(i);
                } else {
                    application.startService(i);
                }
            }
        });
        mHomeWatcher.startWatch();
    }

    private LocalPushService.PushBinder mLocalBinder;

    public void homeCatchOtherDevice(Application application) {
        HomeWatcher mHomeWatcher = new HomeWatcher(application);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                LogUtils.e("====localPushService onHomePressed键被触发====");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                long currentTimestamp = System.currentTimeMillis();
                if (AppLifecycleUtil.isAppOnForeground(application)) {
                    MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, currentTimestamp);
                }
                if (isMainProcess(application)) {
                    bindLocalPushService(application, currentTimestamp);
                }

            }

            @Override
            public void onHomeLongPressed() {  //部分手机不走 onHomePressed();
                LogUtils.e("=====localPushService onHomeLongPressed键被触发====");
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                long currentTimestamp = System.currentTimeMillis();
                if (AppLifecycleUtil.isAppOnForeground(application)) {
                    MmkvUtil.saveLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, currentTimestamp);
                }
                if (isMainProcess(application)) {
                    bindLocalPushService(application, currentTimestamp);
                }
            }
        });
        mHomeWatcher.startWatch();
    }


    private void bindLocalPushService(Application application, long pressTime) {
        if (!RomUtils.checkIsHuaWeiRom()) {
            if (mLocalBinder != null) {
                mLocalBinder.getService().showPopActivity(pressTime);
            } else {
                Intent intent = new Intent(application, LocalPushService.class);
                LocalPushConnection connection = new LocalPushConnection(pressTime);
                application.bindService(intent, connection, Context.BIND_ABOVE_CLIENT);
            }
        }
    }


    private class LocalPushConnection implements ServiceConnection {

        private long mHomePressHomeTime;


        public LocalPushConnection(long time) {
            this.mHomePressHomeTime = time;

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocalBinder = (LocalPushService.PushBinder) service;
            if (mLocalBinder != null) {
                mLocalBinder.getService().showPopActivity(mHomePressHomeTime);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private boolean mIsBack; //mIsBack = true 记录当前已经进入后台

    public void initLifecycle(Application application) {
        LifecycleHelper.registerActivityLifecycle(application, new LifecycleListener() {
            @Override
            public void onBecameForeground(Activity activity) {
                if (SystemUtils.getProcessName(application).equals(BuildConfig.APPLICATION_ID)) {//当前主进程
                    MmkvUtil.saveInt("isback", 0);
                    LogUtils.i("-cgName-----进入前台");

                    BackGroundPulseTimer.getInstance().destroy();

                } else {//非当前主进程
                    return;
                }
                if (null == application || !mIsBack || ActivityCollector.isActivityExist(LockActivity.class)
                        || ActivityCollector.isActivityExist(PopLayerActivity.class)
                        || ActivityCollector.isActivityExist(SplashADActivity.class)
                        || ActivityCollector.isActivityExist(SplashADHotActivity.class)
                        || ActivityCollector.isActivityExist(FullPopLayerActivity.class)
                        || ActivityCollector.isActivityExist(PopPushActivity.class)
                        || ActivityCollector.isActivityExist(ExternalSceneActivity.class)
                        || activity.getLocalClassName().contains(".wx")
                        || activity.getLocalClassName().contains(".aqy")
                        || activity.getLocalClassName().contains(".ks")
                        || activity.getLocalClassName().contains(".tt")
                        || activity.getLocalClassName().contains(".dy")
                        || activity.getLocalClassName().contains("FullPopLayerActivity")
                        || !PreferenceUtil.isNotFirstOpenApp())
                    return;

                if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                        && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                    for (SwitchInfoList.DataBean switchInfo : AppHolder.getInstance().getSwitchInfoList().getData()) {
//                      if (PreferenceUtil.getHomeBackTime() && PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                        if (PositionId.HOT_CODE.equals(switchInfo.getAdvertPosition()) && switchInfo.isOpen() && PreferenceUtil.getHomeBackTime(switchInfo.getHotStartInterval())) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(application.getApplicationContext(), SplashADHotActivity.class);
                            intent.putExtra("activityName", activity.getLocalClassName());
                            application.getApplicationContext().startActivity(intent);
                            mIsBack = false;
                            //only hot launch to send LifecycleEvent
                            EventBus.getDefault().post(new LifecycEvent(true));

                        }
                    }
                }
            }

            @Override
            public void onBecameBackground(Activity activity) {
                if (!SystemUtils.getProcessName(application).equals(BuildConfig.APPLICATION_ID)) {//当前主进程
                    return;
                }
                if (!AppLifecycleUtil.isAppOnForeground(application)) {
                    //app 进入后台
                    mIsBack = true;
                    MmkvUtil.saveInt("isback", 1);
                    PreferenceUtil.saveHomeBackTime();
                   
                    BackGroundPulseTimer timer = BackGroundPulseTimer.getInstance();
                    if (PhoneStateSwitch.isOpen()) {
                        timer.register(new PhoneStatePopChecker());
                    }
                    timer.register(new PowerStatePopChecker()).startTimer();
                }
            }
        });
    }


    public static void post(Runnable runnable) {
        if (sHandler != null && runnable != null) {
            sHandler.post(runnable);
        }
    }

    public static void postDelay(Runnable runnable, long delayTime) {
        if (sHandler != null && runnable != null) {
            sHandler.postDelayed(runnable, delayTime);
        }
    }

    public static void removeTask(Runnable runnable) {
        if (sHandler != null && runnable != null) {
            sHandler.removeCallbacks(runnable);
        }
    }

    private boolean isMainProcess(Application application) {
        String processName = SystemUtils.getProcessName(application);
        return processName.equals(application.getPackageName());
    }

    public void logConfig() {
        if (BuildConfig.DEBUG) {//Timber初始化
            //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
            //并且支持添加多个日志框架(打印策略),做到外部调用一次 CoolingApi,内部却可以做到同时使用多个策略
            //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
            Timber.plant(new Timber.DebugTree());
            // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
////                    Logger.addLogAdapter(new AndroidLogAdapter());
//                    Timber.plant(new Timber.DebugTree() {
//                        @Override
//                        protected void log(int priority, String tag, String message, Throwable t) {
//                            Logger.log(priority, tag, message, t);
//                        }
//                    });
            ButterKnife.setDebug(true);
        }
    }


}
