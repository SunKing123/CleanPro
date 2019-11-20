package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.comm.jksdk.R;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;

import java.util.List;

/**
 * 穿山甲自渲染插屏广告<p>
 *
 * @author zixuefei
 * @since 2019/11/18 11:24
 */
public class CsjCustomInsertScreenAdView extends CHJAdView {
    private Activity activity;
    private TTAdNative mTTAdNative;

    public CsjCustomInsertScreenAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.csj_full_screen_video_ad_view;
    }

    @Override
    public void initView() {
    }

    /**
     * 获取插屏广告并展示
     */
    public void loadCustomInsertScreenAd(final Activity activity, final boolean isFullScreen, final int showTimeSeconds, String adId) {
        if (activity == null) {
            throw new NullPointerException("loadCustomInsertScreenAd activity is null");
        }
        LogUtils.d(TAG, "isFullScreen:" + isFullScreen + " adId:" + adId + " showTimeSeconds:" + showTimeSeconds);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        this.activity = activity;
        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(activity.getApplicationContext());
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "loadNativeAd code:" + code + " message:" + message);
                adError(code, message);
                firstAdError(code, message);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (!CollectionUtils.isEmpty(ads)) {
                    adSuccess();
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        return;
                    }
                    showAdDialog(ads.get(0), isFullScreen, showTimeSeconds);
                } else {
                    adError(CodeFactory.UNKNOWN, "请求广告数据为空");
                }
            }
        });
    }

    /**
     * 展示插屏广告
     */
    private void showAdDialog(TTNativeAd ttNativeAd, boolean isFullScreen, int showTimeSeconds) {
        LogUtils.d(TAG, "showAdDialog:" + isFullScreen + " showTimeSeconds:" + showTimeSeconds);
        if (isFullScreen) {
            InsertScreenAdFullDownloadDialog fullDownloadDialog = new InsertScreenAdFullDownloadDialog(activity, showTimeSeconds);
            fullDownloadDialog.show();
            fullDownloadDialog.loadAd(ttNativeAd);
        } else {
            if (ttNativeAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                InsertScreenAdNormalDownloadDialog normalDownloadDialog = new InsertScreenAdNormalDownloadDialog(activity, showTimeSeconds);
                normalDownloadDialog.show();
                normalDownloadDialog.loadAd(ttNativeAd);
            } else {
                InsertScreenAdNormalBrowseDialog browseDialog = new InsertScreenAdNormalBrowseDialog(activity, showTimeSeconds);
                browseDialog.show();
                browseDialog.loadAd(ttNativeAd);
            }
        }
    }
}
