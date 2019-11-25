package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;
import com.comm.jksdk.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 穿山甲外部插屏广告<p>
 *
 * @author zixuefei
 * @since 2019/11/18 11:24
 */
public class CsjExternalInsertScreenAdView extends CHJAdView {
    private Activity activity;
    private TTAdNative mTTAdNative;
    protected boolean isFullScreen;

    private TextView adTitleTv, adDes, adDownloadBtn;
    private ImageView adCover;
    private RoundImageView appIcon;
    private LinearLayout adContainer;

    public CsjExternalInsertScreenAdView(Context context) {
        this(context, false);
    }

    public CsjExternalInsertScreenAdView(Context context, boolean isFullScreen) {
        super(context);
        this.isFullScreen = isFullScreen;
    }

    @Override
    public int getLayoutId() {
        return R.layout.csj_external_cp_ad_view;
    }

    @Override
    public void initView() {
        adContainer = findViewById(R.id.insert_ad_container);
        adTitleTv = findViewById(R.id.title_tv);
        adDes = findViewById(R.id.full_screen_insert_ad_des);
        adDownloadBtn = findViewById(R.id.full_screen_insert_ad_download);
        adCover = findViewById(R.id.full_screen_insert_ad_view);
        appIcon = findViewById(R.id.full_screen_insert_ad_app_icon);
    }

    /**
     * 获取插屏广告并展示
     */
    public void loadExternalInsertScreenAd(final Activity activity, final int showTimeSeconds, String adId) {
        if (activity == null) {
            throw new NullPointerException("loadCustomInsertScreenAd activity is null");
        }
        LogUtils.d(TAG, "isFullScreen:" + isFullScreen + " adId:" + adId + " showTimeSeconds:" + showTimeSeconds);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        this.activity = activity;
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.ChuanShanJia);
        mAdInfo.setAdAppid(mAppId);
        mAdInfo.setAdId(adId);
        mAdInfo.setAdStyle(Constants.AdStyle.EXTERNAL_CP_01);
        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(activity.getApplicationContext());
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "loadNativeAd code:" + code + " message:" + message);
//                adError(code, message);
                firstAdError(code, message);
//                Toast.makeText(mContext, "loadCustomInsertScreenAd error:" + code + " message:" + message, Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (!CollectionUtils.isEmpty(ads)) {
                    adSuccess(mAdInfo);
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        return;
                    }
                    bindAdData(ads.get(0), isFullScreen, showTimeSeconds);
                } else {
//                    adError(CodeFactory.UNKNOWN, "请求广告数据为空");
                    firstAdError(CodeFactory.UNKNOWN, "请求广告数据为空");
                }
            }
        });
    }

    /**
     * 展示插屏广告
     */
    private void bindAdData(TTNativeAd ttNativeAd, boolean isFullScreen, int showTimeSeconds) {
        LogUtils.d(TAG, "bindAdData:" + isFullScreen + " showTimeSeconds:" + showTimeSeconds);
        if (ttNativeAd.getImageList() != null && !ttNativeAd.getImageList().isEmpty()) {
            TTImage image = ttNativeAd.getImageList().get(0);
            if (image != null && image.isValid()) {
                Glide.with(getContext()).load(image.getImageUrl()).into(adCover);
            }
        }

        TTImage icon = ttNativeAd.getIcon();
        if (icon != null && icon.isValid()) {
            Glide.with(getContext()).load(icon.getImageUrl()).into(appIcon);
        }

        adTitleTv.setText(ttNativeAd.getTitle());
        adDes.setText(ttNativeAd.getDescription());
        adDownloadBtn.setText(ttNativeAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? "下载" : "查看详情");

        bindAd(ttNativeAd);
    }

    private void bindAd(TTNativeAd ttNativeAd) {
        //可以被点击的view, 比如标题、icon等,点击后尝试打开落地页，也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adTitleTv);
        clickViewList.add(adDes);
        clickViewList.add(adCover);
        clickViewList.add(appIcon);
        //触发创意广告的view（点击下载或拨打电话），比如可以设置为一个按钮，按钮上文案根据广告类型设定提示信息
        List<View> creativeViewList = new ArrayList<>();
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
        //creativeViewList.add(nativeView);
        creativeViewList.add(adDownloadBtn);

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ttNativeAd.registerViewForInteraction(adContainer, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "被点击");
                }
                adClicked(mAdInfo);
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "被创意按钮被点击");
                }
                adClicked(mAdInfo);
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    LogUtils.d(TAG, "广告" + ad.getTitle() + "展示");
                }
                adExposed(mAdInfo);
            }
        });
    }
}
