package com.xiaoniu.cleanking.ad.mvp.model;

import android.app.Activity;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.google.gson.Gson;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.bean.AdYLHEmitterBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.mvp.contract.AdContract;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    private final String TAG = "ad_status";

    /**
     * 优量会模板
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     */
    @Override
    public Observable<AdYLHEmitterBean> getYLHTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean) {

        return Observable.create(new ObservableOnSubscribe<AdYLHEmitterBean>() {

            @Override
            public void subscribe(ObservableEmitter<AdYLHEmitterBean> emitter) throws Exception {
                Log.d(TAG, "优量会模板准备请求 id:"+ adRequestBean.getAdvertId());
                ADSize adSize = new ADSize(adRequestParamentersBean.viewWidth, ADSize.AUTO_HEIGHT);
                NativeExpressAD nativeExpressAD = new NativeExpressAD(adRequestParamentersBean.context,
                        adSize,
                        adRequestBean.getAdvertId(),
                        new NativeExpressAD.NativeExpressADListener() {
                            @Override
                            public void onNoAD(AdError adError) {
                                Log.d(TAG, "优量会模板请求失败  id:"+ adRequestBean.getAdvertId()+"  message: " + adError.getErrorMsg() + " code: " + adError.getErrorCode());
                                emitter.onError(new RuntimeException("优量会模板 onNoAD message:" + adError.getErrorMsg() + " code:" + adError.getErrorCode()));
                                StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "fail", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADLoaded(List<NativeExpressADView> list) {
                                Log.d(TAG, "优量会模板 拿到广告准备渲染 " + adRequestBean.getAdvertId());
                                if (CollectionUtils.isEmpty(list)) {
                                    emitter.onError(new RuntimeException("优量会模板 onADLoaded,但是没有广告"));
                                }
                                NativeExpressADView nativeExpressADView = list.get(0);
                                nativeExpressADView.render();
                                emitter.onNext(new AdYLHEmitterBean(nativeExpressADView, adRequestParamentersBean.index, 2));
                            }

                            @Override
                            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板渲染失败 广告位 id: " + adRequestBean.getAdvertId() +"  当前位置index是： " + adRequestParamentersBean.index  );
                                emitter.onError(new RuntimeException("优量会模板 onRenderFail"));
                            }

                            @Override
                            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板渲染成功 广告位 id: " + adRequestBean.getAdvertId()+   "  当前位置index是： " + adRequestParamentersBean.index);
                                StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "success", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADExposure(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板曝光 id:"+adRequestBean.getAdvertId());
                                StatisticsUtils.customADRequest("ad_show", "广告展示曝光", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "success", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADClicked(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板点击 id:"+adRequestBean.getAdvertId());
                                StatisticsUtils.clickAD("ad_click", "广告点击", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADClosed(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板关闭 id:"+adRequestBean.getAdvertId() +"  index"+ adRequestParamentersBean.index);
                                emitter.onNext(new AdYLHEmitterBean(nativeExpressADView, adRequestParamentersBean.index, 3));
                                emitter.onComplete();
                                nativeExpressADView.destroy();
                                StatisticsUtils.clickAD("ad_close_click", "关闭点击", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板 onADLeftApplication ");

                            }

                            @Override
                            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板 onADOpenOverlay ");

                            }

                            @Override
                            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                                Log.d(TAG, "优量会模板 onADCloseOverlay ");

                            }
                        });
                // 注意：如果您在平台上新建原生模板广告位时，选择了支持视频，那么可以进行个性化设置（可选）
                nativeExpressAD.setVideoOption(new VideoOption.Builder()
                        .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
                        .setAutoPlayMuted(true) // 自动播放时为静音
                        .build());
                nativeExpressAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的
                nativeExpressAD.loadAD(1);
            }
        });
    }

    /**
     * 优量会开屏
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     */
    @Override
    public Observable<AdYLHEmitterBean> getYLHSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean) {

        return Observable.create(new ObservableOnSubscribe<AdYLHEmitterBean>() {

            @Override
            public void subscribe(ObservableEmitter<AdYLHEmitterBean> emitter) throws Exception {
                Log.d(TAG, "优量会开屏准备开始"+adRequestBean.getAdvertId());
                SplashAD splashAD = new SplashAD((Activity) adRequestParamentersBean.context,
                        adRequestParamentersBean.skipContainer,
                        adRequestBean.getAdvertId(),
                        new SplashADListener() {
                            @Override
                            public void onADDismissed() {
                                Log.d(TAG, "优量会 开屏----onADDismissed");
                            }

                            @Override
                            public void onNoAD(AdError adError) {
                                Log.d(TAG, "优量会开屏请求失败  id:"+ adRequestBean.getAdvertId()+"  message: " + adError.getErrorMsg() + " code: " + adError.getErrorCode());
                                emitter.onError(new RuntimeException("优量会 开屏----onNoAD  获取广告失败  message:" + adError.getErrorMsg() + " code:" + adError.getErrorCode()));
                                StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "fail", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADPresent() {
                                Log.d(TAG, "优量会开屏请求成功 广告位 id: " + adRequestBean.getAdvertId());
                                emitter.onNext(new AdYLHEmitterBean(adRequestParamentersBean.index, 0, 2));
                                emitter.onComplete();
                                StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "success", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADClicked() {
                                Log.d(TAG, "优量会开屏点击 广告位 id: " + adRequestBean.getAdvertId());
                                StatisticsUtils.clickAD("ad_click", "广告点击", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADTick(long millisUntilFinished) {
                                Log.d(TAG, "优量会 开屏----onADTick"+millisUntilFinished);
                            }

                            @Override
                            public void onADExposure() {
                                Log.d(TAG, "优量会开屏曝光 广告位 id: " + adRequestBean.getAdvertId());
                                StatisticsUtils.customAD("ad_show", "广告展示曝光", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onADLoaded(long l) {
                                Log.d(TAG, "优量会 开屏----onADLoaded"+l);

                            }

                        },
                        adRequestParamentersBean.fetchDelay);
                if (adRequestParamentersBean.adContainer != null) {
                    splashAD.fetchAndShowIn(adRequestParamentersBean.adContainer);
                }
            }
        });
    }

    /**
     * 穿山甲模板
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     */
    @Override
    public Observable<List<TTNativeExpressAd>> getCSJTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean) {
        return Observable.create(new ObservableOnSubscribe<List<TTNativeExpressAd>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TTNativeExpressAd>> emitter) throws Exception {
                Log.d(TAG, "穿山甲模板准备开始 id:" + adRequestBean.getAdvertId());
                TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(adRequestParamentersBean.context);
                //设置广告参数
                AdSlot adSlot = new AdSlot.Builder()
                        .setCodeId(adRequestBean.getAdvertId()) //广告位id
                        .setSupportDeepLink(true)
                        .setAdCount(1) //请求广告数量为1到3条
                        .setExpressViewAcceptedSize(adRequestParamentersBean.viewWidth, adRequestParamentersBean.viewHeight)
                        .setImageAcceptedSize(640, 320) //这个参数设置即可，不影响个性化模板广告的size
                        .build();
                mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "穿山甲模板广告失败 message:" + s + " code:" + i +" 广告位id:" + adRequestBean.getAdvertId()+ "  当前位置index是： " + adRequestParamentersBean.index );
                        emitter.onError(new RuntimeException("code " + i + " message:" + s));
                        emitter.onComplete();
                        StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "fail", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                    }

                    @Override
                    public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                        Log.d(TAG, "穿山甲模板广告成功 广告位id" + adRequestParamentersBean.advertPosition+"  当前位置index是： " + adRequestParamentersBean.index);
                        emitter.onNext(list);
                        emitter.onComplete();
                        StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "success", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                    }
                });
            }
        });
    }

    /**
     * 穿山甲开屏
     *
     * @param adRequestParamentersBean
     * @param adRequestBean
     */
    @Override
    public Observable<TTSplashAd> getCSJSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean) {
        return Observable.create(new ObservableOnSubscribe<TTSplashAd>() {
            @Override
            public void subscribe(ObservableEmitter<TTSplashAd> emitter) throws Exception {
                Log.d(TAG, "穿山甲开屏开始 id:" + adRequestBean.getAdvertId());
                TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(adRequestParamentersBean.context);
                AdSlot adSlot = new AdSlot.Builder()
                        .setCodeId(adRequestBean.getAdvertId())
                        .setSupportDeepLink(true)
                        .setImageAcceptedSize(1080, 1920)
                        .build();
                mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "穿山甲开屏广告失败 message:" + s + " code:" + i +" 广告位id:" + adRequestBean.getAdvertId());
                        emitter.onError(new RuntimeException(s));
                        StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "fail", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                    }

                    @Override
                    public void onTimeout() {
                        Log.d(TAG, "穿山甲开屏广告超时 广告位id:"+adRequestBean.getAdvertId());
                        emitter.onError(new TimeoutException("穿山甲广告商请求超时"));
                        StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "fail", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                    }

                    @Override
                    public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                        Log.d(TAG, "穿山甲开屏广告成功 广告位id:"+adRequestBean.getAdvertId());
                        emitter.onNext(ttSplashAd);
                        emitter.onComplete();
                        StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "success", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                    }
                }, adRequestParamentersBean.fetchDelay);
            }
        });
    }

    /**
     * 广告配置
     *
     * @param commonSubscriber
     */
    public void getSwitchInfoList(AdRequestParamentersBean adRequestParamentersBean, Common4Subscriber<SwitchInfoList> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("channel", ChannelUtil.getChannel());
        map.put("appVersion", AppUtils.getVersionName(adRequestParamentersBean.context, adRequestParamentersBean.context.getPackageName()));
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        if(adRequestParamentersBean.adType.equals(AdType.Splash)){
            ApplicationDelegate.getAppComponent().getApiUserService().getSwitchInfoList(body)
                    .subscribeOn(Schedulers.io())
                    .timeout(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(commonSubscriber);
        }else {
            ApplicationDelegate.getAppComponent().getApiUserService().getSwitchInfoList(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(commonSubscriber);
        }

    }

    /**
     * 埋点获取AdvertPositio
     *
     * @param adRequestParamentersBean
     * @return
     */
    private String getAdvertPosition(AdRequestParamentersBean adRequestParamentersBean) {
        String advertPosition = "1";
        switch (adRequestParamentersBean.advertPosition) {
            case PositionId.DRAW_ONE_CODE:
                advertPosition = "1";
                break;
            case PositionId.DRAW_TWO_CODE:
                advertPosition = "2";
                break;
            case PositionId.DRAW_THREE_CODE:
                advertPosition = "3";
                break;
        }
        return advertPosition;
    }
}



