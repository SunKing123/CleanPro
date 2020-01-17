package com.comm.jksdk.config;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.comm.jksdk.BuildConfig;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static boolean sInit;
    private static String mAppId;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

//    public static TTAdManager get(String appId) {
//        if (!sInit && !TextUtils.isEmpty(appId) && !appId.equals(mAppId)) {
//            sInit = false;
//            mAppId = appId;
//            init(GeekAdSdk.getContext(), mAppId);
//        }
//        if (!sInit) {
//            throw new RuntimeException("TTAdSdk is not init, please check.");
//        }
//        return TTAdSdk.getAdManager();
//    }

    public synchronized static void init(Context context, String appId) {
        mAppId = appId;
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (!sInit) {

            TTAdSdk.init(context, buildConfig(context));
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context) {
//        String chjAppid = SpUtils.getString(Constants.SPUtils.CHJ_APPID, "");
//        if (TextUtils.isEmpty(chjAppid)) {
//            chjAppid = Constants.CHJ_APPID;
//        }
        String chjAppName = AdsConfig.getProductAppName();
//        if (TextUtils.isEmpty(chjAppName)) {
//            chjAppName = Constants.CHJ_APPNAME;
//        }
        boolean adsDebug = false;
        if (BuildConfig.DEBUG) {
            adsDebug = true;
        } else {
            adsDebug = false;
        }

        return new TTAdConfig.Builder()
                .appId(mAppId.trim())
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(chjAppName)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(adsDebug) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G, TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                .keywords("清理大师，手机加速，垃圾清理，抖音，病毒查杀，手机降温，网络加速，酷我音乐，好看点，微鲤，七猫，看点快报，玩赚星球，赚钱，安全卫士，助手，管家，西瓜视频，映客，今日头条，趣头条，火山小视频，成语大富豪，球球视频，波波视频，爱奇艺，拼多多，小说，快手，哔哩哔哩，知乎")
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
    }
    /* .gender(TTAdConstant.GENDER_MALE)  //男性*/

    public static boolean issInit() {
        return sInit;
    }
}
