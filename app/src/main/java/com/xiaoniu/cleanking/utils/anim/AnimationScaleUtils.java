package com.xiaoniu.cleanking.utils.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe:缩放的动画集合
 */
public class AnimationScaleUtils {
    private volatile static AnimationScaleUtils utils;

    public static AnimationScaleUtils getInstance() {
        if (utils == null) {
            synchronized (AnimationScaleUtils.class) {
                if (utils == null) {
                    utils = new AnimationScaleUtils();
                }
            }
        }
        return utils;
    }

    private AnimationScaleUtils() {
    }

    /**
     * 缩放的属性动画==从 1 到 1.1 到 1
     *
     * @param view
     * @param during 毫秒
     * @return
     */
    public AnimatorSet playScaleAnimation(View view, int during) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.setDuration(during);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();
        return animatorSet;
    }
}
