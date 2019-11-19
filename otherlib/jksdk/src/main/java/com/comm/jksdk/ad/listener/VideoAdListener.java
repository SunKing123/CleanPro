package com.comm.jksdk.ad.listener;

/**
 * 视频广告回调器<p>
 *
 * @author zixuefei
 * @since 2019/11/17 13:52
 */
public interface VideoAdListener extends AdListener {
    /**
     * 视频恢复
     */
    void onVideoResume();

    /**
     * 激励视频获得激励回调
     */
    void onVideoRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName);
}
