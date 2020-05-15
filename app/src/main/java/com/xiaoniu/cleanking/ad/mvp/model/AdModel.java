package com.xiaoniu.cleanking.ad.mvp.model;

import android.app.Activity;
import android.content.Context;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.google.gson.Gson;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.mvp.contract.AdContract;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad
 * @ClassName: AdModel
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:52
 */

public class AdModel extends BaseModel implements AdContract.Model {

    @Inject
    UserApiService mService;

    /**
     * 优量会模板
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     * @param adListener
     */
    @Override
    public void getYLHTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, NativeExpressAD.NativeExpressADListener adListener) {
        ADSize adSize = new ADSize(adRequestParamentersBean.viewWidth, ADSize.AUTO_HEIGHT);
        NativeExpressAD nativeExpressAD = new NativeExpressAD(adRequestParamentersBean.context, adSize, PositionId.APPID, adRequestBean.getAdvertId(), adListener);
        // 注意：如果您在平台上新建原生模板广告位时，选择了支持视频，那么可以进行个性化设置（可选）
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build());
        nativeExpressAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的
        nativeExpressAD.loadAD(1);
    }

    /**
     * 穿山甲模板
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     * @param adListener
     */
    @Override
    public void getCSJTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, TTAdNative.NativeExpressAdListener adListener) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(adRequestParamentersBean.context);
        //设置广告参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adRequestBean.getAdvertId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(adRequestParamentersBean.viewWidth, adRequestParamentersBean.viewHeight)
                .setImageAcceptedSize(640, 320) //这个参数设置即可，不影响个性化模板广告的size
                .build();
        mTTAdNative.loadNativeExpressAd(adSlot, adListener);
    }


    /**
     * 优量会开屏
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     * @param adListener
     */
    @Override
    public void getYLHSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, SplashADListener adListener) {
        SplashAD splashAD = new SplashAD((Activity) adRequestParamentersBean.context, adRequestParamentersBean.skipContainer, PositionId.APPID, adRequestBean.getAdvertId(), adListener, adRequestParamentersBean.fetchDelay);
        if (adRequestParamentersBean.adContainer != null) {
            splashAD.fetchAndShowIn(adRequestParamentersBean.adContainer);
        }
    }

    /**
     * 穿山甲开屏
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     * @param adListener
     */
    @Override
    public void getCSJSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean, TTAdNative.SplashAdListener adListener) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(adRequestParamentersBean.context);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adRequestBean.getAdvertId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        mTTAdNative.loadSplashAd(adSlot, adListener, adRequestParamentersBean.fetchDelay);
    }

    /**
     * 广告配置
     *
     * @param commonSubscriber
     */
    public void getSwitchInfoList(Context context, Common4Subscriber<SwitchInfoList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(context, context.getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        ApplicationDelegate.getAppComponent().getApiUserService().getSwitchInfoList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(commonSubscriber);
    }


}



