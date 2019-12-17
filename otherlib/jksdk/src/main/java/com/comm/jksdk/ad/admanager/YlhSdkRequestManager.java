package com.comm.jksdk.ad.admanager;

import android.app.Activity;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdRequestListener;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.comm.util.AdError;

import java.util.List;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.admanager
 * @ClassName: CsjSdkRequestManager
 * @Description: 优量汇广告请求类
 * @Author: fanhailong
 * @CreateDate: 2019/12/2 18:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/2 18:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class YlhSdkRequestManager extends SdkRequestManager implements NativeADUnifiedListener{

    // 广告请求数量
    protected final static int REQUEST_AD_COUNTS = 1;

    private final int MAX_DURATION = 30;

    @Override
    public void requestAd(Activity activity, AdInfo adInfo, AdRequestListener listener) {
        //广告样式
        String style = adInfo.getAdStyle();
        if (Constants.AdStyle.DATU_ICON_TEXT.equals(style) || Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style) || Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)
                || Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style) || Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style) || Constants.AdStyle.BIG_IMG_BUTTON.equals(style) || Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_02.equals(style)
                || Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) {
            //todo请求大图广告
            getAdByBigImg(activity, adInfo, listener);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
            getAdBySplashAd(activity, adInfo, listener);
        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
            getFullScreenVideoAd(activity, adInfo, listener);
        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style) || Constants.AdStyle.CP.equals(style)) {
            getCustomInsertScreenAd(activity, adInfo, listener);
//        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
//            getRewardVideoAd(activity, adInfo, listener);
        } else if(Constants.AdStyle.FEED_TEMPLATE.equals(style)) {
            getFeedTemplate(activity, adInfo, listener);
        } else {
            if (listener != null) {
                listener.adError(adInfo, 2, "暂不支持该样式");
            }
        }
    }

    /**
     * 获取信息流模板广告
     * @param activity
     * @param info
     * @param listener
     */
    private void getFeedTemplate(Activity activity, AdInfo info, AdRequestListener listener) {
        if (listener != null) {
            listener.adSuccess(info);
        }
    }

    /**
     * 请求图片广告
     *
     */
    protected void getAdByBigImg(Activity activity, AdInfo adInfo, AdRequestListener listener) {
        LogUtils.d(TAG, "onADLoaded->请求优量汇广告");
        NativeUnifiedAD mAdManager = new NativeUnifiedAD(activity, adInfo.getAdAppid(), adInfo.getAdId(), new NativeADUnifiedListener() {
            @Override
            public void onNoAD(AdError adError) {
                LogUtils.d(TAG, "onNoAD->请求优量汇失败,ErrorCode:" + adError.getErrorCode() + ",ErrorMsg:" + adError.getErrorMsg());
                if (listener != null) {
                    listener.adError(adInfo, adError.getErrorCode(), adError.getErrorMsg());
                }
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> list) {
                if (CollectionUtils.isEmpty(list)) {
                    if (listener != null) {
                        listener.adError(adInfo, 1, "广告数据为空");
                    }
                    return;
                }
                NativeUnifiedADData nativeUnifiedADData = list.get(0);
                if (nativeUnifiedADData == null) {
                    return;
                }
                caheImage(nativeUnifiedADData);
                String title = nativeUnifiedADData.getTitle();
                adInfo.setAdTitle(title);
                if (nativeUnifiedADData.isAppAd()) {
                    adInfo.setAdClickType(1);
                } else {
                    adInfo.setAdClickType(2);
                }
                adInfo.setNativeUnifiedADData(nativeUnifiedADData);
                if (listener != null) {
                    listener.adSuccess(adInfo);
                }
            }
        });
        mAdManager.loadData(REQUEST_AD_COUNTS);
    }

    private void caheImage(NativeUnifiedADData ad) {
        String imgUrl = ad.getImgUrl();
        String icon = ad.getIconUrl();
        try {
            if (!TextUtils.isEmpty(imgUrl)) {
                cacheImg(imgUrl);
            }
            if (!TextUtils.isEmpty(icon)) {
                cacheImg(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取自定义插屏广告
     * @param info
     * @param listener
     */
    private void getCustomInsertScreenAd(Activity activity, AdInfo info, AdRequestListener listener) {
        if (listener != null) {
            listener.adSuccess(info);
        }
    }

    /**
     * 获取模板插屏广告
     * @param info
     * @param listener
     */
    private void getTemplateInsertScreenAd(Activity activity, AdInfo info, AdRequestListener listener) {
        float expressViewWidth = 300;
        float expressViewHeight = 300;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(info.getAdId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdManagerHolder.get().createAdNative(activity).loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                if (listener != null) {
                    listener.adError(info, code, message);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (CollectionUtils.isEmpty(ads)) {
                    if (listener != null) {
                        listener.adError(info, 1, "广告获取为空");
                    }
                    return;
                }
                TTNativeExpressAd ttNativeExpressAd = ads.get(0);
                if (ttNativeExpressAd == null) {
                    if (listener != null) {
                        listener.adError(info, 1, "广告获取为空");
                    }
                    return;
                }
                info.setTtNativeExpressAd(ttNativeExpressAd);
                if (listener != null) {
                    listener.adSuccess(info);
                }
            }
        });
    }

    /**
     * 获取激励视频广告
     * @param info
     * @param listener
     */
    private void getRewardVideoAd(Activity activity, AdInfo info, AdRequestListener listener) {
        if (listener != null) {
            listener.adSuccess(info);
        }
    }

    /**
     * 获取全屏视频广告
     * @param adInfo
     * @param listener
     */
    private void getFullScreenVideoAd(Activity activity, AdInfo adInfo, AdRequestListener listener) {
        NativeUnifiedAD nativeUnifiedAD = new NativeUnifiedAD(activity, adInfo.getAdAppid(), adInfo.getAdId(), this);
        nativeUnifiedAD.setMaxVideoDuration(MAX_DURATION);

        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        // 本次拉回的视频广告，在用户看来是否为自动播放的
        nativeUnifiedAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO);

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        // 视频播放前，用户看到的广告容器是由SDK渲染的
        nativeUnifiedAD.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK);
        setAdInfo(adInfo);
        setAdRequestListener(listener);
        nativeUnifiedAD.loadData(1);
    }

    /**
     * 请求开屏广告
     */
    private void getAdBySplashAd(Activity activity, AdInfo adInfo, AdRequestListener listener) {
        //优量汇的开屏广告因为请求和回调展示同时进行，也不能预加载。所以直接createview
        if (listener != null) {
            listener.adSuccess(adInfo);
        }
    }


    private AdRequestListener adRequestListener;

    private AdInfo adInfo;

    public void setAdInfo(AdInfo adInfo) {
        this.adInfo = adInfo;
    }

    public void setAdRequestListener(AdRequestListener adRequestListener) {
        this.adRequestListener = adRequestListener;
    }

    @Override
    public void onADLoaded(List<NativeUnifiedADData> list) {
        if (CollectionUtils.isEmpty(list)) {
            if (adRequestListener != null) {
                adRequestListener.adError(adInfo, 1, "没请求到广告数据");
            }
            return;
        }
        NativeUnifiedADData nativeUnifiedADData = list.get(0);
        if (nativeUnifiedADData == null) {
            if (adRequestListener != null) {
                adRequestListener.adError(adInfo, 1, "没请求到广告数据");
            }
            return;
        }
        if (adInfo != null) {
            adInfo.setNativeUnifiedADData(nativeUnifiedADData);
        }
        if (adRequestListener != null) {
            adRequestListener.adSuccess(adInfo);
        }
    }

    @Override
    public void onNoAD(AdError adError) {
        if (adRequestListener != null) {
            adRequestListener.adError(adInfo, adError.getErrorCode(), adError.getErrorMsg());
        }
    }
}
