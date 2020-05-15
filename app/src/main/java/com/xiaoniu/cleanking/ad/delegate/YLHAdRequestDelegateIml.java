package com.xiaoniu.cleanking.ad.delegate;


import android.util.Log;

import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.bean.TemplateAdResponse;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.Deque;
import java.util.List;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.delegate
 * @ClassName: YLHAdRequestDelegateIml
 * @Description:优量会广告请求
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:58
 */

public class YLHAdRequestDelegateIml extends AdRequestDelegateIml {

    public YLHAdRequestDelegateIml(AdModel adModel, AdAgainRequestCallBack adAgainRequestCallBack) {
        super(adModel, adAgainRequestCallBack);
    }

    @Override
    public void requestSplashAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {
        if (null == adRequestBean || null == adRequestParamentersBean) {
            Log.d(TAG, "优量会开屏广告没有获取到下发配置");
            adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
            return;
        }
        Log.d(TAG, "优量会开屏开始 id:" + adRequestBean.getAdvertId());

        adModel.getYLHSplashAd(adRequestParamentersBean, adRequestBean, new SplashADListener() {
            @Override
            public void onADDismissed() {
                Log.d(TAG, "优量会 开屏----onADDismissed");
                if (null != adShowCallBack)
                    adShowCallBack.onAdSkipCallback();
            }

            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "优量会 开屏----onNoAD  获取广告失败  message:" + adError.getErrorMsg() + " code:" + adError.getErrorCode());
                adError(adRequest, adRequestParamentersBean, adShowCallBack);
            }

            @Override
            public void onADPresent() {
                Log.d(TAG, "优量会 开屏----onADPresent");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "优量会 开屏----onADClicked");
                if (null != adShowCallBack) {
                    adShowCallBack.onAdClickCallback();
                }
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", adRequestBean.getAdvertId(), "优量汇", "clod_splash_page", "clod_splash_page", "");
            }

            @Override
            public void onADTick(long millisUntilFinished) {
                Log.d(TAG, "优量会 开屏----onADTick");
                if (Math.round(millisUntilFinished / 1000f) > 4) {
                    if (null != adShowCallBack) {
                        adShowCallBack.onAdTickCallback(millisUntilFinished);
                    }
                }
            }

            @Override
            public void onADExposure() {
                Log.d(TAG, "优量会 开屏----onADExposure");
                if (null != adShowCallBack) {
                    adShowCallBack.onAdShowCallBack(null);
                }
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", adRequestBean.getAdvertId(), "优量汇", "success", "clod_splash_page", "clod_splash_page");
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", adRequestBean.getAdvertId(), "优量汇", "clod_splash_page", "clod_splash_page", "");
            }
        });
    }

//    @Override
//    public void requestTemplateAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {
//        Log.d(TAG, "优量会模板请求开始 id "+adRequestBean.getAdvertId());
//
//        adModel.getYLHTemplateAd(adRequestParamentersBean, adRequestBean, new NativeExpressAD.NativeExpressADListener() {
//            @Override
//            public void onNoAD(AdError adError) {
//                Log.d(TAG, "优量会模板 onNoAD message:" + adError.getErrorMsg() + " code:" + adError.getErrorCode());
//
//                if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
//                    adShowCallBack.onFailure("串行广告结束");
//                    return;
//                }
//                if (adAgainRequestCallBack != null) {
//                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
//                }
//            }
//
//            @Override
//            public void onADLoaded(List<NativeExpressADView> list) {
//                Log.d(TAG, "优量会模板 onADLoaded ");
//                if (CollectionUtils.isEmpty(list) && adAgainRequestCallBack != null) {
//                    if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
//                        adShowCallBack.onFailure("串行广告结束");
//                        return;
//                    }
//                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
//                }
//                NativeExpressADView nativeExpressADView = list.get(0);
//                nativeExpressADView.render();
//                addAdView(adRequestParamentersBean, nativeExpressADView, adShowCallBack);
//                if(adShowCallBack!=null){
//                    adShowCallBack.onAdShowCallBack(true,nativeExpressADView);
//                }
//                nativeExpressADView.destroy();
//            }
//
//            @Override
//            public void onRenderFail(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onRenderFail ");
//
//                if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
//                    adShowCallBack.onFailure("串行广告结束");
//                    return;
//                }
//                if (adAgainRequestCallBack != null) {
//                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
//                }
//            }
//
//            @Override
//            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onRenderSuccess ");
//
//            }
//
//            @Override
//            public void onADExposure(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADExposure ");
//
//            }
//
//            @Override
//            public void onADClicked(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADClicked ");
//            }
//
//            @Override
//            public void onADClosed(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADClosed ");
//                if(adShowCallBack!=null && adRequestParamentersBean!=null){
//                    adShowCallBack.onCloseCallback(adRequestParamentersBean.adContainer);
//                }
//
//            }
//
//            @Override
//            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADLeftApplication ");
//
//            }
//
//            @Override
//            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADOpenOverlay ");
//
//            }
//
//            @Override
//            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
//                Log.d(TAG, "优量会模板 onADCloseOverlay ");
//
//            }
//        });
//    }

    @Override
    public void requestTemplateAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {
        Log.d(TAG, "优量会模板请求开始 id "+adRequestBean.getAdvertId());

        adModel.getYLHTemplateAd(adRequestParamentersBean, adRequestBean, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "优量会模板 onNoAD message:" + adError.getErrorMsg() + " code:" + adError.getErrorCode());

                if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
                    adShowCallBack.onFailure("串行广告结束");
                    return;
                }
                if (adAgainRequestCallBack != null) {
                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
                }
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                Log.d(TAG, "优量会模板 onADLoaded ");
                if (CollectionUtils.isEmpty(list) && adAgainRequestCallBack != null) {
                    if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
                        adShowCallBack.onFailure("串行广告结束");
                        return;
                    }
                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
                }
                NativeExpressADView nativeExpressADView = list.get(0);
                nativeExpressADView.render();
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                Log.d(TAG, "优量会模板 onRenderFail ");

                if (CollectionUtils.isEmpty(adRequest) && adShowCallBack != null) {
                    adShowCallBack.onFailure("串行广告结束");
                    return;
                }
                if (adAgainRequestCallBack != null) {
                    adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
                }
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                Log.d(TAG, "优量会模板 onRenderSuccess ");
                if(adShowCallBack!=null){
                    if(adRequestParamentersBean.index==0){
                        adShowCallBack.onAdShowCallBack(nativeExpressADView);
                    }else {
                        adShowCallBack.onAdListShowCallBack(adRequestParamentersBean.index,nativeExpressADView);
                    }
                }
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                Log.d(TAG, "优量会模板 onADExposure ");

            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                Log.d(TAG, "优量会模板 onADClicked ");
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                Log.d(TAG, "优量会模板 onADClosed ");
                if(adShowCallBack!=null && adRequestParamentersBean!=null){
                    if(adRequestParamentersBean.index==0){
                        adShowCallBack.onCloseCallback();
                    }else {
                        adShowCallBack.onCloseCallback(adRequestParamentersBean.index);
                    }
                    nativeExpressADView.destroy();
                }

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
    }


}
