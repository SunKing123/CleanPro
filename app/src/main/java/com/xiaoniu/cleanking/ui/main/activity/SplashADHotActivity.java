package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.bean.HotStartAction;
import com.xiaoniu.cleanking.bean.PopupWindowType;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.RedPacketEntity;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashHotPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.HotStartEvent;
import com.xiaoniu.cleanking.utils.rxjava.RxTimer;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

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
    RxTimer rxTimer;
    private static final int SP_SHOW_OUT_TIME = 9 * 1000;//开屏总超时时间

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad_hot;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    /**
     * 展示红包
     */
    private void showRedPacket() {
        if (PreferenceUtil.isHaseUpdateVersion() || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_3G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_2G
                || NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO)
            return;
        if (null == AppHolder.getInstance()) {
            return;
        }
        String activityName = getIntent().getStringExtra("activityName");
        boolean isMainPage = activityName.contains(MainActivity.class.getSimpleName());
        //1.先判断内部插屏
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
        //如果内部插屏不展示再判断红包
        if (AppHolder.getInstance().getPopupDataEntity() == null) {
            return;
        }
        RedPacketEntity.DataBean redPacketDataBean = AppHolder.getInstance().getPopupDataFromListByType(
                AppHolder.getInstance().getPopupDataEntity(), PopupWindowType.POPUP_RED_PACKET
        );
        if (redPacketDataBean == null || null == redPacketDataBean.getImgUrls() || redPacketDataBean.getImgUrls().size() <= 0)
            return;
        if (redPacketDataBean.getLocation() == 5) {
            //所有页面展示红包
            if (redPacketDataBean.getTrigger() == 0 || PreferenceUtil.getRedPacketShowCount() % redPacketDataBean.getTrigger() == 0) {
                if (isMainPage) {
                    EventBus.getDefault().post(new HotStartEvent(HotStartAction.RED_PACKET));
                }
            }
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

        //超时定时器
        rxTimer = new RxTimer();
        rxTimer.timer(SP_SHOW_OUT_TIME, number -> {
            mCanJump = true;
            jumpActivity();
        });

        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("hot_splash_page_custom", "热启动页创建时", "hot_splash_page", "hot_splash_page");
    }


    private void initGeekSdkAD() {

        StatisticsUtils.customTrackEvent("ad_request_sdk", "热启动页广告发起请求", "hot_page", "hot_page");

        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.SPLASH_ID, PositionId.HOT_CODE) , new AbsAdBusinessCallback() {
            @Override
            public void onAdLoaded(AdInfoModel adInfoModel) {
                super.onAdLoaded(adInfoModel);
                if (adInfoModel.view!= null && adInfoModel.view.getParent() == null){
                    container.addView(adInfoModel.view);
                }
                if (rxTimer != null){
                    rxTimer.cancel();
                }
            }

            @Override
            public void onAdLoadError(String errorCode, String errorMsg) {
                super.onAdLoadError(errorCode, errorMsg);
                jumpActivity();
            }

            @Override
            public void onAdClose(AdInfoModel adInfoModel) {
                super.onAdClose(adInfoModel);
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
            mCanJump = false;
        } else {
            mCanJump = true;
        }

    }

    @Override
    protected void onDestroy() {
        if (null != rxTimer) {
            rxTimer.cancel();
        }
        super.onDestroy();
    }
}
