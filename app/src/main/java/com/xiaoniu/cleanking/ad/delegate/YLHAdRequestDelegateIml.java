package com.xiaoniu.cleanking.ad.delegate;

import android.util.Log;

import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.bean.AdYLHEmitterBean;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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
        if (checkParamenter(adRequest, adRequestParamentersBean, adRequestBean, adShowCallBack)) {
            return;
        }

        adModel.getYLHSplashAd(adRequestParamentersBean, adRequestBean)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(new Consumer<AdYLHEmitterBean>() {
                    @Override
                    public void accept(AdYLHEmitterBean adYLHEmitterBean) throws Exception {
                        if (adYLHEmitterBean == null || adShowCallBack == null) {
                            return;
                        }
                        adShowCallBack.onAdShowCallBack(adYLHEmitterBean.nativeExpressADView);
//                        adYLHEmitterBean.nativeExpressADView.destroy();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "优量会开屏广告失败  message:"+throwable.getMessage());
                        if (throwable instanceof TimeoutException) {
                            StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "other", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
                        }
                        adError(adRequest, adRequestParamentersBean, adShowCallBack);

                    }
                });

    }

    @Override
    public void requestTemplateAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack) {
        if (checkParamenter(adRequest, adRequestParamentersBean, adRequestBean, adShowCallBack)) {
            return;
        }
        adModel.getYLHTemplateAd(adRequestParamentersBean, adRequestBean)
//                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
//                .timeout(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<AdYLHEmitterBean>() {
                    @Override
                    public void accept(AdYLHEmitterBean adYLHEmitterBean) throws Exception {
                        if (adYLHEmitterBean == null) {
                            return;
                        }
                        switch (adYLHEmitterBean.type) {
                            case 1:
                                adYLHEmitterBean.nativeExpressADView.render();
                                break;
                            case 2:
                                if (adShowCallBack != null) {
                                    if (adYLHEmitterBean.index == 0) {
                                        adShowCallBack.onAdShowCallBack(adYLHEmitterBean.nativeExpressADView);
                                    } else {
                                        adShowCallBack.onAdListShowCallBack(adYLHEmitterBean.index, adYLHEmitterBean.nativeExpressADView);
                                    }
                                }
                                break;
                            case 3:
                                if (adShowCallBack != null) {
                                    if (adYLHEmitterBean.index == 0) {
                                        adShowCallBack.onCloseCallback();
                                    } else {
                                        adShowCallBack.onCloseCallback(adYLHEmitterBean.index);
                                    }
                                }
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "优量会模板广告失败  message:"+throwable.getMessage());
                        if (throwable instanceof TimeoutException) {
                            StatisticsUtils.customADRequest("ad_request", "广告请求", getAdvertPosition(adRequestParamentersBean), adRequestBean.getAdvertId(), "优量汇", "other", adRequestParamentersBean.sourcePageId, adRequestParamentersBean.currentPageId);
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
