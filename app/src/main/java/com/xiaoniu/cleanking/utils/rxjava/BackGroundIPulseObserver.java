package com.xiaoniu.cleanking.utils.rxjava;

/**
 * Created by xinxiaolong on 2020/7/25.
 * email：xinxiaolong123@foxmail.com
 */
public interface BackGroundIPulseObserver {

    void onCreate();

    void onPulse(long progress);

    void onDestroy();
}
