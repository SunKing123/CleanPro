package com.xiaoniu.cleanking.midas;

import android.view.ViewGroup;

import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

/**
 * Created by xinxiaolong on 2020/7/7.
 * email：xinxiaolong123@foxmail.com
 */
public class CMAbsAdCallBack extends AbsAdCallBack {

    public void onAdClose(AdInfo adInfo) {
        super.onAdClose(adInfo);
        ViewGroup viewGroup = adInfo.mAdParameter.getViewContainer();
        viewGroup.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        viewGroup.getLayoutParams().height =ViewGroup.LayoutParams.WRAP_CONTENT;
    }
}
