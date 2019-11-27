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
import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.presenter.VirusKillPresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * @author XiLei
 * @date 2019/11/14.
 * description：病毒查杀
 */
public class VirusKillActivity extends BaseActivity<VirusKillPresenter> implements View.OnClickListener {

    @BindView(R.id.lottie)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.lottie2)
    LottieAnimationView mLottieAnimationView2;
    @BindView(R.id.v_lottie2)
    View mViewLottie2;

    @BindView(R.id.iv_scan_bg05)
    ImageView ivScanBg05;
    @BindView(R.id.iv_scan_bg04)
    ImageView ivScanBg04;
    @BindView(R.id.iv_scan_bg03)
    ImageView ivScanBg03;
    @BindView(R.id.iv_scan_bg02)
    ImageView ivScanBg02;
    @BindView(R.id.iv_scan_bg01)
    ImageView ivScanBg01;

    private ImageView[] mIvs;

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
        NiuDataAPI.onPageStart("virus_killing_animation_page_view_page", "病毒查杀动画页浏览");
        NiuDataAPIUtil.onPageEnd("home_page", "virus_killing_animation_page", "virus_killing_animation_page_view_page", "病毒查杀动画页浏览");
        showColorChange(4);
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
                initLottie2();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initLottie2() {
        if (null == mLottieAnimationView2) return;
        mLottieAnimationView.setVisibility(View.GONE);
        mViewLottie2.setVisibility(View.VISIBLE);
        if (!mLottieAnimationView2.isAnimating()) {
            mLottieAnimationView2.setAnimation("yindao2.json");
            mLottieAnimationView2.setImageAssetsFolder("images_game_yindao2");
            mLottieAnimationView2.playAnimation();
        }
        mLottieAnimationView2.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mLottieAnimationView2) {
                    mLottieAnimationView2.cancelAnimation();
                    mLottieAnimationView2.clearAnimation();
                }
                //设置锁屏数据
                LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(2);
                btnInfo.setNormal(true);
                btnInfo.setCheckResult("500");
                btnInfo.setReShowTime(System.currentTimeMillis() + 1000 * 60 * 5);
                PreferenceUtil.getInstants().save("lock_pos03", new Gson().toJson(btnInfo));
                EventBus.getDefault().post(btnInfo);
                //保存杀毒完成时间
                PreferenceUtil.saveVirusKillTime();
                AppHolder.getInstance().setCleanFinishSourcePageId("virus_killing_animation_page");
                startActivity(new Intent(VirusKillActivity.this, ScreenFinishBeforActivity.class).putExtra(ExtraConstant.TITLE, getString(R.string.virus_kill)));
                finish();
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
        mIvs = new ImageView[]{ivScanBg01, ivScanBg02, ivScanBg03, ivScanBg04, ivScanBg05};
        if (mIvs.length == 5 && index <= 4 && index > 0) {
            if (null == mIvs[index]) return;
            Drawable drawable = mIvs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                animator.setTarget(drawable);
                animator.setDuration(1500);
                if (!animator.isRunning()) {
                    animator.start();
                }
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (index == 1) {
                            Log.v("onAnimationEnd", "onAnimationEnd ");
                            if (animator != null)
                                animator.cancel();
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
        if (null != mLottieAnimationView2 && mLottieAnimationView2.isAnimating()) {
            mLottieAnimationView2.cancelAnimation();
            mLottieAnimationView2.clearAnimation();
        }
        if (null != mLottieAnimationView && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
            mLottieAnimationView.clearAnimation();
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