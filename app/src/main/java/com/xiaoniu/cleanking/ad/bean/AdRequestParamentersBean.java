package com.xiaoniu.cleanking.ad.bean;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.bean
 * @ClassName: AdRequestParamentersBean
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/11 17:50
 */

public class AdRequestParamentersBean {
    //界面标识id
    public String configKey;
    //广告位 id
    public String advertPosition;
    //上下文
    public Context context;
    // 广告类型
    public AdType adType;

    //开屏 跳过时间
    public int fetchDelay;
    //广告的view壳
    public ViewGroup adContainer;
    //开屏 跳过view
    public LinearLayout skipContainer;
    //期望个性化模板广告view的size,单位dp
    public int viewWidth;
    //期待高度
    public int viewHeight;

    //信息流下标
    public int index;

    //埋点 上一级界面id 产品定义
    public String sourcePageId;
    //埋点 界面id 产品定义
    public String currentPageId;


    /**
     * 普通单个广告使用
     *
     * @param configKey
     * @param advertPosition
     * @param context
     * @param adType
     * @param viewWidth      宽度  单位dp
     * @param viewHeight     如果高度需要自定义，请给0
     */
    public AdRequestParamentersBean(String configKey, String advertPosition, Context context, AdType adType, int viewWidth, int viewHeight,String sourcePageId, String currentPageId) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
        this.context = context;
        this.adType = adType;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.sourcePageId = sourcePageId;
        this.currentPageId = currentPageId;
    }

    /**
     * 开屏 使用
     *
     * @param context
     * @param adContainer
     * @param skipContainer
     * @param configKey
     * @param advertPosition
     * @param adType
     * @param fetchDelay
     */
    public AdRequestParamentersBean(Context context, ViewGroup adContainer, LinearLayout skipContainer, String configKey, String advertPosition, AdType adType, int fetchDelay,String sourcePageId, String currentPageId) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
        this.context = context;
        this.adType = adType;
        this.fetchDelay = fetchDelay;
        this.adContainer = adContainer;
        this.skipContainer = skipContainer;
        this.sourcePageId = sourcePageId;
        this.currentPageId = currentPageId;
    }

    /**
     * 信息流使用
     *
     * @param configKey
     * @param advertPosition
     * @param context
     * @param adType
     * @param viewWidth
     * @param viewHeight
     */
    public AdRequestParamentersBean(String configKey, String advertPosition, Context context, AdType adType, int viewWidth, int viewHeight, int index, String sourcePageId, String currentPageId) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
        this.context = context;
        this.adType = adType;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.index = index;
        this.sourcePageId = sourcePageId;
        this.currentPageId = currentPageId;
    }
}
