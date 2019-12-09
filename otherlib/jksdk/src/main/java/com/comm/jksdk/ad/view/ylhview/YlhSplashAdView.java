package com.comm.jksdk.ad.view.ylhview;

import android.app.Activity;
import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

/**
 * 开屏广告view<p>
 *
 * @author zixuefei
 * @since 2019/11/14 21:53
 */
public class YlhSplashAdView extends YlhAdView {
    private ConstraintLayout splashContainer;

    public YlhSplashAdView(Context context) {
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
        loadSplashAd(adInfo, (Activity) mContext, adInfo.getAdAppid(), adInfo.getAdId());
    }

    /**
     * 获取开屏广告并加载
     */
    public void loadSplashAd(AdInfo adInfo, Activity activity, String appId, String adId) {
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        LogUtils.d(TAG, "YLH appId:" + appId + " adId:" + adId);
        SplashAD splashAD = new SplashAD(activity, appId, adId, new SplashADListener() {
            @Override
            public void onADDismissed() {
                LogUtils.d(TAG, "YLH onADDismissed:");
                adClose(adInfo);
            }

            @Override
            public void onNoAD(AdError adError) {
                LogUtils.d(TAG, "YLH onNoAD:");
                adError(adInfo, adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADPresent() {
            }

            @Override
            public void onADClicked() {
                LogUtils.d(TAG, "YLH onADClicked:");
                adClicked(adInfo);
            }

            @Override
            public void onADTick(long l) {

            }

            @Override
            public void onADExposure() {
                LogUtils.d(TAG, "YLH onADClicked:");
                adExposed(adInfo);
            }
        });
        splashAD.fetchAndShowIn(splashContainer);
//        adSuccess(adInfo);
    }
}
