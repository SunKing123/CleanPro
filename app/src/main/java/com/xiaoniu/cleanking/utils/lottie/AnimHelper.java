package com.xiaoniu.cleanking.utils.lottie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;


public class AnimHelper {

    private ObjectAnimator oldAnim = null;
    private ObjectAnimator newAnim = null;

    public AnimHelper() {

    }

    public void changeAnim(View oldView, View newView) {
        oldAnim = ObjectAnimator.ofFloat(oldView, "alpha", 1f, 0)   ;
        newAnim = ObjectAnimator.ofFloat(newView, "alpha", 0f, 1);

        oldAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (oldView != null) {
                    oldView.setVisibility(View.GONE);
                }
            }
        });

        oldAnim.setDuration(1800).start();
        newAnim.setDuration(500).start();
    }

    public void cancel() {
        if (oldAnim != null) {
            oldAnim.cancel();
        }
        if (newAnim != null) {
            newAnim.cancel();
        }
    }

}
