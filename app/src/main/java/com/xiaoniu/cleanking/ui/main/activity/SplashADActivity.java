package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.selfdebug.AppConfig;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.dialog.LaunchPermissionRemindDialog;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.rxjava.RxTimer;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 冷启动开屏页面
 * <p>
 */
public class SplashADActivity extends BaseActivity<SplashPresenter> {
    @Inject
    NoClearSPHelper mSPHelper;
    private ViewGroup container;
    private Disposable mSubscription;
    private boolean mIsOpen; //冷启动广告开关
    private String pushData = null;
    private boolean mCanJump;
    RxTimer rxTimer;
    private static final int SP_SHOW_OUT_TIME = 9 * 1000;//开屏总超时时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        long home = MmkvUtil.getLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, 0L);
        if (home == 0L) {
            MmkvUtil.saveLong(SpCacheConfig.KEY_FIRST_INSTALL_APP_TIME, System.currentTimeMillis());
        }
        getDataFromPush();
        //用户/token校验
        RequestUserInfoUtil.checkUserToken(this);
    }

    private void getDataFromPush() {
        //获取华为平台附带的jpush信息
        if (getIntent().getData() != null) {
            pushData = getIntent().getData().toString();
        }
        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(pushData) && getIntent().getExtras() != null) {
            pushData = getIntent().getExtras().getString("JMessageExtra");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);
        container = this.findViewById(R.id.splash_container);
        mPresenter.spDataInit();
        initNiuData();
        if (!PreferenceUtil.isNotFirstOpenApp()) {//第一次冷启动
            //默认过审状态===权限只在第一次冷启时调用，后面冷启不需要检验
            SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
            MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
            permissionRemind();
        } else {
            oldOptionAction();
        }
        initFileRelation();
        //数美sdk初始化
        mPresenter.initShuMeiSDK();
        //页面创建事件埋点
        StatisticsUtils.customTrackEvent("clod_splash_page_custom", "冷启动创建时", "clod_splash_page", "clod_splash_page");
    }


    /**
     * 第一次启动权限说明
     */
    private void permissionRemind() {
        //引导使用说明埋点
        StatisticsUtils.customTrackEvent("use_guide_page_custom", "使用指引页曝光", "use_guide_page", "use_guide_page");
        new LaunchPermissionRemindDialog(this).setLaunchPermissionListener(() -> {
            StatisticsUtils.trackClick("experience_it_now_button_click", "立即体验按钮点击", "use_guide_page", "use_guide_page");

            requestPhoneStatePermission();
            PreferenceUtil.saveFirstOpenApp();
        }).show();
    }

    /**
     * 之前的页面执行逻辑==现在在权限之后再调用
     */
    private void oldOptionAction() {
        //超时定时器
        rxTimer = new RxTimer();
        rxTimer.timer(SP_SHOW_OUT_TIME, number -> {
            if (PreferenceUtil.isNotFirstOpenApp()) {//非第一次冷启动总超时逻辑；
                mCanJump = true;
                jumpActivity();
            }
        });
        if (NetworkUtils.isNetConnected()) {//正常网络
            mPresenter.getAuditSwitch();
        } else {
            delayJump();
        }
    }


    //初始sd根目录关联关系
    void initFileRelation() {
        SPUtil.setString(mContext, "path_data", FileUtils.readJSONFromAsset(mContext, "sdstorage.json"));
    }

    /**
     * 获取过审开关成功//
     *
     * @param auditSwitch
     */
    public void getAuditSwitch(AuditSwitch auditSwitch) {
        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）；
            //0715戚雯确认，接口异常默认审核状态；
            SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
            MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
        } else {
            SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, auditSwitch.getData());
            MmkvUtil.saveString(SpCacheConfig.AuditSwitch, auditSwitch.getData());
        }

        if (auditSwitch.getData().equals("0")) {//过审中
            delayJump();
        } else if (auditSwitch.getData().equals("1")) {//过审已通过
            mPresenter.getSwitchInfoList();
        }
    }

    /**
     * 获取过审开关失败
     */
    public void getAuditSwitchFail() {
        //0715戚雯确认，接口异常默认审核状态；
        SPUtil.setString(SplashADActivity.this, SpCacheConfig.AuditSwitch, "0");
        MmkvUtil.saveString(SpCacheConfig.AuditSwitch, "0");
        delayJump();
    }

    //延迟跳转
    public void delayJump() {
        this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            jumpActivity();
        });
    }

    //页面跳转
    public void jumpActivity() {
        if (mCanJump) {
            if (!AndroidUtil.isFastDoubleClick()) {
                Intent intent = new Intent(SplashADActivity.this, MainActivity.class);
                if (!TextUtils.isEmpty(pushData)) {
                    intent.putExtra("push_uri", pushData);
                }
                startActivity(intent);
                finish();
            }
            mCanJump = false;
        } else {
            mCanJump = true;
        }

    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null != list && null != list.getData() && list.getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
                if (PositionId.COLD_CODE.equals(switchInfoList.getAdvertPosition()) && PositionId.SPLASH_ID.equals(switchInfoList.getConfigKey())) {
                    mIsOpen = switchInfoList.isOpen();
                    PreferenceUtil.saveCoolStartADStatus(mIsOpen);
                }
            }
        }
        if (!PreferenceUtil.isNotFirstOpenApp()) {//第一次冷启动
            return;
        }
        if (mIsOpen) {//开屏开关已开
            initGeekSdkAD();
        } else {
            jumpActivity();
        }
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {
        this.mSubscription = Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            PreferenceUtil.saveFirstOpenApp();
            jumpActivity();
        });
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        if (null != this.mSubscription) {
            this.mSubscription.dispose();
            this.mSubscription = null;
        }
        if (null != rxTimer) {
            rxTimer.cancel();
        }
        super.onDestroy();
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


    private void initGeekSdkAD() {
        StatisticsUtils.customTrackEvent("ad_request_sdk", "冷启动页广告发起请求", "clod_page", "clod_page");

        MidasRequesCenter.requestAndShowAd(this, MidasConstants.SP_CODE_START_ID, new AbsAdBusinessCallback() {
            @Override
            public void onAdLoaded(AdInfoModel adInfoModel) {
                super.onAdLoaded(adInfoModel);
                if (adInfoModel.view!= null && adInfoModel.view.getParent() == null){
                    container.addView(adInfoModel.view);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                jumpActivity();
                break;
        }
    }

    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mSPHelper.isUploadImei()) {
            //有没有传过imei
            String imei = PhoneInfoUtils.getIMEI(mContext);
            if (TextUtils.isEmpty(imei)) {
                NiuDataAPI.setIMEI("");
                mSPHelper.setUploadImeiStatus(false);
            } else {
                NiuDataAPI.setIMEI(imei);
                mSPHelper.setUploadImeiStatus(true);
            }
        }


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


    //获取Imei
    @SuppressLint("WrongConstant")
    public void requestPhoneStatePermission() {
        PermissionUtils.permission(Manifest.permission.READ_PHONE_STATE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                initNiuData();
                checkReadPermission();
            }

            @Override
            public void onDenied() {
                checkReadPermission();
            }
        }).request();

    }

    private void checkReadPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                oldOptionAction();
            }

            @Override
            public void onDenied() {
                oldOptionAction();
            }
        }).request();

    }


}
