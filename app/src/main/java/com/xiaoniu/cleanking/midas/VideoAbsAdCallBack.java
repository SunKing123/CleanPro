package com.xiaoniu.cleanking.midas;


import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

/**
 * Desc:广告回调抽象类
 * Date: 2020/5/18
 */
public abstract class VideoAbsAdCallBack extends AbsAdBusinessCallback {

    private boolean isComplete = false;

    public VideoAbsAdCallBack() {
        isComplete = false;
    }

    @Override
    public void onAdClose(AdInfoModel adInfoModel) {
        super.onAdClose(adInfoModel);
        onAdClose(adInfoModel,isComplete);
    }

    @Override
    public void onAdVideoComplete(AdInfoModel adInfoModel) {
        super.onAdVideoComplete(adInfoModel);
        isComplete = true;
    }

    public void onAdClose(AdInfoModel adInfo, boolean isComplete) {

    }


}
