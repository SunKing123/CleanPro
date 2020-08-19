package com.xiaoniu.cleanking.ui.accwidget

import android.util.Log
import com.xiaoniu.cleanking.BuildConfig

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
public class WidgetLog {

    companion object{
        fun log(text:String){
            if(BuildConfig.DEBUG)
            Log.e("widget",text)
        }
    }
}
