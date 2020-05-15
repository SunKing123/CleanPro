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
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.json.JSONObject;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<TTSplashAd>() {
                    @Override
                    public void accept(TTSplashAd ttSplashAd) throws Exception {
                        //获取SplashView
                        View view = ttSplashAd.getSplashView();
                        if (view != null) {
                            addAdView(adRequestParamentersBean, view, adShowCallBack);
                            ttSplashAd.setNotAllowSdkCountdown();
                            adShowCallBack.onAdTickCallback(3000);
                            //设置SplashView的交互监听器
                            ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                                @Override
                                public void onAdClicked(View view, int type) {
                                    Log.d(TAG, "穿山甲 开屏----onAdClicked");
                                    if (null != adShowCallBack)
                                        adShowCallBack.onAdClickCallback();
                                    StatisticsUtils.clickAD("ad_click", "广告点击", "1", adRequestBean.getAdvertId(), "穿山甲", "clod_splash_page", "clod_splash_page", "");
                                }

                                @Override
                                public void onAdShow(View view, int type) {
                                    Log.d(TAG, "穿山甲 开屏----onAdShow");
                                    if (null != adShowCallBack)
                                        adShowCallBack.onAdShowCallBack(view);
                                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", adRequestBean.getAdvertId(), "穿山甲", "clod_splash_page", "clod_splash_page", "");
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
                                    StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "clod_splash_page", "clod_splash_page", extension);
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
                        if (throwable instanceof TimeoutException && adShowCallBack != null) {
                            adShowCallBack.onFailure("开屏超时后不在请求，直接结束");
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
                .subscribeOn(Schedulers.io())
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
                                Log.d(TAG, "穿山甲模板 onAdClicked");
                            }


                            @Override
                            public void onAdShow(View view, int type) {
                                Log.d(TAG, "穿山甲模板 onAdShow" + adRequestParamentersBean.index);

                            }

                            @Override
                            public void onRenderFail(View view, String msg, int code) {
                                Log.d(TAG, "穿山甲模板 render fail: " + msg + " code:" + code);
                            }

                            @Override
                            public void onRenderSuccess(View view, float width, float height) {
                                Log.d(TAG, "穿山甲模板 onRenderSuccess" + adRequestParamentersBean.index);

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
                                Log.d(TAG, "穿山甲模板 onSelected" + adRequestParamentersBean.index);
                                if (adShowCallBack != null) {
                                    adShowCallBack.onCloseCallback(adRequestParamentersBean.index);
                                    if (adRequestParamentersBean.index == 0) {
                                        adShowCallBack.onCloseCallback();
                                    } else {
                                        adShowCallBack.onCloseCallback(adRequestParamentersBean.index);
                                    }
                                    ttNativeExpressAd.destroy();
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
                        adError(adRequest, adRequestParamentersBean, adShowCallBack);
                    }
                });
    }

}
