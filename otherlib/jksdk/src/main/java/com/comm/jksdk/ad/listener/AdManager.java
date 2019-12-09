package com.comm.jksdk.ad.listener;

import android.app.Activity;
import android.view.ViewGroup;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.listener
 * @ClassName: AdManager
 * @Description: 广告管理类接口
 * @Author: fanhailong
 * @CreateDate: 2019/11/11 18:44
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/11 18:44
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface AdManager {

    /**
     * 加载图文类广告
     * @param activity
     * @param position
     * @param listener
     */
    void loadAd(Activity activity, String position, AdListener listener);

    /**
     * 预加载图文类广告
     * @param activity
     * @param position
     * @param listener
     */
    void preloadingAd(Activity activity, String position, AdPreloadingListener listener);

    void loadSplashAd(Activity activity, String position, AdListener listener);

    /**
     * 加载全屏视频广告
     * @param activity
     * @param position
     * @param listener
     */
    void loadVideoAd(Activity activity, String position, AdListener listener);

    /**
     * 预加载全屏视频广告
     * @param activity
     * @param position
     * @param listener
     */
    void preloadingVideoAd(Activity activity, String position, AdPreloadingListener listener);

    /**
     * 加载激励广告
     * @param activity
     * @param position
     * @param userId
     * @param orientation
     * @param listener
     */
    void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, AdListener listener);

    /**
     * 预加载激励广告
     * @param activity
     * @param position
     * @param userId
     * @param orientation
     * @param listener
     */
    void preloadingRewardVideoAd(Activity activity, String position, String userId, int orientation, AdPreloadingListener listener);

    /**
     * 加载激励广告
     * @param activity
     * @param position
     * @param userId
     * @param orientation
     * @param listener
     */
    void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, String rewardName, int rewardAmount, AdListener listener);

    /**
     * 预加载激励广告
     * @param activity
     * @param position
     * @param userId
     * @param orientation
     * @param listener
     */
    void preloadingRewardVideoAd(Activity activity, String position, String userId, int orientation, String rewardName, int rewardAmount, AdPreloadingListener listener);

    void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener);

    void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener, String... pos);
}
