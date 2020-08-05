package com.xiaoniu.cleanking.ui.deskpop.deviceinfo;

import com.xiaoniu.clean.deviceinfo.EasyBatteryMod;
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopConfig;
import com.xiaoniu.cleanking.ui.deskpop.base.DeskPopLogger;
import com.xiaoniu.cleanking.ui.newclean.model.PopEventModel;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundPulseTimer;

import org.greenrobot.eventbus.EventBus;

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
        int displayTime= DeskPopConfig.getInstance().getStateDisplayTime();
        if(displayTime==0){
            unRegister();
            return;
        }
        long diffMinutes = (System.currentTimeMillis() - startTime)/1000/60;

        boolean canPop=diffMinutes >=displayTime;

        DeskPopLogger.log("===============in the PhoneStatePopChecker: canPop="+canPop+"   diff="+diffMinutes+"    displayTime="+displayTime+"     needPop()"+needPop());

        if (canPop) {
            checkAndPop();
        }
    }

    private void checkAndPop() {
        if (needPop()) {
            EventBus.getDefault().post(new PopEventModel("deviceInfo"));
            startTime = System.currentTimeMillis();
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
        percent=100-percent;
        if (percent >= 50) {
            return true;
        }

        //当内部存储使用占比≥70%时
        total = easyMemoryMod.getTotalInternalMemorySize();
        available = easyMemoryMod.getAvailableInternalMemorySize();
        percent = (available / total) * 100;
        percent=100-percent;
        if (percent >= 50) {
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
