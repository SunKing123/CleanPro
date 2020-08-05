package com.xiaoniu.cleanking.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.widget.AppCompatTextView;
/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/15
 */
public class BreathTextView extends AppCompatTextView {

//    private ScaleAnimation mScaleAnimation;
//    private boolean isAnimationStart = false;

    private ValueAnimator mValueAnimator;

    public BreathTextView(Context context) {
        this(context, null);
    }

    public BreathTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreathTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimation();
    }

    private void initAnimation() {
//        mScaleAnimation = new ScaleAnimation(
//                1f, 1.15f, 1f, 1.15f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        mScaleAnimation.setDuration(1000);
//        mScaleAnimation.setRepeatMode(Animation.REVERSE);
//        mScaleAnimation.setRepeatCount(ValueAnimator.INFINITE);

        mValueAnimator = ValueAnimator.ofFloat(1F, 1.1F, 1F);
        mValueAnimator.setDuration(1000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(animation -> {
            setScaleX((Float) animation.getAnimatedValue());
            setScaleY((Float) animation.getAnimatedValue());
        });
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (getVisibility() == View.VISIBLE) {
//            startScaleAnimation();
//        }
//    }

//    @Override
//    protected void onVisibilityChanged(View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//        if (View.VISIBLE == visibility) {
//            startScaleAnimation();
//        } else {
//            stopScaleAnimation();
//        }
//    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        stopScaleAnimation();
//    }
//
//    private void startScaleAnimation() {
//        if (mScaleAnimation != null && !isAnimationStart) {
//            mScaleAnimation.start();
//            this.startAnimation(mScaleAnimation);
//            isAnimationStart = true;
//        }
//    }
//
//    private void stopScaleAnimation() {
//        if (mScaleAnimation != null && isAnimationStart) {
//            mScaleAnimation.cancel();
//            this.clearAnimation();
//            isAnimationStart = false;
//        }
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mValueAnimator != null && !mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.end();
        }
    }

}
