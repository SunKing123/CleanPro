package com.xiaoniu.cleanking.ui.newclean.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.callback.OnColorChangeListener;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.interfac.AnimationEnd;
import com.xiaoniu.cleanking.ui.main.interfac.AnimationStateListener;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.interfice.CleanOverListener;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.statistic.NiuDataAPI;

/**
 * 1.2.1 版本清理动画
 */
public class NewCleanAnimView extends RelativeLayout {

    private Context mContext;
    private ImageView mIconOuter;
    private ImageView mIconInner;
    private LinearLayout mLayoutScan;
    private LottieAnimationView mAnimationView;
    private RelativeLayout mLayoutCount;
    private TextView mTextCount;
    private TextView mTextUnit;
    private RelativeLayout mLayoutRoot;
    private TextView mTvAnimTitle;

    private CleanOverListener mCleanOverListener;
    private AnimationStateListener stateListener;

    public void setCleanOverListener(CleanOverListener mCleanOverListener) {
        this.mCleanOverListener = mCleanOverListener;
    }


    public void setStateListener(AnimationStateListener customListener){
        stateListener = customListener;
    }
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
    public NewCleanAnimView(Context context) {
        super(context);
        initView(context);
    }

    public NewCleanAnimView(Context context, AttributeSet attrs) {
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_new_clean_anim, this, true);
        mIconOuter = v.findViewById(R.id.icon_outer);
        mIconInner = v.findViewById(R.id.icon_inner);
        mLayoutScan = v.findViewById(R.id.layout_scan);
        mAnimationView = v.findViewById(R.id.view_lottie);
        mLayoutCount = v.findViewById(R.id.layout_count);
        mTextCount = v.findViewById(R.id.text_count);
        mTextUnit = v.findViewById(R.id.text_unit);
        mLayoutRoot = v.findViewById(R.id.layout_root);
        mTvAnimTitle = v.findViewById(R.id.tv_anim_title);

        mTextCount.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        mTextUnit.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
    }

    public void setData(CountEntity countEntity) {
        if (countEntity == null) {
            return;
        }
        mCountEntity = countEntity;
        mTextCount.setText(mCountEntity.getTotalSize());
        mTextUnit.setText(mCountEntity.getUnit());
    }

    //Step1:上面红色布局和中间1dp的布局动画开始
    public void startCleanAnim(boolean isNeedTranslation) {
        startMiddleAnim(isNeedTranslation);
    }

    /**
     * 第一阶段控件 下移到中心
     */
    public void startMiddleAnim(boolean isNeedTranslation) {
        try {
            //位移的距离
            int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - DisplayUtils.dip2px(200);
            ObjectAnimator outerY = ObjectAnimator.ofFloat(mIconOuter, "translationY", mIconOuter.getTranslationY(), height);
            ObjectAnimator scanY = ObjectAnimator.ofFloat(mLayoutScan, "translationY", mLayoutScan.getTranslationY(), height);
            ObjectAnimator countY = ObjectAnimator.ofFloat(mLayoutCount, "translationY", mLayoutCount.getTranslationY(), height);
            ObjectAnimator innerY = ObjectAnimator.ofFloat(mIconInner, "translationY", mIconInner.getTranslationY(), height);

            ObjectAnimator innerAlpha = ObjectAnimator.ofFloat(mIconInner, "alpha", 1, 1);
            ObjectAnimator outerAlpha = ObjectAnimator.ofFloat(mIconOuter, "alpha", 1, 1);
            ObjectAnimator scanAlpha = ObjectAnimator.ofFloat(mLayoutScan, "alpha", 1, 1);
            ObjectAnimator countAlpha = ObjectAnimator.ofFloat(mLayoutCount, "alpha", 1, 1);

            int time;
            if (isNeedTranslation) {
                time = 0;
            } else {
                time = 0;
            }
            outerY.setDuration(time);
            scanY.setDuration(time);
            innerY.setDuration(time);
            countY.setDuration(time);

            innerAlpha.setDuration(800);
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ObjectAnimator rotation4;
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
            rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 325);

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示吸收动画
     */
    private void showLottieView() {
//        mAnimationView.useHardwareAcceleration();
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
//        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("data_clean_finish.json");
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(stateListener !=null){
                    stateListener.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(stateListener !=null){
                    stateListener.onAnimationEnd();
                }
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
    private static final int FirstLevel = 0xffFD6F46;

    private static final int SecondLevel = 0xffF1D53B;
    /**
     * 第三阶段 绿色
     */
    private static final int ThirdLevel = 0xff06C581;

    private ValueAnimator valueAnimator;

    private boolean isStopClean = false;

    //背景颜色是否已变为红色
    private boolean isChangeRed = false;

    public void setStopClean(boolean stopClean) {
        isStopClean = stopClean;
    }

    /**
     * 开始加速旋转
     *
     * @param animatorSet
     * @param countEntity
     */
    public void startClean(AnimatorSet animatorSet, CountEntity countEntity) {
        if (countEntity==null){
            return;
        }
        valueAnimator = ObjectAnimator.ofFloat(Float.parseFloat(countEntity.getTotalSize()), 0);
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
                if (isStopClean)
                    return;
                if (animatorSet != null) {
                    animatorSet.end();
                }
                if (mCleanOverListener != null){
                    mCleanOverListener.AnimOver();
                }
                //更新首页的显示文案
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

        colorAnim1.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (FirstLevel == animatedValue)
                isChangeRed = true;
            if (mOnColorChangeListener != null) {
                mOnColorChangeListener.onColorChange(animatedValue);
            }
        });

        AnimatorSet animatorSetTimer = new AnimatorSet();
        if (!isChangeRed)
            animatorSetTimer.playTogether(valueAnimator, colorAnim1);
        else
            animatorSetTimer.playTogether(valueAnimator);
        animatorSetTimer.start();
    }

    public void stopClean() {
        setStopClean(true);
        if (valueAnimator != null)
            valueAnimator.cancel();
        // 终止清理，停止吸收动画
        if (mAnimationView != null)
            mAnimationView.cancelAnimation();
        // 终止清理，停止风扇旋转
        if (rotation4 != null)
            rotation4.cancel();
    }
}

