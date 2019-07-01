package com.xiaoniu.cleanking.hotfix.log;

import android.util.Log;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.hotfix.listener.MyPatchListener;


/**
 * Created by admin on 2017/7/10.
 */

public class HotfixLogcat {
    private static final boolean isShowLog = true;
    public static void log(String message){
        if("debug".equalsIgnoreCase(BuildConfig.BUILD_TYPE) || isShowLog){
            Log.v(MyPatchListener.TAG, message);
        }
    }
}
