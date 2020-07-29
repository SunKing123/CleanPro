package com.xiaoniu.cleanking.utils.rxjava;

import com.xiaoniu.cleanking.ui.deskpop.DeskPopLogger;
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
        DeskPopLogger.log("===============startTimer()==========");
    }

    private void callBack(long number) {
        DeskPopLogger.log("===============callBack()==============");
        for (BackGroundIPulseObserver observer : observers) {
            observer.onPulse(number);
        }

    }

    public BackGroundPulseTimer register(BackGroundIPulseObserver observer) {
        DeskPopLogger.log("===============register()==============");
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.onCreate();
        }
        return this;
    }

    public void unRegister(BackGroundIPulseObserver observer) {
        DeskPopLogger.log("===============unRegister(observer)==============");
        observers.remove(observer);
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
        DeskPopLogger.log("===============unRegisterAll()==============");
    }

    public void destroy() {
        DeskPopLogger.log("===============backgroundTimer destroy()==============");
        unRegisterAll();
        timer.cancel();
    }
}
