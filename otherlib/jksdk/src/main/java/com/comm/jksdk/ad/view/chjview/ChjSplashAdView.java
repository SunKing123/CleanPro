package com.comm.jksdk.ad.view.chjview;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
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

    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);
        TTSplashAd ttSplashAd = adInfo.getTtSplashAd();
        loadSplashAd(ttSplashAd, adInfo);
    }

    /**
     * 获取开屏广告并加载
     */
    public void loadSplashAd(TTSplashAd ttSplashAd, AdInfo adInfo) {
        splashContainer.removeAllViews();
        splashContainer.addView(ttSplashAd.getSplashView());
        ttSplashAd.setNotAllowSdkCountdown();
        //设置SplashView的交互监听器
        ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                LogUtils.d(TAG, "onAdClicked");
                adClicked(adInfo);
            }

            @Override
            public void onAdShow(View view, int type) {
                LogUtils.d(TAG, "onAdShow");
                adExposed(adInfo);
            }

            @Override
            public void onAdSkip() {
                LogUtils.d(TAG, "onAdSkip");

            }

            @Override
            public void onAdTimeOver() {
                LogUtils.d(TAG, "onAdTimeOver");
                firstAdError(adInfo, CodeFactory.UNKNOWN, "广告加载超时");
            }
        });
    }
}
