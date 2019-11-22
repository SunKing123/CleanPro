package com.comm.jksdk.ad.view.ylhview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comm.jksdk.R;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.VideoAdListener;
import com.comm.jksdk.constant.Constants;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;
import com.comm.jksdk.utils.CollectionUtils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
 * 优量汇全屏视频广告view<p>
 *
 * @author zixuefei
 * @since 2019/11/16 14:01
 */
public class YlhFullScreenVideoAdView extends YlhAdView implements NativeADUnifiedListener {
    private final int MAX_DURATION = 60;
    // 与广告有关的变量，用来显示广告素材的UI
    private NativeUnifiedAD nativeUnifiedAD;
    private MediaView mMediaView;
    private ImageView mImagePoster;
    private TextView mTimeText;
    private Button mDownloadButton;
    private RelativeLayout mADInfoContainer;
    private NativeAdContainer mContainer;
    private NativeUnifiedADData mAdData;
    private long mTotalTime;

    protected YlhFullScreenVideoAdView(Context context) {
        super(context);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ylh_full_screen_video_ad_view;
    }

    @Override
    public void initView() {
        mMediaView = findViewById(R.id.gdt_media_view);
        mImagePoster = findViewById(R.id.img_poster);
        mADInfoContainer = findViewById(R.id.ad_info_container);
        mDownloadButton = findViewById(R.id.btn_download);
        mContainer = findViewById(R.id.native_ad_container);
        mTimeText = findViewById(R.id.time_text);
    }

    /**
     * 获取全屏视屏广告并加载
     */
    public void loadFullScreenVideoAd(String appId, String adId) {
        mAdInfo = new AdInfo();
        mAdInfo.setAdSource(Constants.AdType.YouLiangHui);
        mAdInfo.setAdAppid(appId);
        mAdInfo.setAdId(adId);

        nativeUnifiedAD = new NativeUnifiedAD(mContext, appId, adId, this);
        nativeUnifiedAD.setMaxVideoDuration(MAX_DURATION);

        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        // 本次拉回的视频广告，在用户看来是否为自动播放的
        nativeUnifiedAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO);

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        // 视频播放前，用户看到的广告容器是由SDK渲染的
        nativeUnifiedAD.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK);

        nativeUnifiedAD.loadData(1);
    }

    @Override
    public void onADLoaded(List<NativeUnifiedADData> list) {
        if (!CollectionUtils.isEmpty(list)) {
            adSuccess(mAdInfo);
            initAd(list.get(0));
        } else {
            firstAdError(CodeFactory.UNKNOWN, "请求广告数据为空");
        }
    }

    @Override
    public void onNoAD(AdError adError) {
        LogUtils.e(TAG, "--------YLH VIDEO ERROR--------");
        if (adError != null) {
//            adError(adError.getErrorCode(), adError.getErrorMsg());
            firstAdError(adError.getErrorCode(), adError.getErrorMsg());
        } else {
//            adError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
            firstAdError(CodeFactory.UNKNOWN, CodeFactory.getError(CodeFactory.UNKNOWN));
        }
    }

    /**
     * 绑定广告数据到UI
     */
    private void initAd(final NativeUnifiedADData ad) {
        mAdData = ad;
        renderAdUi(ad);
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(mDownloadButton);
        ad.bindAdToView(mContext, mContainer, null, clickableViews);
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                LogUtils.d(TAG, "onADExposed: ");
                adExposed(mAdInfo);
            }

            @Override
            public void onADClicked() {
                LogUtils.d(TAG, "onADClicked: " + " clickUrl: ");
                adClicked(mAdInfo);
            }

            @Override
            public void onADError(AdError error) {
                LogUtils.d(TAG, "onADError error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
                adError(error.getErrorCode(), error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                LogUtils.d(TAG, "onADStatusChanged: ");
                updateAdAction(mDownloadButton, ad);
            }
        });

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            mADInfoContainer.setBackgroundColor(Color.parseColor("#00000000"));
            mImagePoster.setVisibility(View.GONE);
            mMediaView.setVisibility(View.VISIBLE);

            ad.bindMediaView(mMediaView, createVideoOptions(), new NativeADMediaListener() {
                @Override
                public void onVideoInit() {
                    LogUtils.d(TAG, "onVideoInit: ");
                }

                @Override
                public void onVideoLoading() {
                    LogUtils.d(TAG, "onVideoLoading: ");
                }

                @Override
                public void onVideoReady() {
                    LogUtils.d(TAG, "onVideoReady: duration:" + mAdData.getVideoDuration());
                }

                @Override
                public void onVideoLoaded(int videoDuration) {
                    LogUtils.d(TAG, "onVideoLoaded: ");
                }

                @Override
                public void onVideoStart() {
                    LogUtils.d(TAG, "onVideoStart: duration:" + mAdData.getVideoDuration());
                    mADInfoContainer.setVisibility(View.VISIBLE);
//                    mTotalTime = ad.getVideoDuration();
//                    mTimeText.setVisibility(View.VISIBLE);
//                    mTimeText.setText((long) Math.floor(mTotalTime / 1000.0) + "s");
                }

                @Override
                public void onVideoPause() {
                    LogUtils.d(TAG, "onVideoPause: ");
                }

                @Override
                public void onVideoResume() {
                    LogUtils.d(TAG, "onVideoResume: ");
                }

                @Override
                public void onVideoCompleted() {
                    LogUtils.d(TAG, "onVideoCompleted: ");
                    removeTimeText();
                    if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                        ((VideoAdListener) mAdListener).onVideoComplete(mAdInfo);
                    }
                }

                @Override
                public void onVideoError(AdError error) {
                    LogUtils.d(TAG, "onVideoError: ");
                    removeTimeText();
                    if (error != null) {
                        adError(error.getErrorCode(), error.getErrorMsg());
                    } else {
                        adError(CodeFactory.UNKNOWN, "视频数据错误");
                    }
                }

                @Override
                public void onVideoStop() {
                    LogUtils.d(TAG, "onVideoStop");
                    removeTimeText();
                    if (mAdListener != null && mAdListener instanceof VideoAdListener) {
                        ((VideoAdListener) mAdListener).onVideoComplete(mAdInfo);
                    }
                }

                @Override
                public void onVideoClicked() {
                    LogUtils.d(TAG, "onVideoClicked");
                    adClicked(mAdInfo);
                }

                private void removeTimeText() {
                    mTimeText.setVisibility(View.GONE);
                }
            });
        } else {
            mADInfoContainer.setVisibility(View.VISIBLE);
            mADInfoContainer.setBackgroundColor(Color.parseColor("#999999"));
        }

        updateAdAction(mDownloadButton, ad);
    }


    /**
     * 填充广告到UI
     */
    private void renderAdUi(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
                || patternType == AdPatternType.NATIVE_VIDEO) {
            Glide.with(mContext).load(ad.getIconUrl()).into((ImageView) findViewById(R.id.img_logo));
            Glide.with(mContext).load(ad.getImgUrl()).into((ImageView) findViewById(R.id.img_poster));
            ((TextView) findViewById(R.id.text_title)).setText(ad.getTitle());
            ((TextView) findViewById(R.id.text_desc)).setText(ad.getDesc());
        } else if (patternType == AdPatternType.NATIVE_3IMAGE) {
            Glide.with(mContext).load(ad.getImgList().get(0)).into((ImageView) findViewById(R.id.img_1));
            Glide.with(mContext).load(ad.getImgList().get(1)).into((ImageView) findViewById(R.id.img_2));
            Glide.with(mContext).load(ad.getImgList().get(2)).into((ImageView) findViewById(R.id.img_3));
            ((TextView) findViewById(R.id.native_3img_title)).setText(ad.getTitle());
            ((TextView) findViewById(R.id.native_3img_desc)).setText(ad.getDesc());
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            Glide.with(mContext).load(ad.getImgUrl()).into((ImageView) findViewById(R.id.img_logo));
            ((TextView) findViewById(R.id.text_title)).setText(ad.getTitle());
            ((TextView) findViewById(R.id.text_desc)).setText(ad.getDesc());
        }
    }


    /**
     * 更新下载按钮进度，状态
     */
    public static void updateAdAction(Button button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("浏览");
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
                button.setText("浏览");
                break;
        }
    }

    private VideoOption createVideoOptions() {
        VideoOption.Builder builder = new VideoOption.Builder();
        builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
        builder.setAutoPlayMuted(true);
        builder.setNeedCoverImage(true);
        builder.setNeedProgressBar(true);
        builder.setEnableDetailPage(true);
        builder.setEnableUserControl(true);
        return builder.build();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdData != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mAdData.resume();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mAdData.destroy();
        }
    }

}
