package com.comm.jksdk.ad.view.ylhview;

import android.app.Activity;
import android.content.Context;

import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.http.utils.LogUtils;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

/**
 * 优量汇模板渲染插屏广告<p>
 *
 * @author zixuefei
 * @since 2019/11/18 11:24
 */
public class YlhTemplateInsertScreenAdView extends YlhAdView {
    private Activity activity;
    private UnifiedInterstitialAD iad;

    public static final String UNIFIED_INTERSTITIAL_ID_LARGE_SMALL = "3040652898151811";// 大小规格
    public static final String UNIFIED_INTERSTITIAL_ID_ONLY_SMALL = "8020259898964453";// 只小规格

    public YlhTemplateInsertScreenAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.csj_full_screen_video_ad_view;
    }

    @Override
    public void initView() {
    }

    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);
        loadTemplateInsertScreenAd(adInfo, (Activity) mContext, adInfo.getAdAppid(), adInfo.getAdId());
    }

    /**
     * 获取插屏广告并展示
     */
    public void loadTemplateInsertScreenAd(AdInfo adInfo, Activity activity,String appId, String adId) {
        if (activity == null) {
            throw new NullPointerException("loadCustomInsertScreenAd activity is null");
        }
        LogUtils.d(TAG, "isFullScreen:"  + " adId:" + adId + " showTimeSeconds:");
        //step3:创建TTAdNative对象,用于调用广告请求接口
        this.activity = activity;

        if (iad != null) {
            iad.close();
            iad.destroy();
            iad = null;
        }
        if (iad == null) {
            iad = new UnifiedInterstitialAD(activity, appId, UNIFIED_INTERSTITIAL_ID_LARGE_SMALL, new UnifiedInterstitialADListener() {

                public void  onVideoCached(){

                }
                @Override
                public void onADReceive() {
                    //广告加载成功
                    if (iad != null) {
                        adSuccess(adInfo);
                        iad.showAsPopupWindow();
                    }
                }

                @Override
                public void onNoAD(AdError adError) {
                    adError(adInfo, 1, "没有广告");
                }

                @Override
                public void onADOpened() {

                }

                @Override
                public void onADExposure() {
                    adExposed(adInfo);
                }

                @Override
                public void onADClicked() {
                    adClicked(adInfo);
                }

                @Override
                public void onADLeftApplication() {

                }

                @Override
                public void onADClosed() {
                    adClose(adInfo);
                }
            });
            iad.loadAD();
        }

    }

}
