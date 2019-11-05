package com.xiaoniu.cleanking.ui.newclean.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
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
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.chuanshanjia.TTAdManagerHolder;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.InsertScreenFinishPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jzvd.Jzvd;

/**
 * 插屏广告
 * 由功能结束页进入
 */
public class InsertScreenFinishActivity extends BaseActivity<InsertScreenFinishPresenter> implements View.OnClickListener {

    private ImageView iv_advert_logo, iv_advert, mCloseIv;
    private TextView tv_advert, tv_advert_content, mBtnDownload;
    private View mViewDownload, mViewContent, mErrorV, v_advert;
    private RoundProgressBar mProgressBar;

    private NativeUnifiedADData mNativeUnifiedADData;
    private NativeUnifiedAD mAdManager;
    private MediaView mMediaView;
    private NativeAdContainer mContainer;

    private String mTitle = "";
    private String mAdvertId = ""; //广告id
    private String mSecondAdvertId = ""; //备用id

    private H mHandler = new H();
    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;
    private static final String TAG = "AD_DEMO";

    //穿山甲相关 begin
    private FrameLayout mChuanShanJiaVideo;
    private TTAdNative mTTAdNative;
    //穿山甲相关 end

    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        mTitle = getIntent().getStringExtra("title");
        mPresenter.getScreentSwitch();

        mContainer = findViewById(R.id.native_ad_container);
        mCloseIv = findViewById(R.id.iv_close);
        mMediaView = findViewById(R.id.gdt_media_view);
        iv_advert_logo = findViewById(R.id.iv_advert_logo);
        iv_advert = findViewById(R.id.iv_advert);
        tv_advert = findViewById(R.id.tv_advert);
        tv_advert_content = findViewById(R.id.tv_advert_content);
        mViewDownload = findViewById(R.id.v_download);
        mBtnDownload = findViewById(R.id.tv_download);
        mViewContent = findViewById(R.id.v_content);
        mErrorV = findViewById(R.id.v_error);
        v_advert = findViewById(R.id.v_advert);
        mChuanShanJiaVideo = findViewById(R.id.v_video_chuanshanjia);
        mProgressBar = findViewById(R.id.skip_view);

        mCloseIv.setOnClickListener(this);
        findViewById(R.id.iv_close_error).setOnClickListener(this);

        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_advert.getLayoutParams();
        params.height = height * 2 / 3;
        v_advert.setLayoutParams(params);
        initChuanShanJia();
    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null == list || null == list.getData() || list.getData().size() <= 0) return;
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {

            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }

            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }

            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }

            } else if (getString(R.string.tool_chat_clear).contains(mTitle)) {//微信专情
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }

            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else if (getString(R.string.game_quicken).contains(mTitle)) { //游戏加速
                if (PositionId.KEY_GAME.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            } else { //立即清理
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey())) {
                    mAdvertId = switchInfoList.getAdvertId();
                    mSecondAdvertId = switchInfoList.getSecondAdvertId();
                }
            }
        }
        Log.d(TAG, "mAdvertId=" + mAdvertId);
        Log.d(TAG, "mSecondAdvertId=" + mSecondAdvertId);
        loadListAd();
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail(String message) {
       /* mViewContent.setVisibility(View.GONE);
        mBtnDownload.setVisibility(View.GONE);
        mErrorV.setVisibility(View.VISIBLE);
        ToastUtils.showShort(message);*/
        finish();
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListConnectError() {
        /*mViewContent.setVisibility(View.GONE);
        mBtnDownload.setVisibility(View.GONE);
        mErrorV.setVisibility(View.VISIBLE);
        ToastUtils.showShort("网络连接失败，请假查您的网络连接");*/
        finish();
    }

    /**
     * 优量汇广告
     */
    private void initNativeUnifiedAD() {
        mCloseIv.setVisibility(View.VISIBLE);
        mAdManager = new NativeUnifiedAD(this, PositionId.APPID, mSecondAdvertId, new NativeADUnifiedListener() {

            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "onNoAd error code: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "fail", NewCleanFinishActivity.currentPage, "screen_advertising");
                /*mViewContent.setVisibility(View.GONE);
                mBtnDownload.setVisibility(View.GONE);
                mErrorV.setVisibility(View.VISIBLE);*/
                finish();
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                if (ads != null && ads.size() > 0) {
                    Message msg = Message.obtain();
                    msg.what = MSG_INIT_AD;
                    mNativeUnifiedADData = ads.get(0);
                    msg.obj = mNativeUnifiedADData;
                    mHandler.sendMessage(msg);
                }
            }
        });
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
        mAdManager.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

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
        mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager.loadData(AD_COUNT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        String functionName = "";
        String functionPosition = "";
        switch (v.getId()) {
            case R.id.iv_close:
                StatisticsUtils.clickAD("full_ad_page_close_click", "全屏广告关闭按钮点击", "1", mSecondAdvertId, "优量汇", NewCleanFinishActivity.currentPage, NewCleanFinishActivity.currentPage, tv_advert.getText().toString().trim());
                finish();
                break;
            case R.id.iv_close_error:
                StatisticsUtils.clickAD("full_ad_page_close_click", "全屏广告关闭按钮点击", "1", mSecondAdvertId, "优量汇", NewCleanFinishActivity.currentPage, NewCleanFinishActivity.currentPage, tv_advert.getText().toString().trim());
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        NiuDataAPI.onPageStart("screen_advertising_view_page", "插屏广告浏览");
        super.onResume();
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData.resume();
        }

    }

    @Override
    protected void onPause() {
        Jzvd.releaseAllVideos();
        NiuDataAPIUtil.onPageEnd(NewCleanFinishActivity.currentPage, "screen_advertising", "screen_advertising_view_page", "插屏广告浏览");
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData.destroy();
        }
        if (null != mTimeHandler) {
            mTimeHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void netError() {

    }

    private class H extends Handler {
        public H() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_AD:
                    NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
                    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad
                                    .getPictureWidth(),
                            ad.getPictureHeight()));
                    initAd(ad);
                    Log.d(TAG, "eCPM = " + mNativeUnifiedADData.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData.getECPMLevel());
                    break;
                case MSG_VIDEO_START:
                    Log.d("AD_DEMO", "handleMessage");
                    iv_advert.setVisibility(View.GONE);
                    mMediaView.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    /**
     * 加载优量汇广告
     *
     * @param ad
     */
    private void initAd(final NativeUnifiedADData ad) {
        renderAdUi(ad);

        List<View> clickableViews = new ArrayList<>();
        // 所有广告类型，注册mDownloadButton的点击事件
        clickableViews.add(mViewDownload);
        ad.bindAdToView(this, mContainer, null, clickableViews);

        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mSecondAdvertId, "优量汇", "success", NewCleanFinishActivity.currentPage, "screen_advertising");
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mSecondAdvertId, "优量汇", NewCleanFinishActivity.currentPage, "screen_advertising", ad.getTitle());
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", mSecondAdvertId, "优量汇", NewCleanFinishActivity.currentPage, "screen_advertising", ad.getTitle());
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode() + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
                updateAdAction(mBtnDownload, ad);
            }
        });
        updateAdAction(mBtnDownload, ad);
        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(true) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(true) //点击视频是否跳转到详情页
                    .setEnableUserControl(false) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
            VideoOption videoOption = builder.build();
            // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
            ad.bindMediaView(mMediaView, videoOption,
                    // 视频相关回调
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.d(TAG, "onVideoInit: ");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.d(TAG, "onVideoLoading: ");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.d(TAG, "onVideoReady: ");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.d(TAG, "onVideoLoaded: ");

                        }

                        @Override
                        public void onVideoStart() {
                            Log.d(TAG, "onVideoStart: ");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.d(TAG, "onVideoResume: ");
                        }

                        @Override
                        public void onVideoCompleted() {
                            if (mNativeUnifiedADData != null) {
                                mNativeUnifiedADData.startVideo();
                            }

                            Log.d(TAG, "onVideoCompleted: ");
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Log.d(TAG, "onVideoError: ");
                        }

                        @Override
                        public void onVideoStop() {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
        }
    }

    public static void updateAdAction(TextView button, NativeUnifiedADData ad) {
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
                button.setText("失败");
                break;
            default:
                button.setText("浏览");
                break;
        }
    }

    // 获取广告资源并加载到UI
    private void renderAdUi(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        Log.d("AD_DEMO", "广告类型=" + patternType);
        tv_advert.setText(ad.getTitle());
        tv_advert_content.setText(ad.getDesc());
        if (!InsertScreenFinishActivity.this.isFinishing()) {
            GlideUtils.loadRoundImage(this, ad.getIconUrl(), iv_advert_logo, 20);
        }
        if (patternType == AdPatternType.NATIVE_VIDEO) {
            iv_advert.setVisibility(View.GONE);
        } else {
            GlideUtils.loadImage(this, ad.getImgUrl(), iv_advert);
        }
    }

    /**
     * 初始化穿山甲
     */
    private void initChuanShanJia() {
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    /**
     * 加载穿山甲广告
     */
    private void loadListAd() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "success", NewCleanFinishActivity.currentPage, "screen_advertising");
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdvertId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(3)
                .build();
        //调用feed广告异步请求接口
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "穿山甲加载失败=" + message);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", mAdvertId, "穿山甲", "fail", NewCleanFinishActivity.currentPage, "screen_advertising");
                initNativeUnifiedAD();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                //加载成功的回调 请确保您的代码足够健壮，可以处理异常情况；
                if (null == ads || ads.isEmpty()) return;
                Log.d(TAG, "穿山甲----广告请求成功--ads.size()=" + ads.size());
                mChuanShanJiaVideo.setVisibility(View.VISIBLE);
                mBtnDownload.setText(getString(R.string.download));
                tv_advert.setText(ads.get(0).getTitle());
                tv_advert_content.setText(ads.get(0).getDescription());

                TTImage icon = ads.get(0).getIcon();
                if (!InsertScreenFinishActivity.this.isFinishing() && icon != null && icon.isValid()) {
                    GlideUtils.loadRoundImage(InsertScreenFinishActivity.this, icon.getImageUrl(), iv_advert_logo, 20);
                }
                Log.d(TAG, "穿山甲--广告类型=" + ads.get(0).getImageMode());
                Log.d(TAG, "穿山甲--广告交互类型=" + ads.get(0).getInteractionType());
                if (ads.get(0).getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) { //视频
                    View video = ads.get(0).getAdView();
                    if (video != null) { //展示视频
                        if (video.getParent() == null) {
                            mChuanShanJiaVideo.removeAllViews();
                            mChuanShanJiaVideo.addView(video);
                        }
                    }
                    //视频播放监听
                    ads.get(0).setVideoAdListener(new TTFeedAd.VideoAdListener() {
                        @Override
                        public void onVideoLoad(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoLoad");
                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {
                            Log.d(TAG, "穿山甲视频---- onVideoError");
                        }

                        @Override
                        public void onVideoAdStartPlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdStartPlay");
                        }

                        @Override
                        public void onVideoAdPaused(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdPaused");
                        }

                        @Override
                        public void onVideoAdContinuePlay(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdContinuePlay");
                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {
                            Log.d(TAG, "穿山甲视频---- onProgressUpdate");
                        }

                        @Override
                        public void onVideoAdComplete(TTFeedAd ad) {
                            Log.d(TAG, "穿山甲视频---- onVideoAdComplete");
                        }
                    });
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.startAnimation(3000, new LinearInterpolator(), new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (null != mTimeHandler) {
                                mTimeHandler.sendEmptyMessageDelayed(0, 1000);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressBar.setVisibility(View.GONE);
                            mCloseIv.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                } else {
                    mCloseIv.setVisibility(View.VISIBLE);
                    TTImage image = ads.get(0).getImageList().get(0);
                    if (image != null && image.isValid()) {
                        GlideUtils.loadImage(InsertScreenFinishActivity.this, image.getImageUrl(), iv_advert);
                    }
                }

                //可以被点击的view, 也可以把convertView放进来意味item可被点击
                List<View> clickViewList = new ArrayList<>();
                clickViewList.add(mViewDownload);
                //触发创意广告的view（点击下载或拨打电话）
                List<View> creativeViewList = new ArrayList<>();
                creativeViewList.add(mBtnDownload);
                //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
                //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
                ads.get(0).registerViewForInteraction((ViewGroup) mViewDownload, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告" + ad.getTitle() + "被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", NewCleanFinishActivity.currentPage, "screen_advertising", ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告" + ad.getTitle() + "被创意按钮被点击");
                            StatisticsUtils.clickAD("ad_click", "广告点击", "1", mAdvertId, "穿山甲", NewCleanFinishActivity.currentPage, "screen_advertising", ad.getTitle());
                        }
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {
                        if (ad != null) {
                            Log.d(TAG, "广告" + ad.getTitle() + "展示");
                            StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", mAdvertId, "穿山甲", NewCleanFinishActivity.currentPage, "screen_advertising", ad.getTitle());
                        }
                    }
                });
            }
        });
    }

    /**
     * 3秒倒计时
     */
    private int time = 3;
    private Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time--;
            mProgressBar.setProgressStr(time + "s");
            if (time < 0) {
                mTimeHandler.removeMessages(0);
            }
            mTimeHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };

}
