package com.comm.jksdk;

import android.content.Context;

import com.comm.jksdk.ad.factory.NativeManagerFactory;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.config.AdsConfig;
import com.comm.jksdk.config.InitBaseConfig;
import com.comm.jksdk.config.listener.ConfigListener;
import com.tencent.mmkv.MMKV;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk
 * @ClassName: GeekAdSdk
 * @Description: java类作用描述
 * @Author: fanhailong
 * @CreateDate: 2019/11/11 17:13
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/11 17:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public final class GeekAdSdk {

    private static boolean mIsInit = false;

    public static Context mContext;
    public static String mRroductName;
    public static String mChannel;
    public static String mIsFormal;

    /**
     * 聚合广告sdk初始化
     * @param context 上下文
     * @param productName 业务线名称 12：日历；13：即刻天气 18：悟空清理
     * @param channel 渠道名称
     * @param isFormal 是否是正式环境 true对应生产环境
     */
    public static void init(Context context, String productName, String csjAppId, String channel, String isFormal){
        mContext = context.getApplicationContext();
        String mmkvPath = MMKV.initialize(mContext);
        mRroductName = productName;
        mChannel = channel;
        mIsFormal = isFormal;
        //初始化基本配置信息
        InitBaseConfig.getInstance().init(mContext);
        InitBaseConfig.getInstance().initChjAd(mContext, csjAppId);
        AdsConfig.setProductName(mRroductName);
        mIsInit = true;
    }

    /**
     * 请求广告配置信息
     * @param listener 回调
     */
    public static void requestConfig(ConfigListener listener){
        checkInit();
        AdsConfig.getInstance(mContext).requestConfig(listener);
    }

    public static void requestConfig(){
        checkInit();
        AdsConfig.getInstance(mContext).requestConfig(null);
    }

    /**
     * 获取广告管理类
     * @return
     */
    public static AdManager getAdsManger(){
        return new NativeManagerFactory().produce();
    }

    /**
     * 设置经度、纬度
     * @param longitude
     * @param latitude
     */
    public static void setLocation(String longitude, String latitude){
        AdsConfig.setLongitude(longitude);
        AdsConfig.setLatitude(latitude);
    }

    /**
     * 设置bid
     * @param bid
     */
    public static void setBid(int bid){
        AdsConfig.setBid(bid);
    }

    /**
     * 设置第一次激活时间
     */
    public static void setActivationTime(long time){
        AdsConfig.setUserActive(time);
    }

    public static boolean isInit() {
        return mIsInit;
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getRroductName() {
        return mRroductName;
    }

    public static String getChannel() {
        return mChannel;
    }

    public static String isFormal() {
        return mIsFormal;
    }

    /**
     * 检测是否初始化
     */
    private static void checkInit() {
        if (!mIsInit) {
            throw new RuntimeException("GeekAdSdk should  be init");
        }
    }
}
