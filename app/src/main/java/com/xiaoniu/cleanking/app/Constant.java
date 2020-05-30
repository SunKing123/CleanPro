package com.xiaoniu.cleanking.app;

import android.Manifest;

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

    //标题
    public static final String NoTitle = "NoTitle";
    //链接
    public static final String URL = "webview_url";
    //Token失效
    public static final String TokenFailure = "1004";

    /**
     * QQ专清图片集合
     */
    public static final String PARAMS_QQ_IMG_LIST = "params_qq_img_list";

    /**
     * QQ专清视频集合
     */
    public static final String PARAMS_QQ_VIDEO_LIST = "params_qq_video_list";

    /**
     * 微信图片清理key
     */
    public static final String WX_CACHE_SIZE_IMG = "wx_cache_size_img";

    /**
     * 微信视频清理key
     */
    public static final String WX_CACHE_SIZE_VIDEO = "wx_cache_size_video";

    //定时任务间隔检测时间（分钟）
    public static final int SCAN_SPACE_LONG = 1;


    public static final String WX_CLEAN_BTN = "wx_clean_btn";
    public static final String STORAGE_CLEAN_BTN = "storage_clean_btn";


    public static final String CHANNEL_TEST = "clean_test";
    public static final String CHANNEL_RELEASE ="clean_release";

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
            "com.geek.jk.weather.fission" // 知心天气
    );

    public static String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

}

