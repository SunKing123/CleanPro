package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;

/**
 * 穿山甲激励视频广告<p>
 *
 * @author zixuefei
 * @since 2019/11/17 15:16
 */
public class CsjRewardVideoAdView extends CHJAdView {
    private TTAdNative mTTAdNative;

    public CsjRewardVideoAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.csj_full_screen_video_ad_view;
    }

    @Override
    public void initView() {
//        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);
//        //step3:创建TTAdNative对象,用于调用广告请求接口
//        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(mContext.getApplicationContext());
    }

    /**
     * 获取激励视屏广告并展示
     */
    public void loadRewardVideoAd(final Activity activity, String adId, String userId, int orientation) {
        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(mContext.getApplicationContext());
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.ChuanShanJia);
        mAdInfo.setAdAppid(mAppId);
        mAdInfo.setAdId(adId);
        LogUtils.d(TAG, "adId:" + adId + " userId:" + userId + " orientation:" + orientation);
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                //奖励的名称
                .setRewardName("金币")
                //奖励的数量
                .setRewardAmount(3)
                //用户id,必传参数
                .setUserID(userId)
                //附加参数，可选
                .setMediaExtra("media_extra")
                //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setOrientation(orientation)
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "rewardVideoAd error:" + code + " message:" + message);
//                adError(code, message);
                firstAdError(code, message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd mttRewardVideoAd) {
                LogUtils.d(TAG, "rewardVideoAd loaded");
                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示
                    mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                        @Override
                        public void onAdShow() {
                            adExposed(mAdInfo);
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            LogUtils.d(TAG, "rewardVideoAd bar click");
                            adClicked(mAdInfo);
                        }

                        @Override
                        public void onAdClose() {
                            adClose(mAdInfo);
                            LogUtils.d(TAG, "rewardVideoAd close");
                        }

                        //视频播放完成回调
                        @Override
                        public void onVideoComplete() {
                            LogUtils.d(TAG, "rewardVideoAd complete");
                            if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                                ((VideoAdListener) mAdListener).onVideoComplete(mAdInfo);
                            }
                        }

                        @Override
                        public void onVideoError() {
                            LogUtils.d(TAG, "rewardVideoAd error");
                            adError(CodeFactory.UNKNOWN, "视频错误");
                        }

                        //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                        @Override
                        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                            LogUtils.d(TAG, "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
                            onVideoRewardVerify(rewardVerify, rewardAmount, rewardName);
                        }

                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                    mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {

                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            LogUtils.d(TAG, "下载中，点击下载区域暂停");

                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            LogUtils.d(TAG, "下载暂停，点击下载区域继续");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            LogUtils.d(TAG, "下载失败，点击下载区域重新下载");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            LogUtils.d(TAG, "下载完成，点击下载区域重新下载");
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            LogUtils.d(TAG, "安装完成，点击下载区域打开");
                        }
                    });
                    mttRewardVideoAd.showRewardVideoAd(activity);
                    adSuccess(mAdInfo);
                } else {
                    onError(CodeFactory.UNKNOWN, "加载激励视频数据为空");
                }
            }
        });
    }

    private void onVideoRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
        if (mAdListener == null) {
            return;
        }

        if (mAdListener instanceof VideoAdListener) {
            ((VideoAdListener) mAdListener).onVideoRewardVerify(mAdInfo, rewardVerify, rewardAmount, rewardName);
        }
    }

}
