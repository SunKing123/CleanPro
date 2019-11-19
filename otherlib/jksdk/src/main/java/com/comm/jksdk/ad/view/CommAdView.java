package com.comm.jksdk.ad.view;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTFeedAd;
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
    protected void adSuccess() {
        if (mAdListener != null) {
            LogUtils.d(TAG, "---------adSuccess------");
            mAdListener.adSuccess();
        } else {
            LogUtils.d(TAG, "---------mAdListener is null------");
        }
    }

    /**
     * 广告展示
     */
    protected void adExposed() {
        if (mAdListener != null) {
            LogUtils.w(TAG, "adExposed()");
            mAdListener.adExposed();
        }
    }

    /**
     * 广告点击
     */
    protected void adClicked() {
        if (mAdListener != null) {
            mAdListener.adClicked();
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
