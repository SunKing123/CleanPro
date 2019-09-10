package com.xiaoniu.cleanking.ui.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;

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
        mLottieAnimationView.useHardwareAcceleration();
        mLottieAnimationView.setAnimation("data_premis.json");
        mLottieAnimationView.setImageAssetsFolder("images");
        mLottieAnimationView.playAnimation();

    }
}
