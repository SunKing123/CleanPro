package com.comm.jksdk.ad.listener;

import com.comm.jksdk.ad.entity.AdInfo;

/**
 * @author: docking
 * @date: 2019/9/7 12:09
 * @description: todo ...
 **/
public interface AdListener {

    /**
     * 广告请求成功
     */
    void adSuccess(AdInfo info);

    /**
     * 广告展示
     */
    void adExposed(AdInfo info);

    /**
     * 广告点击
     */
    void adClicked(AdInfo info);

    /**
     * 广告关闭
     * @param info
     */
    default void adClose(AdInfo info){

    }

    /**
     * 广告失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(int errorCode, String errorMsg);


}
