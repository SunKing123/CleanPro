package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
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
    TextView mTextCount;
    RelativeLayout mLayoutRoot;
    ConstraintLayout mLayoutCleanFinish;
    onAnimEndListener listener;
    /**
     * 清理数据实体类
     */
    private CountEntity mCountEntity;

    public void setListener(onAnimEndListener listener) {
        this.listener = listener;
    }

    //倒计时图片
    public CleanAnimView(Context context) {
        super(context);
        initView(context);
    }

    public CleanAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public interface onAnimEndListener {
        public void onAnimEnd();
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
        mTextCount = v.findViewById(R.id.text_count);
        mLayoutRoot = v.findViewById(R.id.layout_root);
        mLayoutCleanFinish = v.findViewById(R.id.layout_clean_finish);
    }

    public void setData(CountEntity countEntity) {
        mCountEntity = countEntity;
        mTextCount.setText(mCountEntity.getTotalSize() + mCountEntity.getUnit());
    }

    //Step1:上面红色布局和中间1dp的布局动画开始
    public void startTopAnim() {
        int startHeight = DeviceUtils.dip2px(150);
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
        startMiddleAnim();
    }

    /**
     * 第一阶段控件 下移到中心
     */
    public void startMiddleAnim() {
        //位移的距离
        int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - DeviceUtils.dip2px(150);
        ObjectAnimator outerY = ObjectAnimator.ofFloat(mIconOuter, "translationY", mIconOuter.getTranslationY(), height);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(mLayoutScan, "translationY", mLayoutScan.getTranslationY(), height);
        ObjectAnimator countY = ObjectAnimator.ofFloat(mTextCount, "translationY", mTextCount.getTranslationY(), height);
        ObjectAnimator innerY = ObjectAnimator.ofFloat(mIconInner, "translationY", mIconInner.getTranslationY(), height);

        ObjectAnimator innerAlpha = ObjectAnimator.ofFloat(mIconInner, "alpha", 0, 1);
        ObjectAnimator outerAlpha = ObjectAnimator.ofFloat(mIconOuter, "alpha", 0, 1);
        ObjectAnimator scanAlpha = ObjectAnimator.ofFloat(mLayoutScan, "alpha", 0, 1);

        outerY.setDuration(1000);
        scanY.setDuration(1000);
        innerY.setDuration(1000);
        innerAlpha.setDuration(1000);
        countY.setDuration(1000);
        outerAlpha.setDuration(1000);
        scanAlpha.setDuration(1000);

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
                secondLevel(mIconInner, mIconOuter, mTextCount, mCountEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(outerY, innerY, innerAlpha, outerAlpha, scanAlpha, scanY, countY);
        animatorSet.start();

    }

    /**
     * 第二阶段 开始旋转
     *
     * @param iconInner
     * @param iconOuter
     * @param textCount
     * @param countEntity
     */
    public void secondLevel(ImageView iconInner, ImageView iconOuter, TextView textCount, CountEntity countEntity) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 0, 360, 0, 360, 0, 360);
        ObjectAnimator rotation3 = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360, 0, 360, 0, 360, 0, 360);
        ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", 0, 360, 0, 360, 0, 360, 0, 360);

        rotation.setDuration(1300);
        rotation2.setDuration(2000);

        rotation3.setDuration(300);
        rotation3.setRepeatCount(-1);
        rotation4.setRepeatCount(-1);
        rotation4.setDuration(300);

        rotation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //500ms后开始显示回收光点
                new Handler().postDelayed(() -> showLottieView(), 500);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(rotation3, rotation4);
                animatorSet.start();
                startClean(animatorSet, textCount, countEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        rotation.setInterpolator(new AccelerateInterpolator());
        rotation2.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, rotation2);
        animatorSet.start();

    }

    /**
     * 显示吸收动画
     */
    private void showLottieView() {
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data2.json");
        mAnimationView.playAnimation();
    }

    private static final int FirstLevel = 0xffFD6F46;
    private static final int SecondLevel = 0xffF1D53B;
    private static final int ThirdLevel = 0xff06C581;

    /**
     * 开始加速旋转
     *
     * @param animatorSet
     * @param textCount
     * @param countEntity
     */
    public void startClean(AnimatorSet animatorSet, TextView textCount, CountEntity countEntity) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(Float.valueOf(countEntity.getTotalSize()), 0);
        valueAnimator.setDuration(5000);
        String unit = countEntity.getUnit();
        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            textCount.setText(String.format("%s%s", Math.round(animatedValue), unit));
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

        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(mLayoutRoot,"backgroundColor", FirstLevel, SecondLevel);
        ValueAnimator colorAnim2 = ObjectAnimator.ofInt(mLayoutRoot,"backgroundColor", SecondLevel, ThirdLevel);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim2.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(500);
        colorAnim2.setDuration(500);
        colorAnim1.setStartDelay(4000);
        colorAnim2.setStartDelay(4500);

        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(valueAnimator,colorAnim1,colorAnim2);
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
