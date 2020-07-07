package com.xiaoniu.cleanking.midas;

import android.app.Application;
import android.util.Log;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xnad.sdk.MidasAdSdk;
import com.xnad.sdk.ad.entity.ScreenOrientation;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.config.AdConfig;
import com.xnad.sdk.config.AdParameter;

import org.json.JSONObject;

/**
 * @author zhengzhihao
 * @date 2020/7/3 13
 * @mail：zhengzhihao@hellogeek.com
 */
public class MidasRequesCenter {

    /**
     * 初始化广告SDK
     */
    public static void init(Application application) {

        AdConfig configBuild = new AdConfig.Build()
                .setAppId(MidasConstants.APP_ID)//应用ID
                .setProductId(MidasConstants.PRODUCT_ID)//大数据业务线ID
                .setChannel(ChannelUtil.getChannel())//渠道名
                .setServerUrl(BuildConfig.BIGDATA_MD)//业务埋点地址
                .setBusinessUrl(BuildConfig.MIDAS_NIU_DATA_SERVER_URL)//商业变现埋点地址
                .setIsFormal(true)//是否是正式环境 true 线上环境
                .setScreenOrientation(ScreenOrientation.VERTICAL)//设置屏幕方向
                .setCsjAppId(Constant.CSJ_AD_ID)//穿山甲appId
                .setYlhAppId(Constant.YLH_AD_ID)//优量汇appId
                .setNeedInitCsj(true)//如果外部已经初始化了穿山甲，可以填写false
                .setNeedInitYlh(true)//如果外部已经初始化了优量汇，可以填写false
                .setInmoBiAppId(MidasConstants.INMOBI_APPID)//预初始化inmobi
                .build();
        //初始化广告SDK
        MidasAdSdk.init(application, configBuild);


        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });



    }

    /*   AdRequestParams params = new AdRequestParams(new AdRequestParams.Builder()
                .setAdPosition("")
                .setActivity(null));*/
    /**
     * 广告请求入口
     * @param adRequestParams
     * @param absAdCallBack
     */
    public static void requestAd(AdRequestParams adRequestParams, AbsAdCallBack absAdCallBack){
        //上下文、广告位置ID
        AdParameter adParameter = new AdParameter.Builder(adRequestParams.getActivity(),adRequestParams.getAdId())
                //设置填充父布局
                .setViewContainer(adRequestParams.getViewContainer())
                .build();
        MidasAdSdk.getAdsManger().loadAd(adParameter,absAdCallBack);
    }



}
