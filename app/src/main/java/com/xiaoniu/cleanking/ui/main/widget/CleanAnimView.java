package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.callback.OnColorChangeListener;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.interfac.AnimationEnd;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.statistic.NiuDataAPI;


/**
 * @author zhang
 */
public class CleanAnimView extends RelativeLayout {
    private Context mContext;
    LinearLayout mLineTitle;
    TextView tv_qltitle;
    RelativeLayout viewt;
    ImageView mIconOuter;
    ImageView mIconInner;
    LinearLayout mLayoutScan;
    LottieAnimationView mAnimationView;
    LottieAnimationView mAnimationFinishView;
    RelativeLayout mLayoutCount;
    TextView mTextCount;
    TextView mTextUnit;
    TextView mTextSize;
    ImageView iv_back;
    ImageView iv_dun;
    TextView mTextGb;
    RelativeLayout mLayoutRoot;
    ConstraintLayout mLayoutCleanFinish;
    NestedScrollWebView mWebView;
    LinearLayout mLayoutNotNet;
    TextView mTvAnimTitle;
    FrameLayout mFlAnim;
    NestedScrollView mScrollView;

    private AnimationEnd mAnimationEnd;

    public void setAnimationEnd(AnimationEnd mAnimationEnd) {
        this.mAnimationEnd = mAnimationEnd;
    }

    /**
     * 一键清理页面
     */
    public static final int page_junk_clean = 0;
    /**
     * 手机清理页面
     */
    public static final int page_file_clean = 1;
    public static final int page_file_wxclean = 2;

    private int currentPage = 0;

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
        iv_dun = v.findViewById(R.id.iv_dun);
        iv_back = v.findViewById(R.id.iv_back);
        tv_qltitle = v.findViewById(R.id.tv_qltitle);
        mIconOuter = v.findViewById(R.id.icon_outer);
        mIconInner = v.findViewById(R.id.icon_inner);
        mLayoutScan = v.findViewById(R.id.layout_scan);
        mAnimationView = v.findViewById(R.id.view_lottie);
        mAnimationFinishView = v.findViewById(R.id.view_lottie_finish);
        mLayoutCount = v.findViewById(R.id.layout_count);
        mTextCount = v.findViewById(R.id.text_count);
        mTextUnit = v.findViewById(R.id.text_unit);
        mTextSize = v.findViewById(R.id.tv_size);
        mTextGb = v.findViewById(R.id.tv_gb);
        mLayoutRoot = v.findViewById(R.id.layout_root);
        mLayoutCleanFinish = v.findViewById(R.id.layout_clean_finish);
        mWebView = v.findViewById(R.id.web_view);
        mLayoutNotNet = v.findViewById(R.id.layout_not_net);
        mTvAnimTitle = v.findViewById(R.id.tv_anim_title);
        mFlAnim = v.findViewById(R.id.fl_anim);
        mScrollView = v.findViewById(R.id.n_scroll_view);
        initWebView();
        iv_back.setOnClickListener(v1 -> {
            if (listener != null) {
                listener.onClick();
            }
        });
        mLayoutNotNet.setOnClickListener(view -> onTvRefreshClicked());
    }

    public void onTvRefreshClicked() {
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
    }

    boolean isError = false;
    JavaInterface javaInterface;

    public void initWebView() {
        javaInterface = new JavaInterface((Activity) mContext, mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mWebView.addJavascriptInterface(javaInterface, "cleanPage");
        javaInterface.setListener(() -> {

        });
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
                        mLayoutNotNet.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
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

    public void setData(CountEntity countEntity, int page) {
        if (countEntity == null) {
            return;
        }
        currentPage = page;
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
            startHeight = DisplayUtils.dip2px(150);
        } else {
            startHeight = DisplayUtils.dip2px(56);
        }

        int endHeight = DisplayUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        LayoutParams rlp = (LayoutParams) viewt.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.height = currentValue;
            viewt.setLayoutParams(rlp);
        });
        anim.start();
        startMiddleAnim(isNeedTranslation);
    }

    /**
     * 第一阶段控件 下移到中心
     */
    public void startMiddleAnim(boolean isNeedTranslation) {
        //位移的距离
        try {
            int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - DisplayUtils.dip2px(150);
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
            innerAlpha.setDuration(800);
            countY.setDuration(time);
            outerAlpha.setDuration(800);
            scanAlpha.setDuration(800);
            countAlpha.setDuration(800);

            //第一阶段倒转
            ObjectAnimator rotationFistStep = ObjectAnimator.ofFloat(mIconInner, "rotation", 0, -35f);
            rotationFistStep.setDuration(500);

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
                animatorSet.playTogether(innerAlpha, outerAlpha, scanAlpha, countAlpha, outerY, countY, innerY, scanY);
            }
            animatorSet.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 第二阶段 开始旋转
     *
     * @param iconInner
     * @param iconOuter
     * @param countEntity
     */
    public void secondLevel(ImageView iconInner, ImageView iconOuter, CountEntity countEntity) {
        try {
            ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
            ObjectAnimator rotation3 = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
            ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 325);

            rotation.setDuration(500);

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
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(rotation, rotation4);

            animatorSet.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
     * 显示完成动画
     */
    private void showFinishLottieView() {
        mIconOuter.setVisibility(GONE);
        mIconInner.setVisibility(GONE);
        mLayoutCount.setVisibility(GONE);
        mLayoutScan.setVisibility(GONE);
        mTvAnimTitle.setVisibility(VISIBLE);
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("yindao2.json");
        mAnimationView.setImageAssetsFolder("images_game_yindao2");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setViewTrans();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 第一阶段  红色
     */
    private static final int FirstLevel = 0xffFF6862;

    private static final int SecondLevel = 0xffFFB231;
    /**
     * 第三阶段 绿色
     */
    private static final int ThirdLevel = 0xff29CABC;

    /**
     * 开始加速旋转
     *
     * @param animatorSet
     * @param countEntity
     */
    public void startClean(AnimatorSet animatorSet, CountEntity countEntity) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(Float.valueOf(countEntity.getTotalSize()), 0);
        valueAnimator.setDuration(3000);
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
                showFinishLottieView();

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
        colorAnim1.setStartDelay(100);
        ValueAnimator colorAnim2 = ObjectAnimator.ofInt(mLineTitle, "backgroundColor", FirstLevel, SecondLevel, ThirdLevel);
        colorAnim2.setEvaluator(new ArgbEvaluator());
        colorAnim2.setDuration(1000);
        colorAnim2.setStartDelay(100);

        colorAnim1.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mOnColorChangeListener != null) {
                mOnColorChangeListener.onColorChange(animatedValue);
            }
        });

        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(valueAnimator, colorAnim1, colorAnim2);
        animatorSetTimer.start();

    }

    //数字动画播放完后火箭上移，布局高度缩小
    public void setViewTrans() {
        showWebView();

        if (currentPage == page_file_clean) {
            NiuDataAPI.onPageStart("mobile_clean_up_page_view", "手机清理页浏览");
        } else if (currentPage == page_junk_clean) {
            NiuDataAPI.onPageStart("clean_up_page_view", "清理完成页浏览");
        }
    }

    public void showWebView() {
        if (mAnimationEnd != null)
            mAnimationEnd.onAnimationEnd();
    }

    public void setAnimTitle(String animTitle) {
        mTvAnimTitle.setText(animTitle);
    }

    public void setTitle(String title) {
        tv_qltitle.setText(title);
    }

    public void setIcon(int res, int width, int height) {
        iv_dun.setImageResource(res);
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) iv_dun.getLayoutParams();
        llp.width = width;
        llp.height = height;
        iv_dun.setLayoutParams(llp);
    }

    onBackClickListener listener;

    public void setListener(onBackClickListener listener) {
        this.listener = listener;
    }

    public interface onBackClickListener {
        void onClick();
    }
}

