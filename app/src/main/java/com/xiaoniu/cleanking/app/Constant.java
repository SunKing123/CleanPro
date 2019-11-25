package com.xiaoniu.cleanking.app;

import com.xiaoniu.cleanking.BuildConfig;

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

    public static final String CLEAN_DB_NAME = "weatherCity.db";
    public static final String CLEAN_TABLE_NAME = "XNWeatherCityModel";
    public static final String CLEAN_DB_SAVE = "weather_db_issaved";

}

