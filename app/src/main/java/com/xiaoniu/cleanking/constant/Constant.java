package com.xiaoniu.cleanking.constant;

import android.Manifest;

import com.xiaoniu.cleanking.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 常用常量
 * Created by tie on 2017/2/18.
 */

public class Constant {

    public static final String SUCCESS = "000000";
    public static final String SUCCESS_V3 = "0";

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
    public static final String CLEAN_TABLE_NAME = "XNWeatherCityModel";
    public static final String CLEAN_DB_SAVE = "weather_db_issaved";

    public static final String SWITCH_INFO = "switch_info";

    public static final String APP_NAME = "gj_clean";


    /**
     * 数美sdk
     */
    public static final String SMANTIFRAUD_ORGANIZATION = "twbumcBWaJEWBgmNPkNy";//公司标识
    public static final String SMANTIFRAUD_ACCESSKEY = "9b7cNnxUv89g8wC6toXi";//密钥
    public static final String SMANTIFRAUD_AINFOKEY = "XivVizeZcPCRRqxXHHtGawuQUxpWjkLMcqaeBguyoNSCiXEKtjsYalJfnYGHyoQU";//ainfokey
    public static final String SMANTIFRAUD_PUBLIC_KEY = "MIIDLzCCAhegAwIBAgIBMDANBgkqhkiG9w0BAQUFADAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wHhcNMjAwNjI4MDgyNzA4WhcNNDAwNjIzMDgyNzA4WjAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCW0VkSqMdoidqEWiwmeGlwt2xZJun10/vsfXvezuz6N/4I7VTckRtCdSyXWlLZUTZ6gx8+rXYEkvjes+DRlTSHv1Cu++HWwTfmFbWAsZ2P0tTJiEt9JHE/kHsOMm+nsxJOMA18vEG8Ca4sYsMKIVlZcTgnJk0L+B9O+vqEqNosVGfUK17nq4oONx0IwrwO/skb9pMcZdHesVxOilgLZbRsYaNClViIVqpjBqXyGquRFgRtgjdsljGaKD4VQGlp0+gcrL1b0ELv2sToMi1wYO8dIuoukBGzBDyqs6NPELKjtvhmpdW4d2Nlr/IAzsykQgY/nl3+XKdT6pj6AzxaN/YDAgMBAAGjUDBOMB0GA1UdDgQWBBT6cHQqsOBPPwxJfGmzwebJVbm24TAfBgNVHSMEGDAWgBT6cHQqsOBPPwxJfGmzwebJVbm24TAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQBtoZmeJmKYATA8xbNfn+k4L2eKnQsbggv1glTKF1QHmUfnCdDIC05jXv9F6buMQOZ9JimXY/eh8BOhD0ojxjU+fJYgChg88xLyLket1GPYHLBeVUcgCROe8CLYvUc/INFnzSDEms6jMxFLev9f7mLiy1YFRfvPpYuZSJ3asenLnIDZJl5EKCNUKV45wScgqLJQ/zxuJerVfIqLHSLIFjacYcTTuu8uE+1BK5vqI3JRt4wAzOYvPxxz65XFEXjmuZ6KDg9XBwEPJ2ZUJaQGuHKi7KvaMfevzwMXxpuQnmoDLFQ5IQyPBoGTJpLpSdqOYdVZyXtepf9jQzFbu6ogrCbM";//public_key
    public static final String APPLICATION_IDENTIFICATION = "default";         //应用标识
    /*****************************************************/
    //闪验appId
    public static final String SHANYAN_APPID = "bSdGrkKB";
    //umeng   appKey
    public static final String UMENG_APPKEY = "5dcb9de5570df3121b000fbe";
    //微信  appid
    public static final String WEICHAT_APPID = "wx646080363915ffe2";
    //微信  appsecret
    public static final String WEICHAT_APPSECRET = "730da38ee6572f518d5d79891219ad4e";
    //QQ aapId
    public static final String QQ_APPID = "1109516379";
    //QQ aapkey
    public static final String QQ_APPKEY = "SJUCaQdURyRd8Dfi";
    //SinaWeibo aapId
    public static final String SINA_APPID = "1456333364";
    //SinaWeibo aapkey
    public static final String SINA_APPSECRET = "bee74e1ccd541f657875803a7eb32b1b";

    /**
     * 天气接口默认areaCode
     */
    public static final String DEFAULT_AREA_CODE_OF_WEATHER = "121010300";

    /**
     * 天气预报数据缓存；
     */
    public static final String DEFAULT_WEATHER_VIDEO_INFO = "default_weather_video_info";

    /**
     * 白名单
     */

    private static final List<String> list = new ArrayList<>();

    static {
        list.add("com.geek.jk.weather"); // 即刻天气
        list.add("ccom.xujin.weather"); // 知心天气
        list.add("com.yitong.weather"); // 诸葛天气
        list.add("com.geek.jk.calendar.app"); // 即刻万年历
        list.add("com.geek.luck.calendar.app"); // 诸葛万年历
        list.add("com.xiaoniu.cleanking");// 悟空清理
        list.add("com.xiaoniu");
        list.add("com.geek.jk.weather.fission"); // 知心天气
        list.add("com.hellogeek.cleanking"); //清理管家极速版
    }

    public static List<String> WHITE_LIST() {
        return Collections.unmodifiableList(list);
    }
   /* public static final List<String> WHITE_LIST = Arrays.asList(
            "com.geek.jk.weather",  // 即刻天气
            "com.xujin.weather",    // 知心天气
            "com.yitong.weather",   // 诸葛天气
            "com.geek.jk.calendar.app",  // 即刻万年历
            "com.geek.luck.calendar.app", // 诸葛万年历
            "com.xiaoniu.cleanking", // 悟空清理
            "com.xiaoniu",
            "com.geek.jk.weather.fission", // 知心天气
            "com.hellogeek.cleanking"     //清理管家极速版

    );
*/

    public static String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


}

