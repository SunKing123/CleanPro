package com.comm.jksdk.ad.view.ylhview;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:     大图_带icon文字_激光按钮
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */


public class YlhBigImgIcTvFlickerBtAdView extends YlhAdView {
    // 广告实体数据
    private NativeUnifiedADData mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    LinearLayout nativeAdContainer;
    NativeAdContainer adImIayout; //优量汇容器
    ImageView brandIconIm; //广告商图标
    TextView adTitleTv; //广告的title
    TextView adDescribeTv; //广告描述
    ImageView adIm; //广告主体图片
    TextView downTb; //广告下载按钮
    private LottieAnimationView lottAdLownload; //lotto动画

    public YlhBigImgIcTvFlickerBtAdView(Context context) {
        super(context);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_big_ic_tv_fliker_bt_layout;
    }

    @Override
    public void initView() {

        nativeAdContainer = findViewById(R.id.rl_ad_item_root);
        adImIayout = findViewById(R.id.ad_im_layout);
        brandIconIm = findViewById(R.id.brand_icon_im);
        adTitleTv = findViewById(R.id.title_tv);
        adDescribeTv = findViewById(R.id.ad_des);
        adIm = findViewById(R.id.ad_im);
        downTb = findViewById(R.id.tv_ad_download);
        lottAdLownload = findViewById(R.id.lott_ad_download);
//        adLogo = findViewById(R.id.ad_logo);
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
        lottAdLownload.setAnimation("clean_btn_download.json");
        lottAdLownload.setImageAssetsFolder("images_btn_download");
        lottAdLownload.playAnimation();
    }

    @Override
    public void parseAd(AdInfo adInfo) {
        super.parseAd(adInfo);
        NativeUnifiedADData nativeUnifiedADData = adInfo.getNativeUnifiedADData();
        initAdData(nativeUnifiedADData, adInfo);
    }

    //    @Override
//    public void parseYlhAd(List<NativeUnifiedADData> nativeAdList) {
//        super.parseYlhAd(nativeAdList);
//        // 如果没有特定需求，随机取一个
//        if (nativeAdList == null || nativeAdList.isEmpty()) {
//            return;
//        }
////        int size = nativeAdList.size();
////        int index = new Random().nextInt(size);
//        NativeUnifiedADData adData = nativeAdList.get(0);
//        if (adData == null) {
//            adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
//            return;
//        }
//
//        this.mNativeADData = adData;
//
//
//
//        initAdData(adData);
//    }

    /**
     * 初始化广告数据
     *
     * @param adData
     */
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
                        .into(adIm);
            } else {
                // 不需要展示默认图片吗？
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adTitleTv.setText(ad.getTitle());
        adDescribeTv.setText(ad.getDesc());
        Glide.with(mContext).load(ad.getIconUrl()).into(brandIconIm);
        updateAdAction(downTb, ad);
        downTb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                adIm.callOnClick();
            }
        });
//         广告事件监听
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adIm);
        clickableViews.add(lottAdLownload);
        try {
            ad.bindAdToView(nativeAdContainer.getContext(), adImIayout, adlogoParams, clickableViews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ad.setNativeAdEventListener(new NativeADEventListener(){
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
//                updateClickDesc(tvDownload, mNativeADData);
                updateAdAction(downTb, ad);
            }
        });

    }

    public void updateAdAction(TextView button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("查看详情");
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
                button.setText("查看详情");
                break;
        }
    }
}
