package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.VirusKillPresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatusBarUtil;

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

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private FileQueryUtils mFileQueryUtils;
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
        mFileQueryUtils = new FileQueryUtils();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
        if (null != mFileQueryUtils) {
            mPowerSize = mFileQueryUtils.getRunningProcess().size();
        }
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
    }

    private void initLottie() {
        showColorChange(2);
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
                mLottieAnimationView.cancelAnimation();
                mLottieAnimationView.clearAnimation();
                goFinishActivity();
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

    private void goFinishActivity() {
        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_VIRUS.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.virus_kill), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.virus_kill));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.virus_kill));
            startActivity(NewCleanFinishActivity.class, bundle);
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0 || null == mFileQueryUtils) return;
        mRamScale = mFileQueryUtils.computeTotalSize(listInfo);
    }

    public void showColorChange(int index) {
        mIvs = new ImageView[]{ivScanBg01, ivScanBg02, ivScanBg03};
        if (mIvs.length == 3 && index <= 2 && index > 0) {
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
                            if (mObjectAnimator != null)
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
           /* if (!mIsYinDaoFinish && !PreferenceUtil.getGameQuikcenStart()) {
                StatisticsUtils.trackClick("system_return_click", "游戏加速引导页返回按钮点击", AppHolder.getInstance().getCleanFinishSourcePageId(), "gameboost_guidance_page");
            } else if (mIsStartClean) {
                StatisticsUtils.trackClick("system_return_click", "游戏加速动画页返回", "gameboost_incentive_video_end_page", "gameboost_animation_page");
            } else {
                StatisticsUtils.trackClick("system_return_click", "游戏加速添加页返回", "gameboost_guidance_page", "gameboost_add_page");
            }*/
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void netError() {

    }
}