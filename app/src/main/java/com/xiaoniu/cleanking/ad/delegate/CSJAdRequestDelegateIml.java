package com.xiaoniu.cleanking.ad.delegate;


import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.json.JSONObject;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.delegate
 * @ClassName: CSjAdRequestDelegateIml
 * @Description: 穿山甲广告请求
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:59
 */

public class CSJAdRequestDelegateIml extends AdRequestDelegateIml {


    public CSJAdRequestDelegateIml(AdModel adModel, AdAgainRequestCallBack adAgainRequestCallBack) {
        super(adModel, adAgainRequestCallBack);
    }

    @Override
    public void requestSplashAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {

        if (checkParamenter(adRequest, adRequestParamentersBean, adRequestBean, adShowCallBack)) {
            return;
        }

        //开屏限制3s超时，超时后直接结束广告
        adModel.getCSJSplashAd(adRequestParamentersBean, adRequestBean)
//                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(new Consumer<TTSplashAd>() {
                    @Override
                    public void accept(TTSplashAd ttSplashAd) throws Exception {
                        //获取SplashView
                        View view = ttSplashAd.getSplashView();
                        if (view != null) {
                            addAdView(adRequestParamentersBean, view, adShowCallBack);
                            ttSplashAd.setNotAllowSdkCountdown();
                            //设置SplashView的交互监听器
                            ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                                @Override
                                public void onAdClicked(View view, int type) {
                                    Log.d(TAG, "穿山甲 点击 广告位id:"+adRequestBean.getAdvertId());
                                    if (null != adShowCallBack)
                                        adShowCallBack.onAdClickCallback();
                                    StatisticsUtils.clickAD("ad_click", "广告点击", "2", adRequestBean.getAdvertId(), "穿山甲", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                                }

                                @Override
                                public void onAdShow(View view, int type) {
                                    Log.d(TAG, "穿山甲 曝光 广告位id:"+adRequestBean.getAdvertId());
                                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "2", adRequestBean.getAdvertId(), "穿山甲", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                                }

                                @Override
                                public void onAdSkip() {
                                    Log.d(TAG, "穿山甲 开屏----onAdSkip");
                                    if (null != adShowCallBack)
                                        adShowCallBack.onAdSkipCallback();
                                    JSONObject extension = new JSONObject();
                                    try {
                                        extension.put("ad_id", adRequestBean.getAdvertId());
                                        extension.put("ad_agency", "穿山甲");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                    StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "clod_splash_page", "clod_splash_page", extension);
                                }

                                @Override
                                public void onAdTimeOver() {
                                    Log.d(TAG, "穿山甲 开屏----onAdTimeOver");
                                    if (null != adShowCallBack) {
                                        adShowCallBack.onAdSkipCallback();
                                    }
                                }
                            });
                        } else {
                            if (null != adShowCallBack) {
                                adShowCallBack.onAdSkipCallback();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "穿山甲 开屏广告异常  错误message："+throwable.getMessage());
                        if (throwable instanceof TimeoutException ) {
                            StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "other", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            if(adShowCallBack != null){
                                adShowCallBack.onFailure("开屏超时后不在请求，直接结束");
                            }
                        } else {
                            adError(adRequest, adRequestParamentersBean, adShowCallBack);
                        }
                    }
                });
    }

    @Override
    public void requestTemplateAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {

        if (checkParamenter(adRequest, adRequestParamentersBean, adRequestBean, adShowCallBack)) {
            return;
        }

        adModel.getCSJTemplateAd(adRequestParamentersBean, adRequestBean)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
//                .timeout(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<List<TTNativeExpressAd>>() {
                    @Override
                    public void accept(List<TTNativeExpressAd> ads) throws Exception {
                        if (CollectionUtils.isEmpty(ads)) {
                            adError(adRequest, adRequestParamentersBean, adShowCallBack);
                            return;
                        }
                        TTNativeExpressAd ttNativeExpressAd = ads.get(0);
                        ttNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                            @Override
                            public void onAdClicked(View view, int type) {
                                Log.d(TAG, "穿山甲模板广告点击  广告位id:"+adRequestBean.getAdvertId());
                                StatisticsUtils.clickAD("ad_click", "广告点击", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }


                            @Override
                            public void onAdShow(View view, int type) {
                                Log.d(TAG, "穿山甲模板广告曝光  广告位id:"+adRequestBean.getAdvertId());
                                StatisticsUtils.customAD("ad_show", "广告展示曝光", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                            }

                            @Override
                            public void onRenderFail(View view, String msg, int code) {
                                Log.d(TAG, "穿山甲模板广告渲染失败  广告位id:"+adRequestBean.getAdvertId());
                            }

                            @Override
                            public void onRenderSuccess(View view, float width, float height) {
                                Log.d(TAG, "穿山甲模板广告渲染成功  广告位id:"+adRequestBean.getAdvertId());

                                if (adRequestParamentersBean.index == 0) {
                                    adShowCallBack.onAdShowCallBack(view);
                                } else {
                                    adShowCallBack.onAdListShowCallBack(adRequestParamentersBean.index, view);
                                }
                            }
                        });
                        ttNativeExpressAd.setDislikeCallback((Activity) adRequestParamentersBean.context, new TTAdDislike.DislikeInteractionCallback() {

                            @Override
                            public void onSelected(int i, String s) {
                                Log.d(TAG, "穿山甲模板广告关闭  广告位id:"+adRequestBean.getAdvertId());
                                if (adShowCallBack != null) {
                                    adShowCallBack.onCloseCallback(adRequestParamentersBean.index);
                                    if (adRequestParamentersBean.index == 0) {
                                        adShowCallBack.onCloseCallback();
                                    } else {
                                        adShowCallBack.onCloseCallback(adRequestParamentersBean.index);
                                    }
                                    ttNativeExpressAd.destroy();
                                    StatisticsUtils.clickAD("ad_close_click", "关闭点击", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "穿山甲模板 onCancel");

                            }
                        });
                        ttNativeExpressAd.setDownloadListener(new TTAppDownloadListener() {
                            @Override
                            public void onIdle() {
                            }

                            @Override
                            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            }

                            @Override
                            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            }

                            @Override
                            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            }

                            @Override
                            public void onInstalled(String fileName, String appName) {
                            }

                            @Override
                            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            }
                        });
                        ttNativeExpressAd.render();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "穿山甲 模板广告异常  错误message："+throwable.getMessage());
                        if (throwable instanceof TimeoutException ) {
                            StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "穿山甲", "other", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                        }
                        adError(adRequest, adRequestParamentersBean, adShowCallBack);
                    }
                });
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
