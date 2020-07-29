package com.xiaoniu.cleanking.ui.deskpop;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.utils.LogUtils;

/**
 * Created by xinxiaolong on 2020/7/29.
 * email：xinxiaolong123@foxmail.com
 */
public class DeskPopLogger {
    public static void log(String text){
        if (BuildConfig.DEBUG) {
            LogUtils.e("DeskPop ： " + text);
        }
    }
}
