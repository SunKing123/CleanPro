package com.xiaoniu.cleanking.ui.main.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.bean.HotStartAction;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashHotPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.HotStartEvent;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * 热启动开屏广告
 */
public class SplashADHotActivity extends BaseActivity<SplashHotPresenter> {
    @BindView(R.id.error_ad_iv)
    ImageView mErrorAdIv;
    private ViewGroup container;
    private boolean mCanJump;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
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
                    InsertAdSwitchInfoList.DataBean dataBean = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_NEIBU_SCREEN);
                    if (null != dataBean && dataBean.isOpen()) {//内部插屏广告
                        if (!TextUtils.isEmpty(dataBean.getInternalAdRate()) && dataBean.getInternalAdRate().contains(",")) {
                            List<String> internalList = Arrays.asList(dataBean.getInternalAdRate().split(","));
                            InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
                            int startCount = inside.getCount();
                            if (internalList.contains(String.valueOf(startCount)) && isMainPage) {
                                PreferenceUtil.saveScreenInsideTime();
                                EventBus.getDefault().post(new HotStartEvent(HotStartAction.INSIDE_SCREEN));

                                return;
                            }
                        }

                    }
                }
                //所有页面展示红包
                if (redPacketDataBean.getTrigger() == 0 || PreferenceUtil.getRedPacketShowCount() % redPacketDataBean.getTrigger() == 0) {
                    if (isMainPage) {
                        EventBus.getDefault().post(new HotStartEvent(HotStartAction.RED_PACKET));
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
        initGeekSdkAD();
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("hot_splash_page_custom", "热启动页创建时", "hot_splash_page", "hot_splash_page");
    }


    private void initGeekSdkAD() {
        AdRequestParams params = new AdRequestParams.Builder().setAdId(MidasConstants.SP_CODE_START_ID)
                .setActivity(this).setViewContainer(container).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
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
            public void onAdClose(AdInfo adInfo) {
                super.onAdClose(adInfo);
                jumpActivity();
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


    @Override
    public void netError() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCanJump) {
            jumpActivity();
        }
        mCanJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCanJump = false;
    }


    public void jumpActivity() {
        if (mCanJump) {
            //热启动的时候去掉内部插屏和红包相关判断
            showRedPacket();
            finish();
        } else {
            mCanJump = true;
        }

    }
}
