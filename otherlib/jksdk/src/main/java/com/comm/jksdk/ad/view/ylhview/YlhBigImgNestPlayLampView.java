package com.comm.jksdk.ad.view.ylhview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.view.CommAdView;
import com.comm.jksdk.utils.DisplayUtil;
import com.comm.jksdk.widget.RoundImageView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:     大图美女_01_彩带
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */


public class YlhBigImgNestPlayLampView extends CommAdView {
    // 广告实体数据
    private NativeUnifiedADData mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    RelativeLayout nativeAdContainer;

    NativeAdContainer adImIayout; //优量汇容器
    RoundImageView custonIm; //嵌套进来的美女图片
    TextView adDescribeTv; //广告描述
    ImageView adIm; //广告主体图片
    TextView adTitleTv; //广告的title
    View animationView; //跑马灯的view

    private boolean isLamp; //是否带走马灯

    private AnimationDrawable mAnimationDrawable;

    public YlhBigImgNestPlayLampView(Context context) {
        this(context, false);
    }

    public YlhBigImgNestPlayLampView(Context context, boolean isLamp) {
        super(context);
        this.isLamp = isLamp;
        initAnimation();
    }

    protected void initAnimation(){
        if (!isLamp) {
            return;
        }
        if (isLamp) {
            animationView.setBackground(getResources().getDrawable(R.drawable.anim_ad));
            if (animationView.getBackground() instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) animationView.getBackground();
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_big_nest_paly_lamp_layout;
    }

    @Override
    public void initView() {

        nativeAdContainer = findViewById(R.id.rl_ad_item_root);
        adImIayout = findViewById(R.id.ad_im_layout);
        adTitleTv = findViewById(R.id.ad_title_tv);
        adDescribeTv = findViewById(R.id.ad_describe_tv);
        adIm = findViewById(R.id.ad_im);
        animationView = findViewById(R.id.animation_lamp);
        custonIm = findViewById(R.id.custon_im);


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
    public void parseYlhAd(List<NativeUnifiedADData> nativeAdList) {
        super.parseYlhAd(nativeAdList);
        // 如果没有特定需求，随机取一个
        if (nativeAdList == null || nativeAdList.isEmpty()) {
            firstAdError(1, "请求结果为空");
            return;
        }
        int size = nativeAdList.size();
        int index = new Random().nextInt(size);
        NativeUnifiedADData adData = nativeAdList.get(index);
        if (adData == null) {
            return;
        }

        this.mNativeADData = adData;



        initAdData(adData);
    }

    /**
     * 初始化广告数据
     *
     * @param adData
     */
    private void initAdData(NativeUnifiedADData adData) {
        if ( mContext == null) {
            firstAdError(1, "mContext 为空");
            return;
        }
        nativeAdContainer.setVisibility(VISIBLE);

        bindData(adData);

    }

    private void bindData(final NativeUnifiedADData ad) {
        String imgUrl = ad.getImgUrl();
        try {
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(mContext).load(imgUrl)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(adIm);
            } else {
                // 不需要展示默认图片吗？
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adTitleTv.setText(ad.getTitle());
        adDescribeTv.setText(ad.getDesc());
        nativeAdContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                adIm.callOnClick();
            }
        });
//         广告事件监听
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adIm);
        try {
            ad.bindAdToView(nativeAdContainer.getContext(), adImIayout, adlogoParams, clickableViews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ad.setNativeAdEventListener(new NativeADEventListener(){
            @Override
            public void onADExposed() {
                adExposed();
            }

            @Override
            public void onADClicked() {
                adClicked();
            }

            @Override
            public void onADError(AdError adError) {

            }

            @Override
            public void onADStatusChanged() {
            }
        });

    }

//    public void updateAdAction(TextView button, NativeUnifiedADData ad) {
//        if (!ad.isAppAd()) {
//            button.setText("详情");
//            return;
//        }
//        switch (ad.getAppStatus()) {
//            case 0:
//                button.setText("下载");
//                break;
//            case 1:
//                button.setText("启动");
//                break;
//            case 2:
//                button.setText("更新");
//                break;
//            case 4:
//                button.setText(ad.getProgress() + "%");
//                break;
//            case 8:
//                button.setText("安装");
//                break;
//            case 16:
//                button.setText("下载失败，重新下载");
//                break;
//            default:
//                button.setText("详情");
//                break;
//        }
//    }

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
