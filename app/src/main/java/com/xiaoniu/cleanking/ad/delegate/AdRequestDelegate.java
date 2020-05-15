package com.xiaoniu.cleanking.ad.delegate;

import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;

import java.util.Deque;

/**
 * 广告请求实现抽象
 */
public interface AdRequestDelegate {

    /**
     *
     * @param adRequestParamentersBean
     * @param adRequest
     * @param adRequestBean
     */
    void requestSplashAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack);

    void requestTemplateAdvertising(AdRequestParamentersBean adRequestParamentersBean, Deque<AdRequestBean> adRequest, AdRequestBean adRequestBean, AdShowCallBack adShowCallBack);
        /**
         * 回收创建的广告对象，根据具体需要实现
         */
    default void destroy() {
    }
}
