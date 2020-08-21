package com.xiaoniu.cleanking.midas;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.config.AdConfig;
import com.xiaoniu.unitionadbase.config.AdParameter;
import com.xiaoniu.unitionadbase.config.WebViewConfig;
import com.xiaoniu.unitionadbase.impl.IUnitaryListener;
import com.xiaoniu.unitionadbase.model.AdInfoModel;
import com.xiaoniu.unitionadproxy.MidasAdSdk;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        WebViewConfig webViewConfig = new WebViewConfig();
        webViewConfig.statusBarColor = "#06C581";//webView状态栏颜色
        webViewConfig.titleBarColor = "#06C581";//webView标题栏背景颜色
        webViewConfig.isStatusBarDarkFont = false;//webView栏状态字体颜色是否黑色
        webViewConfig.backImageViewRes = R.mipmap.icon_left_arrow_white;//webView标题栏返回图标
        webViewConfig.titleTextViewColor = "#FFFFFFFF";//webView标题栏字体颜色
        AdConfig configBuild = new AdConfig.Build()
                .setAppId(MidasConstants.APP_ID)//应用ID
                .setProductId(MidasConstants.PRODUCT_ID)//大数据业务线ID
                .setChannel(ChannelUtil.getChannel())//渠道名
                .setServerUrl(BuildConfig.BIGDATA_MD)//业务埋点地址
                .setBusinessUrl(BuildConfig.MIDAS_NIU_DATA_SERVER_URL)//商业变现埋点地址
                .setIsFormal(true)//是否是正式环境 true 线上环境
//                .setDrawFeedAdPositions("adpos_7538614881", "adpos_8941326481")//draw信息流广告位id
//                .setDrawFeedWidthPx(DisplayUtils.dp2px(this, 300))//设置draw信息流宽度  单位像素
//                .setDrawFeedHeightPx(DisplayUtils.dp2px(this, 500))//设置draw信息流高度  单位像素
                .setSplashBottomHeightDp(100)//设置启动页半屏底部高度 单位dp
                .setPrimaryColor("#06C581")//设置主题色颜色[主要为倒计时颜色、按钮、箭头颜色设置]
//        .setDisableAllianceInitVariableArray(UnionConstants.AD_SOURCE_FROM_CSJ,UnionConstants.AD_SOURCE_FROM_YLH)//设置禁用联盟初始化可变数组
                .setWebViewConfig(webViewConfig)//设置webview配置
                .setHotFlashIntervalTime(PreferenceUtil.getInstants().getHotTime())//设置热起时间间隔
//                .setShowLogWindow(true)//设置是否显示日志窗口
                .build();
        //初始化广告SDK
        MidasAdSdk.init(application, configBuild);

        //注册单一元监听
        MidasAdSdk.registerUnitaryListener(new IUnitaryListener() {
            @Override
            public void onConfirmExit() {
//                Toast.makeText(getApplicationContext(),"确认退出",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onContinueBrowsing() {
//                Toast.makeText(getApplicationContext(),"继续浏览",Toast.LENGTH_SHORT).show();
            }
        });

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });

    }

    /**
     * 请求并显示广告
     * @param activity  当前页面
     * @param adPositionId  广告位ID
     * @param absAdBusinessCallback 业务回调 [成功、失败、曝光、展示、激励等等]
     */
    public static void requestAndShowAd(Activity activity, String adPositionId, AbsAdBusinessCallback absAdBusinessCallback){
        if (absAdBusinessCallback == null){
            absAdBusinessCallback = new AbsAdBusinessCallback() {};
        }
        if (activity == null){
            return;
        }
        AbsAdBusinessCallback commonCallback = buildCommonCallBack(adPositionId,absAdBusinessCallback);
        AdParameter adParameter = new AdParameter.
                Builder(activity, adPositionId)
                .build();
        MidasAdSdk.loadAd(adParameter, commonCallback);
    }

    /**
     * 中间层回调
     * @param midaId
     * @param adCommonCallback
     * @return
     */
    private static AbsAdBusinessCallback buildCommonCallBack(String midaId, AbsAdBusinessCallback adCommonCallback){
        return new AbsAdBusinessCallback() {
            @Override
            public void onAdLoadError(String errorCode, String errorMsg) {
                super.onAdLoadError(errorCode, errorMsg);
                Map<String, Object> exmap = new HashMap<>();
                exmap.put("midas_id",midaId);
                exmap.put("fail_code",errorCode);
                exmap.put("fail_message",errorMsg);
                StatisticsUtils.customTrackEvent("ad_request_fail_code", "广告请求失败返回错误代码", "all_ad_request", "all_ad_request");
                adCommonCallback.onAdLoadError(errorCode, errorMsg);

            }

            @Override
            public void onAdLoaded(AdInfoModel adInfoModel) {
                super.onAdLoaded(adInfoModel);
                adCommonCallback.onAdLoaded(adInfoModel);
            }

            @Override
            public void onAdExposure(AdInfoModel adInfoModel) {
                super.onAdExposure(adInfoModel);
                adCommonCallback.onAdExposure(adInfoModel);
            }

            @Override
            public void onAdClick(AdInfoModel adInfoModel) {
                super.onAdClick(adInfoModel);
                adCommonCallback.onAdClick(adInfoModel);
            }

            @Override
            public void onAdClose(AdInfoModel adInfoModel) {
                super.onAdClose(adInfoModel);
                adCommonCallback.onAdClose(adInfoModel);

            }

            @Override
            public void onChangeAnotherAd(AdInfoModel adInfoModel) {
                super.onChangeAnotherAd(adInfoModel);
                adCommonCallback.onChangeAnotherAd(adInfoModel);
            }

            @Override
            public void onAdVideoComplete(AdInfoModel adInfoModel) {
                super.onAdVideoComplete(adInfoModel);
                adCommonCallback.onAdVideoComplete(adInfoModel);
            }
        };
    }
    /**
     * 预加载广告
     * @param activity  当前页面
     * @param adPositionId  广告位ID
     */
    public static void preloadAd(Activity activity,String adPositionId){
        if (activity == null){
            return;
        }
        AdParameter adParameter = new AdParameter.
                Builder(activity, adPositionId)
                .build();
        //预加载广告
        MidasAdSdk.preLoad(adParameter);
    }


}
