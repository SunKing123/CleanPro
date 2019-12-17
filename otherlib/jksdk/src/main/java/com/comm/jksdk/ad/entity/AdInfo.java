package com.comm.jksdk.ad.entity;

import android.os.Parcel;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.qq.e.ads.nativ.NativeUnifiedADData;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.entity
 * @ClassName: AdInfo
 * @Description: 广告信息（用来回调给业务线）
 * @Author: fanhailong
 * @CreateDate: 2019/11/21 13:40
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/21 13:40
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class AdInfo extends BaseEntity{


    /**
     * 广告view
     */
    private View adView;
    /**
     * 模板广告的宽
     */
    private float width;
    /**
     * 激励视频用的的用户id
     */
    private String userId;

    /**
     * 激励广告奖励名称
     */
    private String rewardName;

    /**
     * 激励广告奖励金额
     */
    private int rewardAmount;

    /**
     * 优量汇图文广告
     */
    private NativeUnifiedADData nativeUnifiedADData;
    /**
     * 穿山甲原生广告
     */
    private TTNativeAd ttNativeAd;
    /**
     * 穿山甲模板插屏广告、信息流模板广告
     */
    private TTNativeExpressAd ttNativeExpressAd;
    /**
     * 穿山甲激励视频广告
     */
    private TTRewardVideoAd ttRewardVideoAd;
    /**
     * 穿山甲全屏视频广告
     */
    private TTFullScreenVideoAd ttFullScreenVideoAd;
    /**
     * 穿山甲图文类广告
     */
    private TTFeedAd ttFeedAd;

    /**
     * 穿山甲开屏广告
     */
    private TTSplashAd ttSplashAd;
    /**
     * 广告对应的appid
     */
    private String adAppid;
    /**
     * 广告id
     */
    private String adId;

    /**
     * 广告源
     */
    private String adSource;

    /**
     * 广告title（有些有，有些没有）
     */
    private String adTitle;

    /**
     * 广告样式
     */
    private String adStyle;

    /**
     * 广告请求超时时间
     */
    private int adRequestTimeOut;


    /**
     * 点击后的类型：1=下载；2=详情
     */
    private int adClickType;

    /**
     * 位置信息
     */
    private String mPosition;
    /**
     * 是否是预加载
     */
    private boolean mIsPreload;

    /**
     * 是否支持磁盘缓存
     */
    private boolean isDisk;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public boolean isDisk() {
        return isDisk;
    }

    public void setDisk(boolean disk) {
        isDisk = disk;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPreload() {
        return mIsPreload;
    }

    public void setIsPreload(boolean mIsPreload) {
        this.mIsPreload = mIsPreload;
    }

    public NativeUnifiedADData getNativeUnifiedADData() {
        return nativeUnifiedADData;
    }

    public void setNativeUnifiedADData(NativeUnifiedADData nativeUnifiedADData) {
        this.nativeUnifiedADData = nativeUnifiedADData;
    }

    public TTNativeAd getTtNativeAd() {
        return ttNativeAd;
    }

    public void setTtNativeAd(TTNativeAd ttNativeAd) {
        this.ttNativeAd = ttNativeAd;
    }

    public TTNativeExpressAd getTtNativeExpressAd() {
        return ttNativeExpressAd;
    }

    public void setTtNativeExpressAd(TTNativeExpressAd ttNativeExpressAd) {
        this.ttNativeExpressAd = ttNativeExpressAd;
    }

    public TTRewardVideoAd getTtRewardVideoAd() {
        return ttRewardVideoAd;
    }

    public void setTtRewardVideoAd(TTRewardVideoAd ttRewardVideoAd) {
        this.ttRewardVideoAd = ttRewardVideoAd;
    }

    public View getAdView() {
        return adView;
    }

    public void setAdView(View adView) {
        this.adView = adView;
    }

    public TTFullScreenVideoAd getTtFullScreenVideoAd() {
        return ttFullScreenVideoAd;
    }

    public void setTtFullScreenVideoAd(TTFullScreenVideoAd ttFullScreenVideoAd) {
        this.ttFullScreenVideoAd = ttFullScreenVideoAd;
    }

    public TTSplashAd getTtSplashAd() {
        return ttSplashAd;
    }

    public void setTtSplashAd(TTSplashAd ttSplashAd) {
        this.ttSplashAd = ttSplashAd;
    }

    public TTFeedAd getTtFeedAd() {
        return ttFeedAd;
    }

    public void setTtFeedAd(TTFeedAd ttFeedAd) {
        this.ttFeedAd = ttFeedAd;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public int getAdRequestTimeOut() {
        return adRequestTimeOut;
    }

    public void setAdRequestTimeOut(int adRequestTimeOut) {
        this.adRequestTimeOut = adRequestTimeOut;
    }

    public String getAdAppid() {
        return adAppid;
    }

    public void setAdAppid(String adAppid) {
        this.adAppid = adAppid;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdSource() {
        return adSource;
    }

    public void setAdSource(String adSource) {
        this.adSource = adSource;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdStyle() {
        return adStyle;
    }

    public void setAdStyle(String adStyle) {
        this.adStyle = adStyle;
    }

    public int getAdClickType() {
        return adClickType;
    }

    public void setAdClickType(int adClickType) {
        this.adClickType = adClickType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.adAppid);
        dest.writeString(this.adId);
        dest.writeString(this.adSource);
        dest.writeString(this.adTitle);
        dest.writeString(this.adStyle);
        dest.writeInt(this.adClickType);
    }

    public AdInfo() {
    }

    protected AdInfo(Parcel in) {
        this.adAppid = in.readString();
        this.adId = in.readString();
        this.adSource = in.readString();
        this.adTitle = in.readString();
        this.adStyle = in.readString();
        this.adClickType = in.readInt();
    }

    public static final Creator<AdInfo> CREATOR = new Creator<AdInfo>() {
        @Override
        public AdInfo createFromParcel(Parcel source) {
            return new AdInfo(source);
        }

        @Override
        public AdInfo[] newArray(int size) {
            return new AdInfo[size];
        }
    };

    @Override
    public String toString() {
        return "AdInfo{" +
                "adAppid='" + adAppid + '\'' +
                ", adId='" + adId + '\'' +
                ", adSource='" + adSource + '\'' +
                ", adTitle='" + adTitle + '\'' +
                ", adStyle='" + adStyle + '\'' +
                ", adClickType=" + adClickType +
                '}';
    }
}
