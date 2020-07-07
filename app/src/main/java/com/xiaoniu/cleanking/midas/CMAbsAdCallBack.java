package com.xiaoniu.cleanking.midas;

import android.view.ViewGroup;

import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

/**
 * Created by xinxiaolong on 2020/7/7.
 * email：xinxiaolong123@foxmail.com
 */
public class CMAbsAdCallBack extends AbsAdCallBack {

    //关闭广告的时候，设置广告容器的高度为0
    public void onAdClose(AdInfo adInfo) {
        super.onAdClose(adInfo);
        if(adInfo!=null&&adInfo.mAdParameter!=null&&adInfo.mAdParameter.getViewContainer()!=null){
            ViewGroup viewGroup = adInfo.mAdParameter.getViewContainer();
            viewGroup.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            viewGroup.getLayoutParams().height =ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }
}
