package com.comm.jksdk.ad.admanager;

import android.app.Activity;
import android.text.TextUtils;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdRequestListener;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;

import java.util.List;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.admanager
 * @ClassName: CsjSdkRequestManager
 * @Description: java类作用描述
 * @Author: fanhailong
 * @CreateDate: 2019/12/2 18:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/2 18:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CsjSdkRequestManager extends SdkRequestManager {

    @Override
    public void requestAd(Activity activity, AdInfo adInfo, AdRequestListener listener) {
        //广告样式
        String style = adInfo.getAdStyle();
        if (Constants.AdStyle.DATU_ICON_TEXT.equals(style) || Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style) || Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)
                || Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style) || Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style) || Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_02.equals(style)
                || Constants.AdStyle.BIG_IMG_BUTTON.equals(style) || Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style) || Constants.AdStyle.DATU_ICON_TEXT_FLICKER_BUTTON.equals(style)) {
            //todo请求大图广告
            getImageAd(adInfo, listener);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
            getAdBySplashAd(adInfo, listener);
        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
            getFullScreenVideoAd(adInfo, listener);
        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
            getRewardVideoAd(adInfo, listener);
        } else if(Constants.AdStyle.CP.equals(style)) {
            getTemplateInsertScreenAd(activity, adInfo, listener);
        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style)) {
            getCustomInsertScreenAd(adInfo, listener);
        } else if(Constants.AdStyle.FEED_TEMPLATE.equals(style) ||Constants.AdStyle.FEED_TEMPLATE_LAMP.equals(style)) {
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
        //有跑马灯情况重新设置宽度
        if(Constants.AdStyle.FEED_TEMPLATE_LAMP.equals(info.getAdStyle()) ){
            info.setWidth(info.getWidth()-20);
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(info.getAdId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(info.getWidth(),0) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdManagerHolder.get().createAdNative(GeekAdSdk.getContext()).loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
//                TToast.show(NativeExpressActivity.this, "load error : " + code + ", " + message);
//                mExpressContainer.removeAllViews();
                LogUtils.e(TAG, "loadNativeAd code:" + code + " message:" + message);
                if (listener != null) {
                    listener.adError(info, code, message);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
//                if (ads == null || ads.size() == 0){
//                    return;
//                }
//                mTTAd = ads.get(0);
//                bindAdListener(mTTAd);
//                startTime = System.currentTimeMillis();
//                mTTAd.render();
                if (CollectionUtils.isEmpty(ads)) {
                    if (listener != null) {
                        listener.adError(info, 1, "没请求到广告数据");
                    }
                    return;
                }
                TTNativeExpressAd ttNativeAd = ads.get(0);
                if (ttNativeAd == null) {
                    if (listener != null) {
                        listener.adError(info, 1, "没请求到广告数据");
                    }
                    return;
                }
                info.setTtNativeExpressAd(ttNativeAd);
                if (listener != null) {
                    listener.adSuccess(info);
                }
            }
        });
    }

    /**
     * 获取自定义插屏广告
     * @param info
     * @param listener
     */
    private void getCustomInsertScreenAd(AdInfo info, AdRequestListener listener) {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(info.getAdId())
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setImageAcceptedSize(720, 1280)
                //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdManagerHolder.get().createAdNative(GeekAdSdk.getContext()).loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "loadNativeAd code:" + code + " message:" + message);
                if (listener != null) {
                    listener.adError(info, code, message);
                }
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (CollectionUtils.isEmpty(ads)) {
                    if (listener != null) {
                        listener.adError(info, 1, "没请求到广告数据");
                    }
                    return;
                }
                TTNativeAd ttNativeAd = ads.get(0);
                if (ttNativeAd == null) {
                    if (listener != null) {
                        listener.adError(info, 1, "没请求到广告数据");
                    }
                    return;
                }
                info.setTtNativeAd(ttNativeAd);
                if (listener != null) {
                    listener.adSuccess(info);
                }
            }
        });
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
        TTAdManagerHolder.get().createAdNative(activity.getApplicationContext()).loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
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
    private void getRewardVideoAd(AdInfo info, AdRequestListener listener) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(info.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                //奖励的名称
                .setRewardName(TextUtils.isEmpty(info.getRewardName()) ? "金币" : info.getRewardName())
                //奖励的数量
                .setRewardAmount(info.getRewardAmount() == 0 ? 3 : info.getRewardAmount())
                //用户id,必传参数
                .setUserID(TextUtils.isEmpty(info.getUserId()) ?  "": info.getUserId())
                //附加参数，可选
                .setMediaExtra("media_extra")
                //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        //step5:请求广告
        TTAdManagerHolder.get().createAdNative(GeekAdSdk.getContext()).loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "rewardVideoAd error:" + code + " message:" + message);
                if (listener != null) {
                    listener.adError(info, code, message);
                }
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                if (listener != null && info.isPreload()) {
                    listener.adSuccess(info);
                }
                LogUtils.e(TAG, "onRewardVideoCached");
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd mttRewardVideoAd) {
                LogUtils.d(TAG, "rewardVideoAd loaded");
                if (mttRewardVideoAd != null) {
                    info.setTtRewardVideoAd(mttRewardVideoAd);
                    if (listener != null && !info.isPreload()) {
                        listener.adSuccess(info);
                    }
                } else {
                    if (listener != null) {
                        listener.adError(info, 1, "请求结果为空");
                    }
                }
            }
        });
    }

    /**
     * 获取全屏视频广告
     * @param adInfo
     * @param listener
     */
    private void getFullScreenVideoAd(AdInfo adInfo, AdRequestListener listener) {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adInfo.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        TTAdManagerHolder.get().createAdNative(GeekAdSdk.getContext()).loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "loadFullScreenVideoAd error:" + code + " message:" + message);
                if (listener != null) {
                    listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
//                adError(code, message);
//                firstAdError(code, message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                if (ad != null) {
                    adInfo.setTtFullScreenVideoAd(ad);
                    if (listener != null && !adInfo.isPreload()) {
                        listener.adSuccess(adInfo);
                    }
                } else {
                    if (listener != null) {
                        listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    }
                }
            }

            @Override
            public void onFullScreenVideoCached() {
                if (listener != null && adInfo.isPreload()) {
                    listener.adSuccess(adInfo);
                }
            }
        });
    }

    /**
     * 请求开屏广告
     */
    private void getAdBySplashAd(AdInfo adInfo, AdRequestListener listener) {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adInfo.getAdId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                .build();
        TTAdManagerHolder.get().createAdNative(GeekAdSdk.getContext()).loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.e(TAG, "csj errorCode:" + errorCode + " errorMsg:" + errorMsg);
                if (listener != null) {
                    listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
            }

            @Override
            public void onTimeout() {
                LogUtils.e(TAG, "csj splash request time out.");
                if (listener != null) {
                    listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                if (ttSplashAd != null) {
                    LogUtils.d(TAG, "csj onSplashAdLoad:" + ttSplashAd.getInteractionType());
                    adInfo.setTtSplashAd(ttSplashAd);
                    if (listener != null) {
                        listener.adSuccess(adInfo);
                    }
                } else {
                    if (listener != null) {
                        listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    }
                }
            }
        });
    }

    /**
     * 获取图片类广告
     * @param adInfo
     */
    public void getImageAd(AdInfo adInfo, AdRequestListener listener) {
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:创建TTAdNative对象,用于调用广告请求接口
        TTAdNative mTTAdNative = ttAdManager.createAdNative(GeekAdSdk.getContext());
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(mContext);

        LogUtils.d(TAG, "getImageAd->请求穿山甲图文类广告");

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adInfo.getAdId().trim())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1)
                .build();
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                LogUtils.d(TAG, "onNoAD->请求穿山甲失败,ErrorCode:" + i + ",ErrorMsg:" + s);
                if (listener != null) {
                    listener.adError(adInfo, i, s);
                }
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                LogUtils.d(TAG, "onADLoaded->请求穿山甲成功");
                if (CollectionUtils.isEmpty(list)) {
                    if (listener != null) {
                        listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    }
                    return;
                }
                TTFeedAd ttFeedAd = list.get(0);
                if (ttFeedAd == null) {
                    if (listener != null) {
                        listener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    }
                    return;
                }
                caheImage(ttFeedAd);
                String title = ttFeedAd.getTitle();
                adInfo.setAdTitle(title);
                if (TTAdConstant.INTERACTION_TYPE_DOWNLOAD == ttFeedAd.getInteractionType()) {
                    adInfo.setAdClickType(1);
                } else {
                    adInfo.setAdClickType(2);
                }
                adInfo.setTtFeedAd(ttFeedAd);
                if (listener != null) {
                    listener.adSuccess(adInfo);
                }
            }
        });
    }

    private void caheImage(TTFeedAd ttFeedAd) {
        if (ttFeedAd == null) {
            return;
        }
        TTImage image = ttFeedAd.getImageList().get(0);
        TTImage icon = ttFeedAd.getIcon();
        if (image != null && image.isValid() && icon != null && icon.isValid()) {
            try {
                cacheImg(image.getImageUrl(), icon.getImageUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
