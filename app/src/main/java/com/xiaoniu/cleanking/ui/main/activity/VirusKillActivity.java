package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.ad.listener.AdManager;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.presenter.VirusKillPresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author XiLei
 * @date 2019/11/14.
 * description：病毒查杀
 */
public class VirusKillActivity extends BaseActivity<VirusKillPresenter> implements View.OnClickListener {

    @BindView(R.id.lottie)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.iv_scan_bg03)
    ImageView ivScanBg03;
    @BindView(R.id.iv_scan_bg02)
    ImageView ivScanBg02;
    @BindView(R.id.iv_scan_bg01)
    ImageView ivScanBg01;

    private ImageView[] mIvs;
    private ObjectAnimator mObjectAnimator;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_virus_kill;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);
        initLottie();
    }

    private void initLottie() {
        NiuDataAPI.onPageStart("red_envelopes_page_video_view_page", "病毒查杀动画页浏览");
        NiuDataAPIUtil.onPageEnd("home_page", "virus_killing_animation_page", "virus_killing_animation_page_view_page", "病毒查杀动画页浏览");
        showColorChange(2);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mLottieAnimationView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (!mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setAnimation("shadu.json");
            mLottieAnimationView.setImageAssetsFolder("images_virus");
            mLottieAnimationView.playAnimation();
        }
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mLottieAnimationView) {
                    mLottieAnimationView.cancelAnimation();
                    mLottieAnimationView.clearAnimation();
                }
                startActivity(new Intent(VirusKillActivity.this, ScreenFinishBeforActivity.class).putExtra(ExtraConstant.TITLE, getString(R.string.virus_kill)));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void showColorChange(int index) {
        mIvs = new ImageView[]{ivScanBg01, ivScanBg02, ivScanBg03};
        if (mIvs.length == 3 && index <= 2 && index > 0) {
            if (null == mIvs || null == mIvs[index]) return;
            Drawable drawable = mIvs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                mObjectAnimator.setTarget(drawable);
                mObjectAnimator.setDuration(2000);
                if (!mObjectAnimator.isRunning()) {
                    mObjectAnimator.start();
                }
                mObjectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (index == 1) {
                            Log.v("onAnimationEnd", "onAnimationEnd ");
//                            mView.setColorChange(true);
                            if (null != mObjectAnimator)
                                mObjectAnimator.cancel();
                        } else {
                            showColorChange((index - 1));
                        }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mLottieAnimationView && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
            mLottieAnimationView.clearAnimation();
        }
        if (null != mObjectAnimator && mObjectAnimator.isRunning()) {
            mObjectAnimator.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StatisticsUtils.trackClick("system_return_click", "病毒查杀动画页返回", "home_page", "virus_killing_animation_page");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void netError() {

    }
}