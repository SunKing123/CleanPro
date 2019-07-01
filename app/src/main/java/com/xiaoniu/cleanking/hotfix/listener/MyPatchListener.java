package com.xiaoniu.cleanking.hotfix.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.text.TextUtils;

import com.xiaoniu.cleanking.hotfix.log.HotfixLogcat;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;


/**
 * Created by admin on 2017/6/30.
 */

public class MyPatchListener{
    public static final String TAG = "MyPatchListener";
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private static ScreenStateListener mScreenStateListener;
    private static MyPatchListener mInstance;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
    private MyPatchListener(Context context) {
        mContext = context.getApplicationContext();
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    public synchronized static MyPatchListener getInstance(Context context){
        if(mInstance == null){
            mInstance = new MyPatchListener(context);
        }
        return mInstance;
    }

    public MyPatchListener mScreenListener;

    public boolean isPatching() {
        return isPatching;
    }

    private boolean isPatching = false;
    private int tryCount = 3;
    public void initHotFixListener(){

        //Log.v(TAG, "initHotFixListener");
        HotfixLogcat.log("initHotFixListener");
        mScreenListener = MyPatchListener.getInstance(mContext);

        mScreenListener.begin(new MyPatchListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                //Log.e("onUserPresent", "onUserPresent");
                //Logger.e("onUserPresent");
                HotfixLogcat.log("onUserPresent");
            }

            @Override
            public void onScreenOn() {
                //Log.e("onScreenOn", "onScreenOn");
                //Logger.e("onScreenOn");
                HotfixLogcat.log("onScreenOn");
            }

            @Override
            public void onScreenOff() {
                //Log.e("onScreenOff", "onScreenOff");
                HotfixLogcat.log("onScreenOff");
                HotfixLogcat.log(path);
                HotfixLogcat.log( "isPatching " +isPatching);
                //Log.v(TAG, "onScreenOff" + path +" isPatching " +isPatching);
                if(!TextUtils.isEmpty(path) && !isPatching){
                    patching();
                }

            }
        });
    }

    public void patching(){
        isPatching = true;
        //Log.v(TAG, "patching try: " + tryCount);
        HotfixLogcat.log("patching try: " + tryCount);
        if(mContext != null){
            if(tryCount == 0){
                unregisterListener();
            }else{
                HotfixLogcat.log("onReceiveUpgradePatch" + tryCount);
                //Log.v(TAG, "onReceiveUpgradePatch" + tryCount);
                TinkerInstaller.onReceiveUpgradePatch(mContext, path);
            }
            tryCount--;
        }
    }

    public void reload(){
        //Log.v(TAG, "reload");
        HotfixLogcat.log("reload");
        if(mContext != null){
            ShareTinkerInternals.killAllOtherProcess(mContext);
//            Process.killProcess(Process.myPid());
        }
    }

    /**
     * screen状态广播接收者
     */
    public static class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                mScreenStateListener.onUserPresent();
            }
        }
    }

    /**
     * 开始监听screen状态
     *
     * @param listener
     */
    public void begin(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        //Display mDisplay = DISPLAY_SERVICE
        DisplayManager manager2 = (DisplayManager) mContext
                .getSystemService(Context.DISPLAY_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        if(mContext != null && mScreenReceiver != null){
            mContext.unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 启动screen状态广播接收器
     */
    public void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public interface ScreenStateListener {// 返回给调用者屏幕状态信息
        void onScreenOn();

        void onScreenOff();

        void onUserPresent();
    }
}
