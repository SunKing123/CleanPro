package com.comm.jksdk.constant;

import android.content.Context;

public class Constants {

    /**
     * APP内 使用到广告类型
     */
    public interface AdType {
        /**
         * 穿山甲
         */
        String ChuanShanJia = "chuanshanjia";

        /**
         * 优量汇
         */
        String YouLiangHui = "youlianghui";
    }

    /**
     * 广告样式
     */
    public interface AdStyle {

        /**
         * 模板插屏
         */
        String CP = "CP";
        /**
         * 自渲染全屏插屏
         */
        String FULLSCREEN_CP_01 = "FULLSCREEN_CP_01";
        /**
         * 自定义插屏_01
         */
        String CUSTOM_CP = "CUSTOM_CP_01";
        /**
         * 开屏
         */
        String OPEN_ADS = "OPEN_ADS";
        /**
         * 大图_带icon文字
         */
        String DATU_ICON_TEXT = "DATU_ICON_TEXT";
        /**
         * 全屏视频
         */
        String FULL_SCREEN_VIDEO = "FULL_SCREEN_VIDEO";
        /**
         * 激励视频
         */
        String REWARD_VIDEO = "REWARD_VIDEO";
        /**
         * 大图_带icon文字按钮居中
         */
        String DATU_ICON_TEXT_BUTTON_CENTER = "DATU_ICON_TEXT_BUTTON_CENTER";
        /**
         * 大图_带icon文字按钮
         */
        String DATU_ICON_TEXT_BUTTON = "DATU_ICON_TEXT_BUTTON";
        /**
         * 大图_下载播放按钮
         */
        String BIG_IMG_BUTTON = "BIG_IMG_PLAY_BUTTON";
        /**
         * 大图_下载播放按钮_跑马灯
         */
        String BIG_IMG_BUTTON_LAMP = "BIG_IMG_PLAY_BUTTON_LAMP";
        /**
         * 假视频大图_01
         */
        String FAKE_VIDEO_IARGE_IMAGE = "FAKE_VIDEO_IARGE_IMAGE";
        /**
         * 外部弹窗大图广告_01
         */
        String EXTERNAL_DIALOG_BIG_IMAGE_01 = "EXTERNAL_DIALOG_BIG_IMAGE_01";
        /**
         * 外部弹窗大图广告_02
         */
        String EXTERNAL_DIALOG_BIG_IMAGE_02 = "EXTERNAL_DIALOG_BIG_IMAGE_02";
    }


    public interface SPUtils {
        /**
         * 配置信息
         */
        String CONFIG_INFO = "AD_SDK_CONFIG_INFO";
        /**
         * 第一次请求广告系统时间
         */
        String FIRST_REQUEST_AD_TIME = "AD_SDK_FIRST_REQUEST_AD_TIME";
        /**
         * bid
         */
        String BID = "AD_SDK_BID";
        /**
         * 用户激活时间
         */
        String USER_ACTIVE = "AD_SDK_USER_ACTIVE";
        /**
         * 经度
         */
        String LONGITUDE = "AD_SDK_LONGITUDE";

        /**
         * 纬度
         */
        String LATITUDE = "AD_SDK_LATITUDE";
        /**
         * 优量汇广告APPID
         */
        String YLH_APPID = "AD_SDK_YLH_APPID";
        /**
         * 穿山甲广告APPID
         */
        String CHJ_APPID = "AD_SDK_CHJ_APPID";

        /**
         * 优量汇广告APPNAME
         */
        String YLH_APPNAME = "AD_SDK_YLH_APPNAME";

        /**
         * 穿山甲广告APPNAME
         */
        String CHJ_APPNAME = "AD_SDK_CHJ_APPNAME";
    }

    /**
     * 优量汇广告APPID
     */
    public static String YLH_APPID = "";

    /**
     * 穿山甲广告APPID
     */
    public static String CHJ_APPID = "";

    /**
     * 优量汇广告APPNAME
     */
    public static String YLH_APPNAME = "";

    /**
     * 穿山甲广告APPNAME
     */
    public static String CHJ_APPNAME = "";

    /**
     * 客户端随机参数(第一次安装的时候产生)
     */
    public static int bid = -1;

    /**
     * 业务线标识
     */
    public static String productName = "";

    /**
     * 渠道
     */
    public static String marketName = "";
    /**
     * 渠道
     */
    public static String adPositionId = "";
    /**
     * 用户激活时间
     */
    public static long userActive = -1;
    /**
     * 纬度
     */
    public static String latitude = "";
    /**
     * 经度
     */
    public static String longitude = "";
    /**
     * 省份
     */
    public static String province = "";
    /**
     * 城市
     */
    public static String city = "";
    /**
     * 全局context
     */
    public static Context mContext;

    /**
     * 聚合广告sdk版本号
     */
    public static int sdkVisionCode = 2;
}
