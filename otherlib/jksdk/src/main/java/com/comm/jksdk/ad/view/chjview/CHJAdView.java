package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.AdsUtils;
import com.comm.jksdk.utils.CodeFactory;

import java.util.List;

/**
 * 穿山甲
 *
 * @author liupengbing
 * @date 2019/9/24
 */
public class CHJAdView extends CommAdView {
    private String style;

    /**
     * 优化百分比
     */
    protected String mProgress;

    public void setmProgress(String mProgress) {
        this.mProgress = mProgress;
    }

    //    /**
//     * 广告位ID
//     */
//    protected String mAdId = "";
//    /**
//     * 广告appid
//     */
//    protected String mAppId = "";
    // 广告请求数量
    private final static int REQUEST_AD_COUNTS = 1;

    protected Activity mActivity;
    /**
     * 视频广告方向横屏、竖屏
     */
    protected int orientation = TTAdConstant.VERTICAL;

    /**
     * 激励视频userid
     */
    private String userId = "";

    /**
     * 自渲染插屏广告是否是全屏
     */
    private boolean isFullScreen = false;

    /**
     * 自渲染插屏广告展示时长
     */
    private int showTimeSeconds = 3;

    private CommAdView mAdView = null;


    public CHJAdView(Context context, String style, String appId, String mAdId) {
        this(context, null, style, appId, mAdId);

    }

    public CHJAdView(Context context, Activity activity, String style, String appId, String mAdId) {
        super(context, style, mAdId);
        this.mAdId = mAdId;
        this.mContext = context;
        this.mActivity = activity;
        this.style = style;
        this.mAppId = appId;
        LogUtils.d(TAG, "广告样式------->style:" + style);
        if (Constants.AdStyle.BIG_IMG.equals(style)) {
            mAdView = new ChjBigImgAdView(mContext);
        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style)) { // 大图_带icon文字按钮
            mAdView = new ChjBigImgIcTvBtAdView(mContext);
        } else if (Constants.AdStyle.DATU_ICON_TEXT.equals(style)) { //大图_带icon文字
            mAdView = new ChjBigImgIcTvAdView(mContext);
        } else if (Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style)) { //大图_带icon文字按钮居中
            mAdView = new ChjBigImgIcTvBtCenterAdView(mContext);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON.equals(style)) { //大图带按钮（大图_下载播放按钮）
            mAdView = new ChjBigImgAdPlayLampView(mContext);
        } else if (Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)) { //大图带按钮带跑马灯
            mAdView = new ChjBigImgAdPlayLampView(mContext, true);
        } else if (Constants.AdStyle.BIG_IMG_NEST.equals(style)) { //大图嵌套美女图片
            mAdView = new ChjBigImgNestPlayLampView(mContext);
        } else if (Constants.AdStyle.BIG_IMG_NEST_LAMP.equals(style)) { //大图嵌套图片带跑马灯
            mAdView = new ChjBigImgNestPlayLampView(mContext, true);
        } else if (Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) { //假视频大图_01
            mAdView = new ChjBigImgFakeVideoAdView(mContext);
        } else if (Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style)) {
            mAdView = new ChjLeftImgRightTwoTextAdView(mContext);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
            mAdView = new ChjSplashAdView(mContext);
        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
            mAdView = new CsjFullScreenVideoView(mContext);
        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
            mAdView = new CsjRewardVideoAdView(mContext);
        } else if (Constants.AdStyle.CP.equals(style)) { //模板插屏
            mAdView = new CsjTemplateInsertScreenAdView(mContext);
        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) ) { //自定义插屏
            mAdView = new CsjCustomInsertScreenAdView(mContext);
        } else if (Constants.AdStyle.FULLSCREEN_CP_01.equals(style)) { //自定义全屏插屏
            mAdView = new CsjCustomInsertScreenAdView(mContext, true);
        }  else {
            //  all
            //所有样式都支持 随机展示
            //所有样式都支持 随机展示
            int num = AdsUtils.getRandomNum(2);
            LogUtils.w(TAG, "随机显示样式------->style:" + style + " getRandomNum:" + num);
//            Toast.makeText(mContext, "发现未定义样式:" + style + "正试图随机显示样式" + num, Toast.LENGTH_SHORT).show();
            switch (num) {
                case 0:
                    mAdView = new ChjLeftImgRightTwoTextAdView(mContext);
                    break;
                case 1:
                    mAdView = new ChjBigImgAdView(mContext);
                    break;
                default:
                    mAdView = new ChjLeftImgRightTwoTextAdView(mContext);
                    break;
            }

        }
        this.addView(mAdView);

    }

    public CHJAdView(Context context) {
        super(context);

    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public void setShowTimeSeconds(int showTimeSeconds) {
        this.showTimeSeconds = showTimeSeconds;
    }

    @Override
    public void requestAd(int requestType, int adRequestTimeOut) {
        if (mAdView != null) {
            mAdView.setAdListener(mAdListener);
        }
        if (mContext == null) {
            return;
        }
        if (requestType == 0) {
            //SDK
            getAdBySdk(adRequestTimeOut);
        } else {
            //api
        }

    }

    /**
     * 通过SDK获取广告
     */
    protected void getAdBySdk(final int adRequestTimeOut) {
//        String ylhAppid= SpUtils.getString(Constants.SPUtils.YLH_APPID,"");
//        if(TextUtils.isEmpty(ylhAppid)){
//            ylhAppid=Constants.YLH_APPID;
//        }
        LogUtils.d(TAG, "--------开始请求广告-------------广告样式------->style:" + style);
        if (Constants.AdStyle.BIG_IMG.equals(style) || Constants.AdStyle.DATU_ICON_TEXT.equals(style) || Constants.AdStyle.DATU_ICON_TEXT_BUTTON_CENTER.equals(style)
                || Constants.AdStyle.DATU_ICON_TEXT_BUTTON.equals(style) || Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style) || Constants.AdStyle.BIG_IMG_BUTTON_LAMP.equals(style)
                || Constants.AdStyle.BIG_IMG_BUTTON.equals(style) || Constants.AdStyle.BIG_IMG_NEST.equals(style) || Constants.AdStyle.BIG_IMG_NEST_LAMP.equals(style) || Constants.AdStyle.FAKE_VIDEO_IARGE_IMAGE.equals(style)) {
            //todo请求大图广告
            getImageAdBySdk(adRequestTimeOut);
        } else if (Constants.AdStyle.LEFT_IMG_RIGHT_TWO_TEXT.equals(style)) {
            //dodo
            getImageAdBySdk(adRequestTimeOut);
        } else if (Constants.AdStyle.OPEN_ADS.equals(style)) {
            getAdBySplashAd();
        } else if (Constants.AdStyle.FULL_SCREEN_VIDEO.equals(style)) {
            getFullScreenVideoAd();
        } else if (Constants.AdStyle.REWARD_VIDEO.equals(style)) {
            getRewardVideoAd();
        } else if (Constants.AdStyle.CUSTOM_CP.equals(style) || Constants.AdStyle.FULLSCREEN_CP_01.equals(style)) {
            getCustomInsertScreenAd();
        } else if (Constants.AdStyle.CP.equals(style)) {
            getTemplateInsertScreenAd();
        }
    }

    /**
     * 通过SDK获取图片广告
     */
    private void getImageAdBySdk(final int adRequestTimeOut) {
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get(mAppId);
        //step2:创建TTAdNative对象,用于调用广告请求接口
        TTAdNative mTTAdNative = ttAdManager.createAdNative(mContext);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(mContext);

        LogUtils.d(TAG, "onADLoaded->请求穿山甲广告");

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdId.trim())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1)
                .build();
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                LogUtils.d(TAG, "onNoAD->请求穿山甲失败,ErrorCode:" + i + ",ErrorMsg:" + s);

                if (s != null) {
                    adError(i, s);
                    firstAdError(i, s);

                }
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                LogUtils.d(TAG, "onADLoaded->请求穿山甲成功");
                Boolean requestAdOverTime = AdsUtils.requestAdOverTime(adRequestTimeOut);
                if (requestAdOverTime) {
                    adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    return;
                }
                if (list == null || list.isEmpty()) {
                    firstAdError(1, "请求结果为空");
                    return;
                }
                if (mAdView == null) {
                    onError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
                    return;
                }
                mAdInfo = new AdInfo();
                mAdInfo.setAdSource(Constants.AdType.ChuanShanJia);
                mAdInfo.setAdAppid(mAppId);
                mAdInfo.setAdId(mAdId);
                TTFeedAd ttFeedAd = list.get(0);
                if (ttFeedAd != null) {
                    String title = ttFeedAd.getTitle();
                    mAdInfo.setAdTitle(title);
                }
                adSuccess(mAdInfo);
                mAdView.setAdInfo(mAdInfo);
                mAdView.parseChjAd(list);
            }
        });
    }

    /**
     * 请求开屏广告
     */
    protected void getAdBySplashAd() {
        if (mAdView == null) {
            return;
        }
        TTAdManagerHolder.get(mAppId);
        if (mAdView instanceof ChjSplashAdView) {
            mAdView.setAppId(mAppId);
            mAdView.setAdId(mAdId);
            mAdView.setAdInfo(mAdInfo);
            mAdView.setAdListener(mAdListener);
            mAdView.setYlhAdListener(mFirstAdListener);
            ((ChjSplashAdView) mAdView).loadSplashAd(mAppId, mAdId);
        }
    }

    /**
     * 请求全屏视频广告
     */
    protected void getFullScreenVideoAd() {
        if (mAdView == null) {
            return;
        }
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);
        if (mAdView instanceof CsjFullScreenVideoView) {
            mAdView.setAppId(mAppId);
            mAdView.setAdId(mAdId);
            mAdView.setAdInfo(mAdInfo);
            mAdView.setAdListener(mAdListener);
            mAdView.setYlhAdListener(mFirstAdListener);
            ((CsjFullScreenVideoView) mAdView).loadFullScreenVideoAd(mActivity, mAdId, orientation);
        }
    }


    /**
     * 请求激励视频广告
     */
    protected void getRewardVideoAd() {
        if (mAdView == null) {
            return;
        }
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);

        if (mAdView instanceof CsjRewardVideoAdView) {
            mAdView.setAppId(mAppId);
            mAdView.setAdId(mAdId);
            mAdView.setAdInfo(mAdInfo);
            mAdView.setAdListener(mAdListener);
            mAdView.setYlhAdListener(mFirstAdListener);
            ((CsjRewardVideoAdView) mAdView).loadRewardVideoAd(mActivity, mAdId, userId, orientation);
        }
    }

    /**
     * 请求自渲染插屏广告
     */
    protected void getCustomInsertScreenAd() {
        if (mAdView == null) {
            return;
        }

        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);
        if (mAdView instanceof CsjCustomInsertScreenAdView) {
            mAdView.setAppId(mAppId);
            mAdView.setAdId(mAdId);
            mAdView.setAdInfo(mAdInfo);
            mAdView.setAdListener(mAdListener);
            mAdView.setYlhAdListener(mFirstAdListener);
            ((CsjCustomInsertScreenAdView) mAdView).setmProgress(mProgress);
            ((CsjCustomInsertScreenAdView) mAdView).loadCustomInsertScreenAd(mActivity, showTimeSeconds, mAdId);
        }
    }

    /**
     * 模板插屏
     */
    protected void getTemplateInsertScreenAd(){
        if (mAdView == null) {
            return;
        }

        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);
        if (mAdView instanceof CsjTemplateInsertScreenAdView) {
            mAdView.setAppId(mAppId);
            mAdView.setAdId(mAdId);
            mAdView.setAdInfo(mAdInfo);
            mAdView.setAdListener(mAdListener);
            mAdView.setYlhAdListener(mFirstAdListener);
            ((CsjTemplateInsertScreenAdView) mAdView).loadTemplateInsertScreenAd(mActivity, isFullScreen, showTimeSeconds, mAdId);
        }
    }
}
