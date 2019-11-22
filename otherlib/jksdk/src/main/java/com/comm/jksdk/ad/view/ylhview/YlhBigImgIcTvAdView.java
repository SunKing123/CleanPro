package com.comm.jksdk.ad.view.ylhview;

import android.content.Context;
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
  * @Description:     大图_带icon文字
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */


public class YlhBigImgIcTvAdView extends CommAdView {
    // 广告实体数据
    private NativeUnifiedADData mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;

    RelativeLayout nativeAdContainer;
    NativeAdContainer adImIayout; //优量汇容器
    ImageView brandIconIm; //广告商图标
    TextView adTitleTv; //广告的title
    TextView adDescribeTv; //广告描述
    ImageView adIm; //广告主体图片

    public YlhBigImgIcTvAdView(Context context) {
        super(context);

    }


    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_big_ic_tv_layout;
    }

    @Override
    public void initView() {

        nativeAdContainer = findViewById(R.id.rl_ad_item_root);
        adImIayout = findViewById(R.id.ad_im_layout);
        brandIconIm = findViewById(R.id.brand_icon_im);
        adTitleTv = findViewById(R.id.ad_title_tv);
        adDescribeTv = findViewById(R.id.ad_describe_tv);
        adIm = findViewById(R.id.ad_im);

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
        NativeUnifiedADData adData = nativeAdList.get(0);
        if (adData == null) {
            firstAdError(1, "请求结果为空");
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
        Glide.with(mContext).load(ad.getIconUrl()).into(brandIconIm);
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
                adExposed(mAdInfo);
            }

            @Override
            public void onADClicked() {
                adClicked(mAdInfo);
            }

            @Override
            public void onADError(AdError adError) {
                firstAdError(adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
//                updateClickDesc(tvDownload, mNativeADData);
            }
        });

    }

}
