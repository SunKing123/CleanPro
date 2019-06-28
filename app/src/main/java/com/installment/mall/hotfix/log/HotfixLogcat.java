package com.installment.mall.hotfix.log;

import android.util.Log;

import com.installment.mall.BuildConfig;
import com.installment.mall.hotfix.listener.MyPatchListener;


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
