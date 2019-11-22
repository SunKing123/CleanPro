package com.comm.jksdk.ad.view;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.FirstAdListener;
import com.comm.jksdk.http.utils.LogUtils;
import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.util.List;


/**
 * @author: docking
 * @date: 2019/9/7 12:13
 * @description: todo ...
 **/
public class CommAdView extends AbsAdView {

    protected AdListener mAdListener = null;
    protected FirstAdListener mFirstAdListener = null;

    // 广告位ID
    protected String mAdId = "";

    /**
     * 广告appid
     */
    protected String mAppId = "";

    //广告信息
    protected AdInfo mAdInfo;

    public void setAdInfo(AdInfo info){
        this.mAdInfo = info;
    }

    public void setAdId(String mAdId) {
        this.mAdId = mAdId;
    }

    public void setAppId(String mAppId) {
        this.mAppId = mAppId;
    }

    public CommAdView(Context context, String style, String adPositionId) {
        super(context, style, adPositionId);
        initView();
    }

    public CommAdView(Context context) {
        super(context);
        initView();
    }


    @Override
    public int getLayoutId() {
        return 0;
    }

    public void initView() {

    }

    @Override
    public void requestAd(int requestType, int adRequestTimeOut) {

    }

    @Override
    public void parseYlhAd(List<NativeUnifiedADData> nativeAdList) {

    }

    @Override
    public void parseChjAd(List<TTFeedAd> nativeAdList) {

    }


    public void setAdListener(AdListener adListener) {
        mAdListener = adListener;
    }

    public void setYlhAdListener(FirstAdListener adListener) {
        mFirstAdListener = adListener;
    }

    /**
     * 第一个广告请求失败
     *
     * @param errorCode
     * @param errorMsg
     */
    protected void firstAdError(int errorCode, String errorMsg) {
        if (mFirstAdListener != null) {
            mFirstAdListener.firstAdError(errorCode, errorMsg);
        }
    }

    /**
     * 广告请求i成功
     */
    protected void adSuccess(AdInfo adInfo) {
        if (mAdListener != null) {
            LogUtils.d(TAG, "---------adSuccess------");
            mAdListener.adSuccess(adInfo);
        } else {
            LogUtils.d(TAG, "---------mAdListener is null------");
        }
    }

    /**
     * 广告展示
     */
    protected void adExposed(AdInfo adInfo) {
        if (mAdListener != null) {
            LogUtils.w(TAG, "adExposed()");
            mAdListener.adExposed(adInfo);
        }
    }

    /**
     * 广告点击
     */
    protected void adClicked(AdInfo adInfo) {
        if (mAdListener != null) {
            mAdListener.adClicked(adInfo);
        }
    }

    /**
     * 广告被关闭
     * @param adInfo
     */
    protected void adClose(AdInfo adInfo){
        if (mAdListener != null) {
            mAdListener.adClose(adInfo);
        }
    }

    /**
     * 广告失败
     *
     * @param errorCode
     * @param errorMsg
     */
    protected void adError(int errorCode, String errorMsg) {
        if (mAdListener != null) {
            mAdListener.adError(errorCode, errorMsg);
        }
    }
}
