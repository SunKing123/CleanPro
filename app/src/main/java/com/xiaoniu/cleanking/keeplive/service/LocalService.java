package com.xiaoniu.cleanking.keeplive.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.bean.AppPackageNameList;
import com.xiaoniu.cleanking.bean.AppPackageNameListDB;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.keeplive.KeepAliveRuning;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.config.RunMode;
import com.xiaoniu.cleanking.keeplive.receive.NotificationClickReceiver;
import com.xiaoniu.cleanking.keeplive.receive.OnepxReceiver;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.GreenDaoManager;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.quick.QuickUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.keeplive.KeepAliveAidl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;


public final class LocalService extends Service {
    private OnepxReceiver mOnepxReceiver;
    private ScreenStateReceiver screenStateReceiver;


    private boolean isPause = true;//控制暂停
    private MediaPlayer mediaPlayer;
    private LocalBinder mBilder;
    private Handler handler;
    private String TAG = getClass().getSimpleName();
    private KeepAliveRuning mKeepAliveRuning;

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

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }


    private boolean isExeTask;

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
//        otherAppInsertAd();
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
                startActivity(context);
            } else if (intent.getAction().equals("_ACTION_SCREEN_ON")) {
                isPause = true;
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(mOnepxReceiver);
        unregisterReceiver(screenStateReceiver);

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
            if (!(ActivityCollector.currentActivity() instanceof LockActivity)) {
                String auditSwitch = SPUtil.getString(getApplicationContext(), SpCacheConfig.AuditSwitch, "1");
                boolean lock_sw = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_LOCK_SCREEN, PositionId.KEY_ADVERT_LOCK_SCREEN);//锁屏开关
                if (TextUtils.equals(auditSwitch, "1") && lock_sw) { //过审开关打开状态
                    Intent screenIntent = new Intent();
                    screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_LOCKACTIVITY);
                    screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    context.startActivity(screenIntent);
                }
            }

        } catch (Exception e) {
            Log.e("LockerService", "start lock activity error:" + e.getMessage());
        }
    }

    //全局跳转全屏插屏页面
    public void startFullInsertAd(Context context) {
        try {
            String auditSwitch = SPUtil.getString(getApplicationContext(), SpCacheConfig.AuditSwitch, "1");

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
                                int isBack = MmkvUtil.getInt("isback", -1);
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
//        context.startActivity(screenIntent);
    }

    /*----------------------------------------应用内植入插屏-----------------------------------------------------------------------------------------------------*/
    private long runTime = 0;
    @SuppressLint("HandlerLeak")
    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            runTime = System.currentTimeMillis();
            String packageName = getAppInfo();
            Log.e("dong", "packageName=" + packageName);
            if (!TextUtils.equals(packageName, LocalService.this.getPackageName())) {
//                if (!TextUtils.equals(oldPackageName, packageName)) {
                if (MmkvUtil.isShowFullInsert() && isContains(packageName)) {
//                    addCPAD();
                }
//                }
            }
            handler.postDelayed(mTask, 10000);
        }
    };

    private boolean isContains(String packageName) {
        if (!GreenDaoManager.getInstance().isAppListNull()) {
            List<AppPackageNameListDB> list = GreenDaoManager.getInstance().queryAppList(packageName);
            if (list != null && list.size() > 0) {
                AppPackageNameListDB appPackageNameListDB = list.get(0);
                if (TextUtils.isEmpty(appPackageNameListDB.getTime())) {
                    appPackageNameListDB.setTime(dateFormater.get().format(new Date()));
                    appPackageNameListDB.setIndex(1);
                    appPackageNameListDB.setDisplaytime(System.currentTimeMillis());
                    GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                    return true;
                } else {
                    if (isToday(list.get(0).getTime())) {
                        if (isBeforDispalyTime(appPackageNameListDB.getDisplaytime(), mDispalyTime) && appPackageNameListDB.getIndex() < (mShowRoate <= 0 ? 2 : mShowRoate)) {
                            appPackageNameListDB.setIndex(appPackageNameListDB.getIndex() + 1);
                            appPackageNameListDB.setDisplaytime(System.currentTimeMillis());
                            GreenDaoManager.getInstance().updateAppList(appPackageNameListDB);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        appPackageNameListDB.setTime(dateFormater.get().format(new Date()));
                        appPackageNameListDB.setIndex(1);
                        appPackageNameListDB.setDisplaytime(System.currentTimeMillis());
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
            String nowDate = dateFormater.get().format(today);
            String timeDate = dateFormater.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }


    /**
     * 判断给定字符串时间是是否大于指定的时间
     *
     * @param sdate
     * @return boolean
     */
    public boolean isBeforDispalyTime(long sdate, int intervalTime) {
        if (System.currentTimeMillis() - sdate >= intervalTime * 60 * 1000) {
            return true;
        }
        return false;
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

//
//    /**
//     * 进行插入开屏广告
//     */
//    private void addCPAD() {
//        if (!isOpen && MmkvUtil.isShowFullInsert()) {
//            isOpen = true;
//            Intent inten = new Intent(this, TimingReceiver.class);
//            inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            inten.putExtra("action", "app_add_full");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    LocalService.this.sendBroadcast(inten);
//                    isOpen = false;
//                }
//            }, 2000);
//        } else {
//        }
//    }

    private void startFullActivty(Context context) {
        //判断是否进入后台
        int isBack = MmkvUtil.getInt("isback", -1);
        if (isBack != 1 || ActivityCollector.isActivityExistMkv(FullPopLayerActivity.class)) {
            return;
        }
        if (NetworkUtils.isNetConnected()) {
            Toast.makeText(this, "应用内插屏!", Toast.LENGTH_LONG).show();
            Log.e("dong", "应用内插屏!");
            Intent screenIntent = new Intent();
            screenIntent.setClassName(context.getPackageName(), SchemeConstant.StartFromClassName.CLASS_FULLPOPLAYERACTIVITY);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            screenIntent.putExtra("ad_style", PositionId.AD_EXTERNAL_ADVERTISING_04);
//            context.startActivity(screenIntent);
        }
    }


    List<String> appMap = new ArrayList<>();

    private void setAppPackageName() {
        if (appMap.size() <= 0) {
            appMap.add("com.tencent.mm");
            appMap.add("com.ss.android.article.news");
            appMap.add("com.ss.android.ugc.aweme");
            appMap.add("com.smile.gifmaker");
            appMap.add("com.qiyi.video");
        }
    }

    private String getPackName(int packageName) {
        switch (packageName) {
            case 0:
                return "com.xiaoniu.cleanking.wx";
            case 1:
                return "com.xiaoniu.cleanking.tt";
            case 2:
                return "com.xiaoniu.cleanking.dy";
            case 3:
                return "com.xiaoniu.cleanking.ks";
            case 4:
                return "com.xiaoniu.cleanking.aqy";


        }
        return "";
    }

    private void isTimeSucess(int hour) {
        if (MmkvUtil.getLong("appiconTime", 0) == 0) {
            MmkvUtil.saveLong("appiconTime", System.currentTimeMillis());
            return;
        } else if (System.currentTimeMillis() - MmkvUtil.getLong("appiconTime", 0) < (hour * 60 * 1000)) {
            return;
        } else {
            MmkvUtil.saveLong("appiconTime", System.currentTimeMillis());
        }
        setAppIcon();
    }


    private void setAppIcon() {
//        Log.e("dong", "dispalyTime==" + hour);
        setAppPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PermissionUtils.isSecurityPermissionOpen(this)) {
                if (getAppProcessName(this)) {
                    int newIndex = MmkvUtil.getInt("appicon", 0);
                    int oldIndex = MmkvUtil.getInt("odlappicon", -1);
                    if (oldIndex == newIndex) {
                        return;
                    }
                    StatisticsUtils.customTrackEvent("split_icon_meet_the_opportunity_display", "分身图标满足创建时机时", "split_icon", "split_icon");
                    ComponentName apple = new ComponentName(getApplication(), getPackName(newIndex));
                    QuickUtils.getInstant(this).enableComponent(apple);

                    if (oldIndex >= 0) {
                        ComponentName now = new ComponentName(getApplication(), getPackName(oldIndex));
                        QuickUtils.getInstant(this).disableComponent(now);
                    }
                    if (newIndex >= appMap.size() - 1) {
                        MmkvUtil.saveInt("appicon", 0);
                    } else {
                        MmkvUtil.saveInt("appicon", newIndex + 1);
                    }
                    MmkvUtil.saveInt("odlappicon", newIndex);
                    StatisticsUtils.customTrackEvent("split_icon_establish", "分身图标创建时", "split_icon", "split_icon");
                    if (!MmkvUtil.getBool("isExecute", false)) {
                        MmkvUtil.saveBool("isExecute", true);
                    }
                } else if (MmkvUtil.getInt("appicon", 0) < (appMap.size() - 1)) {
                    MmkvUtil.saveInt("appicon", MmkvUtil.getInt("appicon", 0) + 1);
                    setAppIcon();
                } else {
                    MmkvUtil.saveInt("appicon", 0);
                }
            } else if (MmkvUtil.getInt("appicon", 0) == 0 && !MmkvUtil.getBool("isExecute", false)) {
                StatisticsUtils.customTrackEvent("split_icon_meet_the_opportunity_display", "分身图标满足创建时机时", "split_icon", "split_icon");
                ComponentName newAPP = new ComponentName(getApplication(), "com.xiaoniu.cleanking.wx");
                QuickUtils.getInstant(this).enableComponent(newAPP);
                MmkvUtil.saveInt("appicon", 1);
                MmkvUtil.saveInt("odlappicon", 0);
                MmkvUtil.saveBool("isExecute", true);
                StatisticsUtils.customTrackEvent("split_icon_establish", "分身图标创建时", "split_icon", "split_icon");
            }
        }
    }

    private String packageName;

    //因为我的手机是华为手机所以过滤掉了华为，大家可以按需求过滤
    public boolean getAppProcessName(Context context) {
        //当前应用pid
        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // get all apps
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < apps.size(); i++) {
            String name = apps.get(i).activityInfo.packageName;
            if (TextUtils.equals(appMap.get(MmkvUtil.getInt("appicon", 0)), name)) {
                return true;
            }
        }
        return false;
    }


    public void otherAppInsertAd() {
        if (!isExeTask || System.currentTimeMillis() - runTime > 15 * 1000) {
            String auditSwitch = SPUtil.getString(getApplicationContext(), SpCacheConfig.AuditSwitch, "1");
            //过审开关打开状态
            //!PreferenceUtil.isShowAD()广告展示状态
            if (TextUtils.equals(auditSwitch, "1")) {
                String adSwitch = MmkvUtil.getInsertSwitchInfo();
                //应用植入插屏全屏广告
                InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_IMPLANTATION_FULL_SCREEN, adSwitch);
                if (dataBean != null && dataBean.isOpen()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (PermissionUtils.isSecurityPermissionOpen(this)) {
                            JsonToBean(dataBean.getShowRate(), dataBean.getDisplayTime());
                        }
                    } else {
                        JsonToBean(dataBean.getShowRate(), dataBean.getDisplayTime());
                    }
                }
            }
        }
        String auditSwitch = SPUtil.getString(getApplicationContext(), SpCacheConfig.AuditSwitch, "1");
        //过审开关打开状态
        //!PreferenceUtil.isShowAD()广告展示状态
        if (TextUtils.equals(auditSwitch, "1")) {
            String adSwitch = MmkvUtil.getInsertSwitchInfo();
            //应用植入插屏全屏广告
            InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_PAGE_DESK_ICON, adSwitch);
            if (dataBean != null && dataBean.isOpen()) {
                int min = 120;
                if (MmkvUtil.getBool("isExecute", false)) {
                    min = dataBean.getShowRate();
                } else {
                    min = dataBean.getDisplayTime();
                }
                isTimeSucess(min);
            }
        }


    }

    //    private AppPackageNameListDB appPackageNameList;
//    private AppPackageNameListDB.DataBean mDataBean;
    private int mShowRoate = 0;
    private int mDispalyTime = 0;

    private void JsonToBean(int showRoate, int dispalyTime) {
        isExeTask = true;
        mShowRoate = showRoate;
        mDispalyTime = dispalyTime;
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
            }
        }
        handler.postDelayed(mTask, 1000);
    }

}
