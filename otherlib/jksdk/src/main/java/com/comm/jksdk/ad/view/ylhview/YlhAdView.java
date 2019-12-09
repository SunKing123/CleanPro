package com.comm.jksdk.ad.view.ylhview;

import android.app.Activity;
import android.content.Context;

import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.AdsUtils;
import com.qq.e.ads.nativ.NativeUnifiedAD;


/**
 * 目前只有首页广告位使用该模板，后续优化增加其他广告位
 * 优量汇广告模块
 */
public class YlhAdView extends CommAdView {
    private String style;

    /**
     * 视频广告方向横屏、竖屏
     */
    protected int orientation = 1;

    protected Activity mActivity;

    // 广告请求数量
    protected final static int REQUEST_AD_COUNTS = 1;


    protected CommAdView mAdView = null;

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public YlhAdView(Context context, String style, String appId, String mAdId) {
        this(context, null, style, appId, mAdId);
    }

    public YlhAdView(Context context, Activity activity, String style, String appId, String mAdId) {
        super(context, style, mAdId);
        this.mActivity = activity;
        this.mAdId = mAdId;
        this.style = style;
        this.mContext = context;
        this.mAppId = appId;
        LogUtils.d(TAG, "广告样式------->style:" + style);
//        if (Constants.AdStyle.BIG_IMG.equals(style)) {
//            mAdView = new YlhBIgImgAdView(mContext);
//        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style)) { // 大图_带icon文字按钮
//            mAdView = new YlhBigImgIcTvBtAdView(mContext);
//        } else if (Constants.AdStyle.DATU_ICON_TEXT.equals(style)) { //大图_带icon文字
//            mAdView = new YlhBigImgIcTvAdView(mContext);
//        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style)) { //大图_带icon文字按钮居中
//            mAdView = new YlhBigImgIcTvBtCenterAdView(mContext);
//        } else if (Constants.AdStyle.BIG_IMG_BUTTON.equals(style)) { //大图带按钮（大图_下载播放按钮）
//            mAdView = new YlhBigImgAdPlayLampView(mContext);
//        } else if (Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)) { //大图带按钮带跑马灯
//            mAdView = new YlhBigImgAdPlayLampView(mContext, true);
//        } else if (Constants.AdStyle.BIG_IMG_NEST.equals(style)) { //大图嵌套美女图片
//            mAdView = new YlhBigImgNestPlayLampView(mContext);
//        } else if (Constants.AdStyle.BIG_IMG_NEST_LAMP.equals(style)) { //大图嵌套图片带跑马灯
//            mAdView = new YlhBigImgNestPlayLampView(mContext, true);
//        } else if (Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) { //假视频大图_01
//            mAdView = new YlhBigImgFakeVideoAdView(mContext);
//        } else if (Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)) { //外部弹窗大图广告_01
//            mAdView = new YlhExternalDialogBigImageView(mContext);
//        } else if (Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style)) {
//            mAdView = new YlhLeftImgRightTwoTextAdView(mContext);
//        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
//            mAdView = new YlhSplashAdView(mContext);
//        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
//            mAdView = new YlhFullScreenVideoAdView(mContext);
//        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style) || Constants.AdStyle.CP.equals(style)) { //优量汇插屏
//            mAdView = new YlhTemplateInsertScreenAdView(mContext);
//        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
//            mAdView = new YlhRewardVideoAdView(mContext);
//        }else {
//            //all
//            //所有样式都支持 随机展示
//            int num = AdsUtils.getRandomNum(2);
//            LogUtils.w(TAG, "随机显示样式------->style:" + style + " getRandomNum:" + num);
////            Toast.makeText(mContext, "发现未定义样式:" + style + "正试图随机显示样式" + num, Toast.LENGTH_SHORT).show();
//            switch (num) {
//                case 0:
//                    mAdView = new YlhLeftImgRightTwoTextAdView(mContext);
//                    break;
//                case 1:
//                    mAdView = new YlhBIgImgAdView(mContext);
//                    break;
//                default:
//                    mAdView = new YlhLeftImgRightTwoTextAdView(mContext);
//                    break;
//            }
//
//        }
//        mAdView.setAdListener(mAdListener);
//        this.addView(mAdView);

    }

    protected YlhAdView(Context context) {
        super(context);
    }

    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);

    }


    //    @Override
//    public void requestAd(int requestType, int adRequestTimeOut) {
//        if (requestType == 0) {
//            LogUtils.d(TAG, "request ad:" + mAppId + " mAdId:" + mAdId);
//            //SDK
//            getAdBySdk(adRequestTimeOut);
//        } else {
//            //api
//        }
//
//    }

//    /**
//     * 通过SDK获取广告
//     */
//    protected void getAdBySdk(final int adRequestTimeOut) {
//        LogUtils.d(TAG, "--------开始请求广告-------------广告样式------->style:" + style);
//        if (Constants.AdStyle.BIG_IMG.equals(style) || Constants.AdStyle.DATU_ICON_TEXT.equals(style) || Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style) || Constants.AdStyle.EXTERNAL_DIALOG_BIG_IMAGE_01.equals(style)
//                || Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style) || Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style) || Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)
//                || Constants.AdStyle.BIG_IMG_BUTTON.equals(style) || Constants.AdStyle.BIG_IMG_NEST.equals(style) || Constants.AdStyle.BIG_IMG_NEST_LAMP.equals(style) || Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) {
//            //todo请求大图广告
////            getAdByBigImg(adRequestTimeOut);
//        } else if (Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style)) {
//            //dodo
////            getAdByBigImg(adRequestTimeOut);
//        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
////            getAdBySplashAd();
//        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
//            getFullScreenVideoAd();
//        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style) || Constants.AdStyle.CP.equals(style)) {
//            getCustomInsertScreenAd();
//        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
//            getRewardVideoAd();
//        }
//    }

//    /**
//     * 优量汇模板插屏（只有模板插屏）
//     */
//    protected void getCustomInsertScreenAd() {
//        if (mAdView == null) {
//            return;
//        }
//        if (mAdView instanceof YlhTemplateInsertScreenAdView) {
//            mAdView.setAdListener(mAdListener);
//            mAdView.setPollingAdListener(mFirstAdListener);
//            ((YlhTemplateInsertScreenAdView) mAdView).loadTemplateInsertScreenAd(mActivity, mAppId, mAdId);
//        }
//    }


//    /**
//     * 请求图片广告
//     *
//     * @param adRequestTimeOut
//     */
//    protected void getAdByBigImg(final int adRequestTimeOut) {
//        LogUtils.d(TAG, "onADLoaded->请求优量汇广告");
//
//        NativeUnifiedAD mAdManager = new NativeUnifiedAD(mActivity, mAppId, mAdId.trim(), new NativeADUnifiedListener() {
//            @Override
//            public void onADLoaded(List<NativeUnifiedADData> nativeAdList) {
//                LogUtils.d(TAG, "onADLoaded->请求优量汇成功");
//
////                Boolean requestAdOverTime = AdsUtils.requestAdOverTime(adRequestTimeOut);
////                if (requestAdOverTime) {
////                    adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
////                    return;
////                }
//
//                if (nativeAdList == null || nativeAdList.isEmpty()) {
//                    firstAdError(1, "请求结果为空");
//                    return;
//                }
//                if (mAdView == null) {
//                    adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
//                    return;
//                }
//                mAdInfo = new AdInfo();
//                mAdInfo.setAdSource(Constants.AdType.YouLiangHui);
//                mAdInfo.setAdAppid(mAppId);
//                mAdInfo.setAdId(mAdId);
//                NativeUnifiedADData nativeUnifiedADData = nativeAdList.get(0);
//                if (nativeUnifiedADData != null) {
//                    String title = nativeUnifiedADData.getTitle();
//                    mAdInfo.setAdTitle(title);
//                    if (nativeUnifiedADData.isAppAd()) {
//                        mAdInfo.setAdClickType(1);
//                    } else {
//                        mAdInfo.setAdClickType(2);
//                    }
//                }
//                adSuccess(mAdInfo);
////                mAdView.setAdInfo(mAdInfo);
//                mAdView.parseYlhAd(nativeAdList);
//            }
//
//            @Override
//            public void onNoAD(AdError adError) {
//                LogUtils.d(TAG, "onNoAD->请求优量汇失败,ErrorCode:" + adError.getErrorCode() + ",ErrorMsg:" + adError.getErrorMsg());
//
//                adError(adError.getErrorCode(), adError.getErrorMsg());
//                firstAdError(adError.getErrorCode(), adError.getErrorMsg());
//
//
//            }
//        });
//        mAdManager.loadData(REQUEST_AD_COUNTS);
//    }

//    /**
//     * 请求开屏广告
//     */
//    protected void getAdBySplashAd() {
//        if (mAdView == null) {
//            return;
//        }
//        if (mAdView instanceof YlhSplashAdView) {
//            mAdView.setAppId(mAppId);
//            mAdView.setAdId(mAdId);
////            mAdView.setAdInfo(mAdInfo);
//            mAdView.setAdListener(mAdListener);
//            mAdView.setPollingAdListener(mFirstAdListener);
//            ((YlhSplashAdView) mAdView).loadSplashAd(mActivity, mAppId, mAdId);
//        }
//    }

//    /**
//     * 请求全屏视频广告
//     */
//    protected void getFullScreenVideoAd() {
//        if (mAdView == null) {
//            return;
//        }
//        if (mAdView instanceof YlhFullScreenVideoAdView) {
//            mAdView.setAppId(mAppId);
//            mAdView.setAdId(mAdId);
////            mAdView.setAdInfo(mAdInfo);
//            mAdView.setAdListener(mAdListener);
//            mAdView.setPollingAdListener(mFirstAdListener);
//            ((YlhFullScreenVideoAdView) mAdView).loadFullScreenVideoAd(mAppId, mAdId);
//        }
//    }

//    /**
//     * 请求激励视频广告
//     */
//    protected void getRewardVideoAd() {
//        if (mAdView == null) {
//            return;
//        }
//        if (mAdView instanceof YlhRewardVideoAdView) {
//            mAdView.setAppId(mAppId);
//            mAdView.setAdId(mAdId);
////            mAdView.setAdInfo(mAdInfo);
//            mAdView.setAdListener(mAdListener);
//            mAdView.setPollingAdListener(mFirstAdListener);
//            ((YlhRewardVideoAdView) mAdView).loadRewardVideoAd(mActivity, mAdId, orientation);
//        }
//    }

}
