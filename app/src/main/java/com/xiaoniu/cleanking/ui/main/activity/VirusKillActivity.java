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

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private FileQueryUtils mFileQueryUtils;
    private ImageView[] mIvs;
    private ObjectAnimator mObjectAnimator;
    private AdManager mAdManager;

    private String TAG = "GeekSdk";

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
        mAdManager = GeekAdSdk.getAdsManger();
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
                startActivity(new Intent(VirusKillActivity.this, ScreenFinishBeforActivity.class).putExtra("title", getString(R.string.virus_kill)));
              /*  for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.KEY_VIRUS_SCREEN.equals(switchInfoList.getConfigKey())) {
                        if (switchInfoList.isOpen()) {
                            loadGeekAd();
                        } else {
                            goFinishActivity();
                        }
                    }
                }*/
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 全屏插屏广告
     */
/*    private void loadGeekAd() {
        if (null == mAdManager) return;
        mAdManager.loadCustomInsertScreenAd(this, "cp_ad_1", 3, new AdListener() { //暂时这样
            @Override
            public void adSuccess(AdInfo info) {
                Log.d(TAG, "-----adSuccess-----");
            }

            @Override
            public void adExposed(AdInfo info) {
                Log.d(TAG, "-----adExposed-----");
            }

            @Override
            public void adClicked(AdInfo info) {
                Log.d(TAG, "-----adClicked-----");
            }

            @Override
            public void adClose(AdInfo info) {
                Log.d(TAG, "-----adClose-----");
                goFinishActivity();
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                Log.d(TAG, "-----adError-----" + errorMsg);
            }
        });
    }*/

   /* private void goFinishActivity() {
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
        finish();
    }*/

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0 || null == mFileQueryUtils) return;
        mRamScale = mFileQueryUtils.computeTotalSize(listInfo);
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