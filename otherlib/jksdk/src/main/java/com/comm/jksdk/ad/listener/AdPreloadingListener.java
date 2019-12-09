package com.comm.jksdk.ad.listener;

import com.comm.jksdk.ad.entity.AdInfo;

/**
 * @author: fanhailong
 * @date: 2019/9/7 12:09
 * @description: 广告预加载接口
 **/
public interface AdPreloadingListener {

    /**
     * 广告请求成功
     */
    void adSuccess(AdInfo info);

    /**
     * 广告失败
     * @param errorCode
     * @param errorMsg
     */
    void adError(AdInfo info, int errorCode, String errorMsg);


}
