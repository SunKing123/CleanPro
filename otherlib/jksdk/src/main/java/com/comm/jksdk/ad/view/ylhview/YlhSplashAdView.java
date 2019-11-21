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

    protected YlhSplashAdView(Context context) {
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
    public void loadSplashAd(Activity activity, String appId, String adId) {
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.YouLiangHui);
        mAdInfo.setAdAppid(appId);
        mAdInfo.setAdId(adId);
        LogUtils.d(TAG, "YLH appId:" + appId + " adId:" + adId);
        SplashAD splashAD = new SplashAD(activity, appId, adId, new SplashADListener() {
            @Override
            public void onADDismissed() {
                LogUtils.d(TAG, "YLH onADDismissed:");
            }

            @Override
            public void onNoAD(AdError adError) {
                LogUtils.d(TAG, "YLH onNoAD:");
                if (adError != null) {
//                    adError(adError.getErrorCode(), adError.getErrorMsg());
                    firstAdError(adError.getErrorCode(), adError.getErrorMsg());
                } else {
//                    adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    firstAdError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                }
            }

            @Override
            public void onADPresent() {
            }

            @Override
            public void onADClicked() {
                LogUtils.d(TAG, "YLH onADClicked:");
                adClicked(mAdInfo);
            }

            @Override
            public void onADTick(long l) {

            }

            @Override
            public void onADExposure() {
                LogUtils.d(TAG, "YLH onADClicked:");
                adSuccess(mAdInfo);
                adExposed(mAdInfo);
            }
        });
        splashAD.fetchAndShowIn(splashContainer);
    }
}
