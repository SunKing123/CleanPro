package com.xiaoniu.cleanking.utils.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.collection.SparseArrayCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.localpush.LocalPushConfigModel;
import com.xiaoniu.cleanking.ui.main.bean.ExitRetainEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.PushSettingList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO 待优化
public class PreferenceUtil {

    public static final String SHAREPREFERENCE_FILENAME = BuildConfig.APPLICATION_ID + "_sp_file";

    private SharedPreferences sp;

    private PreferenceUtil() {
        sp = ContextUtils.getApplication().getSharedPreferences
                (SHAREPREFERENCE_FILENAME, Context.MODE_PRIVATE);
    }


    private static class PreferenceSingle {
        private static final PreferenceUtil instance = new PreferenceUtil();
    }

    public static PreferenceUtil getInstants() {
        return PreferenceSingle.instance;
    }


    // 根据键名提取键值
    public String get(String key) {

        String record = null;
        try {
            record = sp.getString(key, "");
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
        return record;
    }

    // 根据键名提取键值
    public int getInt(String key) {
        int record = 0;
        try {
            record = sp.getInt(key, 0);
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        } finally {

        }
        return record;
    }

    // 存储键对
    public void save(String key, String value) {
        try {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(key, value);
            edit.commit();
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    // 存储键对
    public boolean saveForResult(String key, String value) {
        try {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(key, value);
            return edit.commit();
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
            return false;
        }
    }

    public void saveInt(String key, int value) {
        try {
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(key, value);
            edit.commit();
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    // 在原有基础上增加值
    public void saveAndApply(String key, String value) {
        try {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(key, sp.getString(key, "") + value);
            edit.commit();
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    // 删除存储值
    public void delete(String key) {
        try {
            SharedPreferences.Editor edit = sp.edit();
            edit.remove(key);
            edit.commit();
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    /**
     * 获取WebView URL
     *
     * @return
     */
    public static String getWebViewUrl() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        // 如果用户成功分享一次后使用清理管家极速版三次则完成页的分享领券页面永久切换到资讯页面；如果用户没有分享但使用清理管家极速版超过20次则完成页的分享领券页面永久切换到资讯页面 开发中
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
    public static void saveJPushAliasCurrentVersion(boolean alias) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_SAVE_JPUSH_ALIAS + BuildConfig.VERSION_CODE, alias).apply();
    }

    /**
     * 判断当前版本是否注册了极光
     */
    public static boolean getIsSaveJPushAliasCurrentVersion(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_JPUSH_ALIAS + BuildConfig.VERSION_CODE, false);
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
     * 保存病毒查杀时间
     *
     * @return
     */
    public static boolean saveVirusKillTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_VIRUS_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次病毒查杀隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getVirusKillTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_VIRUS_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存网络加速时间
     *
     * @return
     */
    public static boolean saveSpeedNetworkTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_SPEED_NETWORK_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 保存网络加速值
     *
     * @return
     */
    public static boolean saveSpeedNetworkValue(String value) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.IS_SAVE_SPEED_NETWORK_VALUE, value).commit();
        return true;
    }

    /**
     * 保存网络加速值
     *
     * @return
     */
    public static String getSpeedNetworkValue() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.IS_SAVE_SPEED_NETWORK_VALUE, "20");
    }

    /**
     * 是否距离上次网络加速至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getSpeedNetWorkTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_SPEED_NETWORK_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }


    /**
     * whether display the kill virus warning?
     * <p>
     * condition: the warning displayed the next day,and no used kill virus.
     *
     * @return true is display
     */
    public static boolean getShowVirusKillWarning() {
        Calendar now = Calendar.getInstance();

        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_VIRUS_TIME, 0);
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(time);

        int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
        int lastDayOfYear = last.get(Calendar.DAY_OF_YEAR);
        //warning displayed the next day。
        return nowDayOfYear - lastDayOfYear >= 1;
    }

    /**
     * get unused the virus kill function days
     *
     * @return
     */
    public static int getUnusedVirusKillDays() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_VIRUS_TIME, 0);
        long nowTime = System.currentTimeMillis();
        if (time == 0) {
            return 0;
        }
        long sec = (nowTime - time) / 1000;
        long min = sec / 60;
        long diffH = min / 60;
        int day = (int) diffH / 24;

        return day;
    }

    /**
     * 保存垃圾清理是否已使用
     */
    public static void saveCleanAllUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_CLEAN_ALL, isUsed).commit();
    }

    /**
     * 获取垃圾清理是否已使用
     *
     * @return
     */
    public static boolean isCleanAllUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_CLEAN_ALL, false);
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
     * 保存游戏加速清理时间
     *
     * @return
     */
    public static boolean saveGameTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_GAME_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次游戏加速清理间隔至少3分钟
     *
     * @return true 3分钟以上 false 小于3分钟
     */
    public static boolean getGameTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_GAME_TIME, 0);
        if (System.currentTimeMillis() - time > 3 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存游戏加速是否已使用
     */
    public static void saveCleanGameUsed(boolean isUsed) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_USED_GAME, isUsed).commit();
    }

    /**
     * 获取游戏加速是否已使用
     *
     * @return
     */
    public static boolean isCleanGameUsed() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_USED_GAME, false);
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
    public static boolean isNotFirstOpenApp() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_SAVE_FIRST_OPEN_APP, false);
    }

    /**
     * 保存一键清理清理时间
     *
     * @return
     */
    public static boolean saveNowCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 是否距离上次一键清理清理间隔至少5分钟
     *
     * @return true 5分钟以上 false 小于5分钟
     */
    public static boolean getNowCleanTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.IS_SAVE_NOW_CLEAN_TIME, 0);
        if (System.currentTimeMillis() - time > 5 * 60 * 1000)
            return true;
        return false;
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
     * 保存清理是否全选
     *
     * @return
     */
    public static boolean saveIsCheckedAll(boolean ischeck) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.RUN_CHECK_IS_CHECK_ALL, ischeck).commit();
        return true;
    }

    /**
     * 获取清理是否全选
     *
     * @return
     */
    public static boolean getIsCheckedAll() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        boolean mul = sharedPreferences.getBoolean(SpCacheConfig.RUN_CHECK_IS_CHECK_ALL, true);
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
        return true;
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
     * 保存手机降了多少温度
     */
    public static void saveCleanCoolNum(int num) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.SAVE_COOL_NUM, num).commit();
    }

    /**
     * 保存手机降了多少温度
     */
    public static int getCleanCoolNum() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.SAVE_COOL_NUM, 4);
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
     * 保存是否后台
     *
     * @return
     */
    public static boolean saveIsProcessBack(boolean isback) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_HOME_BACK, isback).commit();
        return true;
    }

    /**
     * 获取是否后台
     *
     * @return
     */
    public static boolean getIsProcessBack() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.getBoolean(SpCacheConfig.IS_HOME_BACK, false);
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
     * 保存游戏加速完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickGameCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_GAME_COUNT, count).commit();
        return true;
    }

    /**
     * 获取游戏加速完成页点击返回键的次数
     */
    public static int getCleanFinishClickGameCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_GAME_COUNT, 0);
    }


    /**
     * 从后台回到前台的时间是否大于服务配置好的分钟
     *
     * @return true 大于配置分钟 false 小于配置分钟
     */
    public static boolean getHomeBackTime(int configTime) {
        if (configTime == 0) {
            return true;
        } else {
            SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
            long time = sharedPreferences.getLong(SpCacheConfig.IS_HOME_BACK_TIME, 0);
            return System.currentTimeMillis() - time > configTime * 60 * 1000;
        }
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
     * 第一次进入app获取首页推荐功能接口请求失败或者无网络时判断使用
     *
     * @return
     */
    public static boolean saveFirstHomeRecommend(boolean isFirst) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.KEY_FIRST_HOME_RECOMMEND, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_FIRST_HOME_RECOMMEND, isFirst).commit();
        return true;
    }

    public static boolean isFirstHomeRecommend() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.KEY_FIRST_HOME_RECOMMEND, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_FIRST_HOME_RECOMMEND, true);
    }

    /**
     * 保存游戏加速是否已开启
     *
     * @return
     */
    public static boolean saveGameQuikcenStart(boolean isStart) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_GAME_QUIKCEN_START, isStart).commit();
        return true;
    }

    /**
     * 获取游戏加速是否已开启
     */
    public static boolean getGameQuikcenStart() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_GAME_QUIKCEN_START, false);
    }


    /**
     * 保存有游戏加速的百分比
     *
     * @return
     */
    public static void saveGameCleanPer(String count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.GAME_QUIKCEN_NUM, count).commit();
    }

    /**
     * 保存有游戏加速的百分比
     */
    public static String getGameCleanPer() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_USED, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.GAME_QUIKCEN_NUM, "");
    }

    /**
     * 保存病毒查杀完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickVirusCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_VIRUS_COUNT, count).commit();
        return true;
    }

    /**
     * 获取病毒查杀完成页点击返回键的次数
     */
    public static int getCleanFinishClickVirusCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_VIRUS_COUNT, 0);
    }

    /**
     * 保存网络加速完成页点击返回键的次数
     *
     * @return
     */
    public static boolean saveCleanFinishClickNetCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.CLEAN_FINISH_CLICK_NET_COUNT, count).commit();
        return true;
    }

    /**
     * 获取网络加速完成页点击返回键的次数
     */
    public static int getCleanFinishClickNetCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.CLEAN_FINISH_CLICK_NET_COUNT, 0);
    }

    /**
     * 保存红包展示的次数
     *
     * @return
     */
    public static boolean saveRedPacketShowCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.RED_PACKET_SHOW, count).commit();
        return true;
    }

    /**
     * 获取红包展示的次数
     */
    public static int getRedPacketShowCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.RED_PACKET_SHOW, 0);
    }

    /**
     * 保存红包循环展示时展示到第几个
     *
     * @return
     */
    public static boolean saveRedPacketForCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.RED_PACKET_FOR, count).commit();
        return true;
    }

    /**
     * 获取红包循环展示时展示到第几个
     */
    public static int getRedPacketForCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.RED_PACKET_FOR, 0);
    }

    /**
     * 保存红包循环展示频次
     *
     * @return
     */
    public static boolean saveRedPacketShowTrigger(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.RED_PACKET_SHOW_TRIGGER, count).commit();
        return true;
    }

    /**
     * 获取红包循环展示频次
     */
    public static int getRedPacketShowTrigger() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.RED_PACKET_SHOW_TRIGGER, 0);
    }


    /**
     * 保存是否有版本更新
     *
     * @return
     */
    public static boolean saveHaseUpdateVersion(boolean isShow) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.HASE_UPDATE_VERSION, isShow).commit();
        MmkvUtil.saveHaseUpdateVersionMK(isShow);
        return true;
    }


    /**
     * 获取是否有版本更新
     */
    public static boolean isHaseUpdateVersion() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.HASE_UPDATE_VERSION, false);
    }

    /**
     * 保存冷启动开关状态
     *
     * @return
     */
    public static boolean saveCoolStartADStatus(boolean ischeck) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.COOL_START_STATUS, ischeck).commit();
        return true;
    }


    /**
     * 获取冷启动开关状态
     *
     * @return
     */
    public static boolean getCoolStartADStatus() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        boolean mul = sharedPreferences.getBoolean(SpCacheConfig.COOL_START_STATUS, false);
        return mul;
    }


    /**
     * 保存冷启动的时间戳
     *
     * @return
     */
    public static boolean saveCoolStartTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.COOL_START_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 冷启动的时间间隔
     *
     * @return true 大于10分钟 false 小于10分钟
     */
    public static boolean getCoolStartTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.COOL_START_TIME, 0);
        if (time == 0 || System.currentTimeMillis() - time > 10 * 60 * 1000)
            return true;
        return false;
    }

    /**
     * 保存内部插屏广告启动的时间戳
     *
     * @return
     */
    public static boolean saveScreenInsideTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.SCREEN_INSIDE_TIME, System.currentTimeMillis()).commit();
        return true;
    }

    /**
     * 内部插屏广告启动的时间间隔
     *
     * @return true 大于24小时 false 小于24小时
     */
    public static boolean getScreenInsideTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong(SpCacheConfig.SCREEN_INSIDE_TIME, 0);
        if (time == 0) return false;
        return !DateUtils.isSameDay(time, System.currentTimeMillis());
    }

    /**
     * 保存冷启动打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveBottomAdCoolCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.BOTTOM_AD_COOL_COUNT, count).commit();
        return true;
    }


    /**
     * 保存冷、热启动的次数
     *
     * @return
     */
    public static void saveColdAndHotStartCount(InsideAdEntity entity) {
        if (entity != null) {
            SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SpCacheConfig.COLD_AND_HOT_START_COUNT, new Gson().toJson(entity)).apply();
        }
    }

    /**
     * 获取冷、热启动的次数
     */
    public static InsideAdEntity getColdAndHotStartCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(SpCacheConfig.COLD_AND_HOT_START_COUNT, "");
        InsideAdEntity entity;
        if (!TextUtils.isEmpty(json)) {
            entity = new Gson().fromJson(json, InsideAdEntity.class);
        } else {
            entity = new InsideAdEntity(System.currentTimeMillis(), 0);
        }
        return entity;
    }

    /**
     * 获取冷启动打底广告循环展示到第几个
     */
    public static int getBottomAdCoolCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.BOTTOM_AD_COOL_COUNT, 0);
    }

    /**
     * 保存热启动打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveBottomAdHotCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.BOTTOM_AD_HOT_COUNT, count).commit();
        return true;
    }

    /**
     * 获取热启动打底广告循环展示到第几个
     */
    public static int getBottomAdHotCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.BOTTOM_AD_HOT_COUNT, 0);
    }


    /**
     * 锁屏打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveBottomLockAdCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.BOTTOM_AD_LOCK_COUNT, count).commit();
        return true;
    }

    /**
     * 锁屏打底广告循环展示到第几个
     */
    public static int getBottomLockAdCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.BOTTOM_AD_LOCK_COUNT, 0);
    }

    /**
     * 保存完成页广告位1打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveFinishAdOneCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.FINISH_AD_ONE_COUNT, count).commit();
        return true;
    }

    /**
     * 获取完成页广告位1打底广告循环展示到第几个
     */
    public static int getFinishAdOneCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.FINISH_AD_ONE_COUNT, 0);
    }

    /**
     * 保存完成页广告位2打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveFinishAdTwoCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.FINISH_AD_TWO_COUNT, count).commit();
        return true;
    }

    /**
     * 获取完成页广告位2打底广告循环展示到第几个
     */
    public static int getFinishAdTwoCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.FINISH_AD_TWO_COUNT, 0);
    }

    /**
     * 保存完成页广告位3打底广告循环展示到第几个
     *
     * @return
     */
    public static boolean saveFinishAdThreeCount(int count) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SpCacheConfig.FINISH_AD_THREE_COUNT, count).commit();
        return true;
    }

    /**
     * 获取完成页广告位3打底广告循环展示到第几个
     */
    public static int getFinishAdThreeCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SpCacheConfig.FINISH_AD_THREE_COUNT, 0);
    }

    /**
     * 判断6大功能在清理完成页需要展示的数量
     *
     * @return
     */
    public static int getShowCount(Context context, String title, int ramScale, int notifSize, int powerSize) {
        int count = 0;
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_suggest_clean)) && !PreferenceUtil.isCleanAllUsed()) { // 垃圾清理
            count++;
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_one_key_speed)) && !PreferenceUtil.isCleanJiaSuUsed()) {  // 一键加速
            if (!PermissionUtils.isUsageAccessAllowed(context)) {
                count++;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || ramScale > 20) {
                    count++;
                }
            }
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_super_power_saving)) && !PreferenceUtil.isCleanPowerUsed()) { //超强省电
            if (!PermissionUtils.isUsageAccessAllowed(context)) {
                count++;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || powerSize > 0) {
                    count++;
                }
            }
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_notification_clean))) { // 通知栏清理
            if (!NotifyUtils.isNotificationListenerEnabled()) {
                count++;
            } else if (!PreferenceUtil.isCleanNotifyUsed() && notifSize > 0) {
                count++;
            }
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_chat_clear)) && !PreferenceUtil.isCleanWechatUsed()) { // 微信专清
            count++;
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.game_quicken)) && !PreferenceUtil.isCleanGameUsed()) { //游戏加速
            count++;
        }
        if (!title.equals(AppApplication.getInstance().getString(R.string.tool_phone_temperature_low)) && !PreferenceUtil.isCleanCoolUsed()) { //手机降温
            count++;
        }

        count++; //文件清理
        return count;
    }

    //更新按返回键退出程序的次数
    public static void updatePressBackExitAppCount(boolean isPop) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        ExitRetainEntity retainEntity = getPressBackExitAppCount();
        retainEntity.setLastTime(System.currentTimeMillis());
        retainEntity.setBackTotalCount(retainEntity.getBackTotalCount() + 1);
        if (isPop) {
            retainEntity.setPopupCount(retainEntity.getPopupCount() + 1);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.KEY_EXIT_RETAIN_DIALOG_COUNT, new Gson().toJson(retainEntity)).apply();
    }

    //重置按返回键退出程序的次数
    public static void resetPressBackExitAppCount() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ExitRetainEntity retainEntity = new ExitRetainEntity(1, 0, System.currentTimeMillis());
        editor.putString(SpCacheConfig.KEY_EXIT_RETAIN_DIALOG_COUNT, new Gson().toJson(retainEntity)).apply();
    }

    //获取按返回键退出程序的次数
    public static ExitRetainEntity getPressBackExitAppCount() {
        ExitRetainEntity retainEntity;
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(SpCacheConfig.KEY_EXIT_RETAIN_DIALOG_COUNT, "");
        if (!TextUtils.isEmpty(json)) {
            retainEntity = new Gson().fromJson(json, ExitRetainEntity.class);
        } else {
            retainEntity = new ExitRetainEntity(0, 0, System.currentTimeMillis());
        }
        return retainEntity;
    }

    //保存最近一次操作记录_map值
    public static void saveCleanLogMap(Map<String, PushSettingList.DataBean> map) {
        if (map != null) {
            List<PushSettingList.DataBean> list = new ArrayList<PushSettingList.DataBean>(map.values());
            saveCleanLog(new Gson().toJson(list));
        }
    }


    //保存从服务器拉取的本地推送配置
    public static void saveLocalPushConfig(String localPushConfigJson) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.KEY_LOCAL_PUSH_CONFIG_FROM_SERVER, localPushConfigJson).apply();
    }

    //获取从服务器拉取的本地推送配置
    public static SparseArrayCompat<LocalPushConfigModel.Item> getLocalPushConfig() {
        SparseArrayCompat<LocalPushConfigModel.Item> sparseArray = new SparseArrayCompat<>();
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        String localPushJson = sp.getString(SpCacheConfig.KEY_LOCAL_PUSH_CONFIG_FROM_SERVER, "");
        if (!TextUtils.isEmpty(localPushJson)) {
            List<LocalPushConfigModel.Item> itemList = new Gson().fromJson(localPushJson, new TypeToken<List<LocalPushConfigModel.Item>>() {
            }.getType());
            if (itemList != null) {
                for (LocalPushConfigModel.Item item : itemList) {
                    sparseArray.put(item.getOnlyCode(), item);
                }
            }
        }
        return sparseArray;
    }


    //保存最后一次使用【清理、加速、降温、省电】功能的时间
    public static void saveLastUseFunctionTime(final String functionType, Long timestamp) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(functionType + "last_used_time", timestamp).apply();
    }

    //获取最后一次使用【清理、加速、降温、省电】功能的时间
    public static Long getLastUseFunctionTime(final String functionType) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(functionType + "last_used_time", 0L);
    }

    //保存上一次【清理、加速、降温、省电】弹框的时间
    public static void saveLastPopupTime(Long timestamp) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.KEY_GLOBAL_POPUP_TIME, timestamp).apply();
    }

    //获取上一次【清理、加速、降温、省电】弹框的时间
    public static Long getLastPopupTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SpCacheConfig.KEY_GLOBAL_POPUP_TIME, 0L);
    }


    //更新当天弹出【清理、加速、降温、省电】弹框的次数
    public static void updatePopupCount(final String functionType, String dayLimitJson) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(functionType + "today_popup_count", dayLimitJson).apply();
    }

    //获取上一次【清理、加速、降温、省电】弹框的次数
    public static String getPopupCount(final String functionType) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(functionType + "today_popup_count", "");
    }


    //保存最后一次扫描垃圾的时间
    public static void saveLastScanRubbishTime(long time) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.KEY_LAST_BACKGROUND_SCAN_TIME, time).apply();
    }

    //获取最后一次扫描垃圾的时间
    public static Long getLastScanRubbishTime() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SpCacheConfig.KEY_LAST_BACKGROUND_SCAN_TIME, 0L);
    }

    //保存最后一次扫描垃圾的大小
    public static void saveLastScanRubbishSize(long time) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SpCacheConfig.KEY_LAST_BACKGROUND_SCAN_SIZE, time).apply();
    }

    //获取最后一次扫描垃圾的大小
    public static Long getLastScanRubbishSize() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_LOCAL_PUSH_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SpCacheConfig.KEY_LAST_BACKGROUND_SCAN_SIZE, 0L);
    }


    //保存最近一次操作记录
    public static void saveCleanLog(String cleanlog) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_ACTION_LOG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.CLEAN_ACTION_LOG, cleanlog).commit();
    }


    //获取最近一次操作记录
    public static Map<String, PushSettingList.DataBean> getCleanLog() {
        Map<String, PushSettingList.DataBean> map = new HashMap<>();
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CLEAN_ACTION_LOG, Context.MODE_PRIVATE);
        String spString = sharedPreferences.getString(SpCacheConfig.CLEAN_ACTION_LOG, "");
        if (!TextUtils.isEmpty(spString)) {
            List<PushSettingList.DataBean> logInfos = new Gson().fromJson(spString, new TypeToken<List<PushSettingList.DataBean>>() {
            }.getType());
            for (PushSettingList.DataBean info : logInfos) {
                map.put(info.getCodeX(), info);
            }
            return map;
        } else {
            return map;
        }
    }


    //是否上报device_info
    public static void saveIsPushDeviceInfo() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_PUSH_DEVICE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_PUSH_DEVICE_INFO, true).commit();
    }

    //获取是否上报
    public static boolean getIsPushDeviceInfo() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_PUSH_DEVICE_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_PUSH_DEVICE_INFO, false);

    }

    //通知栏权限开关是否开启
    public static void saveIsNotificationEnabled(boolean isEnable) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SpCacheConfig.IS_NOTIFICATION_ENABLED, isEnable).commit();
    }

    //通知栏权限开关是否开启
    public static boolean getIsNotificationEnabled() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SpCacheConfig.IS_NOTIFICATION_ENABLED, true);
    }

    public static void savePrivacyItemRandomIds(String ids) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.WARNED_PRIVACY_RANDOM_IDS, ids).commit();
    }

    public static String getPrivacyItemRandomIds() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.WARNED_PRIVACY_RANDOM_IDS, "");
    }

    public static void saveNetworkItemRandomIds(String ids) {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpCacheConfig.WARNED_NETWORK_RANDOM_IDS, ids).commit();
    }

    public static String getNetworkItemRandomIds() {
        SharedPreferences sharedPreferences = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.IS_NOTIFICATION_ENABLED, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SpCacheConfig.WARNED_NETWORK_RANDOM_IDS, "");
    }

}
