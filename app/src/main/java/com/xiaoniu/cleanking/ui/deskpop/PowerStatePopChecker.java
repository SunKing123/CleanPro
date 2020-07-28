package com.xiaoniu.cleanking.ui.deskpop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppLifecyclesImpl;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 *
 */
public class PowerStatePopChecker implements BackGroundIPulseObserver {

    long startTime;
    private BroadcastReceiver batteryReceiver;
    private int mBatteryPower = 50;  //当前电量监控

    private boolean isCharged = false;  //是否为充电状态

    @Override
    public void onCreate() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPulse(long progress) {
        checkCharge();
    }

    private void checkAndPop() {
        ToastUtils.showShort("弹出手机状态activity");
    }


    @Override
    public void onDestroy() {

    }


    //判断是否充电
    public void checkCharge() {
        try {
            isCharged = false;
            boolean usb = false;//usb充电
            boolean ac = false;//交流电
            boolean wireless = false; //无线充电
            int chargePlug = -1;

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            if (batteryReceiver == null) {
                batteryReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
//                        //获取当前电量，如未获取具体数值，则默认为0
//                        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//                        //获取最大电量，如未获取到具体数值，则默认为100
//                        int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
//                        mBatteryPower = (batteryLevel * 100 / batteryScale);
                    }
                };
            }
            //注册接收器以获取电量信息
            Intent powerIntent = AppApplication.getInstance().registerReceiver(batteryReceiver, iFilter);
            //----判断是否为充电状态-------------------------------
            chargePlug = powerIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usb = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            ac = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            //无线充电---API>=17
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            }

//          Logger.i(SystemUtils.getProcessName(this) + "zz--" + (usb ? "usb" : ac ? "ac" : wireless ? "wireless" : ""));
            isCharged = usb || ac || wireless;
        } catch (Exception e) {
            e.printStackTrace();
            isCharged = false;
        }

        //充电状态变更
        if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 0 && isCharged) {
            startPowerInfo();
        } else if (PreferenceUtil.getInstants().getInt(SpCacheConfig.CHARGE_STATE) == 1 && !isCharged) {//拔电状态变更
        }
        if (!BuildConfig.SYSTEM_EN.contains("prod"))
            ToastUtils.showLong("charge--" + (isCharged ? "充电中" : "未充电"));
//          Logger.i("zz---charge--" + (isCharged ? "充电中" : "未充电"));
        //更新sp当前充电状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.CHARGE_STATE, isCharged ? 1 : 0);
    }

    /**
     * 跳转电池页面；
     */
    public void startPowerInfo() {
        if (AppLifecycleUtil.isAppOnForeground(AppApplication.getInstance())) {
            return;
        }
        int displayTim = 0;
        InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.PAGE_DESK_BATTERY_INFO);
        if (DeskPopConfig.getInstance().isBatteryCanPop()) {
            displayTim = dataBean.getDisplayTime();
            long lastShowTime = MmkvUtil.getLong(PositionId.PAGE_DESK_BATTERY_INFO_TIME, 0);
            if ((System.currentTimeMillis() - lastShowTime) >= 1000 * 60 * displayTim) {
                EventBus.getDefault().post(new PopEventModel("power"));
            }
        }
    }
}
