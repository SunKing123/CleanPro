package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.callback.OnColorChangeListener;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.utils.DeviceUtils;


/**
 * @author zhang
 */
public class CleanAnimView extends RelativeLayout {
    private Context mContext;
    LinearLayout mLineTitle;
    RelativeLayout viewt;
    ImageView mIconOuter;
    ImageView mIconInner;
    LinearLayout mLayoutScan;
    LottieAnimationView mAnimationView;
    RelativeLayout mLayoutCount;
    TextView mTextCount;
    TextView mTextUnit;
    TextView mTextSize;
    TextView mTextGb;
    RelativeLayout mLayoutRoot;
    ConstraintLayout mLayoutCleanFinish;
    WebView mWebView;
    LinearLayout mLayoutNotNet;

    /**
     * 第二阶段
     */
    AnimatorSet animatorStep2;

    private OnColorChangeListener mOnColorChangeListener;

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        mOnColorChangeListener = onColorChangeListener;
    }

    /**
     * 清理数据实体类
     */
    private CountEntity mCountEntity;

    //倒计时图片
    public CleanAnimView(Context context) {
        super(context);
        initView(context);
    }

    public CleanAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 初始化布局
     *
     * @return
     */
    public void initView(Context context) {

        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_clean_anim, this, true);
        viewt = v.findViewById(R.id.viewt);
        mLineTitle = v.findViewById(R.id.line_title);
        mIconOuter = v.findViewById(R.id.icon_outer);
        mIconInner = v.findViewById(R.id.icon_inner);
        mLayoutScan = v.findViewById(R.id.layout_scan);
        mAnimationView = v.findViewById(R.id.view_lottie);
        mLayoutCount = v.findViewById(R.id.layout_count);
        mTextCount = v.findViewById(R.id.text_count);
        mTextUnit = v.findViewById(R.id.text_unit);
        mTextSize = v.findViewById(R.id.tv_size);
        mTextGb = v.findViewById(R.id.tv_gb);
        mLayoutRoot = v.findViewById(R.id.layout_root);
        mLayoutCleanFinish = v.findViewById(R.id.layout_clean_finish);
        mWebView = v.findViewById(R.id.web_view);
        mLayoutNotNet = v.findViewById(R.id.layout_not_net);

        initWebView();

        mLayoutNotNet.setOnClickListener(view-> onTvRefreshClicked());
    }

    public void onTvRefreshClicked() {
        mWebView.loadUrl(ApiModule.Base_H5_Host);
    }

    boolean isError = false;

    public void initWebView() {
        String url = ApiModule.Base_H5_Host;
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
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
                    mLayoutNotNet.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
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

    public void setData(CountEntity countEntity) {
        if (countEntity == null) {
            return;
        }
        mCountEntity = countEntity;
        mTextCount.setText(mCountEntity.getTotalSize());
        mTextUnit.setText(mCountEntity.getUnit());
        mTextSize.setText(mCountEntity.getTotalSize());
        mTextGb.setText(mCountEntity.getUnit());
    }

    //Step1:上面红色布局和中间1dp的布局动画开始
    public void startTopAnim(boolean isNeedTranslation) {
        int startHeight = 0;
        if (isNeedTranslation) {
            startHeight = DeviceUtils.dip2px(150);
        } else {
            startHeight = DeviceUtils.dip2px(56);
        }

        int endHeight = DeviceUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        LayoutParams rlp = (LayoutParams) viewt.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                rlp.height = currentValue;
                viewt.setLayoutParams(rlp);
            }
        });
        anim.start();
        startMiddleAnim(isNeedTranslation);
    }

    /**
     * 第一阶段控件 下移到中心
     */
    public void startMiddleAnim(boolean isNeedTranslation) {
        //位移的距离
        int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - DeviceUtils.dip2px(150);
        ObjectAnimator outerY = ObjectAnimator.ofFloat(mIconOuter, "translationY", mIconOuter.getTranslationY(), height);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(mLayoutScan, "translationY", mLayoutScan.getTranslationY(), height);
        ObjectAnimator countY = ObjectAnimator.ofFloat(mLayoutCount, "translationY", mLayoutCount.getTranslationY(), height);
        ObjectAnimator innerY = ObjectAnimator.ofFloat(mIconInner, "translationY", mIconInner.getTranslationY(), height);

        ObjectAnimator innerAlpha = ObjectAnimator.ofFloat(mIconInner, "alpha", 0, 1);
        ObjectAnimator outerAlpha = ObjectAnimator.ofFloat(mIconOuter, "alpha", 0, 1);
        ObjectAnimator scanAlpha = ObjectAnimator.ofFloat(mLayoutScan, "alpha", 0, 1);
        ObjectAnimator countAlpha = ObjectAnimator.ofFloat(mLayoutCount, "alpha", 0, 1);

        int time = 0;
        if (isNeedTranslation) {
            time = 1000;
        } else {
            time = 10;
        }
        outerY.setDuration(time);
        scanY.setDuration(time);
        innerY.setDuration(time);
        innerAlpha.setDuration(1000);
        countY.setDuration(time);
        outerAlpha.setDuration(1000);
        scanAlpha.setDuration(1000);
        countAlpha.setDuration(1000);

        //第一阶段倒转
        ObjectAnimator rotationFistStep = ObjectAnimator.ofFloat(mIconInner, "rotation", 0, -35f);
        rotationFistStep.setDuration(600);

        innerAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIconInner.setVisibility(View.VISIBLE);
                mIconOuter.setVisibility(View.VISIBLE);
                mLayoutScan.setVisibility(View.VISIBLE);
                new Handler().postDelayed(rotationFistStep::start, 400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //第二阶段开始
                secondLevel(mIconInner, mIconOuter, mCountEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if (isNeedTranslation) {
            animatorSet.playTogether(outerY, innerY, innerAlpha, outerAlpha, scanAlpha, scanY, countY);
        } else {
            animatorSet.playTogether(innerAlpha, outerAlpha, scanAlpha, countAlpha, outerY, countY,innerY, scanY);
        }


        animatorSet.start();

    }

    /**
     * 第二阶段 开始旋转
     *
     * @param iconInner
     * @param iconOuter
     * @param countEntity
     */
    public void secondLevel(ImageView iconInner, ImageView iconOuter, CountEntity countEntity) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
//        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 325,-35,325,-35,325,-35);
        ObjectAnimator rotation3 = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", -35,325);

        rotation.setDuration(500);
//        rotation2.setDuration(1000);

        rotation3.setDuration(300);
        rotation3.setRepeatCount(ValueAnimator.INFINITE);
        rotation3.setInterpolator(new LinearInterpolator());

        rotation4.setDuration(200);
        rotation4.setRepeatCount(ValueAnimator.INFINITE);
        rotation4.setInterpolator(new LinearInterpolator());


        animatorStep2 = new AnimatorSet();
        animatorStep2.playTogether(rotation3);

        rotation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //500ms后开始显示回收光点
                new Handler().postDelayed(() -> showLottieView(), 600);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startClean(animatorStep2, countEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

//        rotation.setInterpolator(new AccelerateInterpolator());
//        rotation2.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, rotation4);

        animatorSet.start();
    }

    /**
     * 显示吸收动画
     */
    private void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("data2.json");
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.playAnimation();
    }

    /**
     * 第一阶段  红色
     */
    private static final int FirstLevel = 0xffFD6F46;

    private static final int SecondLevel = 0xffF1D53B;
    /**
     * 第三阶段 绿色
     */
    private static final int ThirdLevel = 0xff06C581;

    /**
     * 开始加速旋转
     *
     * @param animatorSet
     * @param countEntity
     */
    public void startClean(AnimatorSet animatorSet, CountEntity countEntity) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(Float.valueOf(countEntity.getTotalSize()), 0);
        valueAnimator.setDuration(5000);
        String unit = countEntity.getUnit();
        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            mTextCount.setText(String.format("%s", Math.round(animatedValue)));
            mTextUnit.setText(unit);
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatorSet != null) {
                    animatorSet.end();
                }
                setViewTrans();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(mLayoutRoot, "backgroundColor", FirstLevel, SecondLevel, ThirdLevel);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(1000);
        colorAnim1.setStartDelay(4000);

        colorAnim1.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mOnColorChangeListener != null) {
                mOnColorChangeListener.onColorChange(animatedValue);
            }
        });

        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(valueAnimator, colorAnim1);
        animatorSetTimer.start();

    }


    //数字动画播放完后火箭上移，布局高度缩小
    public void setViewTrans() {
        int bottom = mLineTitle.getBottom();
        mLayoutCleanFinish.setVisibility(VISIBLE);
        int startHeight = DeviceUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight - bottom, 0);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mLayoutCleanFinish.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.topMargin = currentValue;
            mLayoutCleanFinish.setLayoutParams(rlp);
        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
    }
}

