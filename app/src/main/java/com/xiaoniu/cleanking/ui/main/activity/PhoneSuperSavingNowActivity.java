package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.roundedimageview.RoundedImageView;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 超强省电中...
 */
public class PhoneSuperSavingNowActivity extends BaseActivity implements View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private ImageView mBack;
    private TextView mBtnCancel;
    private TextView mTvNum;
    private RelativeLayout mRlResult;
    private LinearLayout mLlResultTop;
    private NestedScrollWebView mNestedScrollWebView;
    private JavaInterface javaInterface;
    private LinearLayout mLayoutNotNet;
    private FrameLayout mFlAnim;
    private ImageView mIvAnimationStartView;
    private LottieAnimationView mLottieAnimationFinishView;
    private boolean isError = false;

    private MyHandler mHandler = new MyHandler(this);

    private int num;
    private TextView mTvAllNum;
    private List<MultiItemInfo> mSelectedList;
    private View mLayIconAnim;
    private RoundedImageView mIvIcon1;
    private RoundedImageView mIvIcon2;
    private boolean isFinish = false;
    private int mTime = 800;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小

    String sourcePage = "";
    String currentPage = "";
    String sysReturnEventName = "";
    String returnEventName = "";

    String viewPageEventCode = "";
    String viewPageEventName = "";
    private boolean mIsFinish; //是否点击了返回键

    private class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                showFinishAnim();
            } else if (msg.what == 2) {
                num--;
                mTvNum.setText(String.valueOf(num));
                if (num > 0) {
                    sendEmptyMessageDelayed(2, mTime);
                } else {
                    sendEmptyMessageDelayed(1, mTime);
                }
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_phone_super_saving_now;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
        num = intent.getIntExtra("processNum", 0);
        mSelectedList = PhoneSuperPowerDetailActivity.sSelectedList;
        if (mSelectedList != null && mSelectedList.size() > 0) {
            mTime = 3000 / mSelectedList.size();
        }
        PhoneSuperPowerDetailActivity.sSelectedList = null;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        viewPageEventCode = "powersave_animation_page_view_page";
        viewPageEventName = "用户在省电动画页浏览";
        sourcePage = "powersave_scan_result_page";
        currentPage = "powersave_animation_page";
        sysReturnEventName = "用户在省电动画页返回";
        returnEventName = "用户在省电动画页返回";


        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        if (Build.VERSION.SDK_INT < 26) {
            getAccessListBelow();
        }
        mAppBarLayout = findViewById(R.id.app_power_saving_bar_layout);
        mBack = findViewById(R.id.iv_back);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mTvNum = findViewById(R.id.tv_num);
        mTvAllNum = findViewById(R.id.tvAllNum);

        mRlResult = findViewById(R.id.rl_result);
        mLlResultTop = findViewById(R.id.viewt_finish);
        mNestedScrollWebView = findViewById(R.id.web_view);
        mLayoutNotNet = findViewById(R.id.layout_not_net);
        mIvAnimationStartView = findViewById(R.id.view_lottie_super_saving_sleep);
        mFlAnim = findViewById(R.id.fl_anim);
        mLottieAnimationFinishView = findViewById(R.id.view_lottie);

        mLayIconAnim = findViewById(R.id.layIconAnim);
        mIvIcon1 = findViewById(R.id.ivIcon1);
        mIvIcon2 = findViewById(R.id.ivIcon2);

        mTvNum.setText(String.valueOf(num));
        mTvAllNum.setText("/" + String.valueOf(num));
        initWebView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mLayoutNotNet.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        if (num <= 0) {
            showCleanFinishUI();
        } else {
            showStartAnim();
            showIconAnim();
            mHandler.sendEmptyMessageDelayed(2, mTime);
        }
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showIconAnim() {
        mLayIconAnim.setVisibility(View.VISIBLE);
        mLayIconAnim.post(() -> {
            Bitmap bitmap = getNextImg();
            if (bitmap != null) {
                mIvIcon1.setImageBitmap(bitmap);
                playIconAnim1(mIvIcon1);
            } else {
                mIvIcon1.setVisibility(View.GONE);
            }

            bitmap = getNextImg();
            if (bitmap != null) {
                mIvIcon2.setImageBitmap(bitmap);
                playIconAnim2(mIvIcon2);
            } else {
                mIvIcon2.setVisibility(View.GONE);
            }
        });
    }

    private void playIconAnim1(final ImageView ivIcon) {
        ivIcon.setVisibility(View.VISIBLE);
        float distance = DisplayUtils.dip2px(40);
        ValueAnimator anim1 = ValueAnimator.ofFloat(0f, distance);
        anim1.setDuration(mTime);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());

        ivIcon.setTranslationY(0);
        ivIcon.setScaleX(1);
        ivIcon.setScaleY(1);
        ivIcon.setAlpha(1f);
        anim1.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            float percent = currentValue / distance;
            ivIcon.setScaleX(1 - percent);
            ivIcon.setScaleY(1 - percent);

            if (1 - percent <= 0.5) {
                percent = 0.5f;
            }
            ivIcon.setAlpha(1 - percent);
            ivIcon.setTranslationY(-currentValue);
        });

        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Bitmap bitmap = getNextImg();
                if (bitmap != null) {
                    ivIcon.setImageBitmap(bitmap);
                    playIconAnim2(ivIcon);
                } else {
                    ivIcon.setVisibility(View.GONE);
                }

            }
        });
        anim1.start();
    }

    private void playIconAnim2(final ImageView ivIcon) {
        ivIcon.setVisibility(View.VISIBLE);
        float distance = DisplayUtils.dip2px(40);
        ivIcon.setTranslationY(distance);
        ivIcon.setScaleX(0);
        ivIcon.setScaleY(0);
        ivIcon.setAlpha(0.5f);

        ValueAnimator anim2 = ValueAnimator.ofFloat(distance, 0f);
        anim2.setDuration(mTime);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());
        anim2.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            float percent = currentValue / distance;
            ivIcon.setScaleX(1 - percent);
            ivIcon.setScaleY(1 - percent);
            float alpha = 1 - percent;
            if (alpha <= 0.5) {
                alpha = 0.5f;
            }
            ivIcon.setAlpha(alpha);
            ivIcon.setTranslationY(currentValue);
        });

        anim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                playIconAnim1(ivIcon);
            }
        });

        anim2.start();
    }

    private int mCurImgIndex = 0;

    private Bitmap getNextImg() {
        if (mSelectedList != null && mCurImgIndex < mSelectedList.size()) {
            MultiItemInfo itemInfo = mSelectedList.get(mCurImgIndex);
            if (itemInfo instanceof PowerChildInfo) {
                mCurImgIndex++;
                PowerChildInfo childInfo = (PowerChildInfo) itemInfo;
                Bitmap icon = AppUtils.getAppIcon(this, childInfo.packageName);
                if (icon == null) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.clean_icon_apk);
                }
                return icon;
            }
        }
        return null;
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showStartAnim() {
        //获取背景，并将其强转成AnimationDrawable
        AnimationDrawable animationDrawable = (AnimationDrawable) mIvAnimationStartView.getBackground();
        //判断是否在运行
        if (!animationDrawable.isRunning()) {
            //开启帧动画
            animationDrawable.start();
        }
    }

    /**
     * 完成动画
     */
    public void showFinishAnim() {
        viewPageEventCode = "powersave_finish_annimation_page_view_page";
        viewPageEventName = "省电完成动画展示页浏览";
        sourcePage = "powersave_animation_page";
        currentPage = "powersave_finish_annimation_page";
        sysReturnEventName = "省电完成动画展示页返回";
        returnEventName = "省电完成动画展示页返回";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);

        SPUtil.setLastPowerCleanTime(System.currentTimeMillis());
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);

        if (mIvAnimationStartView != null)
            mIvAnimationStartView.setVisibility(View.INVISIBLE);

//        mLottieAnimationFinishView.useHardwareAcceleration();
        mLottieAnimationFinishView.setImageAssetsFolder("images");
        mLottieAnimationFinishView.setAnimation("data_clean_finish.json");
        mLottieAnimationFinishView.playAnimation();
        mLottieAnimationFinishView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mRlResult.setVisibility(View.GONE);
                mFlAnim.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showCleanFinishUI();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void showCleanFinishUI() {
        if (mIsFinish) return;
        //保存超强省电 省电完成时间
        if (PreferenceUtil.getPowerCleanTime()) {
            PreferenceUtil.savePowerCleanTime();
        }
        PreferenceUtil.saveCleanPowerUsed(true);
        boolean isOpen = false;
        //solve umeng error --> SwitchInfoList.getData()' on a null object reference
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        AppHolder.getInstance().setCleanFinishSourcePageId("powersave_finish_annimation_page");
        if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.tool_super_power_saving), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_super_power_saving));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_super_power_saving));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            Intent intent = new Intent(PhoneSuperSavingNowActivity.this, NewCleanFinishActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        finish();
    }

    private void showFinishWebview() {
        isFinish = true;

        mLottieAnimationFinishView.setVisibility(View.GONE);
        mFlAnim.setVisibility(View.GONE);
        mRlResult.setVisibility(View.GONE);
        mAppBarLayout.setExpanded(true);
        mLlResultTop.setVisibility(View.VISIBLE);
        mNestedScrollWebView.setVisibility(View.VISIBLE);
        int startHeight = ScreenUtils.getFullActivityHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, 0);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mRlResult.getLayoutParams();
        anim.addUpdateListener(valueAnimator -> {
            rlp.topMargin = (int) valueAnimator.getAnimatedValue();
            if (mRlResult != null)
                mRlResult.setLayoutParams(rlp);
        });
        anim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //省电完成 返回点击
                mIsFinish = true;
                if (isFinish)
                    StatisticsUtils.trackClick("Super_Power_Saving_Completion_Return_click", "\"超强省电完成返回\"点击", "Super_Power_Saving_page", "Super_Power_Saving_Completion_page");
            case R.id.btn_cancel:
                StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
                finish();
                break;
            case R.id.layout_not_net:
                mNestedScrollWebView.loadUrl(PreferenceUtil.getWebViewUrl());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mIsFinish = true;
        StatisticsUtils.trackClick("system_return_click", returnEventName, sourcePage, currentPage);
        super.onBackPressed();
    }

    public void initWebView() {
        javaInterface = new JavaInterface(this, mNestedScrollWebView);
        WebSettings settings = mNestedScrollWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mNestedScrollWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mNestedScrollWebView.addJavascriptInterface(javaInterface, "cleanPage");
        javaInterface.setListener(() -> {

        });
        mNestedScrollWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                StatisticsUtils.trackClick("Super_Power_Saving_Completion_view_page", "\"超强省电完成\"浏览", "Super_Power_Saving_page", "Super_Power_Saving_Completion_page");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(View.GONE);
                    }
                    if (mNestedScrollWebView != null) {
//                        mNestedScrollWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                if (mLayoutNotNet != null) {
//                    mLayoutNotNet.setVisibility(View.VISIBLE);
                }
                if (mNestedScrollWebView != null) {
                    mNestedScrollWebView.setVisibility(View.GONE);
                }
            }
        });

        mNestedScrollWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void reduceSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }

                @Override
                public void totalSize(int p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    getAccessListBelow(strings);
                });
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }
}
