package com.comm.jksdk.ad.view.ylhview;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.comm.jksdk.R;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author liupengbing
 * @date 2019/9/24
 */
public class YlhBIgImgAdView extends YlhAdView {
    ImageView ivImg;
    TextView tvTitle;
    TextView tvSubTitle;
    TextView tvDownload;

    NativeAdContainer nativeAdContainer;
    LinearLayout llNativeAdLayout;
    //注：必须保证NativeAdContainer所有子view的可见性
    TextView tvAdBrowseCount;
    private RelativeLayout rlAdItemRoot;
    // 广告实体数据
    private NativeUnifiedADData mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;
    public YlhBIgImgAdView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_big_layout;
    }

    @Override
    public void initView() {

        ivImg=findViewById(R.id.iv_img);
        tvDownload=findViewById(R.id.tv_download);
        nativeAdContainer=findViewById(R.id.fl_native_ad_container);
        llNativeAdLayout=findViewById(R.id.ll_native_ad_layout);
        tvAdBrowseCount=findViewById(R.id.tv_ad_browse_count);
        rlAdItemRoot=findViewById(R.id.rl_ad_item_root);

        if(mContext==null){
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

    /**
     * 解析广告
     * @param nativeAdList
     */
    @Override
    public void parseYlhAd(List<NativeUnifiedADData> nativeAdList) {
        // 如果没有特定需求，随机取一个
        if (nativeAdList == null || nativeAdList.isEmpty()) {
            return;
        }
        int size = nativeAdList.size();
        int index = new Random().nextInt(size);
        NativeUnifiedADData adData = nativeAdList.get(index);
        if (adData == null) {
            adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            return;
        }

        this.mNativeADData = adData;



        initAdData(adData);
    }

    /**
     * 初始化广告数据
     * @param adData
     */
    private void initAdData(NativeUnifiedADData adData) {
        if(mContext==null){
            return;
        }
        rlAdItemRoot.setVisibility(VISIBLE);

        String imgUrl = adData.getImgUrl();

        try {
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(mContext).load(adData.getImgUrl())
                        .transition(new DrawableTransitionOptions().crossFade())
                        .apply(requestOptions)
                        .into(ivImg);
            } else {
                // 不需要展示默认图片吗？
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//         广告事件监听
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(llNativeAdLayout);
        try {
            adData.bindAdToView(mContext, nativeAdContainer, adlogoParams, clickableViews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adData.setNativeAdEventListener(new NativeADEventListener(){
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
//        updateClickDesc(tvDownload, adData);
    }
}
