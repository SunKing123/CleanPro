package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.utils.SystemHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;


/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/4
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
@Route(path = RouteConstants.ACTIVITY_ASM_GUIDE)
public class ASMGuideActivity extends BaseActivity {

    LottieAnimationView mAnimationView;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_asm_guide;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemHelper.setTopApp(this);
    }

    @Override
    protected void initView() {
        mAnimationView = findViewById(R.id.view_lottie);
        showLottieView();
        StatusBarCompat.translucentStatusBarForImage(this, true, false);
    }
    /**
     * 显示吸收动画
     */
    private void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setAnimation("data_premis.json");
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.playAnimation();
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }
}
