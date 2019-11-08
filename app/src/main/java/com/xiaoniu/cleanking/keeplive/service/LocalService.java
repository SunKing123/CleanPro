package com.xiaoniu.cleanking.keeplive.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.keeplive.KeepAliveRuning;
import com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig;
import com.xiaoniu.cleanking.keeplive.config.NotificationUtils;
import com.xiaoniu.cleanking.keeplive.config.RunMode;
import com.xiaoniu.cleanking.keeplive.receive.NotificationClickReceiver;
import com.xiaoniu.cleanking.keeplive.receive.OnepxReceiver;
import com.xiaoniu.cleanking.keeplive.receive.TimingReceiver;
import com.xiaoniu.cleanking.keeplive.utils.SPUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.keeplive.KeepAliveAidl;

import static com.xiaoniu.cleanking.app.Constant.SCAN_SPACE_LONG;
import static com.xiaoniu.cleanking.keeplive.config.KeepAliveConfig.SP_NAME;


public final class LocalService extends Service {
    private OnepxReceiver mOnepxReceiver;
    private ScreenStateReceiver screenStateReceiver;
    private BroadcastReceiver batteryReceiver;
    private boolean isPause = true;//控制暂停
    private MediaPlayer mediaPlayer;
    private LocalBinder mBilder;
    private Handler handler;
    private String TAG = getClass().getSimpleName();
    private KeepAliveRuning mKeepAliveRuning;
    private int mBatteryPower = 50;  //当前电量监控
    private int temp = 30;
    private boolean isCharged =false;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

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


        //当前电量监控
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mBatteryPower = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }




        //----判断是否为充电状态---结束-------------------------------

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
        if (mKeepAliveRuning == null)
            mKeepAliveRuning = new KeepAliveRuning();
        mKeepAliveRuning.onRuning();
        sendTimingReceiver();
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

    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("_ACTION_SCREEN_OFF")) {
                isPause = false;
                play();
            } else if (intent.getAction().equals("_ACTION_SCREEN_ON")) {
                isPause = true;
                pause();
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
            Intent remoteService = new Intent(LocalService.this,
                    RemoteService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                LocalService.this.startForegroundService(remoteService);
            } else {
                LocalService.this.startService(remoteService);
            }
            Intent intent = new Intent(LocalService.this, RemoteService.class);
            LocalService.this.bindService(intent, connection,
                    Context.BIND_ABOVE_CLIENT);
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

    //启动定时器
    public void sendTimingReceiver(){
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
                    temp = i > 0 ? i : 30+ NumberUtils.mathRandomInt(1,3);
                }
            };
        }
        //注册接收器以获取电量信息
        Intent powerIntent = registerReceiver(batteryReceiver, iFilter);
        //----判断是否为充电状态-------------------------------
        int chargePlug = powerIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
        boolean usb = chargePlug ==BatteryManager.BATTERY_PLUGGED_USB;//usb充电
        boolean ac = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;//交流电
        //无线充电---API>=17
        boolean wireless = false;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        isCharged = usb||ac||wireless;


        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long spacelong = SCAN_SPACE_LONG * 15 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + spacelong;
        Intent i = new Intent(this, TimingReceiver.class);
        i.putExtra("battery",mBatteryPower);
        i.putExtra("temp",temp);
        i.putExtra("isCharged",isCharged);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
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

    private void shouDefNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeepAliveConfig.CONTENT = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.CONTENT);
            KeepAliveConfig.DEF_ICONS = SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RES_ICON, R.drawable.ic_launcher);
            KeepAliveConfig.TITLE = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            String title = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            Log.d("JOB-->" + TAG, "KeepAliveConfig.CONTENT_" + KeepAliveConfig.CONTENT + "    " + KeepAliveConfig.TITLE + "  " + title);
            if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty(KeepAliveConfig.CONTENT)) {
                //启用前台服务，提升优先级
                Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
                intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                Notification notification = NotificationUtils.createNotification(LocalService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
                startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
                Log.d("JOB-->", TAG + "显示通知栏");
            }
        }
    }






}
