package com.comm.jksdk.ad.view.chjview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.DisplayUtil;
import com.comm.jksdk.widget.TopRoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:     穿山甲大图播放按钮跑马灯样式
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */


public class ChjBigImgAdPlayLampView extends CHJAdView {
    // 广告实体数据
    private TTFeedAd mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    RelativeLayout nativeAdContainer;
    ImageView brandIconIm; //广告商图标
    TextView adTitleTv; //广告的title
    TextView adDescribeTv; //广告描述
    TopRoundImageView adIm; //广告主体图片
    TextView downTb; //广告下载按钮
    View animationView; //跑马灯的view
    ImageView adLogo;

    private boolean isLamp; //是否带走马灯

    private AnimationDrawable mAnimationDrawable;

    public ChjBigImgAdPlayLampView(Context context) {
        this(context, false);
    }

    public ChjBigImgAdPlayLampView(Context context, boolean isLamp) {
        super(context);
        this.isLamp = isLamp;
        initAnimation();
    }

    protected void initAnimation(){
        if (!isLamp) {
            return;
        }
        if (isLamp) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                animationView.setBackground(getResources().getDrawable(R.drawable.anim_ad));
                if (animationView.getBackground() instanceof AnimationDrawable) {
                    mAnimationDrawable = (AnimationDrawable) animationView.getBackground();
                }
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.chj_ad_big_paly_lamp_layout;
    }

    @Override
    public void initView() {

        nativeAdContainer = findViewById(R.id.rl_ad_item_root);
        brandIconIm = findViewById(R.id.brand_icon_im);
        adTitleTv = findViewById(R.id.ad_title_tv);
        adDescribeTv = findViewById(R.id.ad_describe_tv);
        adIm = findViewById(R.id.ad_im);
        animationView = findViewById(R.id.animation_lamp);
        downTb = findViewById(R.id.down_bt);
        adLogo = findViewById(R.id.ad_logo);
        if (mContext == null) {
            return;
        }
        int adlogoWidth = DisplayUtil.dp2px(mContext, 30);
        int adlogoHeight = DisplayUtil.dp2px(mContext, 12);
        adlogoParams = new FrameLayout.LayoutParams(adlogoWidth, adlogoHeight);
        adlogoParams.gravity = Gravity.BOTTOM;
        adlogoParams.bottomMargin = DisplayUtil.dp2px(mContext, 8);
        adlogoParams.leftMargin = (int) (getContext().getResources().getDimension(R.dimen.common_ad_img_width_98dp) - adlogoWidth);
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
        if ( mContext == null) {
            firstAdError(adInfo, 1, "mContext 为空");
            return;
        }

        if (adData.getImageMode() != TTAdConstant.IMAGE_MODE_LARGE_IMG) {
            firstAdError(adInfo, 1, "返回结果不是大图");
            return;
        }
        nativeAdContainer.setVisibility(VISIBLE);

        bindData(nativeAdContainer,adData, adInfo);

    }

    private void bindData(View convertView, TTFeedAd ad, AdInfo adInfo) {
        TTImage icon = ad.getIcon();
        if (icon != null && icon.isValid()) {
            Glide.with(mContext).load(icon.getImageUrl())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(requestOptions)
                    .into(brandIconIm);
        }
        adLogo.setImageBitmap(ad.getAdLogo());
        adTitleTv.setText(ad.getTitle());
        adDescribeTv.setText(ad.getDescription());
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adIm);
        clickViewList.add(downTb);
        clickViewList.add(nativeAdContainer);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(nativeAdContainer);
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
                    LogUtils.w(TAG, "deployAditem onAdCreativeClick");
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

        TTImage image = ad.getImageList().get(0);
        if (image != null && image.isValid()) {
            try {
                Glide.with(mContext).load(image.getImageUrl())
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(adIm);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                downTb.setText("下载");
                bindDownloadListener(ad);
                //绑定下载状态控制器
                bindDownLoadStatusController(ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                downTb.setText("详情");
            default:
//                nativeAdContainer.setVisibility(View.GONE);
//                ToastUtils.setToastStrShort("交互类型异常");
        }

    }
    private void bindDownLoadStatusController( final TTFeedAd ad) {
        final DownloadStatusController controller = ad.getDownloadStatusController();
        nativeAdContainer.setOnClickListener(new OnClickListener() {
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

    private void bindDownloadListener( TTFeedAd ad) {
        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                if (!isValid()) {
                    return;
                }
                downTb.setText("下载");
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
                downTb.setText("下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                downTb.setText("立即打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                downTb.setText("立即安装");
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            private boolean isValid() {
                return true;
            }
        };
        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
        ad.setDownloadListener(downloadListener); // 注册下载监听器

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isLamp && mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isLamp && mAnimationDrawable != null) {
            mAnimationDrawable.start();
        }
    }
}
