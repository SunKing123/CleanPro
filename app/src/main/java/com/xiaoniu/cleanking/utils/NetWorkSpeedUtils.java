package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author XiLei
 * @date 2019/11/15.
 * description：获取实时网速
 */
public class NetWorkSpeedUtils {
    private Context context;
    private Handler mHandler;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public NetWorkSpeedUtils(Context context, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showNetSpeed();
        }
    };

    public void startShowNetSpeed() {
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        new Timer().schedule(task, 1000, 1000); // 1s后启动任务，每2s执行一次

    }

    private long getTotalRxBytes() {
        if (null == context) return 0;
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    private void showNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        Message msg = mHandler.obtainMessage();
        msg.what = 100;
        if (speed == 0 && speed2 == 0) {
            msg.obj = "2.66 KB/S";
        } else {
            Log.d("XiLei", "speed2=" + speed2);
            String speed2Result = "0";
            if (speed2 <= 0) {
                speed2Result = "0";
            } else if (String.valueOf(speed2).length() < 2) {
                speed2Result = String.valueOf(speed2);
            } else {
                speed2Result = String.valueOf(speed2).substring(0, 2);
            }
            msg.obj = speed + "." + speed2Result + " KB/S";
        }
        mHandler.sendMessage(msg);//更新界面
    }
}
