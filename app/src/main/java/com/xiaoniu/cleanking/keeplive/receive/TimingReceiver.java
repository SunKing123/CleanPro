package com.xiaoniu.cleanking.keeplive.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.ui.main.bean.CleanLogInfo;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author zhengzhihao
 * @date 2019/10/30 14
 * @mail：zhengzhihao@hellogeek.com
 */
public class TimingReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingReceiver";

//    private static final long GARBAGE_SCAN_LONG =

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<Integer, CleanLogInfo> map = PreferenceUtil.getCleanLog();
        for(Map.Entry<Integer, CleanLogInfo> entry : map.entrySet()){
            CleanLogInfo cleanLogInfo = entry.getValue();
            if(isStartScan(cleanLogInfo)){
                startScan(cleanLogInfo);
            }
        }
    }
    
    public boolean isStartScan(CleanLogInfo cleanLogInfo){
        long lastTime = cleanLogInfo.getLastTime();
        long currentTime =System.currentTimeMillis();
        if((currentTime -lastTime)>=(cleanLogInfo.getSpaceLong()*60*1000)){
           return true;
        }
        return false;

    }


    public void startScan(CleanLogInfo cleanLogInfo){

    }




}
