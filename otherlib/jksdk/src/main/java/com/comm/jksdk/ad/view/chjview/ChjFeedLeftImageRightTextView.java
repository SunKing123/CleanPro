package com.comm.jksdk.ad.view.chjview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.DownloadStatusController;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.AdsUtils;
import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by xinxiaolong on 2020/6/23.
 * email：xinxiaolong123@foxmail.com
 */
public class ChjFeedLeftImageRightTextView extends CHJAdView {
    private ViewGroup adContainer;
    private ImageView adImage;
    private TextView adTitle;
    private TextView adGlanceOver;
    private TextView adLook;
    private ImageView adLogo;

    private RequestOptions requestOptions;

    public ChjFeedLeftImageRightTextView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.chj_ad_feed_left_image_right_text_layout;
    }

    @Override
    public void initView() {
        if (mContext == null) {
            return;
        }

        adContainer = findViewById(R.id.rl_ad_item_root);
        adImage = findViewById(R.id.ad_item_image);
        adTitle = findViewById(R.id.ad_tv_title);
        adGlanceOver = findViewById(R.id.ad_tv_glanceOver);
        adLook = findViewById(R.id.ad_tv_look);
        adLogo = findViewById(R.id.ad_logo);


        requestOptions = new RequestOptions()
                .transforms(new RoundedCorners(DisplayUtil.dp2px(mContext, 3)))
                .error(R.color.returncolor);//图片加载失败后，显示的图片
    }


    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);
        TTFeedAd ttFeedAd = adInfo.getTtFeedAd();
        initAdData(ttFeedAd, adInfo);
    }

    /**
     * 初始化广告数据
     *
     * @param adData
     */
    private void initAdData(TTFeedAd adData, AdInfo adInfo) {
        if (mContext == null) {
            firstAdError(adInfo, 1, "mContext 为空");
            return;
        }

        if (adData.getImageMode() != TTAdConstant.IMAGE_MODE_LARGE_IMG) {
            firstAdError(adInfo, 1, "返回结果不是大图");
            return;
        }

        bindData(adContainer, adData, adInfo);
    }

    private void bindData(View convertView, TTFeedAd ad, AdInfo adInfo) {
        adLogo.setImageBitmap(ad.getAdLogo());
        TTImage image = ad.getImageList().get(0);
        if (image != null && image.isValid()) {
            try {
                Glide.with(mContext).load(image.getImageUrl())
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(adImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        adTitle.setText(AdsUtils.cropContent(19,ad.getDescription()));
        adGlanceOver.setText(AdsUtils.getRandomNumByDigit(6)+"人在浏览");
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adImage);
        clickViewList.add(adLook);
        clickViewList.add(adContainer);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adContainer);
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction((ViewGroup) convertView, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                if (ad != null) {
                    LogUtils.w(TAG, "deployAditem onAdClicked");
                    adClicked(adInfo);
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                if (ad != null) {
                    LogUtils.w(TAG, "deployAditem onAdClicked");
                    adClicked(adInfo);
                }
            }

            @Override
            public void onAdShow(TTNativeAd ttNativeAd) {
                if (ad != null) {
                    LogUtils.w(TAG, "广告" + ad.getTitle() + "展示");
                    adExposed(adInfo);
                }
            }
        });


        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                adLook.setText("下载");
                bindDownloadListener(ad);
                //绑定下载状态控制器
                bindDownLoadStatusController(ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                adLook.setText("立即查看");
            default:
//                nativeAdContainer.setVisibility(View.GONE);
//                ToastUtils.setToastStrShort("交互类型异常");
        }
    }

    private void bindDownLoadStatusController(final TTFeedAd ad) {
        final DownloadStatusController controller = ad.getDownloadStatusController();
        adContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.changeDownloadStatus();
//                    TToast.show(mContext, "改变下载状态");
                    LogUtils.d(TAG, "改变下载状态");
                }
            }
        });
    }

    private void bindDownloadListener(TTFeedAd ad) {
        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                if (!isValid()) {
                    return;
                }
                adLook.setText("下载");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
//                    tvCreativeContent.setText("下载中 percent: 0");
                } else {
//                    tvCreativeContent.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
                }
//                adViewHolder.mStopButton.setText("下载中");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
//                    tvCreativeContent.setText("下载中 percent: 0");
                } else {
//                    tvCreativeContent.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
                }
//                adViewHolder.mStopButton.setText("下载暂停");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adLook.setText("下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adLook.setText("立即打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adLook.setText("立即安装");
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            private boolean isValid() {
                return true;
            }
        };
        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
        ad.setDownloadListener(downloadListener); // 注册下载监听器

    }


}
