package com.xiaoniu.cleanking.ui.finish.base;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.utils.LogUtils;

/**
 * Created by xinxiaolong on 2020/8/8.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanFinishLogger {
    public static void log(String text){
        if (BuildConfig.DEBUG) {
            LogUtils.e("CleanFinish ：" + text);
        }
    }
}
