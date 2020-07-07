package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.bean.HotStartAction;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashHotPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.ui.tool.notify.event.HotStartEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.ad.widget.TemplateView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

/**
 * 热启动开屏广告
 */
public class SplashADHotActivity extends BaseActivity<SplashHotPresenter> {

    @BindView(R.id.error_ad_iv)
    ImageView mErrorAdIv;

    private ViewGroup container;
    private RoundProgressBar skipView;

    private AdManager mAdManager;
    private String mAdTitle = " "; //广告标题
    private String mAdSourse = " "; //广告来源

    private final String TAG = "GeekSdk";

    private boolean adClicked = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
    }

    public void jumpActivity() {
        //热启动的时候去掉内部插屏和红包相关判断
        showRedPacket();
        finish();
    }

    /**
     * 展示红包
     */
    private void showRedPacket() {
        if (PreferenceUtil.isHaseUpdateVersion() || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
            return;
        if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getPopupDataEntity()) {
            return;
        }
        RedPacketEntity.DataBean redPacketDataBean = AppHolder.getInstance().getPopupDataFromListByType(
                AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RED_PACKET
        );
        if (redPacketDataBean == null || null == redPacketDataBean.getImgUrls() || redPacketDataBean.getImgUrls().size() <= 0)
            return;
        switch (redPacketDataBean.getLocation()) {
            case 5:
                String activityName = getIntent().getStringExtra("activityName");
                boolean isMainPage = activityName.contains(MainActivity.class.getSimpleName());
                if (null != AppHolder.getInstance().getInsertAdSwitchMap()) {
                    InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdSwitchMap().get(PositionId.KEY_NEIBU_SCREEN);
                    if (null != dataBean && dataBean.isOpen()) {//内部插屏广告
                        if (!TextUtils.isEmpty(dataBean.getInternalAdRate()) && dataBean.getInternalAdRate().contains(",")) {
                            List<String> internalList = Arrays.asList(dataBean.getInternalAdRate().split(","));
                            InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
                            int startCount = inside.getCount();
                            if (internalList.contains(String.valueOf(startCount)) && isMainPage) {
                                PreferenceUtil.saveScreenInsideTime();
                                EventBus.getDefault().post(new HotStartEvent(HotStartAction.INSIDE_SCREEN));
                                //  startActivity(new Intent(this, ScreenInsideActivity.class));
                                return;
                            }
                        }

                    }
                }
                //所有页面展示红包
                if (redPacketDataBean.getTrigger() == 0 || PreferenceUtil.getRedPacketShowCount() % redPacketDataBean.getTrigger() == 0) {
                    if (isMainPage) {
                        EventBus.getDefault().post(new HotStartEvent(HotStartAction.RED_PACKET));
                        // startActivity(new Intent(this, RedPacketHotActivity.class));
                    }

                }
                break;
        }

    }

    @Override
    protected void initView() {
        if (PreferenceUtil.getScreenInsideTime()) {
            PreferenceUtil.saveRedPacketShowCount(1);
            PreferenceUtil.saveScreenInsideTime();
        } else {
            PreferenceUtil.saveRedPacketShowCount(PreferenceUtil.getRedPacketShowCount() + 1);
        }
        /*保存冷、热启动的次数*/
        InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
        if (DateUtils.isSameDay(inside.getTime(), System.currentTimeMillis())) {
            inside.setCount(inside.getCount() + 1);
        } else {
            inside.setCount(1);
        }
        inside.setTime(System.currentTimeMillis());
        PreferenceUtil.saveColdAndHotStartCount(inside);

        container = this.findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        initGeekSdkAD();
        skipView.setOnClickListener(v -> {
            skipView.clearAnimation();
            JSONObject extension = new JSONObject();
            try {
                extension.put("ad_id", mAdTitle);
                extension.put("ad_agency", mAdSourse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            StatisticsUtils.trackClick("ad_pass_click", "跳过点击", "hot_splash_page", "hot_splash_page");
        });
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("hot_splash_page_custom", "热启动页创建时", "hot_splash_page", "hot_splash_page");
    }

    private boolean mIsAdError;

    private void initGeekSdkAD() {
        StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "hot_splash_page", "hot_splash_page");
        AdRequestParams params=new AdRequestParams.Builder().setAdId(MidasConstants.SP_CODE_START_ID)
                .setActivity(this).setViewContainer(container).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdLoadSuccess(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdLoadSuccess(adInfo);
//                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "clod_splash_page", "clod_splash_page");
            }

            @Override
            public void onAdError(com.xnad.sdk.ad.entity.AdInfo adInfo, int i, String s) {
                super.onAdError(adInfo, i, s);
                jumpActivity();
            }

            @Override
            public void onShowError(int i, String s) {
                super.onShowError(i, s);
                jumpActivity();
            }

            @Override
            public void onAdShow(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdShow(adInfo);
            }

            @Override
            public void onAdClicked(com.xnad.sdk.ad.entity.AdInfo adInfo) {
                super.onAdClicked(adInfo);
            }

            @Override
            public void onAdClose(com.xnad.sdk.ad.entity.AdInfo adInfo, TemplateView templateView) {
                super.onAdClose(adInfo, templateView);
                jumpActivity();
            }
        });
        /*mAdManager = GeekAdSdk.getAdsManger();
        mAdManager.loadSplashAd(this, PositionId.AD_POSITION_HOT_KP, new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                if (null != info) {
                    Log.d(TAG, "-----adSuccess 热启动--" + info.toString());
                    mAdTitle = info.getAdTitle();
                    mAdSourse = info.getAdSource();
                }
                if (null == info || null == container) return;

                if (info.getAdSource().equals(PositionId.AD_SOURCE_CSJ)) {
                    showProgressBar();
                }
                Log.d(TAG, "-----adSuccess 热启动-- 添加了广告");
                container.addView(info.getAdView());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "hot_splash_page", "hot_splash_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed 热启动");

                if (null == info || adClicked) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "hot_splash_page", "hot_splash_page", info.getAdTitle());
                if (info.getAdSource().equals(PositionId.AD_SOURCE_YLH)) {
                    jumpActivity();
                }
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked 热启动");
                if (null == info) return;
                adClicked = true;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "hot_splash_page", "hot_splash_page", info.getAdTitle());
            }

            @Override
            public void adError(AdInfo info, int errorCode, String errorMsg) {
                if (null != info) {
                    Log.e(TAG, "-----adError 热启动-----" + errorCode + "--" + errorMsg + info.toString());
                    StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "fail", "hot_splash_page", "hot_splash_page");
                }
                showProgressBar();
                showBottomAd();
            }
        });*/
    }

    private int mBottomAdShowCount = 0;

    /**
     * 打底广告
     */
    private void showBottomAd() {
        if (null != AppHolder.getInstance().getBottomAdList() &&
                AppHolder.getInstance().getBottomAdList().size() > 0) {
            for (BottoomAdList.DataBean dataBean : AppHolder.getInstance().getBottomAdList()) {
                if (dataBean.getAdvertPosition().equals(PositionId.HOT_CODE)) {
                    if (dataBean.getShowType() == 1) { //循环
                        mBottomAdShowCount = PreferenceUtil.getBottomAdHotCount();
                        if (mBottomAdShowCount >= dataBean.getAdvBottomPicsDTOS().size() - 1) {
                            PreferenceUtil.saveBottomAdHotCount(0);
                        } else {
                            PreferenceUtil.saveBottomAdHotCount(PreferenceUtil.getBottomAdHotCount() + 1);
                        }
                    } else { //随机
                        if (dataBean.getAdvBottomPicsDTOS().size() == 1) {
                            mBottomAdShowCount = 0;
                        } else {
                            mBottomAdShowCount = new Random().nextInt(dataBean.getAdvBottomPicsDTOS().size() - 1);
                        }
                    }
                    StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", " ", "自定义广告", "hot_splash_page", "hot_splash_page", dataBean.getSwitcherName());
                    if (null == mErrorAdIv) return;
                    if (dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getImgUrl().contains(".gif")) {
                        GlideUtils.loadGif(SplashADHotActivity.this, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getImgUrl(), mErrorAdIv, 10000);
                    } else {
                        GlideUtils.loadImage(SplashADHotActivity.this, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getImgUrl(), mErrorAdIv);
                    }
                    mErrorAdIv.setOnClickListener(v -> {
                        mIsAdError = true;
                        StatisticsUtils.clickAD("ad_click", "广告点击", "1", " ", "自定义广告", "hot_splash_page", "hot_splash_page", dataBean.getSwitcherName());
                        AppHolder.getInstance().setCleanFinishSourcePageId("hot_splash_page");
                        startActivityForResult(new Intent(this, AgentWebViewActivity.class)
                                .putExtra(ExtraConstant.WEB_URL, dataBean.getAdvBottomPicsDTOS().get(mBottomAdShowCount).getLinkUrl())
                                .putExtra(ExtraConstant.WEB_FROM, "SplashADHotActivity"), 100);
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                jumpActivity();
                break;
        }
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 自定义倒计时进度条
     */
    private void showProgressBar() {
        skipView.setVisibility(View.VISIBLE);
        skipView.startAnimation(3000, new LinearInterpolator(), new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mIsAdError && !adClicked) {
                    jumpActivity();
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
    public void netError() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adClicked) {
            jumpActivity();
        }
    }
}
