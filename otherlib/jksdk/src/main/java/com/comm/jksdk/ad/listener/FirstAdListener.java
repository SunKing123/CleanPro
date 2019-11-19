package com.comm.jksdk.ad.listener;

/**
 * @author: docking
 * @date: 2019/9/7 12:09
 * @description: todo ...
 **/
public interface FirstAdListener {


    /**
     * 优量汇广告失败  SDK中优量汇请求失败后请求穿山甲广告回掉接口 不向外提供
     * @param errorCode
     * @param errorMsg
     */
    void firstAdError(int errorCode, String errorMsg);

}
