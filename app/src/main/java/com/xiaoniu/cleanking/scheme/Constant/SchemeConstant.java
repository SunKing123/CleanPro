package com.xiaoniu.cleanking.scheme.Constant;

import com.xiaoniu.cleanking.BuildConfig;

/**
 * deprecation:协议常量
 * author:ayb
 * time:2017-6-8
 */
public class SchemeConstant {
    //协议SCHEME和协议HOST
    public static String SCHEME = "cleanking";
    public static String HOST = BuildConfig.APPLICATION_ID;
    public  static String XIAONIU_HOST = "com.xiaoniu.cleanking";//java代码路径
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

    /**
     * 本地push scheme
     */
    public static class LocalPushScheme{
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


    }

    //根据className启动
    public static class StartFromClassName{
        //插屏全屏页面
        public static final String CLASS_FULLPOPLAYERACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.FullPopLayerActivity";

        //插屏非全屏页面
        public static final String CLASS_POPLAYERACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity";

        //插屏锁屏页面
        public static final String CLASS_LOCKACTIVITY = "com.xiaoniu.cleanking.ui.lockscreen.LockActivity";


    }

}
