package com.xiaoniu.cleanking.utils.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by zhaoyingtao
 * Date: 2020/6/15
 * Describe: 右边中间左右移动动画
 */
public class FloatAnimManager {
    private long animTime = 500;
    private ObjectAnimator translationXin;
    private ObjectAnimator translationXout;
    private View mFloatLlyt;
    private boolean isScrolling;
    private int translationDistance = 200;
    private boolean isNeedOptionAnimation;

    public FloatAnimManager(View view, int translationDistance) {
        mFloatLlyt = view;
        this.translationDistance = translationDistance;
    }

    public void setIsNeedOptionAnimation(boolean isNeedOptionAnimation) {
        this.isNeedOptionAnimation = isNeedOptionAnimation;
    }

    public void hindFloatAdvertView() {
        if (!isNeedOptionAnimation || isScrolling) {
            return;
        }
        isScrolling = true;
        if (translationXout == null) {
            translationXout = ObjectAnimator.ofFloat(mFloatLlyt, "translationX", 0, translationDistance);
        }
        translationXout.cancel();
        translationXout.setDuration(animTime);
        translationXout.start();
        translationXout.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isScrolling) {
                    mFloatLlyt.setVisibility(INVISIBLE);
                }
            }
        });
    }

    public void showFloatAdvertView() {
        if (!isNeedOptionAnimation) {
            return;
        }
        isScrolling = false;
        mFloatLlyt.setVisibility(VISIBLE);
        if (translationXin == null) {
            translationXin = ObjectAnimator.ofFloat(mFloatLlyt, "translationX", translationDistance, 0);
        }
        translationXin.cancel();
        translationXin.setDuration(animTime);
        translationXin.start();
    }
}
