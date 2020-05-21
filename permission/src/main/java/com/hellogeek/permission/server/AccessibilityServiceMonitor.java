package com.hellogeek.permission.server;

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

import com.hellogeek.permission.bean.CustomSharedPreferences;
import com.hellogeek.permission.server.interfaces.IAccessibilityServiceMonitor;
import com.hellogeek.permission.strategy.ServiceEvent;
import com.hellogeek.permission.util.Constant;
import com.hellogeek.permission.util.PhoneRomUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;



/**
 * Desc:
 * <p>
 * <p>
 * Date: 2019/11/4
 * service在manifest中注册
 */
public class AccessibilityServiceMonitor extends AccessibilityService {
    private static final String TAG = "AccessibilityServiceMon";
    private int count = 0;
    private static IAccessibilityServiceMonitor mAccessibilityServiceMonitor;
    public static AccessibilityServiceMonitor instance;

    public static AccessibilityServiceMonitor getInstance() {
        if (instance == null) {
            Log.e(TAG, "AblService辅助服务未开启");
        }
        return instance;
    }


    public static void setAccessibilityEvent(IAccessibilityServiceMonitor accessibilityServiceMonitor) {
        if (accessibilityServiceMonitor != null) {
            mAccessibilityServiceMonitor = accessibilityServiceMonitor;
        }
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        if (Constant.inExecution) {
            if (PhoneRomUtils.getPhoneType() == PhoneRomUtils.MEIZU || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("Oppo A59m") || PhoneRomUtils.isXiaoMI9SDK29()|| PhoneRomUtils.isXiaoV11()) {
                backHandler.sendEmptyMessageDelayed(1, 300);
                backHandler.sendEmptyMessageDelayed(1, 600);
                backHandler.sendEmptyMessageDelayed(1, 900);
            } else {
                backHandler.sendEmptyMessageDelayed(1, 300);
                if (PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBEM00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBAT00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R9s")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PACT00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A37m")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R11") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PAFM00")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("PBAM00") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R11S")
                        || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A59S") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A57")
                        ||PhoneRomUtils.isOppo()|| PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A83")||PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R11t")  || PhoneRomUtils.isOppoR9() || PhoneRomUtils.isOppoReno() || PhoneRomUtils.isOppoR17() || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO A33") || PhoneRomUtils.getPhoneModel().equalsIgnoreCase("OPPO R7")) {
                    backHandler.sendEmptyMessageDelayed(2, 600);
                } else {
                    backHandler.sendEmptyMessageDelayed(1, 600);
                }
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!Constant.inExecution) {
            Log.i("permissionService", "false");
            return;
        }
        if (mAccessibilityServiceMonitor != null) {
            Log.i("permissionService", "true");
            mAccessibilityServiceMonitor.onEvent(event);
        }
    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        //服务开启时，调用
        //setServiceInfo();这个方法同样可以实现xml中的配置信息
        //可以做一些开启后的操作比如点两下返回
        EventBus.getDefault().post(new ServiceEvent(this));
        instance = this;
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
                        if ( Constant.inExecution) {
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                        Log.i("permissionService1", "GLOBAL_ACTION_BACK");
                    }
                    StatisticsUtils.customTrackEvent("accessibility_open_success", "无障碍权限开启成功", "cold_splash_page", "system_settings_page");
                    break;
                case 2:
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    if (!cn.getPackageName().equalsIgnoreCase(getPackageName())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (Constant.inExecution) {
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            }
                            Log.i("permissionService2", "GLOBAL_ACTION_BACK");
                        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
