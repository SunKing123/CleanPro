package com.comm.jksdk.ad.view.ylhview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.comm.jksdk.ad.view.chjview.CHJAdView;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * 优量汇激励视频广告<p>
 *
 * @author zixuefei
 * @since 2019/11/17 15:16
 */
public class YlhRewardVideoAdView extends CHJAdView {

    public static final String REWARD_VIDEO_AD_POS_ID_SUPPORT_H = "2090845242931421";//支持竖版出横版视频
    public static final String REWARD_VIDEO_AD_POS_ID_UNSUPPORT_H = "5040942242835423";//不支持竖版出横版视频

    private RewardVideoAD rewardVideoAD;

    public YlhRewardVideoAdView(Context context) {
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
     * 获取激励视屏广告并展示
     */
    public void loadRewardVideoAd(final Activity activity, String adId, int orientation) {
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        if (orientation == 1) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == 2) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.YouLiangHui);
        mAdInfo.setAdAppid(mAppId);
        mAdInfo.setAdId(adId);
        // 1. 初始化激励视频广告
        rewardVideoAD = new RewardVideoAD(activity, mAppId, REWARD_VIDEO_AD_POS_ID_UNSUPPORT_H, new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                //广告加载成功标志
                if (rewardVideoAD != null) {
                    rewardVideoAD.showAD();
                }
                adSuccess(mAdInfo);
            }

            @Override
            public void onVideoCached() {
                //视频素材缓存成功，可在此回调后进行广告展示

            }

            @Override
            public void onADShow() {

            }

            @Override
            public void onADExpose() {
                adExposed(mAdInfo);
            }

            @Override
            public void onReward() {

            }

            @Override
            public void onADClick() {
                adClicked(mAdInfo);
            }

            @Override
            public void onVideoComplete() {

            }

            @Override
            public void onADClose() {
                adClose(mAdInfo);
            }

            @Override
            public void onError(AdError adError) {
                firstAdError(adError.getErrorCode(), adError.getErrorMsg());
            }
        });
        // 2. 加载激励视频广告
        rewardVideoAD.loadAD();
    }

}
