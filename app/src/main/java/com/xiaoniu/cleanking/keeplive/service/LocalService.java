package com.xiaoniu.cleanking.keeplive.service;

import android.annotation.SuppressLint;
<<<<<<< Updated upstream
import android.app.ActivityManager;
=======
>>>>>>> Stashed changes
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

<<<<<<< Updated upstream
import androidx.annotation.RequiresApi;

=======
>>>>>>> Stashed changes
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.umeng.commonsdk.debug.E;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.bean.AppPackageNameList;
<<<<<<< Updated upstream
import com.xiaoniu.cleanking.bean.AppPackageNameListDB;
=======
>>>>>>> Stashed changes
import com.xiaoniu.cleanking.keeplive.KeepAliveRuning;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.config.RunMode;
import com.xiaoniu.cleanking.keeplive.receive.NetWorkStateReceiver;
import com.xiaoniu.cleanking.keeplive.receive.NotificationClickReceiver;
import com.xiaoniu.cleanking.keeplive.receive.OnepxReceiver;
import com.xiaoniu.cleanking.keeplive.receive.TimingReceiver;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.GreenDaoManager;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.keeplive.KeepAliveAidl;

<<<<<<< Updated upstream
import java.text.SimpleDateFormat;
import java.util.Date;
=======
>>>>>>> Stashed changes
import java.util.List;

import static com.xiaoniu.cleanking.app.Constant.SCAN_SPACE_LONG;
import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;


public final class LocalService extends Service {
    private OnepxReceiver mOnepxReceiver;
    private ScreenStateReceiver screenStateReceiver;
    private BroadcastReceiver batteryReceiver;
    private NetWorkStateReceiver netWorkStateReceiver;

    private boolean isPause = true;//控制暂停
    private MediaPlayer mediaPlayer;
    private LocalBinder mBilder;
    private Handler handler;
    private String TAG = getClass().getSimpleName();
    private KeepAliveRuning mKeepAliveRuning;
    private int mBatteryPower = 50;  //当前电量监控
    private int temp = 30;           //点前电池温度
    private boolean isCharged = false;  //是否为充电状态

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("本地服务", "：本地服务启动成功");
        if (mBilder == null) {
            mBilder = new LocalBinder();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isPause = pm.isScreenOn();
        if (handler == null) {
            handler = new Handler();
        }
        //默认连接状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.WIFI_STATE, 1);


    }

<<<<<<< Updated upstream
//    private AppPackageNameListDB appPackageNameList;
//    private AppPackageNameListDB.DataBean mDataBean;

    private void JsonToBean() {
        isExeTask = true;
        if (GreenDaoManager.getInstance().isAppListNull()) {
            String json = FileUtils.readJSONFromAsset(this, "applist.json");
            try {
                AppPackageNameList appPackageNameList = new Gson().fromJson(json, AppPackageNameList.class);
                if (appPackageNameList != null) {
                    for (AppPackageNameListDB appPackageNameListDB : appPackageNameList.getData()) {
                        GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                    }
                }
            } catch (Exception e) {
=======
    private AppPackageNameList appPackageNameList;
    private AppPackageNameList.DataBean mDataBean;

    private void JsonToBean() {
        if (appPackageNameList != null && appPackageNameList.getData() != null && appPackageNameList.getData().size() > 0) {
        }else{
            String json = FileUtils.readJSONFromAsset(this, "applist.json");
            try {
                appPackageNameList = new Gson().fromJson(json, AppPackageNameList.class);
            } catch (Exception e) {

>>>>>>> Stashed changes
            }
        }
        handler.postDelayed(mTask, 10000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }
<<<<<<< Updated upstream

    private boolean isExeTask;

=======
  private boolean isExeTask;
>>>>>>> Stashed changes
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //播放无声音乐
        KeepAliveConfig.runMode = SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RUN_MODE);
        Log.d(TAG, "运行模式：" + KeepAliveConfig.runMode);
        if (mediaPlayer == null && KeepAliveConfig.runMode == RunMode.HIGH_POWER_CONSUMPTION) {
            mediaPlayer = MediaPlayer.create(this, R.raw.novioce);
            mediaPlayer.setVolume(0f, 0f);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.i(TAG, "循环播放音乐");
                    play();
                }
            });
            play();
        }
        //定时循环任务_位置注意
        sendTimingReceiver(intent, !(null == mOnepxReceiver));
        //网络状态监听
        netWorkStateListener();
        //像素保活
        if (mOnepxReceiver == null) {
            mOnepxReceiver = new OnepxReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(mOnepxReceiver, intentFilter);

        //屏幕点亮状态监听，用于单独控制音乐播放
        if (screenStateReceiver == null) {
            screenStateReceiver = new ScreenStateReceiver();
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("_ACTION_SCREEN_OFF");
        intentFilter2.addAction("_ACTION_SCREEN_ON");
        registerReceiver(screenStateReceiver, intentFilter2);


        //开启一个前台通知，用于提升服务进程优先级
        shouDefNotify();

        //绑定守护进程
        try {
            Intent intent3 = new Intent(this, RemoteService.class);
            this.bindService(intent3, connection, Context.BIND_ABOVE_CLIENT);
        } catch (Exception e) {
            Log.i("RemoteService--", e.getMessage());
        }
        //隐藏服务通知
        try {
            if (Build.VERSION.SDK_INT < 25) {
                startService(new Intent(this, HideForegroundService.class));
            }
        } catch (Exception e) {
            Log.i("HideForegroundService--", e.getMessage());
        }
<<<<<<< Updated upstream
        if (!isExeTask) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isSecurityPermissionOpen(this)) {
                    JsonToBean();
                }
            } else {
                JsonToBean();
            }
//            String auditSwitch = SPUtil.getString(getApplicationContext(), AppApplication.AuditSwitch, "1");
//            //过审开关打开状态
//            //!PreferenceUtil.isShowAD()广告展示状态
//            if (TextUtils.equals(auditSwitch, "1")) {
//                String adSwitch = MmkvUtil.getSwitchInfo();
//                //应用植入插屏全屏广告
//                InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_IMPLANTATION_FULL_SCREEN, adSwitch);
//                if (dataBean != null && dataBean.isOpen()) {
//                    isExeTask = true;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        if (isSecurityPermissionOpen(this)) {
//                            JsonToBean();
//                        }
//                    } else {
//                        JsonToBean();
//                    }
//                }
//            }
=======
        if (!isExeTask){
            JsonToBean();
>>>>>>> Stashed changes
        }

        if (mKeepAliveRuning == null)
            mKeepAliveRuning = new KeepAliveRuning();
        mKeepAliveRuning.onRuning();
        return START_STICKY;
    }


    private void play() {
        Log.i(TAG, "播放音乐");
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    //锁屏状态监听
    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("_ACTION_SCREEN_OFF")) {
                isPause = false;
                play();
                startActivity(context);
            } else if (intent.getAction().equals("_ACTION_SCREEN_ON")) {
                isPause = true;
                pause();
                startActivity(context);
            }
        }
    }


    private final class LocalBinder extends KeepAliveAidl.Stub {
        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            shouDefNotify();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent remoteService = new Intent(LocalService.this, RemoteService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalService.this.startForegroundService(remoteService);
            } else {
                LocalService.this.startService(remoteService);
            }
            Intent intent = new Intent(LocalService.this, RemoteService.class);
            LocalService.this.bindService(intent, connection, Context.BIND_ABOVE_CLIENT);
            PowerManager pm = (PowerManager) LocalService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn) {
                sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            } else {
                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                if (mBilder != null && KeepAliveConfig.foregroundNotification != null) {
                    KeepAliveAidl guardAidl = KeepAliveAidl.Stub.asInterface(service);
                    guardAidl.wakeUp(SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE),
                            SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.CONTENT),
                            SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RES_ICON));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 启动定时器
     *
     * @param intent
     * @param islaunched 判断是否已经启动
     */
    public void sendTimingReceiver(Intent intent, boolean islaunched) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (!islaunched || (null != intent && intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("heartbeat"))) {//心跳action
            checkCharge();
            watchingBattery();

            try {
                long triggerAtTime = SystemClock.elapsedRealtime() + (SCAN_SPACE_LONG * 1000);
                Intent i = new Intent(this, TimingReceiver.class);
                i.putExtra("action", "scan_heart");
                i.putExtra("temp", temp);
                i.putExtra("battery", mBatteryPower);
                i.putExtra("isCharged", isCharged);
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else {
                    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ((!islaunched || (null != intent && intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("unlock_screen")))) { //解锁操作action
            try {
                long triggerAtTime = SystemClock.elapsedRealtime() + (Constant.UNLOCK_SPACE_LONG * 3000);
                Intent inten = new Intent(this, TimingReceiver.class);
                inten.putExtra("action", "unlock_screen");
                inten.putExtra("temp", temp);
                inten.putExtra("battery", mBatteryPower);
                inten.putExtra("isCharged", isCharged);
                PendingIntent pi = PendingIntent.getBroadcast(this, NumberUtils.mathRandomInt(0, 100), inten, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else {
                    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if ((!islaunched || (null != intent && intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("home")))) { //home_键监听
            try {
                long triggerAtTime = SystemClock.elapsedRealtime() + (Constant.UNLOCK_SPACE_LONG * 1000);
                Intent inten = new Intent(this, TimingReceiver.class);
                inten.putExtra("action", "home");
                inten.putExtra("temp", temp);
                inten.putExtra("battery", mBatteryPower);
                inten.putExtra("isCharged", isCharged);
                PendingIntent pi = PendingIntent.getBroadcast(this, NumberUtils.mathRandomInt(0, 100), inten, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                } else {
                    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //开启前台通知
    private void shouDefNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeepAliveConfig.CONTENT = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.CONTENT);
            KeepAliveConfig.DEF_ICONS = SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RES_ICON, R.drawable.ic_launcher);
            KeepAliveConfig.TITLE = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            String title = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            Log.d("JOB-->" + TAG, "KeepAliveConfig.CONTENT_" + KeepAliveConfig.CONTENT + "    " + KeepAliveConfig.TITLE + "  " + title);
            if (TextUtils.isEmpty(KeepAliveConfig.TITLE)) {
                KeepAliveConfig.TITLE = getString(R.string.push_content_default_title);
            }
            if (TextUtils.isEmpty(KeepAliveConfig.CONTENT)) {
                KeepAliveConfig.CONTENT = getString(R.string.push_content_default_content);
            }

            //部分场景下KeepAliveConfig.TITLE 空字符,小米monkeybug修改
//           if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty(KeepAliveConfig.CONTENT)) {
            //启用前台服务，提升优先级
            Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
            intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Notification notification = NotificationUtils.createNotification(LocalService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
            startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
            Log.d("JOB-->", TAG + "显示通知栏");
//            }
        }
    }


    //判断是否充电
    public void checkCharge() {
        try {
            boolean usb = false;//usb充电
            boolean ac = false;//交流电
            boolean wireless = false; //无线充电
            int chargePlug = -1;

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            if (batteryReceiver == null) {
                batteryReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //获取当前电量，如未获取具体数值，则默认为0
                        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        //获取最大电量，如未获取到具体数值，则默认为100
                        int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                        mBatteryPower = (batteryLevel * 100 / batteryScale);
                        //获取当前电池温度
                        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                        int i = temp / 10;
                        temp = i > 0 ? i : 30 + NumberUtils.mathRandomInt(1, 3);
                    }
                };
            }
            //注册接收器以获取电量信息
            Intent powerIntent = registerReceiver(batteryReceiver, iFilter);
            //----判断是否为充电状态-------------------------------
            chargePlug = powerIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usb = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            ac = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            //无线充电---API>=17
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            }

            Logger.i(SystemUtils.getProcessName(this) + "zz--" + (usb ? "usb" : ac ? "ac" : wireless ? "wireless" : ""));
            isCharged = usb || ac || wireless;
        } catch (Exception e) {
            e.printStackTrace();
            isCharged = false;
        }

        //充电状态变更
        if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 0 && isCharged && !ActivityCollector.isActivityExist(FullPopLayerActivity.class)) {
            startFullInsertAd(this);
<<<<<<< Updated upstream
        } else if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 1 && !isCharged && !ActivityCollector.isActivityExist(FullPopLayerActivity.class)) {//拔电状态变更
=======
        } else if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 1 && !isCharged && !ActivityCollector.isActivityExist(PopLayerActivity.class)) {//拔电状态变更
>>>>>>> Stashed changes
            startFullInsertAd(this);
        }
        if (!BuildConfig.SYSTEM_EN.contains("prod"))
            ToastUtils.showShort("charge--" + (isCharged ? "充电中" : "未充电"));
        Logger.i("zz---charge--" + (isCharged ? "充电中" : "未充电"));
        //更新sp当前充电状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.CHARGE_STATE, isCharged ? 1 : 0);

    }

    //当前电量监控
    public void watchingBattery() {
        try {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBatteryPower = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        } catch (Exception e) {
            mBatteryPower = 50;
            e.printStackTrace();
        }
    }

    /**
     * 注册网络状态监听
     */
    public void netWorkStateListener() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(mOnepxReceiver);
        unregisterReceiver(screenStateReceiver);
        if (batteryReceiver != null)
            unregisterReceiver(batteryReceiver);

        if (netWorkStateReceiver != null)
            unregisterReceiver(netWorkStateReceiver);
        if (mKeepAliveRuning != null) {
            mKeepAliveRuning.onStop();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }


    //全局跳转锁屏页面
    public void startActivity(Context context) {
        try {
            String auditSwitch = SPUtil.getString(getApplicationContext(), AppApplication.AuditSwitch, "1");
            if (TextUtils.equals(auditSwitch, "1")) { //过审开关打开状态
                Intent screenIntent = new Intent();
                screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_LOCKACTIVITY);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                context.startActivity(screenIntent);
            }
        } catch (Exception e) {
            Log.e("LockerService", "start lock activity error:" + e.getMessage());
        }
    }

    //全局跳转全屏插屏页面
    public void startFullInsertAd(Context context) {
        try {
            String auditSwitch = SPUtil.getString(getApplicationContext(), AppApplication.AuditSwitch, "1");

            //过审开关打开状态
            //!PreferenceUtil.isShowAD()广告展示状态
            if (TextUtils.equals(auditSwitch, "1") && MmkvUtil.isShowFullInsert()) {

                String adSwitch = MmkvUtil.getSwitchInfo();
                //内外部插屏
                InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_INTERNAL_EXTERNAL_FULL, adSwitch);
                //外部插屏
                InsertAdSwitchInfoList.DataBean dataBean01 = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_EXTERNAL_FULL, adSwitch);

                if (null != context && null != dataBean) {//内外部插屏
                    int showTimes = 2;
                    if (dataBean.isOpen()) {
                        showTimes = dataBean.getShowRate();
                        if (MmkvUtil.fullInsertPageIsShow(showTimes)) {
                            startFullInsertIntent(context, PositionId.AD_EXTERNAL_ADVERTISING_03);
                        }
                    } else {
                        if (null != dataBean01) {       //外部插屏
                            if (dataBean01.isOpen()) {
                                showTimes = dataBean01.getShowRate();
                                //判断应用是否进入后台
                                int isBack = MmkvUtil.getInt("isback");
                                if (isBack != 1)
                                    return;

                                if (MmkvUtil.fullInsertPageIsShow(showTimes)) {
                                    startFullInsertIntent(context, PositionId.AD_EXTERNAL_ADVERTISING_02);
                                }

                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.e("LockerService", "start lock activity error:" + e.getMessage());
        }
    }


    public void startFullInsertIntent(Context context, String adStyle) {
        Intent screenIntent = new Intent();
        screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        screenIntent.putExtra("ad_style", adStyle);
        context.startActivity(screenIntent);
    }

    private String oldPackageName = "";
    @SuppressLint("HandlerLeak")
    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            String packageName = getAppInfo();
<<<<<<< Updated upstream
            if (!TextUtils.equals(oldPackageName, packageName)) {
                if (MmkvUtil.isShowFullInsert() && isContains(packageName)) {
                    oldPackageName = packageName;
                    addCPAD();
                }
=======
            if (isContains(packageName) && !TextUtils.equals(oldPackageName, packageName)) {
                oldPackageName = packageName;
                addCPAD();
>>>>>>> Stashed changes
            } else {
                oldPackageName = "";
            }
            handler.postDelayed(mTask, 10000);
        }
    };

    private boolean isContains(String packageName) {
<<<<<<< Updated upstream
        if (!GreenDaoManager.getInstance().isAppListNull()) {
            List<AppPackageNameListDB> list = GreenDaoManager.getInstance().queryAppList(packageName);
            if (list != null && list.size() > 0) {
                AppPackageNameListDB appPackageNameListDB = list.get(0);
                if (TextUtils.isEmpty(appPackageNameListDB.getTime())) {
                    appPackageNameListDB.setTime(dateFormater2.get().format(new Date()));
                    GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                    return true;
                } else {
                    if (isToday(list.get(0).getTime())) {
                        if (appPackageNameListDB.getIndex() < 2) {
                            appPackageNameListDB.setIndex(appPackageNameListDB.getIndex() + 1);
                            GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        appPackageNameListDB.setTime(dateFormater2.get().format(new Date()));
                        appPackageNameListDB.setIndex(1);
                        GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    private ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private UsageStatsManager mUsageStatsManager;

    public String getAppInfo() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mUsageStatsManager == null) {
                mUsageStatsManager = ((UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE));
            }
            if (mUsageStatsManager != null) {
                long now = System.currentTimeMillis();
                // get app data during 60s
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                // get present app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    return stats.get(j).getPackageName();
                }
            }
        } else {
            ActivityManager manager = (ActivityManager) this.getSystemService(this.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
            if (taskInfos.size() > 0)
                return taskInfos.get(0).topActivity.getPackageName();
            else
                return "";
        }
        return "null";
    }

    private boolean isOpen;


//    /**
//     * 判断是否系统app
//     *
//     * @param pkgName
//     */
//    private boolean isSystemApp(String pkgName) {
//        boolean isSystemApp = false;
//        PackageInfo pi = null;
//        try {
//            PackageManager pm = getApplicationContext().getPackageManager();
//            pi = pm.getPackageInfo(pkgName, 0);
//        } catch (Throwable t) {
//            Log.w(TAG, t.getMessage(), t);
//        }
//        // 是系统中已安装的应用
//        if (pi != null) {
//            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
//            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
//            isSystemApp = isSysApp || isSysUpd;
//        }
//        return isSystemApp;
//    }

=======
        if (appPackageNameList != null && appPackageNameList.getData() != null) {
            for (AppPackageNameList.DataBean dataBean : appPackageNameList.getData()) {
                if (packageName.contains(dataBean.getPackageName()) && dataBean.getIndex() < 2) {
                    mDataBean = dataBean;
                    return true;
                }
            }
        }
        return false;
    }

    private UsageStatsManager mUsageStatsManager;

    public String getAppInfo() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mUsageStatsManager != null) {
                long now = System.currentTimeMillis();
                // get app data during 60s
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                // get present app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    if (!isSystemApp(stats.get(j).getPackageName())) {
                        return stats.get(j).getPackageName();
                    } else {
                        return "null";
                    }
                }
            } else {
                mUsageStatsManager = ((UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE));
            }
        }
        return "null";
    }

    private boolean isOpen;


    /**
     * 判断是否系统app
     *
     * @param pkgName
     */
    private boolean isSystemApp(String pkgName) {
        boolean isSystemApp = false;
        PackageInfo pi = null;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            pi = pm.getPackageInfo(pkgName, 0);
        } catch (Throwable t) {
            Log.w(TAG, t.getMessage(), t);
        }
        // 是系统中已安装的应用
        if (pi != null) {
            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            isSystemApp = isSysApp || isSysUpd;
        }
        Log.w("hijacking", "系统app ==" + isSystemApp);
        return isSystemApp;
    }

>>>>>>> Stashed changes
    /**
     * 进行插入开屏广告
     */
    private void addCPAD() {
        if (!isOpen) {
            isOpen = true;
<<<<<<< Updated upstream
//            Intent inten = new Intent(this, TimingReceiver.class);
//            inten.putExtra("action", "add_cp_ad");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startFullActivty(LocalService.this);

//                    LocalService.this.sendBroadcast(inten);
=======
            Intent inten = new Intent(this, TimingReceiver.class);
            inten.putExtra("action", "add_cp_ad");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LocalService.this.sendBroadcast(inten);
                    mDataBean.setIndex(mDataBean.getIndex() + 1);
>>>>>>> Stashed changes
                    isOpen = false;
                }
            }, 2000);
        } else {
        }
    }
<<<<<<< Updated upstream


    private void startFullActivty(Context context) {
        //判断是否进入后台
        int isBack = MmkvUtil.getInt("isback");
        if (isBack != 1 || ActivityCollector.isActivityExistMkv(FullPopLayerActivity.class)) {
            return;
        }
        if (NetworkUtils.isNetConnected()) {
            Intent screenIntent = new Intent();
            screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_04);
            context.startActivity(screenIntent);
        } else {

        }
    }


    //判断用户对应的安全权限有没有打开
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isSecurityPermissionOpen(Context context) {
        long endTime = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, endTime);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }
=======
>>>>>>> Stashed changes


}
