package com.xiaoniu.cleanking.ad.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.ad.AdUnionType;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.delegate.AdRequestDelegate;
import com.xiaoniu.cleanking.ad.delegate.CSJAdRequestDelegateIml;
import com.xiaoniu.cleanking.ad.delegate.YLHAdRequestDelegateIml;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.contract.AdContract;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad
 * @ClassName: AdPresenter
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:52
 */
public class AdPresenter extends RxPresenter<AdContract.View, AdModel> implements AdContract.Presenter, AdAgainRequestCallBack {
    private AdModel adModel;
    private AdShowCallBack adShowCallBack;


    public AdPresenter() {
        this.adModel = new AdModel();
    }

    /**
     * 请求广告
     *
     * @param adRequestParamentersBean
     */
    @Override
    public void requestAd(AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack) {
        this.adShowCallBack = adShowCallBack;
        if (adRequestParamentersBean == null || adShowCallBack == null) {
            Log.d("ad_status", "广告没有获取到广告数据！");
            adShowCallBack.onFailure("没有获取到广告数据");
            return;
        }
        try {
            arrangementData(adRequestParamentersBean);
        } catch (Exception e) {
            Log.d("ad_status", e.getMessage());
        }
    }

    /**
     * 取本地，取网络数据
     *
     * @param adRequestParamentersBean
     */
    private void arrangementData(AdRequestParamentersBean adRequestParamentersBean) throws Exception {
        Map<String, SwitchInfoList.DataBean> adMap = AppHolder.getInstance().getSwitchInfoMap();
        SwitchInfoList.DataBean adinfoBean = getAdInfo(adMap, adRequestParamentersBean);
        if (adinfoBean != null) {
            dispatcherUnion(requestData(adinfoBean), adRequestParamentersBean);
        } else {
            //本地没有，走网络
            getNetAdInfo(adRequestParamentersBean);
            Log.d("ad_status", "本地没有数据，开始走网络！");

        }
    }

    /**
     * 广告商分发
     *
     * @param adRequest
     * @param adBean
     * @throws InterruptedException
     */
    private void dispatcherUnion(Deque<AdRequestBean> adRequest, AdRequestParamentersBean adBean) throws InterruptedException {
        if (CollectionUtils.isEmpty(adRequest)) {
            Log.d("ad_status", "广告没有获取到广告数据！");
            adShowCallBack.onFailure("没有获取到广告数据");
            return;
        }

        AdRequestBean adRequestBean = adRequest.poll();
        AdRequestDelegate adRequestDelegate = null;
        //广告联盟
        switch (adRequestBean.getAdvertSource()) {
            case AdUnionType.YOU_LIANG_HUI:
                adRequestDelegate = new YLHAdRequestDelegateIml(adModel, this);
                break;
            case AdUnionType.CHUAN_SHAN_JIA:
                adRequestDelegate = new CSJAdRequestDelegateIml(adModel, this);
                break;
        }

        dispatcherType(adRequest, adRequestDelegate, adRequestBean, adBean);
    }

    /**
     * 类型分发
     *
     * @param adRequest
     * @param adRequestDelegate
     * @param adRequestBean
     * @param adBean
     * @throws InterruptedException
     */
    private void dispatcherType(Deque<AdRequestBean> adRequest, AdRequestDelegate adRequestDelegate, AdRequestBean adRequestBean, AdRequestParamentersBean adBean) throws InterruptedException {
        //目前只支持了开屏和模板，新加类型需要在加一下
        switch (adBean.adType) {
            case Splash:
                adRequestDelegate.requestSplashAdvertising(adBean, adRequest, adRequestBean, adShowCallBack);
                break;
            case Template:
                adRequestDelegate.requestTemplateAdvertising(adBean, adRequest, adRequestBean, adShowCallBack);
                break;
        }
    }

    /**
     * 二次请求回调
     *
     * @param adRequest
     * @param adRequestParamentersBean
     */
    @Override
    public void againRequestCallback(Deque<AdRequestBean> adRequest, AdRequestParamentersBean adRequestParamentersBean) {
        try {
            if (CollectionUtils.isEmpty(adRequest) || null == adRequestParamentersBean) {
                return;
            }
            dispatcherUnion(adRequest, adRequestParamentersBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 优先级数据拼装
     *
     * @param adInfo
     * @return
     * @throws InterruptedException
     */
    private Deque<AdRequestBean> requestData(SwitchInfoList.DataBean adInfo) throws Exception {
        Deque<AdRequestBean> request = new ArrayDeque<AdRequestBean>();
        request.add(new AdRequestBean(adInfo.getAdvertId(), adInfo.getAdvertSource()));
        if (!TextUtils.isEmpty(adInfo.getSecondAdvertSource()) && !TextUtils.isEmpty(adInfo.getSecondAdvertId())) {
            request.add(new AdRequestBean(adInfo.getSecondAdvertId(), adInfo.getSecondAdvertSource()));
        }
        return request;
    }

    /**
     * 获取本地数据
     *
     * @param adMap
     * @param adRequestParamentersBean
     * @return
     */
    private SwitchInfoList.DataBean getAdInfo(Map<String, SwitchInfoList.DataBean> adMap, AdRequestParamentersBean adRequestParamentersBean) {
        SwitchInfoList.DataBean adinfoBean = null;

        if (CollectionUtils.isEmpty(adMap)) {
            if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                List<SwitchInfoList.DataBean> adList = ApplicationDelegate.getAppDatabase().adInfotDao().getAdInfo(adRequestParamentersBean.configKey, adRequestParamentersBean.advertPosition);
                if (!CollectionUtils.isEmpty(adList)) {
                    adinfoBean = adList.get(0);
                }
            }
        } else {
            adinfoBean = adMap.get(adRequestParamentersBean.configKey + adRequestParamentersBean.advertPosition);
        }
        return adinfoBean;
    }

    /**
     * 拉去忘了配置再次发起分发请求
     *
     * @param adRequestParamentersBean
     */
    private void getNetAdInfo(AdRequestParamentersBean adRequestParamentersBean) {
        adModel.getSwitchInfoList(adRequestParamentersBean.context, new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                if (null != switchInfoList) {
                    AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
                    try {
                        dispatcherUnion(requestData(getAdInfo(AppHolder.getInstance().getSwitchInfoMap(), adRequestParamentersBean)), adRequestParamentersBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                    AppHolder.getInstance().setSwitchInfoMap(ApplicationDelegate.getAppDatabase().adInfotDao().getAll());
                }
            }
        });
    }

}
