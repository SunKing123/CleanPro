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
    public static final String CACHES_KEY_FIRST = "key_caches_files_first";
    public static final String KEY_FIRST_HOME_RECOMMEND = "key_first_home_recommend";

    public static final String MUL_RUN_CACHES_CUSTOM = "mul_run_caches_custom";
    public static final String RUN_CACHES_IS_CHECK_ALL = "run_caches_is_check_all";
    public static final String RUN_CHECK_IS_CHECK_ALL = "run_is_check_all";
    public static final String CLEAN_USED = "clean_used";
    public static final String CLEAN_ACTION_LOG = "clean_action_log";


    public static final String CACHES_LOCAL_PUSH_CONFIG = "caches_local_push_config";
    public static final String KEY_LOCAL_PUSH_CONFIG_FROM_SERVER = "push_config_from_server";
    //垃圾清理
    public static final String KEY_FUNCTION_CLEAR_RUBBISH = "function_clear_rubbish";
    //一键加速
    public static final String KEY_FUNCTION_SPEED_UP = "function_speed_up";
    //手机降温
    public static final String KEY_FUNCTION_COOL = "function_cool";
    //超强省电
    public static final String KEY_FUNCTION_POWER_SAVING = "function_power_saving";

    //本地弹窗的间隔时间(所有的弹窗共享这个时间)
    public static final String KEY_GLOBAL_POPUP_TIME = "local_push_popup_time";


    //后台每隔一小时扫描一次垃圾，该值保存上一次后台扫描完成后的时间
    public static final String KEY_LAST_BACKGROUND_SCAN_TIME = "background_scan_time";
    //后台每隔一小时扫描一次垃圾，该值保存上一次后台扫描到的垃圾大小(单位为MB)
    public static final String KEY_LAST_BACKGROUND_SCAN_SIZE = "background_scan_size";
    //保存上一次清理APP触发HOME键的时间
    public static final String KEY_LAST_CLEAR_APP_PRESSED_HOME = "clear_app_pressed_home";
    //第一次安装APP的时间
    public static final String KEY_FIRST_INSTALL_APP_TIME="first_install_app_time";

    //保存挽留弹窗的次数
    public static final String KEY_EXIT_RETAIN_DIALOG_COUNT = "exit_retain_dialog_count";

    public static final String IS_PUSH_DEVICE_INFO = "is_push_device_info";
    public static final String IS_NOTIFICATION_ENABLED = "is_notification_enabled";

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
    public static final String IS_SAVE_ERROR_POWER = "error_power";
    //清理间隔时间
    public static final String IS_SAVE_CLEAN_TIME = "clean_time";
    //一键清理间隔时间
    public static final String IS_SAVE_ONE_KEY_CLEAN_TIME = "one_key_clean_time";

    //病毒查杀间隔时间
    public static final String IS_SAVE_VIRUS_TIME = "virus_time";
    //网络加速间隔时间
    public static final String IS_SAVE_SPEED_NETWORK_TIME = "speed_network_time";
    public static final String IS_SAVE_SPEED_NETWORK_VALUE = "speed_network_value";

    public static final String IS_SAVE_NOW_CLEAN_TIME = "now_clean_time";
    public static final String IS_SAVE_WeCLEAN_TIME = "weclean_time";
    public static final String IS_SAVE_NOTIFICATION_TIME = "notification_time";
    public static final String IS_SAVE_POWER_TIME = "power_time";
    public static final String IS_SAVE_COOLINF_TIME = "cooling_time";
    public static final String IS_SAVE_FIRST_OPEN_APP = "first_open_app";
    public static final String IS_SAVE_GAME_TIME = "game_time";

    public static final String IS_SAVE_FIRST_HOME_ICON = "first_home_icon";
    public static final String IS_HOME_BACK_TIME = "home_back";
    public static final String IS_HOME_BACK = "is_home_back";
    public static final String CLEAN_FINISH_CLICK_COUNT = "CleanFinishClickCount";
    public static final String CLEAN_FINISH_CLICK_JIAU_COUNT = "CleanFinishClickJiaSuCount";
    public static final String CLEAN_FINISH_CLICK_POWER_COUNT = "CleanFinishClickPowerCount";
    public static final String CLEAN_FINISH_CLICK_NOTIFY_COUNT = "CleanFinishClickNotifyCount";
    public static final String CLEAN_FINISH_CLICK_WECHAT_COUNT = "CleanFinishClickWechatCount";
    public static final String CLEAN_FINISH_CLICK_COOL_COUNT = "CleanFinishClickCoolCount";
    public static final String CLEAN_FINISH_CLICK_QQ_COUNT = "CleanFinishClickQQCount";
    public static final String CLEAN_FINISH_CLICK_PHONE_COUNT = "CleanFinishClickPhoneCount";
    public static final String CLEAN_FINISH_CLICK_GAME_COUNT = "CleanFinishClickGameCount";
    public static final String CLEAN_FINISH_CLICK_VIRUS_COUNT = "CleanFinishClickViursCount";
    public static final String CLEAN_FINISH_CLICK_NET_COUNT = "CleanFinishClickNetCount";
    public static final String LENGTHEN_AWAIT_TIME = "LengthenAwaitTime";
    public static final String IS_FIRST_HOME_RECOMMEND = "is_first_home_recommend";
    public static final String IS_GAME_QUIKCEN_START = "is_game_quikcen_start";
    public static final String GAME_QUIKCEN_NUM = "game_quikcen_num";
    public static final String RED_PACKET_SHOW = "RedPacketShow";
    public static final String RED_PACKET_FOR = "RedPacketFor";
    public static final String COLD_AND_HOT_START_COUNT = "cold_and_hot_start_count_data";
    public static final String RED_PACKET_SHOW_TRIGGER = "RedPacketShowTrigger";
    public static final String IS_SHOW_AD = "isShowAd";
    public static final String HASE_UPDATE_VERSION = "HaseUpdateVersion";
    public static final String COOL_START_STATUS = "CoolStartStatus";
    public static final String COOL_START_TIME = "CoolStartTime";
    public static final String SCREEN_INSIDE_TIME = "ScreenInsideTime";
    public static final String BOTTOM_AD_COOL_COUNT = "BottomAdCoolCount";
    public static final String BOTTOM_AD_HOT_COUNT = "BottomAdHotCount";
    public static final String FINISH_AD_ONE_COUNT = "FinishAdOneCount";
    public static final String FINISH_AD_TWO_COUNT = "FinishAdTwoCount";
    public static final String FINISH_AD_THREE_COUNT = "FinishAdThreeCount";
    public static final String BOTTOM_AD_LIST = "BottomAdList";
    public static final String BOTTOM_AD_LOCK_COUNT = "BottomAdLockCount";

    //埋点上一级页面
    public static final String ONKEY = "one_key";
    public static final String PHONE_CLEAN = "phone_clean";
    public static final String WETCHAT_CLEAN = "wetchat_clean";
    public static final String QQ_CLEAN = "qq_clean";
    public static final String PHONE_COOLING = "phone_cooling";
    public static final String NOTITY = "notity";
    public static final String SUPER_POWER_SAVING = "super_power_saving";
    public static final String BANNER = "banner";
    public static final String APP_ID = "com.hellogeek.cleanking";
    //头条信息流
    public static final String NEWS_BASEURL = "http://newswifiapi.dftoutiao.com/jsonnew/refresh?qid=qid11381";
    //瑞狮信息流地址
    public static final String RUISHI_BASEURL = "http://api.viaweb.cn/";

    public static final String IS_USED_CLEAN_ALL = "is_used_clean_all";
    public static final String IS_USED_JIASU = "is_used_jiasu";
    public static final String IS_USED_POWER = "is_used_power";
    public static final String IS_USED_NOTIFY = "is_used_notify";
    public static final String IS_USED_WECHAT = "is_used_wechat";
    public static final String IS_USED_COLL = "is_used_coll";
    public static final String SAVE_COOL_NUM = "save_cool_num";
    public static final String IS_USED_GAME = "is_used_game";
    //wifi连接状态
    public static final String WIFI_STATE = "wifi_state";
    //充电状态切换
    public static final String CHARGE_STATE = "charge_state";

    //外部插屏时间、次数
    public static final String POP_LAYER_TIME = "pop_time";
    public static final String POP_LAYER_NUMBERS = "pop_numbers";

    //外部全屏插屏时间、次数
    public static final String POP_FULL_LAYER_TIME = "pop_full_time";
    public static final String POP_FULL_LAYER_NUMBERS = "pop_full_numbers";

    public static final String MKV_KEY_HOME_CLEANED_DATA = "mkv_key_home_cleaned_data";

    public final static String AuditSwitch = "AuditSwitch";

    public final static String WARNED_RANDOM_IDS = "warned_random_ids";

}
