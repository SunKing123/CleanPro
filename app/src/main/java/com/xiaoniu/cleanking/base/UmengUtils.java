package com.xiaoniu.cleanking.base;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class UmengUtils {

    public static void event(Context context, UmengEnum umengEnum){
        MobclickAgent.onEvent(context,umengEnum.eventId);
    }

}
