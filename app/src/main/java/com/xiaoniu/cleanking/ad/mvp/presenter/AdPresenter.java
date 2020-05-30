package com.xiaoniu.cleanking.ad.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoniu.cleanking.ad.AdUnionType;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.delegate.AdRequestDelegate;
import com.xiaoniu.cleanking.ad.delegate.CSJAdRequestDelegateIml;
import com.xiaoniu.cleanking.ad.delegate.YLHAdRequestDelegateIml;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdAgainRequestCallBack;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.contract.AdContract;
import com.xiaoniu.cleanking.ad.mvp.model.AdModel;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.common.utils.NetworkUtils;

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

    private static final String TAG = "AdPresenter";

    private AdModel adModel;
    private AdShowCallBack adShowCallBack;


    public AdPresenter() {
        this.adModel = new AdModel();
    }

    /**
     * 请求广告
     * @param adRequestParamentersBean
     * @param adShowCallBack
     */
    @Override
    public void requestAd(AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack) {
        requestAd(false, adRequestParamentersBean, adShowCallBack);
    }

    /**
     *
     * @param isSplash : true : is cold Splash
     * @param adRequestParamentersBean
     * @param adShowCallBack
     */
    public void requestAd(boolean isSplash, AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack) {
        this.adShowCallBack = adShowCallBack;
        if (adRequestParamentersBean == null || adShowCallBack == null || (!adRequestParamentersBean.adType.equals(AdType.Splash) && !NetworkUtils.isNetConnected())) {//开屏无网络走缓存数据（有效期1小时）
            Log.d(TAG, "!--->requestAd------没有获取到广告数据！--isSplash:"+isSplash);
            adShowCallBack.onFailure("没有获取到广告数据");
            return;
        }
        String auditSwitch=SPUtil.getString(adRequestParamentersBean.context, AppApplication.AuditSwitch, "1");
        if (auditSwitch.equals("0")) {
            Log.d(TAG, "!--->requestAd------过审开关关闭！  isSplash:"+isSplash);
            adShowCallBack.onFailure("过审开关关闭");
            return;
        }
        Log.d(TAG, "!--->requestAd----isSplash:"+isSplash);
        if (isSplash) {
            getNetAdInfo(adRequestParamentersBean);
        } else {
            try {
                arrangementData(adRequestParamentersBean);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
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
            if (adinfoBean.isOpen()) {
                dispatcherUnion(requestData(adinfoBean), adRequestParamentersBean);
            } else {
                adShowCallBack.onFailure("广告位关闭");
            }
        } else {
            //本地没有，走网络
            getNetAdInfo(adRequestParamentersBean);
            Log.d(TAG, "arrangementData--本地没有数据，开始走网络！");
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
            Log.d(TAG, "!--->dispatcherUnion---没有获取到广告数据！");
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
                    Log.d(TAG, "!--->getAdInfo--local--111--adMap isEmpty, db is not Empty");
                    adinfoBean = adList.get(0);
                }
            }
        } else {
            Log.d(TAG, "getAdInfo--local--222-- adMap is not Empty");
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
        // Log.d(TAG, "!--->getNetAdInfo----adRequestParamentersBean:"+adRequestParamentersBean);
        adModel.getSwitchInfoList(adRequestParamentersBean, new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                if (null != switchInfoList && null != switchInfoList.getData()) {
                    try {
                        Log.d(TAG, "!--->getNetAdInfo-----getData-----");
                        AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
                        SwitchInfoList.DataBean adinfo = getAdInfo(AppHolder.getInstance().getSwitchInfoMap(), adRequestParamentersBean);
                        AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
                        if (adinfo == null || adShowCallBack == null || !adinfo.isOpen()) {
                            Log.d(TAG, "!--->getNetAdInfo------没有获取到广告数据！");
                            adShowCallBack.onFailure("广告没有获取到广告数据");
                        } else {
                            dispatcherUnion(requestData(adinfo), adRequestParamentersBean);
                        }
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
                try {
                    //开屏场景接口超时情况特殊处理(提升ADRequest/AdShow数量)
                    if (adRequestParamentersBean.adType.equals(AdType.Splash)) {
                        SwitchInfoList.DataBean adinfo = getAdInfo(AppHolder.getInstance().getSwitchInfoMap(), adRequestParamentersBean);
                        if (adinfo == null || adShowCallBack == null || !adinfo.isOpen()) {
                            adShowCallBack.onFailure("暂无本地缓存数据或开关关闭");
                        } else {
                            dispatcherUnion(requestData(adinfo), adRequestParamentersBean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });
    }

}
