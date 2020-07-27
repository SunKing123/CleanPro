package com.xiaoniu.cleanking.ui.deskpop;

import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.rxjava.BackGroundIPulseObserver;
import com.xiaoniu.common.utils.ToastUtils;

/**
 *
 */
public class PowerStatePopChecker implements BackGroundIPulseObserver {

    long startTime;

    @Override
    public void onCreate() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPulse(long progress) {
        ToastUtils.showShort("心跳");
        LogUtils.i("zzz----心跳");
        long diff = System.currentTimeMillis() - startTime;
        diff = diff / 1000;
        if (diff > 10) {
            checkAndPop();
        }
    }

    private void checkAndPop() {
        ToastUtils.showShort("弹出手机状态activity");
    }


    @Override
    public void onDestroy() {

    }
}
