package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.lxj.xpopup.XPopup;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.enums.AdType;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.ad.mvp.presenter.AdPresenter;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.SplashPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.view.RoundProgressBar;
import com.xiaoniu.cleanking.utils.CountDownTimer;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.PermissionUtils;
import com.xiaoniu.cleanking.utils.PhoneInfoUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.UserAgreementDialog;
import com.xiaoniu.cleanking.widget.UserRefuseDialog;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 这是demo工程的入口Activity，在这里会首次调用广点通的SDK。
 * <p>
 * 在调用SDK之前，如果您的App的targetSDKVersion >= 23，那么建议动态申请相关权限。
 */
public class SplashADActivity extends BaseActivity<SplashPresenter> implements View.OnClickListener {


    @Inject
    NoClearSPHelper mSPHelper;

    //广告展示容器
    private FrameLayout ad_container;
    //跳过布局
    private LinearLayout skip_layout;
    //广告圆形进度条
    private RoundProgressBar skip_view;
    //一键授权引导页根布局
    private RelativeLayout rl_open_new;
    //一键授权跳过计时器
    private TextView tv_skip;
    //一键修复按钮
    private Button btn_repair_now;

    public boolean canJump = false;
    private final int DEFAULT_TIME = 5000;
    private long loadTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean hasJump = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RxPermissions rxPermissions;
    private CountDownTimer countDownTimer;
    private boolean isAgreeUserAgreement;
    private int coldBootStartIndex;
    private String[] basicPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_ad;
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        //初始化View对象
        createView();
        //设置跳过不可点击
        tv_skip.setEnabled(false);
        //初始化权限检测
        rxPermissions = new RxPermissions(this);
        //倒计时
        initCountDown();
        //获取是否同意用户协议
        isAgreeUserAgreement = SPUtil.getBoolean(this, SPUtil.IS_AGREE_USER_AGREEMENT, false);
        //获取冷启动次数
        coldBootStartIndex = SPUtil.getInt(this, SPUtil.IS_Secondary_Cold_BootStart, -1);
        //重置冷启动次数+1
        SPUtil.setInt(SplashADActivity.this, SPUtil.IS_Secondary_Cold_BootStart, coldBootStartIndex + 1);
    }

    private void initCountDown() {
        countDownTimer = new CountDownTimer(new CountDownTimer.OnTimeChangedListener() {
            @Override
            public void onTicket(long currentTime) {
                if (currentTime == 0) {
                    tv_skip.setEnabled(true);
                    tv_skip.setText("跳过");

                    //如果展示跳过按钮之后5s之后自动跳转到主页面
                    Disposable disposable = Observable.timer(5, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> routeMainActivity());
                    compositeDisposable.add(disposable);
                } else {
                    tv_skip.setText(String.valueOf(currentTime));
                }
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private void createView() {
        //广告展示容器
        ad_container = findViewById(R.id.ad_container);
        //跳过布局
        skip_layout = findViewById(R.id.skip_layout);
        //广告圆形进度条
        skip_view = findViewById(R.id.skip_view);
        skip_view.setOnClickListener(this);
        //一键授权引导页根布局
        rl_open_new = findViewById(R.id.rl_open_new);
        //一键授权跳过计时器
        tv_skip = findViewById(R.id.tv_skip);
        //一键修复按钮
        btn_repair_now = findViewById(R.id.btn_repair_now);
    }

    @Override
    protected void initView() {

        StatisticsUtils.customTrackEvent("cold_splash_page_custom", "冷启动创建时",
                "cold_splash_page", "cold_splash_page");
        loadTime = System.currentTimeMillis();

        //初始化清理Sp配置项
        initSpConfig();

        //检查是否授予查看手机状态权限
        checkPhoneStatePermission();
    }

    /**
     * 检查是否授权查看手机状态权限
     */
    private void checkPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            StatisticsUtils.customTrackEvent("identify_device_information", "识别设备信息弹窗曝光",
                    "launch_page", "launch_page");
            Disposable disposable = rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe(aBoolean -> {
                if (aBoolean) {
                    StatisticsUtils.trackClick("identify_device_information_allow_click", "识别设备信息权限允许点击",
                            "cold_splash_page", "cold_splash_page");

                    initNiuData();
                } else {
                    StatisticsUtils.trackClick("identify_device_information_prohibit_click",
                            "识别设备信息权限禁止点击", "cold_splash_page", "cold_splash_page");
                }
                dispatchAfterPermission();
            });
            compositeDisposable.add(disposable);
        } else {
            dispatchAfterPermission();
        }
    }

    /**
     * 授权之后分发逻辑
     */
    private void dispatchAfterPermission() {
        //请求屏蔽开发
        mPresenter.getAuditSwitch();
        //请求下发配置开关
        if (hasAgreeUserAgreement()) {
            //由于只要展示用户协议弹框，则就是首次冷启动，所以只要进入该分支，则非首次冷启动
            if (hasSecondaryColdBootStart()) {
                //如果是二次冷启动，如果用户已经授权过文件读写权限，则直接展示一键授权弹框，否则直接进入首页，并展示文件读写权限引导
                if (!hasStoragePermission()) {
                    mHandler.postDelayed(this::routeMainActivity, 1200);
                } else {
                    if (!hasOneKeyPermission()) {
                        showOneKeyPermission();
                    } else {
                        mHandler.postDelayed(this::routeMainActivity, 1200);
                    }
                }
            }
        } else {
            //展示用户协议弹框
            showUserAgreement();
        }
    }

    /**
     * 是否存在一键授权必须权限
     */
    private boolean hasOneKeyPermission() {
        return ExternalInterface.getInstance(this).isOpenAllPermission(this);
    }

    /**
     * 判断是否有文件读写权限
     */
    private boolean hasStoragePermission() {
        return PermissionUtils.checkPermission(this, basicPermissions);
    }

    /**
     * 展示一键授权页面
     */
    private void showOneKeyPermission() {
        StatisticsUtils.onPageStart("open_screen_permission_guide_page_view_page", "开屏权限引导页浏览");
        rl_open_new.setVisibility(View.VISIBLE);

        //点击修复-一键授权
        btn_repair_now.setOnClickListener(v -> {
            SPUtil.setRepair(SplashADActivity.this, "isRepair", true);

            PermissionIntegrate.getPermission().startWK(SplashADActivity.this);
            StatisticsUtils.trackClick("repair_now_button_click", "立即修复按钮点击",
                    "open_screen_permission_guide_page", "open_screen_permission_guide_page");

            StatisticsUtils.onPageEnd("open_screen_permission_guide_page_view_page", "开屏权限引导页浏览",
                    "open_screen_permission_guide_page", "open_screen_permission_guide_page");

            finish();
        });

        //开始计时跳过
        countDownTimer.start(3000);

        //点击跳过，进入到首页
        tv_skip.setOnClickListener(v -> {

            routeMainActivity();

            StatisticsUtils.trackClick("ad_pass_click", "跳过点击",
                    "cold_splash_page", "cold_splash_page");

            StatisticsUtils.onPageEnd("open_screen_permission_guide_page_view_page", "开屏权限引导页浏览",
                    "open_screen_permission_guide_page", "open_screen_permission_guide_page");
        });
    }

    /**
     * 是否是二次冷启动
     */
    private boolean hasSecondaryColdBootStart() {
        return coldBootStartIndex == 2;
    }

    /**
     * 是否同意过用户协议
     */
    private boolean hasAgreeUserAgreement() {
        return isAgreeUserAgreement;
    }

    /**
     * 展示用户协议弹框
     */
    private void showUserAgreement() {
        UserAgreementDialog agreementDialog = new UserAgreementDialog(this,
                new UserAgreementDialog.OnUserAgreementClickListener() {
                    @Override
                    public void onUserAgree() {
                        //更新是否同意用户协议状态
                        SPUtil.setBoolean(SplashADActivity.this, SPUtil.IS_AGREE_USER_AGREEMENT, true);
                        SPUtil.setInt(SplashADActivity.this, SPUtil.IS_Secondary_Cold_BootStart, 2);
                        routeMainActivity();
                    }

                    @Override
                    public void onUserRefuse() {
                        showUserRefuseDialog();
                    }
                });
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show();
    }

    /**
     * 展示用户拒绝协议弹框
     */
    private void showUserRefuseDialog() {
        UserRefuseDialog refuseDialog = new UserRefuseDialog(this, this::showUserAgreement);
        new XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(refuseDialog)
                .show();
    }

    /**
     * 跳转到MainActivity界面
     */
    private void routeMainActivity() {
        startActivity(new Intent(SplashADActivity.this, MainActivity.class));

        Map<String, Object> extParam = new HashMap<>();
        extParam.put("cold_start_on_time", (System.currentTimeMillis() - loadTime));
        StatisticsUtils.customTrackEvent("cold_start_on_time", "冷启动开启总时长",
                "cold_splash_page", "cold_splash_page", extParam);

        finish();
    }

    /**
     * 初始化清理sp配置
     */
    private void initSpConfig() {
        Disposable disposable = Observable.create(e -> {
            PreferenceUtil.saveCleanAllUsed(false);
            PreferenceUtil.saveCleanJiaSuUsed(false);
            PreferenceUtil.saveCleanPowerUsed(false);
            PreferenceUtil.saveCleanNotifyUsed(false);
            PreferenceUtil.saveCleanWechatUsed(false);
            PreferenceUtil.saveCleanCoolUsed(false);
            PreferenceUtil.saveCleanGameUsed(false);
            SPUtil.setString(mContext, "path_data", FileUtils.readJSONFromAsset(mContext, "sdstorage.json"));
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
        }, Throwable::printStackTrace);
        compositeDisposable.add(disposable);
    }

    /**
     * 埋点事件
     */
    private void initNiuData() {
        if (!mSPHelper.isUploadImei()) {
            //有没有传过imei
            String imei = PhoneInfoUtils.getIMEI(mContext);
            if (TextUtils.isEmpty(imei)) {
                if (!mSPHelper.isUploadEmpImei()) { //空imei只上报一次
                    NiuDataAPI.setIMEI("");
                    mSPHelper.setUploadEmpImeiStatus(true);
                }
                mSPHelper.setUploadImeiStatus(false);
            } else {
                NiuDataAPI.setIMEI(imei);
                mSPHelper.setUploadImeiStatus(true);
            }
        }
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，
     * 点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            routeMainActivity();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    /**
     * 过审请求失败
     */
    public void getAuditSwitchFailure() {
        if (hasAgreeUserAgreement() && !hasSecondaryColdBootStart()) {
            routeMainActivity();
        }
    }

    /**
     * 获取过审开关成功
     */
    public void getAuditSwitch(AuditSwitch auditSwitch) {
        if (auditSwitch == null) {
            //如果接口异常，可以正常看资讯  状态（0=隐藏，1=显示）
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, "1");
        } else {
            SPUtil.setString(SplashADActivity.this, AppApplication.AuditSwitch, auditSwitch.getData());
        }

        if (auditSwitch != null && auditSwitch.getData().equals("1") && hasAgreeUserAgreement() && !hasSecondaryColdBootStart()) {
            loadAd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
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

    /**
     * 请求广告
     */
    private void loadAd() {
        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(
                this,
                ad_container,
                skip_layout,
                PositionId.SPLASH_ID,
                PositionId.COLD_CODE,
                AdType.Splash,
                6000,
                "cold_splash_page",
                "cold_splash_page");
        new AdPresenter().requestAd(true, adRequestParamentersBean, new AdShowCallBack() {

            @Override
            public void onAdShowCallBack(View view) {
                if (skip_view != null) {
                    showProgressBar();
                }
            }

            @Override
            public void onFailure(String message) {
                if (hasJump) {
                    return;
                }
                hasJump = true;
                routeMainActivity();
            }
        });
    }

    /**
     * 跳过进度条
     */
    private void showProgressBar() {
        skip_view.setVisibility(View.VISIBLE);
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(1, DEFAULT_TIME);
        mValueAnimator.setDuration(DEFAULT_TIME);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(valueAnimator -> {
            if (skip_view != null) {
                int progress = ((int) valueAnimator.getAnimatedValue() * 100) / DEFAULT_TIME;
                skip_view.setProgress(progress);
                if (progress == 100) {
                    next();
                }
            }
        });
        mValueAnimator.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_view:
                next();
                break;
        }
    }
}
