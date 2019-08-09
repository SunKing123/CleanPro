package com.xiaoniu.cleanking.utils.update;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.AndroidUtil;

//TODO 待优化
public class PreferenceUtil {

    //获取WebView URL
    public static String getWebViewUrl(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.WEB_URL,ApiModule.Base_H5_Host +"/activity_page.html" + "?deviceId=" + AndroidUtil.getUdid());
    }

    //保存WebView URL
    public static void saveWebViewUrl(String url){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.WEB_URL, url).commit();

    }
}
