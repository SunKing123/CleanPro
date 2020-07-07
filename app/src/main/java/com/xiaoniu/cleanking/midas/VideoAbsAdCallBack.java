package com.xiaoniu.cleanking.midas;

import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;


/**
 * Desc:广告回调抽象类
 * Date: 2020/5/18
 */
public abstract class VideoAbsAdCallBack extends AbsAdCallBack {

    private boolean isComplete = false;

    public VideoAbsAdCallBack() {
        isComplete = false;
    }

    @Override
    public void onAdClose(AdInfo adInfo) {
        super.onAdClose(adInfo);
        onAdClose(adInfo,isComplete);
    }
    @Override
    public void onAdVideoComplete(AdInfo adInfo) {
        super.onAdVideoComplete(adInfo);
        isComplete = true;
    }

    public void onAdClose(AdInfo adInfo, boolean isComplete) {

    }
}
