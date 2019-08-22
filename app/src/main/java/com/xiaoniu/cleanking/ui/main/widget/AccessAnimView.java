package com.xiaoniu.cleanking.ui.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.DisplayUtils;

/**
 * @author zhang
 */
public class AccessAnimView extends RelativeLayout {
    private Context mContext;
    RelativeLayout mRlAnimBg;
    LinearLayout line_hj;
    TextView line_access;
    LinearLayout line_size;
    LinearLayout line_title;
    ImageView iv_bot;
    ImageView iv_yu1;
    ImageView iv_yu2;
    ImageView iv_yu3;
    ImageView iv_yu4;
    ImageView iv_yu5;
    ImageView iv_yu6;
    ImageView iv_yu7;
    ImageView iv_yu8;
    TextView tv_size;
    TextView tv_gb;
    TextView tv_title_name;
    LinearLayout line_allnum;
    int sizeMb;
    String strGb;
    onAnimEndListener listener;
    FrameLayout mFlAnim;
    LottieAnimationView mAnimationView;
    ValueAnimator mValueAnimator;
    ImageView mIvSpeedAnim;
    LottieAnimationView mAnimationCloudView;

    public void setListener(onAnimEndListener listener) {
        this.listener = listener;
    }

    //倒计时图片
    public AccessAnimView(Context context) {
        super(context);
        initView(context);
    }

    public AccessAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public interface onAnimEndListener {
        void onAnimEnd();

        void onStatusBarColorChanged(int colorRes);
    }

    /**
     * 初始化布局
     *
     * @return
     */
    public void initView(Context context) {

        mContext = context;
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_access_anim, this, true);
        mRlAnimBg = v.findViewById(R.id.rl_anim_bg);
        line_hj = v.findViewById(R.id.line_hj);
        iv_bot = v.findViewById(R.id.iv_bot);
        tv_size = v.findViewById(R.id.tv_size);
        tv_gb = v.findViewById(R.id.tv_gb);
        line_access = v.findViewById(R.id.line_access);
        line_size = v.findViewById(R.id.line_size);
        line_allnum = v.findViewById(R.id.line_allnum);
        line_title = v.findViewById(R.id.line_title);
        iv_yu1 = v.findViewById(R.id.iv_yu1);
        iv_yu2 = v.findViewById(R.id.iv_yu2);
        iv_yu3 = v.findViewById(R.id.iv_yu3);
        iv_yu4 = v.findViewById(R.id.iv_yu4);
        iv_yu5 = v.findViewById(R.id.iv_yu5);
        iv_yu6 = v.findViewById(R.id.iv_yu6);
        iv_yu7 = v.findViewById(R.id.iv_yu7);
        iv_yu8 = v.findViewById(R.id.iv_yu8);
        tv_title_name = v.findViewById(R.id.tv_title_name);
        mAnimationView = v.findViewById(R.id.view_lottie);
        mFlAnim = v.findViewById(R.id.fl_anim);
        mIvSpeedAnim = v.findViewById(R.id.iv_speed_anim);
        mAnimationCloudView = v.findViewById(R.id.view_lottie_speed);
    }

    public void setData(int sizeMb, String strGb) {
        this.sizeMb = sizeMb;
        this.strGb = strGb;
        tv_size.setText(sizeMb + "");
        tv_gb.setText("MB");
    }

    public void setTitleName(String name){
        tv_title_name.setText(name);
    }

    //Step1:上面红色布局和中间1dp的布局动画开始
    public void startTopAnim() {
        int startHeight = DisplayUtils.dip2px(150);
        int endHeight = DisplayUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (LayoutParams) mRlAnimBg.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.height = currentValue;
            mRlAnimBg.setLayoutParams(rlp);
        });
        anim.start();
        startMiddleAnim();
    }

    //中间的控件高度变化
    public void startMiddleAnim() {
        int startHeight = DisplayUtils.dip2px(30);
        int endHeight = DisplayUtils.dip2px(300);
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) line_allnum.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.topMargin = currentValue;
            line_allnum.setLayoutParams(rlp);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setHjAnim();
            }
        });
        anim.start();
    }

    //Step2:高度下降完毕，中间的火箭缩放动画开始，动画播放完毕后火箭底部的帧动画开始播放
    public void setHjAnim() {
        line_hj.setVisibility(VISIBLE);
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                0f, 1.0f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                0f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(line_hj, anim4, anim5).setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                iv_bot.setImageResource(R.drawable.anim_hj);
                AnimationDrawable animationDrawable = (AnimationDrawable) iv_bot.getDrawable();
                if (!animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                setNumAnim(tv_size, tv_gb, mRlAnimBg, sizeMb, 0, TextUtils.equals(strGb, "GB") ? 2 : 1);
            }
        });
        animator.start();
    }

    /**
     * 数字动画 清理开始
     *
     * @param
     * @param startNum
     * @param endNum
     * @param type     1是MB，2是GB
     */
    boolean canPlaying = true;
    /**
     * 第一阶段  红色
     */
    private static final int FirstLevel = 0xffFD6F46;

    private static final int SecondLevel = 0xffF1D53B;
    /**
     * 第三阶段 绿色
     */
    private static final int ThirdLevel = 0xff06C581;

    public void setNumAnim(TextView tv_size, TextView tv_gb, View viewt, int startNum, int endNum, int type) {
        ObjectAnimator oa1 = setYuAnim(iv_yu1, 0);
        ObjectAnimator oa2 = setYuAnim(iv_yu2, 80);
        ObjectAnimator oa3 = setYuAnim(iv_yu3, 160);
        ObjectAnimator oa4 = setYuAnim(iv_yu4, 240);

        ObjectAnimator oa5 = setYuAnim(iv_yu5, 320);
        ObjectAnimator oa6 = setYuAnim(iv_yu6, 400);
        ObjectAnimator oa7 = setYuAnim(iv_yu7, 480);
        ObjectAnimator oa8 = setYuAnim(iv_yu8, 540);
        ValueAnimator anim = ValueAnimator.ofInt(startNum, endNum);
        anim.setDuration(3000);
        anim.setInterpolator(new DecelerateInterpolator());
        canPlaying = true;
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            tv_size.setText(currentValue + "");
            Log.e("asdf", "时间：" + animation.getAnimatedFraction());
            Log.d("asdf", "cuurent time " + animation.getCurrentPlayTime());
            if (canPlaying && animation.getAnimatedFraction() > 0.933) {
                canPlaying = false;
                cancelYuAnims(oa1, oa2, oa3, oa4, oa5, oa6, oa7, oa8);
                //播放的后500ms，背景色改变
//                    setBgChanged(viewt, 200);
            }
            if (currentValue == endNum) {
                tv_size.setText(type == 1 ? String.valueOf(currentValue) : String.valueOf(NumberUtils.getFloatStr2(currentValue / 1024)));
                tv_gb.setText(type == 1 ? "MB" : "GB");
            }
        });
//        anim.start();

        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(viewt, "backgroundColor", FirstLevel, SecondLevel, ThirdLevel);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(800);
        colorAnim1.setStartDelay(2200);

        colorAnim1.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            line_title.setBackgroundColor(animatedValue);
            if (listener != null) {
                listener.onStatusBarColorChanged(animatedValue);
            }
        });

        colorAnim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new android.os.Handler().postDelayed(() -> setViewTrans(), 200);
            }
        });


        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(anim, colorAnim1);
        animatorSetTimer.start();
    }

    boolean isFirstChangeColor = true;
    boolean isFirstChangeColor1 = true;
    boolean isFirstChangeColor2 = true;
    boolean isFirstChangeColor3 = true;

    //数字动画播放的最后200ms，背景颜色变化
    public void setBgChanged(View viewt, long time) {
        isFirstChangeColor = true;
        isFirstChangeColor1 = true;
        isFirstChangeColor2 = true;
        isFirstChangeColor3 = true;
        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.setDuration(time);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            if (currentValue < 66 && currentValue >= 0) {
                if (listener != null && isFirstChangeColor1) {
                    listener.onStatusBarColorChanged(R.color.color_FD6F46);
                    isFirstChangeColor1 = false;
                }
                viewt.setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
                line_title.setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
            } else if (currentValue < 77 && currentValue >= 66) {
                if (listener != null && isFirstChangeColor2) {
                    listener.onStatusBarColorChanged(R.color.color_06C581);
                    isFirstChangeColor2 = false;
                }
                viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
            } else if (currentValue >= 77) {
                if (listener != null && isFirstChangeColor3) {
                    listener.onStatusBarColorChanged(R.color.color_06C581);
                    isFirstChangeColor3 = false;
                }
                viewt.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                line_title.setBackgroundColor(getResources().getColor(R.color.color_06C581));
                if (isFirstChangeColor) {
                    isFirstChangeColor = false;
                    if (listener != null)
                        listener.onAnimEnd();
                }
            }

        });
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new android.os.Handler().postDelayed(() -> setViewTrans(), 200);
            }
        });
    }

    //数字动画播放完后火箭上移，布局高度缩小
    public void setViewTrans() {
        line_size.setVisibility(GONE);
        line_access.setVisibility(GONE);

        int endHeight = DisplayUtils.dip2px(150);
        int startHeight = DisplayUtils.getScreenHeight();
        mValueAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        mValueAnimator.setDuration(500);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (LayoutParams) mRlAnimBg.getLayoutParams();
        mValueAnimator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.height = currentValue;
            mRlAnimBg.setLayoutParams(rlp);
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
            }
        });
        ObjectAnimator animator = createFadeAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startFinishAnimator();
            }
        });
    }

    /**
     * @return 火箭向上飞
     */
    public ObjectAnimator createFadeAnimator() {

        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", line_hj.getTranslationY(), (-1) * DisplayUtils.dip2px(306));
//        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(line_hj, translationY);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        return animator;
    }

    //下雨动画
    public ObjectAnimator setYuAnim(View viewY, long delay) {
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", DisplayUtils.dip2px(112) * -1, DisplayUtils.getScreenHeight() + DisplayUtils.dip2px(112));
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewY, translationY);
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.INFINITE);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewY.setVisibility(VISIBLE);
            }
        });
        new android.os.Handler().postDelayed(() -> {
//        animator.setRepeatMode(ValueAnimator.INFINITE);
            animator.start();
        }, delay);
        return animator;
    }

    public void cancelYuAnims(ObjectAnimator oa1, ObjectAnimator oa2, ObjectAnimator oa3, ObjectAnimator oa4, ObjectAnimator oa5, ObjectAnimator oa6, ObjectAnimator oa7, ObjectAnimator oa8) {
        if (oa1 != null) oa1.cancel();
        if (oa2 != null) oa2.cancel();
        if (oa3 != null) oa3.cancel();
        if (oa4 != null) oa4.cancel();
        if (oa5 != null) oa5.cancel();
        if (oa6 != null) oa6.cancel();
        if (oa7 != null) oa7.cancel();
        if (oa8 != null) oa8.cancel();
        iv_yu1.setVisibility(GONE);
        iv_yu2.setVisibility(GONE);
        iv_yu3.setVisibility(GONE);
        iv_yu4.setVisibility(GONE);
        iv_yu5.setVisibility(GONE);
        iv_yu6.setVisibility(GONE);
        iv_yu7.setVisibility(GONE);
        iv_yu8.setVisibility(GONE);
    }

    public void startFinishAnimator() {
        mFlAnim.setVisibility(VISIBLE);
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data_clean_finish.json");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //保存清理完成次数
                PreferenceUtil.saveCleanNum();

                mFlAnim.setVisibility(View.GONE);
                mValueAnimator.start();
                if (listener != null)
                    listener.onAnimEnd();
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
     * 显示lottie动画 火箭从底部飞出
     */
    public void showLottieView() {
        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(true);
        for (int i = 1; i <= 21; i++) {
            int id = getResources().getIdentifier("icon_one_key_speed_" + i, "drawable", mContext.getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            animationDrawable.addFrame(drawable, 50);
        }
        mIvSpeedAnim.setBackground(animationDrawable);
//        animationDrawable.start();
        startAnimator();
    }

    /**
     * 显示lottie动画 火箭从底部飞出
     */
    public void startAnimator() {
        mAnimationCloudView.useHardwareAcceleration();
        mAnimationCloudView.setImageAssetsFolder("images");
        mAnimationCloudView.setAnimation("data_one_key_speed.json");
        mAnimationCloudView.playAnimation();
    }
}

