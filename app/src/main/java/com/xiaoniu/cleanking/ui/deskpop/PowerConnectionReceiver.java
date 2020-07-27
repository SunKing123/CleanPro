package com.xiaoniu.cleanking.ui.deskpop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.ToastUtils;

/**
 * @author zzh
 * @date 2020/7/27 09
 * @mail：zhengzhihao@xiaoniuhy.com
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        //remove some variables if you don't need it.
        LogUtils.i("---zz---"+isCharging+"\n");
        ToastUtils.showLong("--zz--" + isCharging + "\n" + chargePlug + usbCharge + acCharge);
    }
}