package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.entity.AdInfo;
import com.comm.jksdk.ad.listener.AdListener;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashHotPresenter;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.json.JSONObject;

import java.util.Map;

/**
 * 热启动开屏广告
 */
public class SplashADHotActivity extends BaseActivity<SplashHotPresenter> {

    private ViewGroup container;
    private RoundProgressBar skipView;

    private AdManager mAdManager;
    private String mAdTitle = " "; //广告标题
    private String mAdSourse = " "; //广告来源

    private final String TAG = "GeekSdk";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
    }

    public void jumpActivity() {
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
        if (null == AppHolder.getInstance() || null == AppHolder.getInstance().getRedPacketEntityList()
                || null == AppHolder.getInstance().getRedPacketEntityList().getData()
                || AppHolder.getInstance().getRedPacketEntityList().getData().size() <= 0
                || null == AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls()
                || AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getImgUrls().size() <= 0)
            return;
        Log.d("XiLei", "PreferenceUtil.getRedPacketShowCount()=" + PreferenceUtil.getRedPacketShowCount());
        switch (AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getLocation()) {
            case 5:
                if (null != AppHolder.getInstance().getInsertAdSwitchmap() && AppHolder.getInstance().getInsertAdSwitchmap().size() >= 0) {
                    Map<String, InsertAdSwitchInfoList.DataBean> map = AppHolder.getInstance().getInsertAdSwitchmap();
                    if (null != map.get(PositionId.KEY_NEIBU_SCREEN)) {
                        InsertAdSwitchInfoList.DataBean dataBean = map.get(PositionId.KEY_NEIBU_SCREEN);
                        Log.d("XiLei", "dataBean.isOpen()=" + dataBean.isOpen());
                        if (dataBean.isOpen()) {//内部插屏广告
                            if (dataBean.getShowRate() == 1 || PreferenceUtil.getRedPacketShowCount() == dataBean.getShowRate()) {
                                PreferenceUtil.saveScreenInsideTime();
                                startActivity(new Intent(this, ScreenInsideActivity.class));
                                return;
                            }
                        }
                    }
                }
                //所有页面展示红包
                if (AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getTrigger() == 0
                        || PreferenceUtil.getRedPacketShowCount() % AppHolder.getInstance().getRedPacketEntityList().getData().get(0).getTrigger() == 0) {
                    startActivity(new Intent(this, RedPacketHotActivity.class));
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

    private void initGeekSdkAD() {
        mAdManager = GeekAdSdk.getAdsManger();
//        mAdManager.loadSplashAd(this, "hot_kp", new AdListener() { //暂时注释
        mAdManager.loadSplashAd(this, "hot_kp", new AdListener() {
            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
                if (null != info) {
                    mAdTitle = info.getAdTitle();
                    mAdSourse = info.getAdSource();
                }
                if (null == info || null == container) return;
                showProgressBar();
                container.addView(mAdManager.getAdView());
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", info.getAdId(), info.getAdSource(), "success", "hot_splash_page", "hot_splash_page");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
                if (null == info) return;
                StatisticsUtils.customAD("ad_show", "广告展示曝光", "1", info.getAdId(), info.getAdSource(), "hot_splash_page", "hot_splash_page", "");
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
                if (null == info) return;
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", info.getAdId(), info.getAdSource(), "hot_splash_page", "hot_splash_page", "");
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                Log.e(TAG, "-----adError 热启动-----" + errorMsg);
                StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "fail", "hot_splash_page", "hot_splash_page");
                finish();
                showRedPacket();
            }
        });
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
                jumpActivity();
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
}
