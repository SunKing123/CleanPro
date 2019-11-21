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
import com.comm.jksdk.http.utils.LogUtils;
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


public class YlhLeftImgRightTwoTextAdView extends YlhAdView {
    ImageView ivImg;
    TextView tvTitle;
    TextView tvSubTitle;
    TextView tvDownload;

    NativeAdContainer nativeAdContainer;
    LinearLayout llNativeAdLayout;
    //注：必须保证NativeAdContainer所有子view的可见性
    TextView tvAdBrowseCount;

    // 广告实体数据
    private NativeUnifiedADData mNativeADData = null;
    private RequestOptions requestOptions;
    private FrameLayout.LayoutParams adlogoParams;
    private RelativeLayout rlAdItemRoot;

    public YlhLeftImgRightTwoTextAdView(Context context) {
        super(context);

    }

    @Override
    public int getLayoutId() {
        return R.layout.ylh_ad_left_img_right_two_text_layout;
    }


    @Override
    public void initView() {
        tvTitle=findViewById(R.id.tv_title);
        tvSubTitle=findViewById(R.id.tv_subTitle);
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
        if(tvTitle==null||mContext==null){
            return;
        }
        rlAdItemRoot.setVisibility(VISIBLE);
        long downloadCount = adData.getDownloadCount();
        String title = adData.getTitle();
        String desc = adData.getDesc();
        String imgUrl = adData.getImgUrl();
        // 标题
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        // 子标题
        if (!TextUtils.isEmpty(desc)) {
            tvSubTitle.setText(desc);
        }

//         预览描述
        String browseDesc = getBrowseDesc(downloadCount);
        tvAdBrowseCount.setText(browseDesc);

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
                LogUtils.w(TAG, "Ad onADExposed");
                adExposed(mAdInfo);
            }

            @Override
            public void onADClicked() {
                LogUtils.w(TAG, "Ad onADClicked");
                adClicked(mAdInfo);
            }

            @Override
            public void onADError(AdError adError) {
                firstAdError(adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                updateClickDesc(tvDownload, mNativeADData);
            }
        });
        updateClickDesc(tvDownload, adData);
    }



    private int getRandowNum() {
        //为2000到10000的随机数
        int num = (int) (Math.random() * 8000 + 2000);
        return num;
    }

    /**
     * 更新浏览人数量
     * @param downloadCount
     * @return
     */
    private String getBrowseDesc(long downloadCount) {
        String desc = "";
        if (downloadCount <= 0) {
            desc = getRandowNum() + "人浏览";
        } else if (0 < downloadCount && downloadCount < 10000) {
            desc = downloadCount + "人浏览";
        } else {
            desc = downloadCount / 10000 + "w+人浏览";
        }
        return desc;
    }

    /**
     * 更新点击描述
     * @param text
     * @param nativeUnifiedADData
     */
    protected void updateClickDesc(TextView text, NativeUnifiedADData nativeUnifiedADData) {
        if (!nativeUnifiedADData.isAppAd()) {
            text.setText("查看详情");
            return;
        }
//        获取应用状态，0:未开始下载；1:已安装；2:需要更新；4:下载中；8:下载完成；16:下载失败；32:下载暂停；64:下载删除
        switch (nativeUnifiedADData.getAppStatus()) {
            case 0:
                text.setText("未开始下载");
                break;
            case 1:
                text.setText("已安装");
                break;
            case 2:
                text.setText("需要更新");
                break;
            case 4:
                text.setText("下载中");
                break;
            case 8:
                text.setText("下载完成");
                break;
            case 16:
                text.setText("下载失败");
                break;
            case 32:
                text.setText("下载暂停");
                break;
            case 64:
                text.setText("下载删除");
                break;
            default:
                text.setText("查看详情");
                break;
        }
    }


}
