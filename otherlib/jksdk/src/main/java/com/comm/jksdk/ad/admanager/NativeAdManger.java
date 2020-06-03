package com.comm.jksdk.ad.admanager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.factory.RequestManagerFactory;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.AdPreloadingListener;
import com.comm.jksdk.ad.listener.AdRequestListener;
import com.comm.jksdk.ad.listener.AdRequestManager;
import com.comm.jksdk.ad.listener.FirstAdListener;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.comm.jksdk.ad.view.chjview.CHJAdView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgAdPlayLampView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgFakeVideoAdView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgIcTvAdView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgIcTvBtAdView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgIcTvBtCenterAdView;
import com.comm.jksdk.ad.view.chjview.ChjBigImgIcTvFlickerBtAdView;
import com.comm.jksdk.ad.view.chjview.ChjExternalDialogBigImageView;
import com.comm.jksdk.ad.view.chjview.ChjFullScreenBigImgIcTvBtCenterAdView;
import com.comm.jksdk.ad.view.chjview.ChjSplashAdView;
import com.comm.jksdk.ad.view.chjview.InsertScreenAdFullDownloadDialog;
import com.comm.jksdk.ad.view.chjview.InsertScreenAdNormalDownloadDialog;
import com.comm.jksdk.ad.view.ylhview.YlhAdView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgAdPlayLampView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgFakeVideoAdView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgIcTvAdView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgIcTvBtAdView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgIcTvBtCenterAdView;
import com.comm.jksdk.ad.view.ylhview.YlhBigImgIcTvFlickerBtAdView;
import com.comm.jksdk.ad.view.ylhview.YlhExternalDialogBigImageView;
import com.comm.jksdk.ad.view.ylhview.YlhFullBigImgIcTvBtCenterAdView;
import com.comm.jksdk.ad.view.ylhview.YlhFullScreenVideoAdView;
import com.comm.jksdk.ad.view.ylhview.YlhSplashAdView;
import com.comm.jksdk.bean.ConfigBean;
import com.comm.jksdk.cache.CacheAd;
import com.comm.jksdk.config.AdsConfig;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;
import com.comm.jksdk.utils.DisplayUtil;
import com.comm.jksdk.utils.ToastUtils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;


/**
 * 原生广告管理器
 *
 * @author: docking
 * @date: 2019/9/7 10:22
 * @description: todo ...
 **/
public class NativeAdManger implements AdManager {
    protected final String TAG = "GeekAdSdk-->";

    private List<ConfigBean.AdListBean.AdsInfosBean> adsInfoslist = new ArrayList();

    public NativeAdManger() {
    }

    /**
     * acitvity对象,优量汇开屏、视频广用到
     */
    protected Activity mActivity;
    /**
     * 广告监听器
     */
    private AdListener mAdListener;

    /**
     * 广告预加载监听器
     */
    private AdPreloadingListener mAdPreloadingListener;

    /**
     * 自渲染插屏广告展示时长
     */
    private int showTimeSeconds = 3;

    /**
     * 请求方式：0 - SDK 1 - API
     */
    private int requestType = 0;

    private boolean firstRequestAd = true;

    /**
     * 插屏的百分比
     */
    private String mProgress;



    private FirstAdListener mFirstAdListener = new FirstAdListener() {
        @Override
        public void firstAdError(AdInfo adInfo, int errorCode, String errorMsg) {
            LogUtils.w(TAG, "回传--->请求第一个广告失败");

            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (adInfo.isPreload()) {
                    if (mAdPreloadingListener != null) {
                        mAdPreloadingListener.adError(adInfo, errorCode, errorMsg);
                    }
                } else {
                    if (mAdListener != null) {
                        mAdListener.adError(adInfo, errorCode, errorMsg);
                    }
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (adInfo.isPreload()) {
                    if (mAdPreloadingListener != null) {
                        mAdPreloadingListener.adError(adInfo, errorCode, errorMsg);
                    }
                } else {
                    if (mAdListener != null) {
                        mAdListener.adError(adInfo, errorCode, errorMsg);
                    }
                }
                return;
            }
            firstRequestAd = false;
            againRequest(adInfo, mAdsInfosBean);
        }
    };


    @Override
    public void loadNativeTemplateAd(Activity activity, String position, float width, AdListener listener) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        adInfo.setWidth(width);
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadAd(Activity activity, String position, AdListener listener) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    /**
     * 准备数据
     * @param adInfo
     */
    public void readyInfo(AdInfo adInfo){
        //获取本地配置信息
        adsInfoslist.clear();
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(adInfo.getPosition());
        if (mConfigInfoBean == null) {
//            if (mAdListener != null) {
//                mAdListener.adError(adInfo, CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
//            }
            return;
        }
        adInfo.setAdStyle(mConfigInfoBean.getAdStyle());
        adInfo.setAdRequestTimeOut(mConfigInfoBean.getAdRequestTimeOut());
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());
    }

    @Override
    public void preloadingAd(Activity activity, String position, AdPreloadingListener listener) {
        mAdPreloadingListener = listener;
        AdInfo adInfo = new AdInfo();
        adInfo.setIsPreload(true);
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }

            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdPreloadingListener != null) {
                mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadSplashAd(Activity activity, String position, AdListener listener) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadVideoAd(Activity activity, String position, AdListener listener) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void preloadingVideoAd(Activity activity, String position, AdPreloadingListener listener) {
        mAdPreloadingListener = listener;
        AdInfo adInfo = new AdInfo();
        adInfo.setIsPreload(true);
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdPreloadingListener != null) {
                mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, AdListener listener) {
        loadRewardVideoAd(activity, position, userId, orientation, "", 0, listener);
    }

    @Override
    public void preloadingRewardVideoAd(Activity activity, String position, String userId, int orientation, AdPreloadingListener listener) {
        preloadingRewardVideoAd(activity, position, userId, orientation, "", 0, listener);
    }

    @Override
    public void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, String rewardName, int rewardAmount, AdListener listener) {
        AdInfo adInfo = new AdInfo();
        adInfo.setUserId(userId);
        adInfo.setRewardName(rewardName);
        adInfo.setRewardAmount(rewardAmount);
        mAdListener = listener;
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void preloadingRewardVideoAd(Activity activity, String position, String userId, int orientation, String rewardName, int rewardAmount, AdPreloadingListener listener) {
        mAdPreloadingListener = listener;
        AdInfo adInfo = new AdInfo();
        adInfo.setUserId(userId);
        adInfo.setRewardName(rewardName);
        adInfo.setRewardAmount(rewardAmount);
        adInfo.setIsPreload(true);
        adInfo.setDisk(true);
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdPreloadingListener != null) {
                mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    @Override
    public void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener, String... pos) {
        mAdListener = listener;
        AdInfo adInfo = new AdInfo();
        if (!CollectionUtils.isEmpty(pos)) {
            mProgress = pos[0];
        }
        try {
            mActivity = activity;
            //设置广告位置信息
            adInfo.setPosition(position);
            //获取本地配置信息
            readyInfo(adInfo);
            if (CollectionUtils.isEmpty(adsInfoslist)) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
            if (mAdsInfosBean == null) {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
                return;
            }
            againRequest(adInfo, mAdsInfosBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    /**
     * 轮询请求
     * @param adInfo
     * @param adsInfosBean
     */
    public void againRequest(AdInfo adInfo, ConfigBean.AdListBean.AdsInfosBean adsInfosBean) {
        if (adInfo == null) {
            adInfo = new AdInfo();
        }
        //某些特有的数据清空，避免污染下一次请求数据
        adInfo.setAdTitle("");
        adInfo.setAdClickType(0);
        adInfo.setRequestOrder(adsInfosBean.getRequestOrder());
        //广告源
        adInfo.setAdSource(adsInfosBean.getAdUnion());
        //广告id
        adInfo.setAdId(adsInfosBean.getAdId());
        //广告对应的appid
        adInfo.setAdAppid(adsInfosBean.getAdsAppId());
        //请求类型 0 - SDK 1 - API
        requestType = adsInfosBean.getRequestType();
        if (requestType == 0) {
            sdkRequest(adInfo);
        } else {
            apiRequest(adInfo);
        }
    }

    /**
     * api 请求
     */
    public void apiRequest(AdInfo adInfo){
        // TODO: 2019/12/3
        if (mAdListener != null) {
            mAdListener.adError(adInfo, 2, "暂时不支持api广告");
        }
    }

    /**
     * sdk 请求
     */
    public void sdkRequest(AdInfo adInfo){
        AdRequestManager adRequestManager = new RequestManagerFactory().produce(adInfo);
        if (adRequestManager == null) {
            if (adInfo.isPreload()) {
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
            } else {
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
            }
            return;
        }
        //是否已有缓存Adinfo
        AdInfo temAdinfo = CacheAd.getAd(adInfo.getPosition());
        if (temAdinfo != null) {
            if (adInfo.isPreload()) {//预加载成功
                if (mAdPreloadingListener != null) {
                    mAdPreloadingListener.adSuccess(temAdinfo);
                }
            } else {//清除缓存重新加载
                CacheAd.removeAd(adInfo.getPosition());
                createAdView(mActivity, temAdinfo);
            }
            return;
        }
        if (GeekAdSdk.mIsFormal.equals("btest")) {//测试环境打印广告配置
            LogUtils.i("pengfei-\nadPosition：" + adInfo.getPosition() + "\nadStyle:" + adInfo.getAdStyle()+"\nadId:"+adInfo.getAdId()+"\nadUnion:"+adInfo.getAdSource()+"\nrequestOrder:"+adInfo.getRequestOrder());
            ToastUtils.showToast(mActivity, "adPosition：" + adInfo.getPosition() + "\nadStyle:" + adInfo.getAdStyle()+"\nadId:"+adInfo.getAdId()+"\nadUnion:"+adInfo.getAdSource()+"\nrequestOrder:"+adInfo.getRequestOrder(), Toast.LENGTH_LONG,0);
        }
        //没有缓存对象,走正常流程
        adRequestManager.requestAd(mActivity, adInfo, new AdRequestListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (adInfo.isPreload()) {
//                    if (!adInfo.isDisk()) {
//                    }
                    CacheAd.setAd(info.getPosition(), adInfo);
                    if (mAdPreloadingListener != null) {
                        mAdPreloadingListener.adSuccess(info);
                    }
                    return;
                }
                createAdView(mActivity, info);
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                if (mFirstAdListener != null) {
                    mFirstAdListener.firstAdError(info, errorCode, errorMsg);
                }
            }
        });
    }

    /**
     * 创建广告View
     */
    private void createAdView(Activity activity, AdInfo adInfo) {
        String adSource = adInfo.getAdSource();
        if (Constants.AdType.ChuanShanJia.equals(adSource)) {
            createCsjAdView(activity, adInfo);
//            CommAdView mAdView = new CHJAdView(GeekAdSdk.getContext(), activity, adInfo.getAdStyle(), adInfo.getAdAppid(), adInfo.getAdId());
//            ((CHJAdView) mAdView).setOrientation(orientation);
//            if (!TextUtils.isEmpty(userId)) {
//                ((CHJAdView) mAdView).setUserId(userId);
//            }
//            ((CHJAdView) mAdView).setFullScreen(isFullScreen);
//            ((CHJAdView) mAdView).setShowTimeSeconds(showTimeSeconds);
//            ((CHJAdView) mAdView).setmProgress(mProgress);
        } else if (Constants.AdType.YouLiangHui.equals(adSource)) {
//            AbsAdView mAdView = new YlhAdView(GeekAdSdk.getContext(), activity, adStyle, appId, mAdId);
            createYlhAdView(activity, adInfo);
        } else {
            // 暂不处理
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
        }
    }

    /**
     * 创建穿山甲view
     * @param activity
     * @param adInfo
     */
    private void createCsjAdView(Activity activity, AdInfo adInfo){
        if (activity == null) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        if (activity.isFinishing()) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        String style = adInfo.getAdStyle();
        //信息流模板广告_带跑马灯
        if (Constants.AdStyle.FEED_TEMPLATE_LAMP.equals(style)) {
            showCsjFeedTemplate(activity, adInfo,true);
            return;
        }
        //信息流模板广告
        if (Constants.AdStyle.FEED_TEMPLATE.equals(style)) {
            showCsjFeedTemplate(activity, adInfo,false);
            return;
        }
        //全屏视频
        if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
            showCsjFullVideo(activity, adInfo);
            return;
        }
        //激励视频
        if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
            showCsjRewardVideo(activity, adInfo);
            return;
        }
        //模板插屏
        if (Constants.AdStyle.CP.equals(style)) {
            showCsjTemplateInsertScreen(activity, adInfo);
            return;
        }
        //自定义插屏
        if (Constants.AdStyle.CUSTOM_CP.equals(style)) {
            showCsjCustomInsertScreen(activity, adInfo);
            return;
        }
        //自定义全屏插屏
        if (Constants.AdStyle.FULLSCREEN_CP_01.equals(style)) {
            showCsjInsertFullScreen(activity, adInfo);
            return;
        }
        CHJAdView adView = null;
        if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style)) { // 大图_带icon文字按钮
            adView = new ChjBigImgIcTvBtAdView(activity);
        } else if(Constants.AdStyle.DATU_ICON_TEXT_FLICKER_BUTTON.equals(style)){// 大图_带icon文字_激光按钮
            adView = new ChjBigImgIcTvFlickerBtAdView(activity);
        } else if (Constants.AdStyle.DATU_ICON_TEXT.equals(style)) { //大图_带icon文字
            adView = new ChjBigImgIcTvAdView(activity);
        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style)) { //大图_带icon文字按钮居中
            adView = new ChjBigImgIcTvBtCenterAdView(activity);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON.equals(style)) { //大图带按钮（大图_下载播放按钮）
            adView = new ChjBigImgAdPlayLampView(activity);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)) { //大图带按钮带跑马灯
            adView = new ChjBigImgAdPlayLampView(activity, true);
        } else if (Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) { //假视频大图_01
            adView = new ChjBigImgFakeVideoAdView(activity);
        } else if (Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)) { //外部弹窗大图广告_01
            adView = new ChjExternalDialogBigImageView(activity);
        } else if (Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_02.equals(style)) { //外部弹窗大图广告_02
//            adView = new ChjExternalDialogBigImage2View(activity);
            adView = new ChjFullScreenBigImgIcTvBtCenterAdView(activity);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) { //开屏广告
            adView = new ChjSplashAdView(activity);
        }
        if (adView == null) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        adView.setAdListener(mAdListener);
        adView.setPollingAdListener(mFirstAdListener);
        adInfo.setAdView(adView);
        //填充物料
        adView.parseAd(adInfo);
        if (mAdListener != null) {
            mAdListener.adSuccess(adInfo);
        }
    }

    /**
     * 显示穿山甲信息模板广告
     * @param activity
     * @param info
     */
    private void showCsjFeedTemplate(Activity activity, AdInfo info,boolean islamp) {
        TTNativeExpressAd ttNativeExpressAd = info.getTtNativeExpressAd();
        ttNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {

            @Override
            public void onAdDismiss() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }

            @Override
            public void onAdClicked(View view, int type) {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onAdShow(View view, int type) {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
//                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                if (mAdListener != null) {
                    mAdListener.adError(info, code, msg);
                }
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
//                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
//                TToast.show(mContext, "渲染成功");
//                mExpressContainer.removeAllViews();
//                mExpressContainer.addView(view);
                if(islamp){//带跑马灯样式
                    RelativeLayout rl = new RelativeLayout(activity);
                    int currentpx = DisplayUtil.dp2px(activity,10);
                    rl.setPadding(currentpx,currentpx,currentpx,currentpx);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        AnimationDrawable mAnimationDrawable;
                        rl.setBackground(activity.getResources().getDrawable(R.drawable.anim_ad));
                        if (rl.getBackground() instanceof AnimationDrawable) {
                            mAnimationDrawable = (AnimationDrawable) rl.getBackground();
                            mAnimationDrawable.start();
                        }
                    }
                    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rl.addView(view, lp1);
                    info.setAdView(rl);
                }else{
                    info.setAdView(view);
                }

                if (mAdListener != null) {
                    mAdListener.adSuccess(info);
                }
            }
        });
        //dislike设置
//        bindDislike(ttNativeExpressAd, false);
        if (ttNativeExpressAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            ttNativeExpressAd.setDownloadListener(new TTAppDownloadListener() {
                @Override
                public void onIdle() {
//                TToast.show(NativeExpressActivity.this, "点击开始下载", Toast.LENGTH_LONG);
                }

                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
//                    TToast.show(NativeExpressActivity.this, "下载中，点击暂停", Toast.LENGTH_LONG);
//                }
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
                }

                @Override
                public void onInstalled(String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
                }
            });
        }

        ttNativeExpressAd.render();
    }

    /**
     * 显示穿山甲自定义全屏插屏广告
     * @param activity
     * @param info
     */
    private void showCsjInsertFullScreen(Activity activity, AdInfo info) {
        TTNativeAd ttNativeAd = info.getTtNativeAd();
        InsertScreenAdFullDownloadDialog fullDownloadDialog = new InsertScreenAdFullDownloadDialog(activity, showTimeSeconds);
        fullDownloadDialog.setListenr(new InsertScreenAdFullDownloadDialog.OnClickListenr() {
            @Override
            public void onClick() {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onAdShow() {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onClose() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }
        });
        fullDownloadDialog.show();
        fullDownloadDialog.loadAd(ttNativeAd);
        if (mAdListener != null) {
            mAdListener.adSuccess(info);
        }
    }

    /**
     * 显示穿山甲自定义插屏
     * @param activity
     * @param info
     */
    private void showCsjCustomInsertScreen(Activity activity, AdInfo info) {
        TTNativeAd ttNativeAd = info.getTtNativeAd();
        InsertScreenAdNormalDownloadDialog normalDownloadDialog = new InsertScreenAdNormalDownloadDialog(activity, showTimeSeconds);
        normalDownloadDialog.setListenr(new InsertScreenAdNormalDownloadDialog.OnClickListenr() {
            @Override
            public void onClick() {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onAdShow() {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onClose() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }
        });
        normalDownloadDialog.show();
        normalDownloadDialog.setProgress(mProgress);
        normalDownloadDialog.loadAd(ttNativeAd);
    }

    /**
     * 显示穿山甲模板插屏
     * @param activity
     * @param info
     */
    private void showCsjTemplateInsertScreen(Activity activity, AdInfo info) {
        TTNativeExpressAd ttNativeExpressAd = info.getTtNativeExpressAd();
        ttNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onAdShow(View view, int i) {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                if (mAdListener != null) {
                    mAdListener.adError(info, i, s);
                }
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                if (mAdListener != null) {
                    mAdListener.adSuccess(info);
                }
                ttNativeExpressAd.showInteractionExpressAd(activity);
            }

            @Override
            public void onAdDismiss() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }
        });
        ttNativeExpressAd.render();
    }

    /**
     * 显示穿山甲激励视频
     * @param activity
     * @param adInfo
     */
    private void showCsjRewardVideo(Activity activity, AdInfo adInfo) {
        TTRewardVideoAd ttRewardVideoAd = adInfo.getTtRewardVideoAd();
        ttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                if (mAdListener != null) {
                    mAdListener.adExposed(adInfo);
                }
            }

            @Override
            public void onAdVideoBarClick() {
                LogUtils.d(TAG, "rewardVideoAd bar click");
                if (mAdListener != null) {
                    mAdListener.adClicked(adInfo);
                }
            }

            @Override
            public void onAdClose() {
                LogUtils.d(TAG, "rewardVideoAd close");
                if (mAdListener != null) {
                    mAdListener.adClose(adInfo);
                }
            }

            //视频播放完成回调
            @Override
            public void onVideoComplete() {
                LogUtils.d(TAG, "rewardVideoAd complete");
                if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                    ((VideoAdListener) mAdListener).onVideoComplete(adInfo);
                }
            }

            @Override
            public void onVideoError() {
                LogUtils.d(TAG, "rewardVideoAd error");
                if (mAdListener != null) {
                    mAdListener.adError(adInfo, 1, "rewardVideoAd error");
                }
            }

            //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                LogUtils.d(TAG, "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
                if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                    ((VideoAdListener) mAdListener).onVideoRewardVerify(adInfo, rewardVerify, rewardAmount, rewardName);
                }
            }

            @Override
            public void onSkippedVideo() {

            }
        });
        ttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {

            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                LogUtils.d(TAG, "下载中，点击下载区域暂停");

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                LogUtils.d(TAG, "下载暂停，点击下载区域继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                LogUtils.d(TAG, "下载失败，点击下载区域重新下载");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                LogUtils.d(TAG, "下载完成，点击下载区域重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                LogUtils.d(TAG, "安装完成，点击下载区域打开");
            }
        });
        ttRewardVideoAd.showRewardVideoAd(activity);
        if (mAdListener != null) {
            mAdListener.adSuccess(adInfo);
        }
    }

    /**
     * 显示穿山甲全屏视频
     * @param activity
     * @param adInfo
     */
    private void showCsjFullVideo(Activity activity, AdInfo adInfo){
        TTFullScreenVideoAd ttFullScreenVideoAd = adInfo.getTtFullScreenVideoAd();
        if (mAdListener != null) {
            mAdListener.adSuccess(adInfo);
        }
        ttFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

            @Override
            public void onAdShow() {
                if (mAdListener != null) {
                    mAdListener.adExposed(adInfo);
                }
            }

            @Override
            public void onAdVideoBarClick() {
                if (mAdListener != null) {
                    mAdListener.adClicked(adInfo);
                }
            }

            @Override
            public void onAdClose() {
                if (mAdListener != null) {
                    mAdListener.adClose(adInfo);
                }
            }

            @Override
            public void onVideoComplete() {
                if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                    ((VideoAdListener) mAdListener).onVideoComplete(adInfo);
                }
            }

            @Override
            public void onSkippedVideo() {

            }
        });
        //step6:在获取到广告后展示
        ttFullScreenVideoAd.showFullScreenVideoAd(activity);
    }

    /**
     * 创建优量汇view
     * @param activity
     * @param adInfo
     */
    private void createYlhAdView(Activity activity, AdInfo adInfo) {
        if (activity == null) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        if (activity.isFinishing()) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        String style = adInfo.getAdStyle();
        //信息流模板广告
        if (Constants.AdStyle.FEED_TEMPLATE.equals(style)) {
            showYlhFeedTemplate(activity, adInfo);
            return;
        }
//        //全屏视频
//        if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
//            showYlhFullVideo(activity, adInfo);
//            return;
//        }
        //激励视频
        if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
            showYlhRewardVideo(activity, adInfo);
            return;
        }
        //展示优量插屏广告（优量汇只有模板插屏）
        if (Constants.AdStyle.CP.equals(style) || Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style)) {
            showYlhInsertScreen(activity, adInfo);
            return;
        }
        YlhAdView adView = null;
        if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style)) { // 大图_带icon文字按钮
            adView = new YlhBigImgIcTvBtAdView(activity);
        } else if(Constants.AdStyle.DATU_ICON_TEXT_FLICKER_BUTTON.equals(style)){ // 大图_带icon文字_激光按钮
            adView = new YlhBigImgIcTvFlickerBtAdView(activity);
        } else if (Constants.AdStyle.DATU_ICON_TEXT.equals(style)) { //大图_带icon文字
            adView = new YlhBigImgIcTvAdView(activity);
        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style)) { //大图_带icon文字按钮居中
            adView = new YlhBigImgIcTvBtCenterAdView(activity);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON.equals(style)) { //大图带按钮（大图_下载播放按钮）
            adView = new YlhBigImgAdPlayLampView(activity);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)) { //大图带按钮带跑马灯
            adView = new YlhBigImgAdPlayLampView(activity, true);
        } else if (Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) { //假视频大图_01
            adView = new YlhBigImgFakeVideoAdView(activity);
        } else if (Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)) { //外部弹窗大图广告_01
            adView = new YlhExternalDialogBigImageView(activity);
        } else if (Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_02.equals(style)) { //外部弹窗大图广告_02
            adView = new YlhFullBigImgIcTvBtCenterAdView(activity);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) { //开屏广告
            adView = new YlhSplashAdView(activity);
        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) { //全屏视频广告
            adView = new YlhFullScreenVideoAdView(activity);
        }
        if (adView == null) {
            if (mAdListener != null) {
                mAdListener.adError(adInfo, CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        adView.setAdListener(mAdListener);
        adView.setPollingAdListener(mFirstAdListener);
        adInfo.setAdView(adView);
        //填充物料
        adView.parseAd(adInfo);
        if (mAdListener != null) {
            mAdListener.adSuccess(adInfo);
        }
    }

    NativeExpressADView nativeExpressADView = null;
    /**
     * 展示优量汇信息流模板广告
     * @param activity
     * @param info
     */
    private void showYlhFeedTemplate(Activity activity, AdInfo info) {
//        ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT
        NativeExpressAD nativeExpressAD = new NativeExpressAD(activity, new ADSize((int) info.getWidth(), ADSize.AUTO_HEIGHT), info.getAdAppid(), info.getAdId(), new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                // 释放前一个展示的NativeExpressADView的资源
                if (nativeExpressADView != null) {
                    nativeExpressADView.destroy();
                }

//                if (container.getVisibility() != View.VISIBLE) {
//                    container.setVisibility(View.VISIBLE);
//                }
//
//                if (container.getChildCount() > 0) {
//                    container.removeAllViews();
//                }

                nativeExpressADView = list.get(0);
                if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                    nativeExpressADView.setMediaListener(new NativeExpressMediaListener() {
                        @Override
                        public void onVideoInit(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoLoading(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {

                        }

                        @Override
                        public void onVideoStart(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoPause(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoComplete(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {

                        }

                        @Override
                        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {

                        }

                        @Override
                        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {

                        }
                    });
                }
                // 广告可见才会产生曝光，否则将无法产生收益。
                info.setAdView(nativeExpressADView);
                nativeExpressADView.render();
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                if (mAdListener != null) {
                    mAdListener.adError(info, 2, "on render fail");
                }
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                if (mAdListener != null) {
                    mAdListener.adSuccess(info);
                }
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onNoAD(AdError adError) {
                if (mAdListener != null) {
                    mAdListener.adError(info, adError.getErrorCode(), adError.getErrorMsg());
                }
            }
        }); // 这里的Context必须为Activity
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS) // 设置什么网络环境下可以自动播放视频
                .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
                .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
//        nativeExpressAD.setMaxVideoDuration(getMaxVideoDuration());
        nativeExpressAD.setMaxVideoDuration(8);
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
         */
        nativeExpressAD.setVideoPlayPolicy(getVideoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS, activity));  // 本次拉回的视频广告，在用户看来是否为自动播放的
        nativeExpressAD.loadAD(1);
    }

    public static int getVideoPlayPolicy(int autoPlayPolicy, Context context){
        if(autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS){
            return VideoOption.VideoPlayPolicy.AUTO;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                    : VideoOption.VideoPlayPolicy.MANUAL;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER){
            return VideoOption.VideoPlayPolicy.MANUAL;
        }
        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }

    /**
     * 展示优量汇激励视频
     * @param activity
     * @param info
     */
    private void showYlhRewardVideo(Activity activity, AdInfo info) {
        String REWARD_VIDEO_AD_POS_ID_UNSUPPORT_H = "5040942242835423";//不支持竖版出横版视频
        int orientation = 1;
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        if (orientation == 1) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == 2) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // 1. 初始化激励视频广告
        RewardVideoAD rewardVideoAD = null;
        RewardVideoAD finalRewardVideoAD = rewardVideoAD;
        rewardVideoAD = new RewardVideoAD(activity, info.getAdAppid(), REWARD_VIDEO_AD_POS_ID_UNSUPPORT_H, new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                //广告加载成功标志
                finalRewardVideoAD.showAD();
                if (mAdListener != null) {
                    mAdListener.adError(info, 1, "没有广告");
                }
//                adSuccess(mAdInfo);
            }

            @Override
            public void onVideoCached() {
                //视频素材缓存成功，可在此回调后进行广告展示

            }

            @Override
            public void onADShow() {

            }

            @Override
            public void onADExpose() {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onReward() {

            }

            @Override
            public void onADClick() {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onVideoComplete() {
                if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                    ((VideoAdListener) mAdListener).onVideoComplete(info);
                }
            }

            @Override
            public void onADClose() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }

            @Override
            public void onError(AdError adError) {
                if (mAdListener != null) {
                    mAdListener.adError(info, adError.getErrorCode(), adError.getErrorMsg());
                }
            }
        });
        // 2. 加载激励视频广告
        rewardVideoAD.loadAD();
    }

    UnifiedInterstitialAD iad = null;
    /**
     * 展示优量插屏广告（优量汇只有模板插屏）
     * @param activity
     * @param info
     */
    private void showYlhInsertScreen(Activity activity, AdInfo info) {
//        UnifiedInterstitialAD iad = null;
        String UNIFIED_INTERSTITIAL_ID_LARGE_SMALL = "3040652898151811";// 大小规格
        if (iad != null) {
            iad.close();
            iad.destroy();
            iad = null;
        }
        iad = new UnifiedInterstitialAD(activity, info.getAdAppid(), UNIFIED_INTERSTITIAL_ID_LARGE_SMALL, new UnifiedInterstitialADListener() {
            @Override
            public void onADReceive() {
                //广告加载成功
//                    if (iad != null) {
//                        adSuccess(adInfo);
//                    }
                showAd(iad);
            }

            @Override
            public void onNoAD(AdError adError) {
                if (mAdListener != null) {
                    mAdListener.adError(info, 1, "没有广告");
                }
            }

            @Override
            public void onADOpened() {

            }

            @Override
            public void onADExposure() {
                if (mAdListener != null) {
                    mAdListener.adExposed(info);
                }
            }

            @Override
            public void onADClicked() {
                if (mAdListener != null) {
                    mAdListener.adClicked(info);
                }
            }

            @Override
            public void onADLeftApplication() {

            }

            @Override
            public void onADClosed() {
                if (mAdListener != null) {
                    mAdListener.adClose(info);
                }
            }
        });
        iad.loadAD();
//        if (iad == null) {
//            UnifiedInterstitialAD finalIad = iad;
//        }
    }
    private void showAd(UnifiedInterstitialAD iad){
        if (iad != null) {
            iad.showAsPopupWindow();
        }
    }
}
