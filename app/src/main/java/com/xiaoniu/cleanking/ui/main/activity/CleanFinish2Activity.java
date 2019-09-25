package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.fragment.CleanFinishWebFragment;
import com.xiaoniu.cleanking.ui.main.interfac.AppBarStateChangeListener;
import com.xiaoniu.cleanking.ui.news.fragment.NewsFragment;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.List;
import java.util.Random;

import cn.jzvd.Jzvd;

import static android.view.View.GONE;

/**
 * 清理完成 显示咨询
 */
public class CleanFinish2Activity extends BaseActivity implements NativeExpressAD.NativeExpressADListener {

    private static final String TAG = "CleanFinish2Activity";
    private NewsFragment mNewsFragment;
    private LinearLayout mLlTopTitle;
    private AppBarLayout mAppBarLayout;
    private ImageView mIvBack;
    private boolean isAnimShow = false;
    private String mTitle;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;
    private TextView mTopSubTitle;
    private LinearLayout mLlNoNet;

    private ViewGroup mContainer;
    private NativeExpressADView nativeExpressADView;
    private NativeExpressAD nativeExpressAD;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_finish_layout;
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        mIvBack = findViewById(R.id.iv_back_clear_finish);
        mTvSize = findViewById(R.id.tv_size);
        mTvGb = findViewById(R.id.tv_clear_finish_gb_title);
        mLlTopTitle = findViewById(R.id.ll_top_title);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mTopSubTitle = findViewById(R.id.tv_top_sub_title);
        mTvQl = findViewById(R.id.tv_ql);
        mLlNoNet = findViewById(R.id.layout_not_net);
        mContainer = findViewById(R.id.container);
        Intent intent = getIntent();
        changeUI(intent);
    }

    private void changeUI(Intent intent) {
        if (PreferenceUtil.isNoFirstOpenCLeanFinishApp()){
            if (AppHolder.getInstance().getSwitchInfoList() != null){
                for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()){
                    if (PositionId.FINISH_ID.equals(switchInfoList.getId())){
                        if (switchInfoList.isIsOpen()){
                            //加载广告
                            refreshAd();
                        }
                    }
                }
            }
        }else {
            PreferenceUtil.saveFirstOpenCLeanFinishApp();
        }

        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvSize.setText(num);
            mTvGb.setText(unit);
            if (TextUtils.isEmpty(mTitle))
                mTitle = getString(R.string.app_name);
            if (getString(R.string.app_name).contains(mTitle)) {
                //悟空清理
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            }else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已加速");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            }else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
                //手机清理
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle)||getString(R.string.tool_chat_clear_n).contains(mTitle)) {
                //微信专情
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已清理");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                if (TextUtils.isEmpty(num) || num.equals("0.0")|| num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
            }
            mTopSubTitle.setText(mTitle);
        }
        showUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mAppBarLayout.setExpanded(true);
        changeUI(intent);
    }

    private void showUI(){
        if (!NetworkUtils.isNetConnected()) {
            ToastUtils.showShort(getString(R.string.tool_no_net_hint));
            mLlNoNet.setVisibility(View.VISIBLE);
        }
        showNews();
    }
    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> {
            if (getString(R.string.tool_one_key_speed).contains(mTitle))
                StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", AppHolder.getInstance().getSourcePageId(), "one_click_acceleration_clean_up_page");
            finish();
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    if (!isAnimShow)
                        return;
                    if (mNewsFragment == null) return;
                    //展开状态
                    mNewsFragment.moveNavigation(false);
                    hideBackImg(true);
                    mNewsFragment.getIvBack().setVisibility(View.VISIBLE);
                    isAnimShow = false;
                } else if (state == State.COLLAPSED) {
                    if (isAnimShow)
                        return;
                    //折叠状态
                    if (mNewsFragment == null) return;
                    mNewsFragment.getIvBack().setVisibility(View.VISIBLE);
                    mNewsFragment.moveNavigation(true);
                    hideBackImg(false);
                    mLlTopTitle.setVisibility(View.INVISIBLE);
                    isAnimShow = true;
                }
            }
        });
        mLlNoNet.setOnClickListener(v -> showUI());
    }

    private void hideBackImg(boolean hide) {
        ObjectAnimator animator;
        if (hide) {
            animator = ObjectAnimator.ofFloat(mNewsFragment.getIvBack(), "alpha", 1, 0);
            animator.setDuration(200);
        } else {
            animator = ObjectAnimator.ofFloat(mNewsFragment.getIvBack(), "alpha", 0, 1);
            animator.setDuration(500);
        }

        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
                if (hide) {
                    mLlTopTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void loadData() {

    }

    private void showNews() {
        hideToolBar();
        mNewsFragment = NewsFragment.getNewsFragment("white");
        replaceFragment(R.id.fragment_container, mNewsFragment, false);
    }

    private void showWeb() {
        setLeftTitle(mTitle);
        mLlTopTitle.setVisibility(GONE);
        replaceFragment(R.id.fragment_container, CleanFinishWebFragment.getInstance(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle))
            StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", "selected_page", "one_click_acceleration_clean_up_page");
            if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            NiuDataAPI.onPageStart("one_click_acceleration_clean_up_view_page", "一键加速清理完成页浏览");
        }else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            NiuDataAPI.onPageEnd("one_click_acceleration_clean_up_view_page", "一键加速清理完成页浏览");
        }else {
            NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
        }
        Jzvd.releaseAllVideos();
        super.onPause();
    }
    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
// 释放前一个展示的NativeExpressADView的资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (mContainer.getVisibility() != View.VISIBLE) {
            mContainer.setVisibility(View.VISIBLE);
        }

        if (mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            nativeExpressADView.setMediaListener(mediaListener);
        }
        // 广告可见才会产生曝光，否则将无法产生收益。
        mContainer.addView(nativeExpressADView);
        nativeExpressADView.render();
    }
    /**
     * 获取播放器实例
     *
     * 仅当视频回调{@link NativeExpressMediaListener#onVideoInit(NativeExpressADView)}调用后才会有返回值
     *
     * @param videoPlayer
     * @return
     */
    private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            StringBuilder videoBuilder = new StringBuilder();
            videoBuilder.append("{state:").append(videoPlayer.getVideoState()).append(",")
                    .append("duration:").append(videoPlayer.getDuration()).append(",")
                    .append("position:").append(videoPlayer.getCurrentPosition()).append("}");
            return videoBuilder.toString();
        }
        return null;
    }

    private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoInit: " + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoLoading");
        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
            Log.i(TAG, "onVideoReady");
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoStart: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPause: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoComplete: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
            Log.i(TAG, "onVideoError");
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageOpen");
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageClose");
        }
    };
    private void refreshAd() {
        try {
            /**
             *  如果选择支持视频的模版样式，请使用{@link PositionId#NATIVE_EXPRESS_SUPPORT_VIDEO_POS_ID}
             */
            nativeExpressAD = new NativeExpressAD(this, getMyADSize(), PositionId.APPID,  PositionId.CLEAN_FINISH_ID, this); // 这里的Context必须为Activity
            nativeExpressAD.setVideoOption(new VideoOption.Builder()
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS) // 设置什么网络环境下可以自动播放视频
                    .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
                    .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
            nativeExpressAD.setMaxVideoDuration(5);
            /**
             * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
             * 如果广告位仅支持图文广告，则无需调用
             */

            /**
             * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
             *
             * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
             *
             * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
             */
            nativeExpressAD.setVideoPlayPolicy(getVideoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS, this));  // 本次拉回的视频广告，在用户看来是否为自动播放的
            nativeExpressAD.loadAD(1);
        } catch (NumberFormatException e) {
            Log.w(TAG, "ad size invalid.");
            Toast.makeText(this, "请输入合法的宽高数值", Toast.LENGTH_SHORT).show();
        }
    }

    private ADSize getMyADSize() {
        int w = ADSize.FULL_WIDTH ;
        int h = -2;
        return new ADSize(w, h);
    }


    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {
        if (mContainer != null && mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
            mContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {
        if (mContainer != null && mContainer.getChildCount() > 0) {
            mContainer.removeAllViews();
            mContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onNoAD(AdError adError) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }
    }

    public static int getVideoPlayPolicy(int autoPlayPolicy, Context context){
        if(autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS){
            return VideoOption.VideoPlayPolicy.AUTO;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                    : VideoOption.VideoPlayPolicy.MANUAL;
        }else if(autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER){
            return VideoOption.VideoPlayPolicy.MANUAL;
        }
        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }

}
