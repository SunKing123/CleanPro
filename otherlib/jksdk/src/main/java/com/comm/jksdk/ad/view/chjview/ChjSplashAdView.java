package com.comm.jksdk.ad.view.chjview;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.comm.jksdk.R;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;

import androidx.constraintlayout.widget.ConstraintLayout;

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
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                .build();
        TTAdManagerHolder.get(mAppId).createAdNative(mContext).loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "csj errorCode:" + errorCode + " errorMsg:" + errorMsg);
                adError(errorCode, errorMsg);
                firstAdError(errorCode, errorMsg);
                Toast.makeText(mContext, "loadSplashAd error:" + errorCode + " message:" + errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTimeout() {
                adError(CodeFactory.UNKNOWN, "请求广告超时");
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                if (ttSplashAd != null) {
                    LogUtils.d(TAG, "csj onSplashAdLoad:" + ttSplashAd.getInteractionType());
                    splashContainer.removeAllViews();
                    splashContainer.addView(ttSplashAd.getSplashView());
                    adSuccess();
                    ttSplashAd.setNotAllowSdkCountdown();
                    //设置SplashView的交互监听器
                    ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            LogUtils.d(TAG, "onAdClicked");
                            adClicked();
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            LogUtils.d(TAG, "onAdShow");
                            adExposed();
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
                    adError(CodeFactory.UNKNOWN, "请求广告为空");
                }
            }
        });
    }
}
