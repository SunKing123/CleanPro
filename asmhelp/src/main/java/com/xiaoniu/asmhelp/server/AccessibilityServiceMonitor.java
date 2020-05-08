package com.xiaoniu.asmhelp.server;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.xiaoniu.asmhelp.util.PhoneRomUtils;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/6/28
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class AccessibilityServiceMonitor extends AccessibilityService {
    private static final String TAG = "AccessibilityServiceMon";
    private static AccessibilityServiceMonitor mAccessibilityServiceMonitor;
    private int count = 0;

    public static AccessibilityServiceMonitor getInstance() {
        if (mAccessibilityServiceMonitor == null) {
//            throw new NullPointerException("AblService辅助服务未开启");
        }
        return mAccessibilityServiceMonitor;
    }


    @Override
    public void onCreate() {
        mAccessibilityServiceMonitor = this;
        Log.d(TAG, "onCreate: ");
        if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.MEIZU || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Oppo A59m")) {
            backHandler.sendEmptyMessageDelayed(1, 300);
            backHandler.sendEmptyMessageDelayed(1, 600);
            backHandler.sendEmptyMessageDelayed(1, 900);
        } else {
            backHandler.sendEmptyMessageDelayed(1, 300);
            if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBEM00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R9s")
                    || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PACT00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A37m")
                    || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R11")|| PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PAFM00")
                    || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBAM00")|| PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R11S")
                    || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A59S") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A57")
                    || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A83")) {
                backHandler.sendEmptyMessageDelayed(2, 600);
            } else {
                backHandler.sendEmptyMessageDelayed(1, 600);
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        int eventType = event.getEventType();
//        int typeWindowStateChanged = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        mAccessibilityServiceMonitor = this;
        //服务开启时，调用
        //setServiceInfo();这个方法同样可以实现xml中的配置信息
        //可以做一些开启后的操作比如点两下返回
        Log.d(TAG, "onServiceConnected: ");


    }

    @SuppressLint("HandlerLeak")
    Handler backHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            count++;
            if (count > 4) {
                return;
            }
            switch (msg.what) {
                case 1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    break;
                case 2:
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    if (!cn.getPackageName().equalsIgnoreCase(getPackageName())) {
                        backHandler.sendEmptyMessageDelayed(2, 300);
                    }

                    break;
            }

        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        //关闭服务时,调用
        //如果有资源记得释放
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }

}
