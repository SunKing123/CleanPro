package com.xiaoniu.cleanking.ui.deskpop.state;

import com.xiaoniu.clean.deviceinfo.EasyBatteryMod;
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.deskpop.DeskPopConfig;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundPulseTimer;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 */
public class PhoneStatePopChecker implements BackGroundIPulseObserver {

    long startTime;

    @Override
    public void onCreate() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPulse(long progress) {
        int time= DeskPopConfig.getStateConfig().getDisplayTime();
        if(time==0){
            unRegister();
            return;
        }
        long diff = System.currentTimeMillis() - startTime;
        diff = diff / 1000;
        diff = diff / 60;

        boolean canPop=diff >=time;

        LogUtils.e("===============pulseTimer   in the PhoneStatePopChecker: canPop="+canPop+"   diff="+diff+"    displayTime="+time);
        if (canPop) {
            checkAndPop();
        }
    }

    private void checkAndPop() {
        if (needPop()) {
            unRegister();
            ExternalPhoneStateActivity.start(AppApplication.getInstance());
        }
    }

    private void unRegister(){
        BackGroundPulseTimer.getInstance().unRegister(this);
    }

    @Override
    public void onDestroy() {

    }

    //条件判断
    private boolean needPop() {
        //当运行内存使用占比≥70%时
        EasyMemoryMod easyMemoryMod = new EasyMemoryMod(AppApplication.getInstance());
        double total = easyMemoryMod.getTotalRAM();
        double available = easyMemoryMod.getAvailableRAM();
        double percent = (available/total) * 100;
        if (percent >= 70) {
            return true;
        }

        //当内部存储使用占比≥70%时
        total = easyMemoryMod.getTotalInternalMemorySize();
        available = easyMemoryMod.getAvailableInternalMemorySize();
        percent = (available / total) * 100;
        if (percent >= 70) {
            return true;
        }

        //当电池电量＜20%
        EasyBatteryMod easyBatteryMod = new EasyBatteryMod(AppApplication.getInstance());
        if (easyBatteryMod.getBatteryPercentage() < 20) {
            return true;
        }

        //当电池温度＞37°
        if (easyBatteryMod.getBatteryTemperature() > 37) {
            return true;
        }

        return false;
    }
}
