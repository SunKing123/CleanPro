package com.xiaoniu.cleanking.utils.rxjava;

import com.xiaoniu.cleanking.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 * 后台心跳回调，15秒一次
 */
public class BackGroundPulseTimer {

    private List<BackGroundIPulseObserver> observers = new ArrayList<>();
    private RxTimer timer;
    final int MILLI_SECONDS = 15000;
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
        timer.interval(MILLI_SECONDS, this::callBack);
        LogUtils.e("===============pulseTimer  startTimer()==========");
    }

    private void callBack(long number) {

        LogUtils.e("===============pulseTimer  callBack()==========");

        for (BackGroundIPulseObserver observer : observers) {
            observer.onPulse(number);
        }

    }

    public BackGroundPulseTimer register(BackGroundIPulseObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.onCreate();
        }
        LogUtils.e("===============pulseTimer  register()==========");
        return this;
    }

    public void unRegister(BackGroundIPulseObserver observer) {
        observers.remove(observer);
        LogUtils.e("===============pulseTimer  unRegister(observer)==========");
    }

    public boolean hasObserver() {
        return observers.size() > 0;
    }

    private void unRegisterAll() {
        Iterator<BackGroundIPulseObserver> iterator = observers.iterator();
        while (iterator.hasNext()) {
            BackGroundIPulseObserver observer = iterator.next();
            observer.onDestroy();
            iterator.remove();
        }
        LogUtils.e("===============pulseTimer  unRegisterAll()==========");
    }

    public void destroy() {
        unRegisterAll();
        timer.cancel();
    }
}
