package com.comm.jksdk.ad.view.ylhview;

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
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.utils.AdsUtils;
import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinxiaolong on 2020/6/23.
 * email：xinxiaolong123@foxmail.com
 */
public class YlhFeedLeftImageRightTextView extends YlhAdView {
    private ViewGroup adContainer;
    private NativeAdContainer container;
    private ImageView adImage;
    private TextView adTitle;
    private TextView adGlanceOver;
    private TextView adLook;

    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    public YlhFeedLeftImageRightTextView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_feed_left_image_right_text_layout;
    }

    @Override
    public void initView() {
        if (mContext == null) {
            return;
        }

        adContainer = findViewById(R.id.rl_ad_item_root);
        container = findViewById(R.id.ad_im_layout);
        adImage = findViewById(R.id.ad_item_image);
        adTitle = findViewById(R.id.ad_tv_title);
        adGlanceOver = findViewById(R.id.ad_tv_glanceOver);
        adLook = findViewById(R.id.ad_tv_look);

        int adlogoWidth = DisplayUtil.dp2px(mContext, 30);
        int adlogoHeight = DisplayUtil.dp2px(mContext, 12);
        adlogoParams = new FrameLayout.LayoutParams(adlogoWidth, adlogoHeight);
        adlogoParams.gravity = Gravity.BOTTOM;
        adlogoParams.bottomMargin = DisplayUtil.dp2px(mContext, 2);
        adlogoParams.leftMargin = DisplayUtil.dp2px(mContext, 2);
        requestOptions = new RequestOptions()
                .transforms(new RoundedCorners(DisplayUtil.dp2px(mContext, 3)))
                .error(R.color.returncolor);//图片加载失败后，显示的图片

    }

    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);
        NativeUnifiedADData nativeUnifiedADData = adInfo.getNativeUnifiedADData();
        initAdData(nativeUnifiedADData, adInfo);
    }

    private void initAdData(NativeUnifiedADData adData, AdInfo adInfo) {
        bindData(adData, adInfo);
    }

    private void bindData(final NativeUnifiedADData ad, AdInfo adInfo) {

        String imgUrl = ad.getImgUrl();
        try {
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(mContext).load(imgUrl)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(adImage);
            } else {
                // 不需要展示默认图片吗？
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adTitle.setText(AdsUtils.cropContent(19,ad.getDesc()));
        adGlanceOver.setText(AdsUtils.getRandomNumByDigit(6) + "人在浏览");
        adLook.setText("立即查看");
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adImage);

        try {
            ad.bindAdToView(adContainer.getContext(), container, adlogoParams, clickableViews);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adLook.setOnClickListener(v -> adImage.callOnClick());
        adTitle.setOnClickListener(v -> adImage.callOnClick());

        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                adExposed(adInfo);
            }

            @Override
            public void onADClicked() {
                adClicked(adInfo);
            }

            @Override
            public void onADError(AdError adError) {
                adError(adInfo, adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                updateAdAction(adLook, ad);
            }
        });
    }


    public void updateAdAction(TextView button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("详情");
            return;
        }
        switch (ad.getAppStatus()) {
            case 0:
                button.setText("下载");
                break;
            case 1:
                button.setText("启动");
                break;
            case 2:
                button.setText("更新");
                break;
            case 4:
                button.setText(ad.getProgress() + "%");
                break;
            case 8:
                button.setText("安装");
                break;
            case 16:
                button.setText("下载失败，重新下载");
                break;
            default:
                button.setText("详情");
                break;
        }
    }
}
