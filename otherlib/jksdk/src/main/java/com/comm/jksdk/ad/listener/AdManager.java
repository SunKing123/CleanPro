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

    void loadAd(Activity activity, String position, AdListener listener);

    void loadSplashAd(Activity activity, String position, AdListener listener);

    void loadVideoAd(Activity activity, String position, AdListener listener);

    void loadRewardVideoAd(Activity activity, String position, String userId, int orientation, AdListener listener);

    void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener);

    void loadCustomInsertScreenAd(Activity activity, String position, int showTimeSeconds, AdListener listener, String... pos);

    ViewGroup getAdView();
}
