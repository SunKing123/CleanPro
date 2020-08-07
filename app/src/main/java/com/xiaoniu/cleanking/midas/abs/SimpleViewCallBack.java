package com.xiaoniu.cleanking.midas.abs;

import android.view.View;
import android.widget.FrameLayout;

import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

/**
 * Desc:
 * <p>
 * Author: AnYaBo
 * Date: 2020/8/7
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:anyabo@xiaoniu.com
 * Update Comments:
 *
 * @author anyabo
 */
public class SimpleViewCallBack extends AbsAdBusinessCallback {
    FrameLayout adContainer;

    public SimpleViewCallBack(FrameLayout adContainer) {
        this.adContainer = adContainer;
    }

    @Override
    public void onAdLoaded(AdInfoModel adInfoModel) {
        super.onAdLoaded(adInfoModel);
        if (adContainer != null){
            adInfoModel.addInContainer(adContainer);
        }
    }


    @Override
    public void onAdLoadError(String errorCode, String errorMsg) {
        super.onAdLoadError(errorCode, errorMsg);
    }


    @Override
    public void onAdExposure(AdInfoModel adInfoModel) {
        super.onAdExposure(adInfoModel);
    }

    @Override
    public void onAdClick(AdInfoModel adInfoModel) {
        super.onAdClick(adInfoModel);
    }

    @Override
    public void onAdClose(AdInfoModel adInfoModel) {
        super.onAdClose(adInfoModel);
        if (adContainer != null){
            adContainer.setVisibility(View.GONE);
        }
    }

}
