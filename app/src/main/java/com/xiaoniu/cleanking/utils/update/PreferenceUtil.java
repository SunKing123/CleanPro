package com.xiaoniu.cleanking.utils.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.common.utils.DeviceUtils;

//TODO 待优化
public class PreferenceUtil {

    /**
     * 获取WebView URL
     *
     * @return
     */
    public static String getWebViewUrl() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        // 如果用户成功分享一次后使用悟空清理三次则完成页的分享领券页面永久切换到资讯页面；如果用户没有分享但使用悟空清理超过20次则完成页的分享领券页面永久切换到资讯页面 开发中
        String infoStream = ApiModule.SHOPPING_MALL + "?deviceId=" + DeviceUtils.getUdid() + "&type=2";
        if (!getClearNum() || !getShareNum())
            return infoStream;

        return sharedPreferences.getString(SpCacheConfig.WEB_URL, infoStream);
    }

    /**
     * 保存WebView URL
     *
     * @param url
     */
    public static void saveWebViewUrl(String url) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.WEB_URL, url + "?deviceId=" + DeviceUtils.getUdid() + "&type=2").commit();
    }

    /**
     * 保存分享次数  超过3次永久不能分享
     *
     * @return
     */
    public static boolean saveShareNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.SHARE_NUM, sharedPreferences.getInt(SpCacheConfig.SHARE_NUM, 0) + 1).commit();
        return true;
    }

    /**
     * 保存分享次数  超过3次永久不能分享
     *
     * @return
     */
    public static boolean getShareNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.SHARE_NUM, 0) > -1)
            return false;
        return true;
    }


    /**
     * 保存清理次数  清理20次后，永久显示资讯页面
     *
     * @return
     */
    public static boolean saveCleanNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAR_NUM, sharedPreferences.getInt(SpCacheConfig.CLEAR_NUM, 0) + 1).commit();
        return true;
    }

    /**
     * 保存清理次数  清理20次后，永久显示资讯页面
     *
     * @return
     */
    public static boolean getClearNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(SpCacheConfig.CLEAR_NUM, 0) > -1)
            return false;
        return true;
    }

    /**
     * 保存极光 激活
     *
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
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_JPUSH_ALIAS, false);
    }

    /**
     * 保存是否开启锁屏新闻
     *
     * @param isOpen
     * @return
     */
    public static boolean saveScreenTag(boolean isOpen) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_SREEN_TAG, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     *
     * @return true 开启 false 关闭
     */
    public static boolean getScreenTag() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_SREEN_TAG, false);
    }

    /**
     * 保存是否开启低电量提醒
     *
     * @param isOpen
     * @return
     */
    public static boolean saveLower(boolean isOpen) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_LOWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     *
     * @return true 开启 false 关闭
     */
    public static boolean getLower() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_LOWER, false);
    }

    /**
     * 保存是否开启夜间省电提醒
     *
     * @param isOpen
     * @return
     */
    public static boolean saveNightPower(boolean isOpen) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_NIGHT_POWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启锁屏新闻
     *
     * @return true 开启 false 关闭
     */
    public static boolean getNightPower() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_NIGHT_POWER, false);
    }

    /**
     * 保存是否开启异常耗电提醒
     *
     * @param isOpen
     * @return
     */
    public static boolean saveErrorPower(boolean isOpen) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_ERROR_POWER, isOpen).commit();
        return true;
    }

    /**
     * 是否开启异常耗电提醒
     *
     * @return true 开启 false 关闭
     */
    public static boolean getErrorPower() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_ERROR_POWER, false);
    }

    /**
     * 保存一键加速清理时间
     *
     * @return
     */
    public static boolean saveCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_CLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次一键加速清理间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_CLEAN_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }


    /**
     * 保存一键加速是否已使用
     */
    public static void saveCleanJiaSuUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_JIASU, isUsed).commit();
    }

    /**
     * 获取一键加速是否已使用
     *
     * @return
     */
    public static boolean isCleanJiaSuUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_JIASU, false);
    }

    /**
     * 保存微信专清清理时间
     *
     * @return
     */
    public static boolean saveWeChatCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_WeCLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次微信专清间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getWeChatCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_WeCLEAN_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存微信清理是否已使用
     */
    public static void saveCleanWechatUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_WECHAT, isUsed).commit();
    }

    /**
     * 获取微信清理是否已使用
     *
     * @return
     */
    public static boolean isCleanWechatUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_WECHAT, false);
    }

    /**
     * 保存已进入APP
     *
     * @return
     */
    public static boolean saveFirstOpenApp() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_APP, true).commit();
        return true;
    }

    /**
     * 是否是首次进入应用
     *
     * @return
     */
    public static boolean isNoFirstOpenApp() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_APP, false);
    }

    /**
     * 保存已进入清理完成页面
     *
     * @return
     */
    public static boolean saveFirstOpenCLeanFinishApp() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_CLEAN_FINISH_APP, true).commit();
        return true;
    }

    /**
     * 是否是首次进入清理完成页面
     *
     * @return
     */
    public static boolean isNoFirstOpenCLeanFinishApp() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_CLEAN_FINISH_APP, false);
    }

    /**
     * 保存立即清理清理时间
     *
     * @return
     */
    public static boolean saveNowCleanTime() {
        Constant.APP_IS_LIVE = "1";
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }


    /**
     * 保存清理运行内存是否全选
     *
     * @return
     */
    public static boolean saveCacheIsCheckedAll(boolean ischeck) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.RUN_CACHES_IS_CHECK_ALL, ischeck).commit();
        return true;
    }


    /**
     * 获取清理勾选页面三分钟内的随机 乘数
     *
     * @return
     */
    public static boolean getCacheIsCheckedAll() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        boolean mul = sharedPreferences.getBoolean(SpCacheConfig.RUN_CACHES_IS_CHECK_ALL, true);
        return mul;
    }

    /**
     * 保存清理勾选页面三分钟内的随机 乘数
     *
     * @return
     */
    public static boolean saveMulCacheNum(float mul) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(SpCacheConfig.MUL_RUN_CACHES_CUSTOM, mul).commit();
        return true;
    }


    /**
     * 获取清理勾选页面三分钟内的随机 乘数
     *
     * @return
     */
    public static float getMulCacheNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        float mul = sharedPreferences.getFloat(SpCacheConfig.MUL_RUN_CACHES_CUSTOM, 1f);
        return mul;
    }


    /**
     * 保存立即清理清理时间
     *
     * @return
     */
    public static boolean saveCustom(String key, long value) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value).commit();
        return true;
    }

    /**
     * 是否距离上次立即清理清理间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getNowCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存通知栏清理时间
     *
     * @return
     */
    public static boolean saveNotificationCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_NOTIFICATION_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次通知栏清理间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getNotificationCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_NOTIFICATION_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存通知栏清理是否已使用
     */
    public static void saveCleanNotifyUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_NOTIFY, isUsed).commit();
    }

    /**
     * 获取通知栏清理是否已使用
     *
     * @return
     */
    public static boolean isCleanNotifyUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_NOTIFY, false);
    }


    /**
     * 保存超强省电清理时间
     *
     * @return
     */
    public static boolean savePowerCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_POWER_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次超强省电间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getPowerCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_POWER_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }


    /**
     * 保存超强省电是否已使用
     */
    public static void saveCleanPowerUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_POWER, isUsed).commit();
    }

    /**
     * 获取超强省电是否已使用
     *
     * @return
     */
    public static boolean isCleanPowerUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_POWER, false);
    }

    /**
     * 保存手机降温时间
     *
     * @return
     */
    public static boolean saveCoolingCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_COOLINF_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次手机降温间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getCoolingCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_COOLINF_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存手机降温是否已使用
     */
    public static void saveCleanCoolUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_COLL, isUsed).commit();
    }

    /**
     * 获取手机降温是否已使用
     *
     * @return
     */
    public static boolean isCleanCoolUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_COLL, false);
    }

    /**
     * 是否第一次进入App(用于首页清理icon状态变化)
     *
     * @return
     */
    public static boolean saveFirstForHomeIcon(boolean isFirst) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_KEY_FIRST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_FIRST_HOME_ICON, isFirst).commit();
        return true;
    }

    public static boolean isFirstForHomeIcon() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_KEY_FIRST, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_HOME_ICON, true);
    }

    /**
     * 保存点击home键退居后台时间
     *
     * @return
     */
    public static boolean saveHomeBackTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_HOME_BACK_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 保存垃圾清理完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_COUNT, count).commit();
        return true;
    }

    /**
     * 获取垃圾清理完成页点击返回键的次数
     */
    public static int getCleanFinishClickCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_COUNT, 0);
    }

    /**
     * 保存一键加速完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickJiaSuCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_JIAU_COUNT, count).commit();
        return true;
    }

    /**
     * 获取一键加速完成页点击返回键的次数
     */
    public static int getCleanFinishClickJiaSuCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_JIAU_COUNT, 0);
    }

    /**
     * 保存超强省电完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickPowerCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_POWER_COUNT, count).commit();
        return true;
    }

    /**
     * 获取超强省电完成页点击返回键的次数
     */
    public static int getCleanFinishClickPowerCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_POWER_COUNT, 0);
    }

    /**
     * 保存通知栏清理完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickNotifyCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_NOTIFY_COUNT, count).commit();
        return true;
    }

    /**
     * 获取通知栏清理完成页点击返回键的次数
     */
    public static int getCleanFinishClickNotifyCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_NOTIFY_COUNT, 0);
    }

    /**
     * 保存微信清理完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickWechatCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_WECHAT_COUNT, count).commit();
        return true;
    }

    /**
     * 获取微信清理完成页点击返回键的次数
     */
    public static int getCleanFinishClickWechatCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_WECHAT_COUNT, 0);
    }

    /**
     * 保存手机降温完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickCoolCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_COOL_COUNT, count).commit();
        return true;
    }

    /**
     * 获取手机降温完成页点击返回键的次数
     */
    public static int getCleanFinishClickCoolCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_COOL_COUNT, 0);
    }

    /**
     * 保存QQ清理完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickQQCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_QQ_COUNT, count).commit();
        return true;
    }

    /**
     * 获取QQ清理完成页点击返回键的次数
     */
    public static int getCleanFinishClickQQCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_QQ_COUNT, 0);
    }

    /**
     * 保存手机清理完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickPhoneCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_PHONE_COUNT, count).commit();
        return true;
    }

    /**
     * 获取手机清理完成页点击返回键的次数
     */
    public static int getCleanFinishClickPhoneCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_PHONE_COUNT, 0);
    }


    /**
     * 从后台回到前台的时间是否大于5分钟
     *
     * @return true 大于5分钟 false 小于5分钟
     */
    public static boolean getHomeBackTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_HOME_BACK_TIME, 0);
//        if (System.currentTimeMillis() - time > 5 * 60 * 1000) //暂时注释
        if (System.currentTimeMillis() - time > 1 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 获取延长待机时间
     */
    public static String getLengthenAwaitTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.LENGTHEN_AWAIT_TIME, "");
    }

    /**
     * 保存延长待机时间
     *
     * @return
     */
    public static boolean saveLengthenAwaitTime(String time) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.LENGTHEN_AWAIT_TIME, time).commit();
        return true;
    }

    /**
     * 判断6大功能在清理完成页需要展示的数量
     *
     * @return
     */
    public static int getShowCount(String title, int ramScale, int notifSize, int powerSize) {
        int count = 0;
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_one_key_speed)) && getCleanTime()) {  // 一键加速
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || ramScale > 20) {
                Log.d("XiLei", "111111111");
                count++;
            }
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_phone_temperature_low)) && getCoolingCleanTime()) { //手机降温
            Log.d("XiLei", "22222222");
            count++;
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_chat_clear)) && getWeChatCleanTime()) { // 微信专清
            Log.d("XiLei", "33333333333");
            count++;
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_notification_clean)) && getNotificationCleanTime() && notifSize > 0) { // 通知栏清理
            Log.d("XiLei", "444444444");
            count++;
        }
        Log.d("XiLei", "555555555555");
        count++; //文件清理
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_super_power_saving)) && getPowerCleanTime()) { //超强省电
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || powerSize > 0) {
                Log.d("XiLei", "6666666666");
                count++;
            }
        }
        return count;
    }
}
