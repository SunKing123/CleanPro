package com.xiaoniu.cleanking.ad.delegate;

import android.util.Log;
import android.view.View;

import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.utils.CollectionUtils;

import java.util.Deque;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.delegate
 * @ClassName: AdRequestDelegateIml
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/12 16:55
 */

public abstract class AdRequestDelegateIml implements AdRequestDelegate {
    protected final String TAG = "ad_status";

    protected AdModel adModel;
    protected AdAgainRequestCallBack adAgainRequestCallBack;

    public AdRequestDelegateIml(AdModel adModel, AdAgainRequestCallBack adAgainRequestCallBack) {
        this.adModel = adModel;
        this.adAgainRequestCallBack = adAgainRequestCallBack;
    }

    /**
     * addview
     *
     * @param adRequestParamentersBean
     * @param adView
     * @param adShowCallBack
     */
    protected void addAdView(AdRequestParamentersBean adRequestParamentersBean, View adView, AdShowCallBack adShowCallBack) {
        if (adRequestParamentersBean == null || adRequestParamentersBean.adContainer == null || adView == null) {
            if (adShowCallBack != null) {
                adShowCallBack.onFailure("串行请求失败");
            }
            return;
        }
        adRequestParamentersBean.adContainer.removeAllViews();
        adRequestParamentersBean.adContainer.addView(adView);
    }

    /**
     * @param adRequest
     * @param adRequestParamentersBean
     * @param adShowCallBack
     */
    protected void adError(Deque<AdRequestBean> adRequest, AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack) {
        if (CollectionUtils.isEmpty(adRequest)) {
            if (null != adShowCallBack) {
                adShowCallBack.onFailure("本次串行所有广告都没有加载成功");
                Log.d(TAG, "本次串行所有广告都没有加载成功");
                return;
            }

        } else {
            if (null != adAgainRequestCallBack)
                adAgainRequestCallBack.againRequestCallback(adRequest, adRequestParamentersBean);
        }
        if (null != adShowCallBack)
            adShowCallBack.onErrorCallback("广告加载失败");
    }


}
