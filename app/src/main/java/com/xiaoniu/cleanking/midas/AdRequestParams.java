package com.xiaoniu.cleanking.midas;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


import com.comm.jksdk.utils.DisplayUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xnad.sdk.ad.entity.AdInfo;

import java.lang.ref.WeakReference;

/**
 * Desc:广告请求参数
 * <p>
 * Author: AnYaBo
 * Date: 2020/5/16
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:anyabo@xiaoniu.com
 * Update Comments:
 *
 * @author anyabo
 */
public class AdRequestParams {
    /**
     * 上下文
     */
    Activity activity;
    /**
     * 广告id
     */
    String adId;
    /**
     * 超时时间
     */
    int adTimeout;
    /**
     * 事件名
     */
    String eventName;
    /**
     * 广告位信息
     */
    String adPosition;

    /**
     * 请求时间戳
     */
    long requestTimestamp;

    /**
     * 是否自渲染显示缓存
     */
    boolean needSelfRenderCache = true;
    /**
     * 跳过倒计时容器
     */
    View skipView;
    /**
     * 广告添加容器
     */
    ViewGroup viewContainer;
    /**
     * 在列表中的下标默认-1，建议在列表中传入对应下标或者0，否则会导致在onDetachedFromWindow中释放资源
     */
    private int index = -1;
    /**
     * 是否是缓存广告
     */
    private boolean isCache;
    /**
     * 是否跳过广告开关配置
     */
    private boolean isSkipSwitch = false;
    /**
     * 广告view的重复展示监听事件
     */
    private AdRepeatShowListener mAdRepeatShowListener;
    /**
     * 广告图片加载成功监听事件
     */
    private AdTemplateLoadListener mAdTemplateLoadListener;


    private AdInfo adInfo;


    private int viewWidth;

    public AdRequestParams(Builder builder) {
        if (builder.activityWeakReference != null) {
            this.activity = builder.activityWeakReference.get();
        }
        this.adId = builder.adId;
        this.eventName = builder.eventName;
        this.adPosition = builder.adPosition;
        this.adTimeout = builder.adTimeout;
        this.needSelfRenderCache = builder.needSelfRenderCache;
        this.skipView = builder.skipView;
        this.viewContainer = builder.viewContainer;
        this.index = builder.index;
        this.isCache = builder.isCache;
        this.requestTimestamp = builder.requestTimestamp;
        this.mAdRepeatShowListener = builder.mAdRepeatShowListener;
        this.mAdTemplateLoadListener = builder.mAdTemplateLoadListener;
        this.isSkipSwitch = builder.isSkipSwitch;
        this.adInfo = builder.adInfo;
        this.viewWidth = builder.viewWidth;
    }


    public Activity getActivity() {
        return activity;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public String getAdId() {
        return adId;
    }


    public String getEventName() {
        return eventName;
    }

    public String getAdPosition() {
        return adPosition;
    }

    public int getAdTimeout() {
        return adTimeout;
    }


    public boolean isNeedSelfRenderCache() {
        return needSelfRenderCache;
    }

    public View getSkipView() {
        return skipView;
    }

    public ViewGroup getViewContainer() {
        return viewContainer;
    }

    public int getIndex() {
        return index;
    }

    public boolean isCache() {
        return isCache;
    }

    public boolean isSkipSwitch() {
        return isSkipSwitch;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * 当前广告是否在列表中
     *
     * @return
     */
    public boolean isRecyclerViewList() {
        return index != -1;
    }

    public AdRepeatShowListener getAdCustomerViewListener() {
        return mAdRepeatShowListener;
    }

    public AdTemplateLoadListener getAdTemplateLoadListener() {
        return mAdTemplateLoadListener;
    }

    public static class Builder {
        /**
         * 弱引用activity上下文
         */
        private WeakReference<Activity> activityWeakReference;
        /**
         * 广告来源
         */
        private String adUnion;
        /**
         * 应用ID
         */
        String appId;
        /**
         * 广告id
         */
        String adId;
        /**
         * 优先级
         */
        String priority;
        /**
         * 超时时间
         */
        int adTimeout;
        /**
         * 事件名
         */
        String eventName;
        /**
         * 广告位
         */
        String adPosition;
        /**
         * 是否自渲染显示缓存
         */
        boolean needSelfRenderCache = true;
        /**
         * 跳过倒计时容器
         */
        View skipView;
        /**
         * 广告添加容器
         */
        ViewGroup viewContainer;
        /**
         * 在列表中的下标默认-1，建议在列表中传入对应下标或者0，否则会导致在onDetachedFromWindow中释放资源
         */
        private int index = 0;
        /**
         * 是否是缓存广告
         */
        private boolean isCache;
        /**
         * 是否跳过广告开关配置
         */
        private boolean isSkipSwitch;

        /**
         * 请求时间戳
         */
        private long requestTimestamp;
        /**
         * 广告view的重复展示监听事件
         */
        private AdRepeatShowListener mAdRepeatShowListener;
        /**
         * 广告图片加载成功监听事件
         */
        private AdTemplateLoadListener mAdTemplateLoadListener;

        private AdInfo adInfo;

        private int viewWidth;


        public Builder setActivity(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
            return this;
        }

        public Builder setAdUnion(String adUnion) {
            this.adUnion = adUnion;
            return this;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setAdId(String adId) {
            this.adId = adId;
            return this;
        }

        public Builder setPriority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder setAdPosition(String adPosition) {
            this.adPosition = adPosition;
            return this;
        }

        public Builder setAdTimeout(int adTimeout) {
            this.adTimeout = adTimeout;
            return this;
        }

        public Builder setNeedSelfRenderCache(boolean needSelfRenderCache) {
            this.needSelfRenderCache = needSelfRenderCache;
            return this;
        }

        public Builder setSkipView(View skipView) {
            this.skipView = skipView;
            return this;
        }

        public Builder setViewContainer(ViewGroup viewContainer) {
            this.viewContainer = viewContainer;
            return this;
        }

        public Builder setRequestTimestamp(long requestTimestamp) {
            this.requestTimestamp = requestTimestamp;
            return this;
        }

        public Builder setIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder setCache(boolean cache) {
            this.isCache = cache;
            return this;
        }

        public Builder setSkipSwitch(boolean skipSwitch) {
            this.isSkipSwitch = skipSwitch;
            return this;
        }

        public Builder setAdCustomerViewListener(AdRepeatShowListener l) {
            this.mAdRepeatShowListener = l;
            return this;
        }

        public Builder setAdTemplateLoadListener(AdTemplateLoadListener a) {
            this.mAdTemplateLoadListener = a;
            return this;
        }


        public Builder setAdInfo(AdInfo a) {
            this.adInfo = a;
            return this;
        }


        public Builder setViewWidth(int width){
            this.viewWidth =width;
            return this;
        }
        
        public Builder setViewWidthOffset(int offset){
            if(activityWeakReference==null||activityWeakReference.get()==null){
                return this;
            }
            this.viewWidth = ScreenUtils.getScreenWidth(activityWeakReference.get()) - DisplayUtil.dip2px(activityWeakReference.get(),offset);
            return this;
        }
        public AdRequestParams build() {
            return new AdRequestParams(this);
        }
    }

}
