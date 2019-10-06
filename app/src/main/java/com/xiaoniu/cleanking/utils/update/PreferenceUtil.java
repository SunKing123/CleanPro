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
        String infoStream = ApiModule.SHOPPING_MALL + "?deviceId=" + DeviceUtils.getUdid() + "&type=2";
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
        editor.putString(SpCacheConfig.WEB_URL, url + "?deviceId=" + DeviceUtils.getUdid() + "&type=2").commit();
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
    public static boolean getShareNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.SHARE_NUM,0) > -1)
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
    public static boolean getClearNum(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.CLEAR_NUM,0) > -1)
            return false;
        return true;
    }

    /**
     * 保存极光 激活
     * @param alias
     * @return
     */
    public static boolean saveJPushAlias(boolean alias) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_JPUSH_ALIAS, alias).commit();
        return true;
    }
    /**
     * 保存极光 激活
     */
    public static boolean getIsSaveJPushAlias(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_JPUSH_ALIAS,false);
    }

    /**
     * 保存是否开启锁屏新闻
     * @param isOpen
     * @return
     */
    public static boolean saveScreenTag(boolean isOpen){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_SREEN_TAG, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     * @return true 开启 false 关闭
     */
    public static boolean getScreenTag(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_SREEN_TAG,false);
    }
    /**
     * 保存是否开启低电量提醒
     * @param isOpen
     * @return
     */
    public static boolean saveLower(boolean isOpen){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_LOWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     * @return true 开启 false 关闭
     */
    public static boolean getLower(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_LOWER,false);
    }

    /**
     * 保存是否开启夜间省电提醒
     * @param isOpen
     * @return
     */
    public static boolean saveNightPower(boolean isOpen){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_NIGHT_POWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     * @return true 开启 false 关闭
     */
    public static boolean getNightPower(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_NIGHT_POWER,false);
    }
    /**
     * 保存是否开启异常耗电提醒
     * @param isOpen
     * @return
     */
    public static boolean saveErrorPower(boolean isOpen){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_ERROR_POWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启异常耗电提醒
     * @return true 开启 false 关闭
     */
    public static boolean getErrorPower(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_ERROR_POWER,false);
    }

    /**
     * 保存一键加速清理时间
     * @return
     */
    public static boolean saveCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_CLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次一键加速清理间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_CLEAN_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存微信专清清理时间
     * @return
     */
    public static boolean saveWeChatCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_WeCLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次微信专清间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getWeChatCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_WeCLEAN_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存已进入APP
     * @return
     */
    public static boolean saveFirstOpenApp(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_APP, true).commit();
        return true;
    }

    /**
     * 是否是首次进入应用
     * @return
     */
    public static boolean isNoFirstOpenApp(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_APP,false);
    }
    /**
     * 保存已进入清理完成页面
     * @return
     */
    public static boolean saveFirstOpenCLeanFinishApp(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_CLEAN_FINISH_APP, true).commit();
        return true;
    }

    /**
     * 是否是首次进入清理完成页面
     * @return
     */
    public static boolean isNoFirstOpenCLeanFinishApp(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_CLEAN_FINISH_APP,false);
    }

    /**
     * 保存立即清理清理时间
     * @return
     */
    public static boolean saveNowCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次立即清理清理间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getNowCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存通知栏清理时间
     * @return
     */
    public static boolean saveNotificationCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_NOTIFICATION_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次通知栏清理间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getNotificationCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_NOTIFICATION_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }


    /**
     * 保存超强省电清理时间
     * @return
     */
    public static boolean savePowerCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_POWER_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次超强省电间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getPowerCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_POWER_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存手机降温时间
     * @return
     */
    public static boolean saveCoolingCleanTime(){
        SharedPreferences sharedPreferences =  AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_COOLINF_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次手机降温间隔至少3分钟
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getCoolingCleanTime(){
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_COOLINF_TIME,0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

}
