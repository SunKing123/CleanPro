package com.xiaoniu.cleanking.scheme.Constant;

import com.xiaoniu.cleanking.BuildConfig;

/**
 * deprecation:协议常量
 * author:ayb
 * time:2017-6-8
 */
public class SchemeConstant {
    //协议SCHEME和协议HOST
    public static final String SCHEME = "cleankingmajor";
    public static final String HOST = BuildConfig.APPLICATION_ID;
    public static final String XIAONIU_HOST = "com.xiaoniu.cleanking";//java代码路径
    //公共协议类型
    public static final String JUMP = "/jump";
    public static final String NATIVE = "/native";
    public static final String NATIVE_NO_PARAMS = "/native_no_params";

    //公共参数获取
    public static final String H5_URL = "url";//H5地址
    public static final String H5_IS_NO_TITLE = "is_no_title";//H5是否有标题
    public static final String H5_TITLE = "h5_title";//H5标题
    public static final String NATIVE_NAME = "name";
    public static final String ANDROID_NAME = "a_name";
    //native协议
    public static final String NATIVE_MAIN = "main";

    //native params参数
    public static final String EXTRA_MAIN_INDEX = "main_index";
    //刮刮乐相关
    public static final String IS_FULL_SCREEN = "isfullscreen";
    public static final String TAB_INDEX = "tabIndex";
    public static final String NEED_LOGIN = "need_login";
    public static final String NEED_YK_LOGIN = "need_yk_login";
    public static final String REWARD_TOP = "/rewardPop";
    public static final String CLOSE = "/close";
    public static final String HOME = "/home";
    //params 参数
    public static final String PARAMS_MAIN_INDEX = "main_index";
    public static final String TOTAL_COIN = "totalCoin";
    public static final String COIN = "coin";
    public static final String IS_DOUBLE = "isDouble";
    public static final String TASK_ID = "taskId";
    public static final String DOUBLEDMAGNIFICATION = "doubledMagnification";
    public static final String IS_REFRESH = "isRefresh";
    public static final String SIGNDAY = "signDay";
    public static final String CARD_POSITION = "cardPosition";
    public static final String TARGET = "target";
    public static final String URL = "url";
    //area=1  区域1
    //area=2  区域2
    public static final String AREA = "area";
    /**
     * 广告配置新增参数
     * source:广告来源
     * applicationId:广告配置id(穿山甲、优量汇)
     * codeId:广告位id
     * adDes:埋点描述
     */
    public static final String AD_SOURCE = "source";
    public static final String AD_SOURCE2 = "source2";
    public static final String AD_APPLICATIONID = "applicationId";
    public static final String AD_CODEID = "codeId";
    public static final String AD_CODEID2 = "codeId2";
    public static final String AD_ADDES = "adDes";
    public static final String AD_CURRENT_PAGE_ID = "currentPageId";
    public static final String AD_AD_POSITION_ID = "adPosition";
    public static final String AD_AD_POSITION_ID2 = "adPosition2";

    /**
     * 本地push scheme
     */
    public static class LocalPushScheme {
        //通知栏清理
        public static final String SCHEME_NOTIFY_ACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "tool.notify.activity.NotifyCleanGuideActivity";

        //一键清理
        public static final String SCHEME_NOWCLEANACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "newclean.activity.NowCleanActivity";

        //一键加速
        public static final String SCHEME_PHONEACCESSACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "main.activity.PhoneAccessActivity";

        //超强省电
        public static final String SCHEME_PHONESUPERPOWERACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "main.activity.PhoneSuperPowerActivity";

        //手机降温
        public static final String SCHEME_PHONECOOLINGACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "main.activity.PhoneCoolingActivity";

        //病毒查杀
        public static final String SCHEME_VIRUSKILLACTIVITY = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "main.activity.VirusKillActivity";

        //跳转到冷启
        public static final String SCHEME_COLD_START = SCHEME + "://" + HOST + NATIVE_NO_PARAMS + "?" + ANDROID_NAME + "=" + "main.activity.SplashADActivity";


    }

    //根据className启动
    public static class StartFromClassName {
        //插屏全屏页面
        public static final String CLASS_FULLPOPLAYERACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity";

        //插屏非全屏页面
        public static final String CLASS_POPLAYERACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity";

        //插屏锁屏页面
        public static final String CLASS_LOCKACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.LockActivity";

        //本地推送弹窗界面
        public static final String CLASS_LOCAL_PUSH_ACTIVITY = "com.xiaoniu.cleanking.ui.localpush.PopPushActivity";
    }

}
