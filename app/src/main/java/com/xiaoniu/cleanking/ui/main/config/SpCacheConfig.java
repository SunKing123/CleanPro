package com.xiaoniu.cleanking.ui.main.config;

/**
 * sp 配置文件
 * Created by lang.chen on 2019/7/3
 */
public class SpCacheConfig {

    /**
     * 文件缓存
     */
    public static final String CACHES_FILES_NAME = "key_caches_files";
    public static final String CACHES_KEY_MUSCI = "key_caches_files_music";
    public static final String CACHES_KEY_APK = "key_caches_files_apk";
    public static final String CACHES_KEY_VIDEO = "key_caches_files_video";

    /**
     * 安装包白名单文件缓存配置
     */
    //文件名
    public static final String CACHES_NAME_WHITE_LIST_INSTALL_PACKE = "caches_name_white_list_install_packe";
    public static final String CACHES_NAME_WXQQ_CACHE = "caches_name_wxqq_cache";
    //key包名
    public static final String WHITE_LIST_KEY_INSTALL_PACKE_NAME = "white_list_install_packe_name";
    public static final String WHITE_LIST_SOFT_KEY_INSTALL_PACKE_NAME = "white_list_soft_install_packe_name";
    public static final String WX_CACHE_SIZE = "wx_cache_size";
    public static final String QQ_CACHE_SIZE = "qq_cache_size";
    //path 路径
    public static final String WHITE_LIST_KEY_DIRECTORY = "white_list_directory";

    //微信包名
    public static final String CHAT_PACKAGE = "com.tencent.mm";
    //QQ包名
    public static final String QQ_PACKAGE = "com.tencent.mobileqq";
    public static final String ITEM_TITLE_NAME = "title_name";
    //WebView URL
    public static final String WEB_URL = "web_url";
    //分享次数
    public static final String SHARE_NUM = "share_num";
    //清理次数
    public static final String CLEAR_NUM = "clean_num";
    //记录是否已保存极光
    public static final String IS_SAVE_JPUSH_ALIAS = "j_push_alias";
    //记录是否锁屏新闻
    public static final String IS_SAVE_SREEN_TAG = "screen_tag";
    //保存低电量提醒
    public static final String IS_SAVE_LOWER = "low_power";
    //夜间省电
    public static final String IS_SAVE_NIGHT_POWER = "night_power";
    //异常耗电提醒
    public static final  String IS_SAVE_ERROR_POWER = "error_power";
    //清理间隔时间
    public static final  String IS_SAVE_CLEAN_TIME = "clean_time";
    public static final  String IS_SAVE_WeCLEAN_TIME = "Weclean_time";
    public static final  String IS_SAVE_FIRST_OPEN_APP = "first_open_app";
    public static final  String IS_SAVE_FIRST_OPEN_CLEAN_FINISH_APP = "first_open_clean_finish_app";
    //埋点上一级页面
    public static final String ONKEY = "one_key";
    public static final String PHONE_CLEAN = "phone_clean";
    public static final String WETCHAT_CLEAN = "wetchat_clean";
    public static final String QQ_CLEAN = "qq_clean";
    public static final String PHONE_COOLING = "phone_cooling";
    public static final String NOTITY = "notity";
    public static final String SUPER_POWER_SAVING = "super_power_saving";
    public static final String BANNER = "banner";
    public static final String APP_ID = "com.xiaoniu.cleanking";
}
