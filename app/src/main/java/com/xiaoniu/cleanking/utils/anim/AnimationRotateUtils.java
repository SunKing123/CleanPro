package com.xiaoniu.cleanking.utils.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe:旋转的动画集合
 */
public class AnimationRotateUtils {
    private volatile static AnimationRotateUtils utils;

    public static AnimationRotateUtils getInstance() {
        if (utils == null) {
            synchronized (AnimationRotateUtils.class) {
                if (utils == null) {
                    utils = new AnimationRotateUtils();
                }
            }
        }
        return utils;
    }

    private AnimationRotateUtils() {
    }

    /**
     * 旋转动画
     *
     * @param ivAnim
     * @param duration 毫秒
     * @return
     */
    public Animation playRotateAnim(View ivAnim, int duration) {
        Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (ivAnim == null || duration <= 0) {
            return animation;
        }
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        ivAnim.startAnimation(animation);//開始动画
        return animation;
    }
}
