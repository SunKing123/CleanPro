package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.http.utils.LogUtils;

/**
 * 穿山甲
 *
 * @author liupengbing
 * @date 2019/9/24
 */
public class CHJAdView extends CommAdView {
    private String style;

    /**
     * 优化百分比
     */
    protected String mProgress;


    protected Activity mActivity;
    /**
     * 视频广告方向横屏、竖屏
     */
    protected int orientation = TTAdConstant.VERTICAL;

    /**
     * 激励视频userid
     */
    private String userId = "";

    /**
     * 自渲染插屏广告是否是全屏
     */
    private boolean isFullScreen = false;

    /**
     * 自渲染插屏广告展示时长
     */
    private int showTimeSeconds = 3;

    private CommAdView mAdView = null;


    public CHJAdView(Context context, String style, String appId, String mAdId) {
        this(context, null, style, appId, mAdId);

    }

    public CHJAdView(Context context, Activity activity, String style, String appId, String mAdId) {
        super(context, style, mAdId);
        this.mAdId = mAdId;
        this.mContext = context;
        this.mActivity = activity;
        this.style = style;
        this.mAppId = appId;
        LogUtils.d(TAG, "广告样式------->style:" + style);
        this.addView(mAdView);
    }

    public CHJAdView(Context context) {
        super(context);

    }


    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

}
