package com.xiaoniu.cleanking.ad.interfaces;

import android.view.View;

import com.xiaoniu.cleanking.ad.bean.TemplateAdResponse;

import java.util.List;


public interface AdShowCallBack {

    /**
     * 曝光
     */
    default void onAdShowCallBack(boolean isSucess, View view){}

    /**
     * 信息流渲染显示
     * @param index
     * @param view
     */
    default void onAdListShowCallBack(int index, View view){}

    /**
     * 广告点击
     */
    default void onAdClickCallback() {
    }

    /**
     * 关闭广告
     * 对于开屏相当于（ onAdDismissed  广告关闭时调用，可能是用户关闭或者展示时间到。此时一般需要跳过开屏的 Activity，进入应用内容页面）
     */
    default void onCloseCallback(View view) {
    }

    /**
     * 关闭信息流中的item
     * @param index
     */
    default void onCloseCallback(int index) {
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间，单位是 ms  开屏用到
     *
     * @param l
     */
    default void onAdTickCallback(long l) {
    }

    /**
     * 跳过
     */
    default void onAdSkipCallback() {
    }


    /**
     * 单次请求联盟失败
     *
     * @param message
     */
    default void onErrorCallback(String message) {
    }

    /**
     * 一次串行请求结束且最终失败失败
     *
     * @param message
     */
    default void onFailure(String message) {
    }

}
