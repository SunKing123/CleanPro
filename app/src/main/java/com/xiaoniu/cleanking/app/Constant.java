package com.xiaoniu.cleanking.app;

import android.Manifest;

import com.xiaoniu.cleanking.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * 常用常量
 * Created by tie on 2017/2/18.
 */

public class Constant {

    public static final String SUCCESS = "000000";

    public static String APP_IS_LIVE = "";
    //用户中心跳转通用H5 Activity字段
    //标题
    public static final String Title = "activity_title";
    public static final String TAG = "splash";

    //极客广告sdk_业务线id
    public static final String GEEK_ADSDK_PRODUCT_NAME = BuildConfig.ADSDK_PRODUCT_NAME;

    //标题
    public static final String NoTitle = "NoTitle";
    //链接
    public static final String URL = "webview_url";
    //Token失效
    public static final String TokenFailure = "1004";

    /**
     * QQ专清图片集合
     */
    public static final String PARAMS_QQ_IMG_LIST="params_qq_img_list";
    /**
     * QQ专清视频集合
     */
    public static final String PARAMS_QQ_VIDEO_LIST="params_qq_video_list";

    /** 微信图片清理key*/
    public static final String WX_CACHE_SIZE_IMG="wx_cache_size_img";

    /** 微信视频清理key*/
    public static final String WX_CACHE_SIZE_VIDEO="wx_cache_size_video";

    //全局心跳间隔（秒）
    public static final int SCAN_SPACE_LONG = 20;

    //手势解锁后_悬浮页面（秒）
    public static final int UNLOCK_SPACE_LONG = 1;

    //点击Home_悬浮页面（秒）
    public static final int HOME_SPACE_LONG = 1;

    //穿山甲广告app_id
    public static final String CSJ_AD_ID = "5036430";

    //优量汇app_id
    public static final String YLH_AD_ID = "1110047950";

    public static final String WEATHER_DB_NAME = "weatherCity.db";
    public static final String CLEAN_DB_NAME = "databases/clean_list.db";
    public static final String CLEAN_TABLE_NAME = "XNWeatherCityModel";
    public static final String CLEAN_DB_SAVE = "weather_db_issaved";

    public static final String SWITCH_INFO = "switch_info";


    /**
     * 白名单
     */
    public static final List<String> WHITE_LIST = Arrays.asList(
            "com.geek.jk.weather",  // 即刻天气
            "com.xujin.weather",    // 知心天气
            "com.yitong.weather",   // 诸葛天气
            "com.geek.jk.calendar.app",  // 即刻万年历
            "com.geek.luck.calendar.app", // 诸葛万年历
            "com.xiaoniu.cleanking", // 悟空清理
            "com.xiaoniu",
            "com.geek.jk.weather.fission", // 知心天气
            "com.hellogeek.cleanking"     //清理极速管家

    );

    public static String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}

