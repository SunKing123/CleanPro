package com.hellogeek.permission.activity;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.hellogeek.permission.R;

import androidx.annotation.Nullable;


/**
 * 动画引导
 */
public class PhonePremisActivity extends Activity {

    //动画
    private LottieAnimationView mLottieAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_guide);
        initViews();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    private void initViews() {
        mLottieAnimationView = findViewById(R.id.view_lottie);
//        mLottieAnimationView.useHardwareAcceleration();
        mLottieAnimationView.setAnimation("data_premis.json");
        mLottieAnimationView.setImageAssetsFolder("images");
        mLottieAnimationView.playAnimation();

        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLottieAnimationView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
}
