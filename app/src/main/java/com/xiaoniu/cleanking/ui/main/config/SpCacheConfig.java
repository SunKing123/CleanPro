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
}
