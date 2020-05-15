package com.xiaoniu.cleanking.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * @Description:
 * @Author: Xiaodong.He
 * @Date: 2020/5/15
 */
public class BreathTextView extends AppCompatTextView {

    private ScaleAnimation mScaleAnimation;
    private boolean isAnimationStart = false;

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
        mScaleAnimation = new ScaleAnimation(
                1f, 1.15f, 1f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setDuration(1000);
        mScaleAnimation.setRepeatMode(Animation.REVERSE);
        mScaleAnimation.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == View.VISIBLE) {
            startScaleAnimation();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (View.VISIBLE == visibility) {
            startScaleAnimation();
        } else {
            stopScaleAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScaleAnimation();
    }

    private void startScaleAnimation() {
        if (mScaleAnimation != null && !isAnimationStart) {
            mScaleAnimation.start();
            this.startAnimation(mScaleAnimation);
            isAnimationStart = true;
        }
    }

    private void stopScaleAnimation() {
        if (mScaleAnimation != null && isAnimationStart) {
            mScaleAnimation.cancel();
            this.clearAnimation();
            isAnimationStart = false;
        }
    }

}
