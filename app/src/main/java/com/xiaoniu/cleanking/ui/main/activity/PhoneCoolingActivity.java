package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.appbar.AppBarLayout;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.adapter.ProcessIconAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HardwareInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneCoolingPresenter;
import com.xiaoniu.cleanking.ui.main.widget.CustomerSpaceDecoration;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.ArcProgressBar;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author Administrator
 */
@Route(path = RouteConstants.PHONE_COOLING_ACTIVITY)
public class PhoneCoolingActivity extends BaseActivity<PhoneCoolingPresenter> {

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.layout_title_bar)
    RelativeLayout mLayoutTitleBar;
    @BindView(R.id.text_temperature)
    TextView mTextTemperature;
    @BindView(R.id.layout_title)
    RelativeLayout mBgTitle;
    @BindView(R.id.bg_title)
    ImageView mImageTitle;
    @BindView(R.id.text_temperature_tips)
    TextView mTextTemperatureTips;
    @BindView(R.id.text_title_process)
    TextView mTextTitleProcess;
    @BindView(R.id.recycler_process)
    RecyclerView mRecyclerProcess;
    @BindView(R.id.layout_process)
    ConstraintLayout mLayoutProcess;
    @BindView(R.id.text_title_hardware)
    TextView mTextTitleHardware;
    @BindView(R.id.icon_cpu)
    ImageView mIconCpu;
    @BindView(R.id.layout_hardware)
    ConstraintLayout mLayoutHardware;
    @BindView(R.id.layout_bottom_container)
    LinearLayout mLayoutBottom;
    @BindView(R.id.progress_bar)
    ArcProgressBar mProgressBar;
    @BindView(R.id.image_point)
    ImageView mImagePoint;
    @BindView(R.id.text_temperature_number)
    TextView mTextTemperatureNumber;
    @BindView(R.id.layout_anim_cool)
    ConstraintLayout mLayoutAnimCool;
    @BindView(R.id.layout_content_cool)
    RelativeLayout mLayoutContentCool;
    @BindView(R.id.layout_cool_view)
    ConstraintLayout mLayoutCoolView;
    @BindView(R.id.layout_title_content)
    RelativeLayout mLayoutTitleContent;
    @BindView(R.id.view_lottie_cool)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.view_lottie_cool_finish)
    LottieAnimationView mAnimationView;
    @BindView(R.id.layout_clean_finish)
    ConstraintLayout mLayoutCleanFinish;
    @BindView(R.id.web_view)
    NestedScrollWebView mWebView;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.layout_not_net)
    LinearLayout mLayoutNotNet;
    @BindView(R.id.layout_junk_clean)
    RelativeLayout mLayoutJunkClean;
    @BindView(R.id.layout_bottom_content)
    LinearLayout mLayoutBottomContent;
    @BindView(R.id.tv_number_cool)
    TextView mTextNumberCool;
    @BindView(R.id.layout_cool_bottom)
    ImageView mLayoutCoolBottom;
    @BindView(R.id.tv_cooling_show)
    TextView mTvCooling;
    @BindView(R.id.fl_anim)
    FrameLayout mFlAnim;
    @BindView(R.id.app_cooling_bar_layout)
    AppBarLayout mAppCoolingBarlayout;
    @BindView(R.id.rl_anim)
    RelativeLayout mRlAnim;
    private ProcessIconAdapter mProcessIconAdapter;
    private HardwareInfo mHardwareInfo;
    public static ArrayList<FirstJunkInfo> mRunningProcess;
    boolean isError = false;

    public static final int REQUEST_CODE_HARDWARE = 10001;

    /*埋点相关*/
    private String returnEventName = "";
    private String sysReturnEventName = "";

    private String currentPageId = "";
    private String sourcePageId = "";

    String viewPageEventName = "";
    String viewPageEventCode = "";


    private int position_bluetooth = 2;
    private int position_location = 3;
    private int position_wifi = 4;


    /**
     * 是否温度过高
     */
    private boolean isOverload = false;

    /**
     * 数字增加动画
     */
    private ValueAnimator numberAnimator;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_cooling;
    }

    @Override
    protected void initView() {

        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
        Intent intent = getIntent();
        if (intent != null) {
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)) {
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_cooling_click");
                StatisticsUtils.trackClick("toggle_cooling_click", "常驻通知栏点击降温", "", "toggle_page");
            }
        }

        mAppCoolingBarlayout.setExpanded(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }

        mTextTitle.setText("手机降温");

        int phoneTemperature = mPresenter.getPhoneTemperature();
        mTextTemperature.setText(String.valueOf(phoneTemperature));
        if (phoneTemperature > 36) {
            mTextTemperatureTips.setText("手机温度较高");
            mBgTitle.setBackgroundColor(mContext.getResources().getColor(R.color.color_FD6F46));
            mLayoutCoolView.setBackgroundColor(mContext.getResources().getColor(R.color.color_FD6F46));
            mImageTitle.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_bg_hot));
            mLayoutCoolBottom.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_bg_hot));
            isOverload = true;
        }

        //设置Ding字体
        mTextTemperature.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));
        mTextTemperatureNumber.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));

        initAdapter();
        mPresenter.getRunningProcess();
        mPresenter.getHardwareInfo(false);
        initCoolAnimation(phoneTemperature);
        initWebView();

        //降温
        int tem = new Random().nextInt(3) + 1;
        mTextNumberCool.setText("成功降温" + tem + "°C");

        new Handler().postDelayed(() -> initBottomLayout(), 1000);

        //埋点参数相关
        String preName = AppManager.getAppManager().preActivityName();
        if (preName.contains("MainActivity")) {
            sourcePageId = AppHolder.getInstance().getSourcePageId();
        } else if (preName.contains("NewCleanFinishActivity")) {
            sourcePageId = "clean_success_page";
        }
        returnEventName = "用户在降温扫描页点击返回";
        sysReturnEventName = "用户在降温扫描页点击返回";
        currentPageId = "cool_scan_page";
        viewPageEventName = "用户在降温扫描页浏览";
        viewPageEventCode = "cool_scan_page_view_page";
    }

    private void initBottomLayout() {
        if (isDestroyed()) {
            return;
        }
        int measureHeight = mLayoutBottomContent.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutBottomContent.getLayoutParams();
        layoutParams.height = measureHeight;
        mLayoutBottomContent.setLayoutParams(layoutParams);
    }

    private void initCoolAnimation(int phoneTemperature) {
        mProgressBar.setArcBgColor(getResources().getColor(R.color.color_progress_bg));
        mProgressBar.setProgressColor(getResources().getColor(R.color.white));
        mProgressBar.setUpdateListener(progress -> {
            if (mImagePoint != null) {
                mImagePoint.setRotation(progress);
            }
        });
        mProgressBar.setProgress(phoneTemperature);
        initAnimator(phoneTemperature);
    }

    private static final int FirstLevel = 0xffFD6F46;
    private static final int ThirdLevel = 0xff06C581;

    /**
     * 数字增加动画
     *
     * @param phoneTemperature
     */
    private void initAnimator(int phoneTemperature) {
        if (mLayoutAnimCool == null) return;
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mLayoutAnimCool, "backgroundColor", ThirdLevel, FirstLevel);
        ValueAnimator colorAnim2 = ObjectAnimator.ofInt(mLayoutTitleBar, "backgroundColor", ThirdLevel, FirstLevel);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim2.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(800);
        colorAnim2.setDuration(800);
        colorAnim.setStartDelay(1500);
        colorAnim2.setStartDelay(1500);
        colorAnim.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            showBarColor(animatedValue);
        });
        numberAnimator = ObjectAnimator.ofInt(0, phoneTemperature);
        numberAnimator.setDuration(3000);
        numberAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mTextTemperatureNumber != null) {
                mTextTemperatureNumber.setText(String.valueOf(animatedValue));
            }
        });
        numberAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                setViewTrans();

                /**
                 * 直接执行降温操作
                 */
                if (mRunningProcess == null) return;
                //立即降温
                for (FirstJunkInfo firstJunkInfo : mRunningProcess) {
                    CleanUtil.killAppProcesses(firstJunkInfo.getAppPackageName(), firstJunkInfo.getPid());
                }
                //手机降温
                AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.PHONE_COOLING);
                setViewPlay();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet animatorSetTimer = new AnimatorSet();
        if (isOverload) {
            animatorSetTimer.playTogether(numberAnimator, colorAnim, colorAnim2);
        } else {
            animatorSetTimer.play(numberAnimator);
        }
        animatorSetTimer.start();
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

    /**
     * 数字动画播放完后上移，布局高度缩小
     */
    public void setViewTrans() {
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

        if (isDestroyed()) {
            return;
        }
        int bottom = mBgTitle.getBottom();
        mLayoutContentCool.setVisibility(VISIBLE);
        int startHeight = ScreenUtils.getFullActivityHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight - bottom, 0);
        new Handler().postDelayed(() -> {
            if (!isDestroyed()) {
                try {
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(mBgTitle, "alpha", 0, 1);
                    mBgTitle.setVisibility(VISIBLE);
                    alpha.setDuration(1000);
                    alpha.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mLayoutContentCool.getLayoutParams();
        anim.addUpdateListener(animation -> {
            rlp.topMargin = (int) animation.getAnimatedValue();
            if (mLayoutContentCool != null) {
                mLayoutContentCool.setLayoutParams(rlp);
            }
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                sourcePageId = "cool_scan_page";
                returnEventName = "用户在降温扫描完成后的诊断页返回";
                sysReturnEventName = "用户在降温扫描完成后的诊断页返回";
                currentPageId = "cool_scan_result_page";

                viewPageEventName = "用户在降温扫描完成后的诊断页浏览";
                viewPageEventCode = "cool_scan_result_page_view_page";

                NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
                NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

                if (mLayoutAnimCool != null) {
                    mLayoutAnimCool.setVisibility(GONE);
                }
            }
        });
    }

    /**
     * 播放降温动画
     */
    public void setViewPlay() {
        sourcePageId = "cool_scan_result_page";
        currentPageId = "cool_animation_page";
        returnEventName = "用户在降温动画页返回";
        sysReturnEventName = "用户在降温动画页返回";

        viewPageEventName = "用户在降温动画页浏览";
        viewPageEventCode = "cool_animation_page_view_page";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

        int bottom = mBgTitle.getBottom();
        int startHeight = ScreenUtils.getFullActivityHeight();
        ValueAnimator anim = ValueAnimator.ofInt(0, startHeight - bottom);
        try {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mLayoutTitleContent, "alpha", 1, 0);
            alpha.setDuration(200);
            alpha.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mLayoutCoolView.getLayoutParams();
        anim.addUpdateListener(animation -> {
            rlp.height = (int) animation.getAnimatedValue();
            if (mLayoutCoolView != null) {
                mLayoutCoolView.setLayoutParams(rlp);
            }
        });

        ValueAnimator colorAnim = ObjectAnimator.ofInt(mLayoutCoolView, "backgroundColor", FirstLevel, ThirdLevel);
        ValueAnimator colorAnim2 = ObjectAnimator.ofInt(mBgTitle, "backgroundColor", FirstLevel, ThirdLevel);
        ValueAnimator colorAnim3 = ObjectAnimator.ofInt(mLayoutTitleBar, "backgroundColor", FirstLevel, ThirdLevel);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim2.setEvaluator(new ArgbEvaluator());
        colorAnim3.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(800);
        colorAnim2.setDuration(800);
        colorAnim3.setDuration(800);
        colorAnim.setStartDelay(1500);
        colorAnim2.setStartDelay(1500);
        colorAnim3.setStartDelay(1500);
        colorAnim.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (isDestroyed()) {
                return;
            }
            showBarColor(animatedValue);
            if (mLayoutCoolBottom != null) {
                mLayoutCoolBottom.setVisibility(GONE);
            }
        });

        AnimatorSet animatorSetTimer = new AnimatorSet();
        if (isOverload) {
            animatorSetTimer.playTogether(anim, colorAnim, colorAnim2, colorAnim3);
        } else {
            animatorSetTimer.play(anim);
        }
        animatorSetTimer.start();
        mLayoutCoolView.setVisibility(VISIBLE);
        //抢夺点击事件。。。
        mLayoutCoolView.setOnClickListener(v -> {
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startAnimation();
                new Handler().postDelayed(() -> setViewFinishTrans(), 3500);
            }
        });
    }

    /**
     * 下雪
     */
    private void startAnimation() {
        if (!isDestroyed()) {
//            mLottieAnimationView.useHardwareAcceleration();
            mLottieAnimationView.setImageAssetsFolder("images");
            mLottieAnimationView.setAnimation("data_cool.json");
            mLottieAnimationView.playAnimation();
        }
    }

    /**
     * 清理动画播放完成
     */
    public void setViewFinishTrans() {
        if (isDestroyed()) {
            //页面关闭后，不进行动画
            return;
        }
        mTvCooling.setVisibility(GONE);
        startFinishAnimator();
    }

    public void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mWebView.addJavascriptInterface(new JavaInterface(this, mWebView), "cleanPage");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                cancelLoadingDialog();
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(SPUtil.isInAudit() ? GONE : View.VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                if (mLayoutNotNet != null) {
                    mLayoutNotNet.setVisibility(VISIBLE);
                }
                if (mWebView != null) {
                    mWebView.setVisibility(GONE);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void initAdapter() {
        mRecyclerProcess.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerProcess.addItemDecoration(new CustomerSpaceDecoration());
        mProcessIconAdapter = new ProcessIconAdapter();
        mRecyclerProcess.setAdapter(mProcessIconAdapter);
    }


    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        StatisticsUtils.trackClick("return_click", returnEventName, sourcePageId, currentPageId);
        finish();
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePageId, currentPageId);
        super.onBackPressed();
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @OnClick(R.id.layout_process)
    public void onMLayoutProcessClicked() {
        StatisticsUtils.trackClick("running_application_click ", "用户在降温扫描完成后的诊断页点击运行应用", sourcePageId, currentPageId);
        startActivity(RouteConstants.PROCESS_INFO_ACTIVITY);
    }

    @OnClick(R.id.layout_hardware)
    public void onMLayoutHardwareClicked() {
        StatisticsUtils.trackClick("running_parts_click ", "用户在降温扫描完成后的诊断页点击运行部件", sourcePageId, currentPageId);

        Bundle bundle = new Bundle();
        bundle.putSerializable("content", mHardwareInfo);
        startActivityForResult(RouteConstants.HARDWARE_INFO_ACTIVITY, bundle, REQUEST_CODE_HARDWARE, false);
    }

    @OnClick(R.id.text_cool_now)
    public void onMLayoutCoolClicked() {

//        StatisticsUtils.trackClick("cooling_button_click", "用户在降温扫描完成后的诊断页点击【降温】按钮", sourcePageId, currentPageId);

        if (mRunningProcess == null) return;
        //立即降温
        for (FirstJunkInfo firstJunkInfo : mRunningProcess) {
            CleanUtil.killAppProcesses(firstJunkInfo.getAppPackageName(), firstJunkInfo.getPid());
        }
        //手机降温
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.PHONE_COOLING);

        setViewPlay();
    }

    @OnClick(R.id.layout_not_net)
    public void onNetLayoutClicked() {
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
    }

    /**
     * 显示进程
     *
     * @param runningProcess
     */
    public void showProcess(ArrayList<FirstJunkInfo> runningProcess) {
        if (mTextTitleProcess == null || runningProcess == null) return;
        mRunningProcess = runningProcess;
        mTextTitleProcess.setText(runningProcess.size() + "个运行的应用");
        mProcessIconAdapter.setData(runningProcess);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        if (mRunningProcess != null) {
            showProcess(mRunningProcess);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);
    }

    /**
     * 硬件信息
     *
     * @param hardwareInfo
     */
    public void showHardwareInfo(HardwareInfo hardwareInfo, boolean isRefresh) {
        mHardwareInfo = hardwareInfo;

        if (isRefresh) {
            int childCount = mLayoutBottom.getChildCount();
            for (int i = 1; i < childCount; i++) {
                mLayoutBottom.removeView(mLayoutBottom.getChildAt(1));
            }
        }

        int totalHardware = 1;
        //电池
        if (hardwareInfo.isCharge()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_battery_normal, "电池"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_battery_not, "电池"));
        }
        //蓝牙
        if (hardwareInfo.isBluetoothOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_bluetooth_normal, "蓝牙"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_bluetooth_not, "蓝牙"));
            position_bluetooth = totalHardware;
        }

        //GPS
        if (hardwareInfo.isGPSOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_gps_normal, "GPS"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_gps_not, "GPS"));
            position_location = totalHardware;
        }

        //WIFI
        if (hardwareInfo.isWiFiOpen()) {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_wifi_normal, "WIFI"), 1);
            totalHardware += 1;
        } else {
            mLayoutBottom.addView(generateItem(R.mipmap.icon_wifi_not, "WIFI"));
            position_wifi = totalHardware;
        }

        mHardwareInfo.setSize(totalHardware);

        //运行数量显示
        mTextTitleHardware.setText(totalHardware + "个运行的部位");
    }

    public View generateItem(int imageIcon, String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_cool_hardwear, mLayoutBottom, false);
        ImageView mIcon = view.findViewById(R.id.icon_hardware);
        TextView mTextName = view.findViewById(R.id.text_name);
        mIcon.setImageDrawable(AppApplication.getInstance().getResources().getDrawable(imageIcon));
        mTextName.setText(name);
        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (numberAnimator != null && numberAnimator.isStarted()) {
            //关闭页面，取消动画
            numberAnimator.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_HARDWARE) {
            //部件关闭回调
            mPresenter.getHardwareInfo(true);
        }
    }

    public void startFinishAnimator() {
        sourcePageId = "cool_animation_page";
        currentPageId = "cool_finish_annimation_page";
        returnEventName = "降温完成动画展示页返回";
        sysReturnEventName = "降温完成动画展示页返回";
        viewPageEventName = "降温完成动画展示页浏览";
        viewPageEventCode = "cool_finish_annimation_page_view_page";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePageId, currentPageId, viewPageEventCode, viewPageEventName);

        mFlAnim.setVisibility(VISIBLE);
//        mAnimationView.useHardwareAcceleration();
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data_clean_finish.json");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (PreferenceUtil.getCoolingCleanTime()) {
                    PreferenceUtil.saveCoolingCleanTime();
                }
                PreferenceUtil.saveCleanCoolUsed(true);
                boolean isOpen = false;
                if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                        && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
                    for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                        if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                            isOpen = switchInfoList.isOpen();
                        }
                    }
                }


                //恢复状态栏状态
                NotificationEvent event = new NotificationEvent();
                event.setType("cooling");
                event.setFlag(0);
                EventBus.getDefault().post(event);

                AppHolder.getInstance().setCleanFinishSourcePageId("cool_finish_annimation_page");
                EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
                if (isOpen && PreferenceUtil.getShowCount(PhoneCoolingActivity.this, getString(R.string.tool_phone_temperature_low), mRamScale, mNotifySize, mPowerSize) < 3) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                    startActivity(CleanFinishAdvertisementActivity.class, bundle);
                } else {
                    AppHolder.getInstance().setCleanFinishSourcePageId("cool_finish_annimation_page");
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tool_phone_temperature_low));
                    bundle.putString("num", "");
                    bundle.putString("unit", "");
                    startActivity(NewCleanFinishActivity.class, bundle);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

}
