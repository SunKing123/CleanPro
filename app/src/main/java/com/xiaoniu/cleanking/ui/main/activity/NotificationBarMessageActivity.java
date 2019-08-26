package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanSetActivity;
import com.xiaoniu.common.base.BaseActivity;

import static android.view.View.VISIBLE;

/**
 * 通知栏清理
 */
public class NotificationBarMessageActivity extends BaseActivity {

    private LinearLayout mLlClean,mLlScan;
    private FrameLayout mFlClean;
    private TextView mTvClean;
    private TextView mTvTitle;
    private ImageView mIvRight;
    private ImageView mIvBack;
    private LottieAnimationView mAnimationView;
    private ImageView mIconInner;

    private RelativeLayout mLlCount;

    private CountEntity mCountEntity = new CountEntity();
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_message;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mLlClean = findViewById(R.id.fl_clean_layout);
        mFlClean = findViewById(R.id.fl_clean);
        mTvClean = findViewById(R.id.tv_clean);
        mTvTitle = findViewById(R.id.tv_title);
        mIvRight = findViewById(R.id.iv_set);
        mIvBack = findViewById(R.id.iv_back);
        mAnimationView = findViewById(R.id.view_lottie);
        mIconInner = findViewById(R.id.icon_inner);
        mLlScan = findViewById(R.id.layout_scan);
        mLlCount = findViewById(R.id.layout_count);
        mTvTitle.setText(getString(R.string.tool_notification_clean));
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> finish());
        mTvClean.setOnClickListener(v -> {
            mLlClean.setVisibility(View.GONE);
            mTvClean.setVisibility(View.GONE);
            mFlClean.setVisibility(VISIBLE);

            playStarAnimation();
        });
        mIvRight.setOnClickListener(v -> startActivity(new Intent(this, NotifyCleanSetActivity.class)));
    }

    private void playStarAnimation() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data.json");
        mAnimationView.playAnimation();
        startCleanAnimation(mIconInner,mLlScan,mLlCount,mCountEntity);
    }
    /**
     * 开始清理动画
     *  @param iconInner
     * @param layoutScan
     * @param countEntity
     */
    public void startCleanAnimation(ImageView iconInner, LinearLayout layoutScan, RelativeLayout layoutCount, CountEntity countEntity) {
        iconInner.setVisibility(VISIBLE);

        ObjectAnimator innerAlpha = ObjectAnimator.ofFloat(iconInner, "alpha", 0, 1);
        innerAlpha.setDuration(1000);

        //第一阶段倒转
        ObjectAnimator rotationFistStep = ObjectAnimator.ofFloat(iconInner, "rotation", 0, -35f);
        rotationFistStep.setDuration(600);

        innerAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                iconInner.setVisibility(VISIBLE);
                new Handler().postDelayed(rotationFistStep::start, 400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //第二阶段开始
               secondLevel(iconInner, countEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(innerAlpha);
        animatorSet.start();
    }
    public void secondLevel(ImageView iconInner, CountEntity countEntity) {
        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 0, 360, 0, 360, 0, 360, 0);
        ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", 0, 360);
        rotation2.setDuration(1100);
        rotation4.setRepeatCount(-1);
        rotation4.setDuration(200);
        rotation4.setInterpolator(new LinearInterpolator());

//        new Handler().postDelayed(() -> mView.showLottieView(), 700);

        rotation2.setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotation2, rotation4);
        animatorSet.start();
    }

    @Override
    protected void loadData() {

    }

}
