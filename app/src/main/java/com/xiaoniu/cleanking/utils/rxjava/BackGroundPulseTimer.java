package com.xiaoniu.cleanking.utils.rxjava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 * 后台心跳回调，15秒一次
 */
public class BackGroundPulseTimer {

    List<BackGroundIPulseObserver> observers = new ArrayList<>();
    RxTimer timer;
    private static BackGroundPulseTimer backGroundPulseTimer;

    public static BackGroundPulseTimer getInstance() {
        if (backGroundPulseTimer == null) {
            backGroundPulseTimer = new BackGroundPulseTimer();
        }
        return backGroundPulseTimer;
    }

    private BackGroundPulseTimer() {
        timer = new RxTimer();
    }

    public void startTimer() {
        timer.interval(1000, this::callBack);
    }

    private void callBack(long number) {
        for (BackGroundIPulseObserver observer : observers) {
            observer.onPulse(number);
        }
    }

    public BackGroundPulseTimer register(BackGroundIPulseObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.onCreate();
        }
        return backGroundPulseTimer;
    }

    private void unRegisterAll() {
        for (BackGroundIPulseObserver observer : observers) {
            observer.onDestroy();
            observers.remove(observer);
        }
    }

    public void destroy() {
        unRegisterAll();
        timer.cancel();
        timer = null;
    }
}
