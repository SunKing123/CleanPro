package com.xiaoniu.cleanking.utils.update;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.common.utils.DeviceUtils;

//TODO 待优化
public class PreferenceUtil {

    /**
     * 获取WebView URL
     * @return
     */
    public static String getWebViewUrl(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        // 如果用户成功分享一次后使用悟空清理三次则完成页的分享领券页面永久切换到资讯页面；如果用户没有分享但使用悟空清理超过20次则完成页的分享领券页面永久切换到资讯页面 开发中
        String infoStream = ApiModule.SHOPPING_MALL + "?deviceId=" + DeviceUtils.getUdid() + "&type=]\2";
        if (!getClearNum() || !getShareNum())
            return infoStream;

        return sharedPreferences.getString(SpCacheConfig.WEB_URL, infoStream);
    }

    /**
     * 保存WebView URL
     * @param url
     */
    public static void saveWebViewUrl(String url){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.WEB_URL, url + "?deviceId=" + DeviceUtils.getUdid()).commit();
    }

    /**
     * 保存分享次数  超过3次永久不能分享
     * @return
     */
    public static boolean saveShareNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.SHARE_NUM, sharedPreferences.getInt(SpCacheConfig.SHARE_NUM,0) + 1).commit();
        return true;
    }

    /**
     * 保存分享次数  超过3次永久不能分享
     * @return
     */
    private static boolean getShareNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.SHARE_NUM,0) > 3)
            return false;
        return true;
    }


    /**
     * 保存清理次数  清理20次后，永久显示资讯页面
     * @return
     */
    public static boolean saveCleanNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAR_NUM, sharedPreferences.getInt(SpCacheConfig.CLEAR_NUM,0) + 1).commit();
        return true;
    }

    /**
     * 保存清理次数  清理20次后，永久显示资讯页面
     * @return
     */
    private static boolean getClearNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.CLEAR_NUM,0) > 20)
            return false;
        return true;
    }
}
