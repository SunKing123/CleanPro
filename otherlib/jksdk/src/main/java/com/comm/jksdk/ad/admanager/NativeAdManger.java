package com.comm.jksdk.ad.admanager;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.comm.jksdk.ad.listener.FirstAdListener;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.ad.view.chjview.CHJAdView;
import com.comm.jksdk.ad.view.ylhview.YlhAdView;
import com.comm.jksdk.bean.ConfigBean;
import com.comm.jksdk.config.AdsConfig;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;
import com.comm.jksdk.utils.SpUtils;

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
    private CommAdView mAdView = null;


    private RelativeLayout adParentView;

    private List<ConfigBean.AdListBean.AdsInfosBean> adsInfoslist = new ArrayList();

    public NativeAdManger() {
    }

    /**
     * acitvity对象,优量汇开屏、视频广用到
     */
    protected Activity mActivity;
    /**
     * 广告ID
     */
    private String mAdId;
    /**
     * 广告sdk对应的appid
     */
    private String mAppId;
    /**
     * 广告监听器
     */
    private AdListener mAdListener;

    /**
     * 广告样式
     */
    private String adStyle = "";
    private int adRequestTimeOut;
    /**
     * 广告类型
     */
    private String adUnion;

    /**
     * 视频广告方向: 1 竖屏, 2 横屏
     */
    private int orientation = 1;

    /**
     * 激励视频userid
     */
    private String userId = "";

    /**
     * 自渲染插屏广告是否是全屏
     */
    private boolean isFullScreen = false;

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
     * 创建广告View
     *
     * @param adType 广告样式
     */
    private void createAdView(Activity activity, String adType, String appId, String mAdId) {

        if (Constants.AdType.ChuanShanJia.equals(adType)) {
            mAdView = new CHJAdView(GeekAdSdk.getContext(), activity, adStyle, appId, mAdId);
            ((CHJAdView) mAdView).setOrientation(orientation);
            if (!TextUtils.isEmpty(userId)) {
                ((CHJAdView) mAdView).setUserId(userId);
            }
            ((CHJAdView) mAdView).setFullScreen(isFullScreen);
            ((CHJAdView) mAdView).setShowTimeSeconds(showTimeSeconds);
        } else if (Constants.AdType.YouLiangHui.equals(adType)) {
            mAdView = new YlhAdView(GeekAdSdk.getContext(), activity, adStyle, appId, mAdId);
        } else {
            // 暂不处理
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }

        if (mAdView != null && mAdListener != null) {
            //向客户端提供接口
            mAdView.setAdListener(mAdListener);
            //ylh请求失败请求chj广告接口回掉
            mAdView.setYlhAdListener(mFirstAdListener);
        }

        adParentView.removeAllViews();
        adParentView.addView(mAdView);
        requestAd();
    }


    /**
     * 请求广告
     */
    private void requestAd() {
        if (mAdView != null) {
            //第一次请求广告保存请求时间
            if (firstRequestAd) {
                Long curTime = System.currentTimeMillis();
                SpUtils.putLong(Constants.SPUtils.FIRST_REQUEST_AD_TIME, curTime);
            }
            mAdView.requestAd(requestType, adRequestTimeOut);
        }
    }

    @Override
    public RelativeLayout getAdView() {
        return adParentView;
    }

    private FirstAdListener mFirstAdListener = new FirstAdListener() {
        @Override
        public void firstAdError(int errorCode, String errorMsg) {
            LogUtils.w(TAG, "回传--->请求第一个广告失败");
            firstRequestAd = false;
            againRequest();

        }
    };

    @Override
    public void loadAd(Activity activity, String position, AdListener listener) {
        mActivity = activity;
        mAdListener = listener;
        //创建view
        adParentView = new RelativeLayout(GeekAdSdk.getContext());
        //获取本地配置信息
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(position);
        if (mConfigInfoBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
            }
            return;
        }
        //当前广告位所对应的配置信息 存储到curAdlist
        adStyle = mConfigInfoBean.getAdStyle();
        adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
        adsInfoslist.clear();
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());

        againRequest();
    }

    /**
     * 开屏广告加载方法
     *
     * @param activity
     * @param position
     * @param listener
     */
    @Override
    public void loadSplashAd(Activity activity, String position, AdListener listener) {
        mAdListener = listener;
        mActivity = activity;
        //创建view
        adParentView = new RelativeLayout(GeekAdSdk.getContext());
        //获取本地配置信息
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(position);
        if (mConfigInfoBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
            }
            return;
        }
        //当前广告位所对应的配置信息 存储到curAdlist
        adStyle = mConfigInfoBean.getAdStyle();
        adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
        adsInfoslist.clear();
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());

        againRequest();
    }

    /**
     * 全屏视频广告加载方法
     *
     * @param activity
     * @param position
     * @param listener
     */
    @Override
    public void loadVideoAd(Activity activity, String position, AdListener listener) {
        mAdListener = listener;
        mActivity = activity;
        orientation = 1;
        //创建view
        adParentView = new RelativeLayout(GeekAdSdk.getContext());
        //获取本地配置信息
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(position);
        if (mConfigInfoBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
            }
            return;
        }
        //当前广告位所对应的配置信息 存储到curAdlist
        adStyle = mConfigInfoBean.getAdStyle();
        adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
        adsInfoslist.clear();
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());

        againRequest();
    }

    /**
     * 激励视频广告加载方法
     *
     * @param activity
     * @param position
     * @param listener
     */
    @Override
    public void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, AdListener listener) {
        mAdListener = listener;
        mActivity = activity;
        this.orientation = orientation;
        this.userId = userId;
        //创建view
        adParentView = new RelativeLayout(GeekAdSdk.getContext());
        //获取本地配置信息
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(position);
        if (mConfigInfoBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
            }
            return;
        }
        //当前广告位所对应的配置信息 存储到curAdlist
        adStyle = mConfigInfoBean.getAdStyle();
        adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
        adsInfoslist.clear();
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());

        againRequest();
    }

    /**
     * 自定义插屏广告加载方法
     *
     * @param activity
     * @param position
     * @param listener
     */
    @Override
    public void loadCustomInsertScreenAd(Activity activity, String position, boolean isFullScreen, int showTimeSeconds, AdListener listener) {
        mAdListener = listener;
        mActivity = activity;
        this.isFullScreen = isFullScreen;
        this.showTimeSeconds = showTimeSeconds;
        //创建view
        adParentView = new RelativeLayout(GeekAdSdk.getContext());
        //获取本地配置信息
        ConfigBean.AdListBean mConfigInfoBean = AdsConfig.getInstance(GeekAdSdk.getContext()).getConfig(position);
        if (mConfigInfoBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.LOCAL_INFO_EMPTY, CodeFactory.getError(CodeFactory.LOCAL_INFO_EMPTY));
            }
            return;
        }
        //当前广告位所对应的配置信息 存储到curAdlist
        adStyle = mConfigInfoBean.getAdStyle();
        adRequestTimeOut = mConfigInfoBean.getAdRequestTimeOut();
        adsInfoslist.clear();
        adsInfoslist.addAll(mConfigInfoBean.getAdsInfos());

        againRequest();
    }

    /**
     * 轮询请求
     */
    public void againRequest() {
        if (CollectionUtils.isEmpty(adsInfoslist)) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        ConfigBean.AdListBean.AdsInfosBean mAdsInfosBean = adsInfoslist.remove(0);
        if (mAdsInfosBean == null) {
            if (mAdListener != null) {
                mAdListener.adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            }
            return;
        }
        adUnion = mAdsInfosBean.getAdUnion();
        mAdId = mAdsInfosBean.getAdId();
        mAppId = mAdsInfosBean.getAdsAppId();
        createAdView(mActivity, adUnion, mAppId, mAdId);
    }
}
