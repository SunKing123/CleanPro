package com.comm.jksdk.ad.listener;

/**
 * @author: docking
 * @date: 2019/9/7 12:09
 * @description: todo ...
 **/
public interface AdListener {

    /**
     * 广告请求成功
     */
    void adSuccess();

    /**
     * 广告展示
     */
    void adExposed();

    /**
     * 广告点击
     */
    void adClicked();

    /**
     * 广告失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(int errorCode, String errorMsg);

}
