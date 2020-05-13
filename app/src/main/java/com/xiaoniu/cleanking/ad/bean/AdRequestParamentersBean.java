package com.xiaoniu.cleanking.ad.bean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.TTAdNative;
import com.qq.e.ads.splash.SplashADListener;
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
    public RoundProgressBar skipContainer;
    //期望个性化模板广告view的size,单位dp
    public int viewWidth;
    //期待高度
    public int viewHeight;

    public int index;



    public AdRequestParamentersBean(Context context, String configKey, String advertPosition,  AdType adType) {
        this.context = context;
        this.configKey=configKey;
        this.advertPosition=advertPosition;
        this.adType = adType;
    }

    /**
     * 开屏 使用
     * @param context
     * @param adContainer
     * @param skipContainer
     * @param configKey
     * @param advertPosition
     * @param adType
     * @param fetchDelay
     */
    public AdRequestParamentersBean( Context context,ViewGroup adContainer, RoundProgressBar skipContainer,String configKey, String advertPosition, AdType adType, int fetchDelay) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
        this.context = context;
        this.adType = adType;
        this.fetchDelay = fetchDelay;
        this.adContainer = adContainer;
        this.skipContainer = skipContainer;
    }

    /**
     *
     * @param configKey
     * @param advertPosition
     * @param context
     * @param adType
     * @param viewWidth
     * @param viewHeight
     */
    public AdRequestParamentersBean(String configKey, String advertPosition, Context context, AdType adType,  int viewWidth, int viewHeight,int index) {
        this.configKey = configKey;
        this.advertPosition = advertPosition;
        this.context = context;
        this.adType = adType;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.index=index;
    }
}
