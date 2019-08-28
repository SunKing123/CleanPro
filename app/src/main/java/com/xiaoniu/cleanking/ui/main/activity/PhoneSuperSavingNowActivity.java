package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
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
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.cleanking.widget.roundedimageview.RoundedImageView;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;

import java.lang.ref.WeakReference;
import java.util.List;

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
    public List<MultiItemInfo> mSelectedList;
    private View mLayIconAnim;
    private RoundedImageView mIvIcon1;
    private RoundedImageView mIvIcon2;

    class MyHandler extends Handler {
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
                    sendEmptyMessageDelayed(2, 800);
                } else {
                    sendEmptyMessageDelayed(1, 800);
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
        PhoneSuperPowerDetailActivity.sSelectedList = null;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
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
    protected void setListener() {
        mBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mLayoutNotNet.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        if (num <= 0) {
            showFinishWebview();
        } else {
            showStartAnim();
            showIconAnim();
            mHandler.sendEmptyMessageDelayed(2, 800);
        }
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showIconAnim() {
        mLayIconAnim.setVisibility(View.VISIBLE);
        mLayIconAnim.post(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }


    private void playIconAnim1(final ImageView ivIcon) {
        ivIcon.setVisibility(View.VISIBLE);
        float distance = DisplayUtils.dip2px(40);
        ValueAnimator anim1 = ValueAnimator.ofFloat(0f, distance);
        anim1.setDuration(800);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());

        ivIcon.setTranslationY(0);
        ivIcon.setScaleX(1);
        ivIcon.setScaleY(1);
        ivIcon.setAlpha(1f);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float percent = currentValue / distance;
                ivIcon.setScaleX(1 - percent);
                ivIcon.setScaleY(1 - percent);

                if (1 - percent <= 0.5) {
                    percent = 0.5f;
                }
                ivIcon.setAlpha(1 - percent);
                ivIcon.setTranslationY(-currentValue);
            }
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
        anim2.setDuration(800);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
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
            }
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
                return AppUtils.getAppIcon(this, childInfo.packageName);
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
        SPUtil.setLastPowerCleanTime(System.currentTimeMillis());
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);

        mLottieAnimationFinishView.useHardwareAcceleration();
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
                showFinishWebview();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void showFinishWebview() {
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
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.layout_not_net:
                mNestedScrollWebView.loadUrl(PreferenceUtil.getWebViewUrl());
                break;
        }
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
                    mLayoutNotNet.setVisibility(View.VISIBLE);
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
}
