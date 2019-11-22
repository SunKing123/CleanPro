package com.comm.jksdk.ad.view.chjview;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;

/**
 * 穿山甲开屏广告view<p>
 *
 * @author zixuefei
 * @since 2019/11/15 11:31
 */
public class ChjSplashAdView extends CHJAdView {
    private ConstraintLayout splashContainer;

    public ChjSplashAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ylh_splash_ad_view;
    }

    @Override
    public void initView() {
        splashContainer = findViewById(R.id.splash_ad_container);
    }

    /**
     * 获取开屏广告并加载
     */
    public void loadSplashAd(String appId, String adId) {
        LogUtils.d(TAG, "csj appId:" + appId + " adId:" + adId);
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.ChuanShanJia);
        mAdInfo.setAdAppid(appId);
        mAdInfo.setAdId(adId);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                .build();
        TTAdManagerHolder.get(mAppId).createAdNative(mContext).loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.e(TAG, "csj errorCode:" + errorCode + " errorMsg:" + errorMsg);
//                adError(errorCode, errorMsg);
                firstAdError(errorCode, errorMsg);
//                Toast.makeText(mContext, "loadSplashAd error:" + errorCode + " message:" + errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTimeout() {
//                adError(CodeFactory.UNKNOWN, "请求广告超时");
                firstAdError(CodeFactory.UNKNOWN, "请求广告超时");
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                if (ttSplashAd != null) {
                    LogUtils.d(TAG, "csj onSplashAdLoad:" + ttSplashAd.getInteractionType());
                    adSuccess(mAdInfo);
                    splashContainer.removeAllViews();
                    splashContainer.addView(ttSplashAd.getSplashView());
                    ttSplashAd.setNotAllowSdkCountdown();
                    //设置SplashView的交互监听器
                    ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            LogUtils.d(TAG, "onAdClicked");
                            adClicked(mAdInfo);
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            LogUtils.d(TAG, "onAdShow");
                            adExposed(mAdInfo);
                        }

                        @Override
                        public void onAdSkip() {
                            LogUtils.d(TAG, "onAdSkip");

                        }

                        @Override
                        public void onAdTimeOver() {
                            LogUtils.d(TAG, "onAdTimeOver");
                            adError(CodeFactory.UNKNOWN, "广告加载超时");
                        }
                    });
                } else {
                    firstAdError(CodeFactory.UNKNOWN, "请求广告为空");
                }
            }
        });
    }
}
