package com.xiaoniu.cleanking.ui.external;

import com.jess.arms.base.App;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundPulseTimer;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.common.utils.ToastUtils;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 */
public class PhoneStatePopChecker implements BackGroundIPulseObserver{

    long startTime;

    @Override
    public void onCreate() {
        startTime=System.currentTimeMillis();
    }

    @Override
    public void onPulse(long progress) {
        long diff=System.currentTimeMillis()-startTime;
        diff=diff/1000;
        if(diff>10){
            checkAndPop();
        }
    }

    private void checkAndPop(){
        ExternalPhoneStateActivity.start(AppApplication.getInstance());
    }

    @Override
    public void onDestroy() {

    }
}
