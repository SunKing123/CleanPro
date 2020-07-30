package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.NetWorkPresenter;
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NetWorkSpeedUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * @author XiLei
 * @date 2019/11/15.
 * description：网络加速
 */
public class NetWorkActivity extends BaseActivity<NetWorkPresenter> implements View.OnClickListener {

    @BindView(R.id.lottie)
    LottieAnimationView mLottieAnimationView;
    @BindView(R.id.tv_num)
    TextView mNumTv;
    @BindView(R.id.tv_net_num)
    TextView mNetNumTv;

    private ValueAnimator mValueAnimator;
    private NetWorkSpeedUtils mNetWorkSpeedUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_network;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForWindow(this);
        mNumTv.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        initLottieYinDao();
    }

    private void initLottieYinDao() {
        if (null != mHandler) {
            mNetWorkSpeedUtils = new NetWorkSpeedUtils(NetWorkActivity.this, mHandler);
            mNetWorkSpeedUtils.startShowNetSpeed();
        }
        if (!mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setAnimation("wangluo.json");
            mLottieAnimationView.setImageAssetsFolder("images_network");
            mLottieAnimationView.playAnimation();
        }
        mValueAnimator = ValueAnimator.ofInt(1, 100);
        mValueAnimator.setDuration(1850);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.start();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int currentValue = (int) valueAnimator.getAnimatedValue();
                mNumTv.setText(String.valueOf(currentValue));
            }
        });

        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (TextUtils.isEmpty(mStartNetNumber)) return;
                mNetNumTv.setText("现网速度： " + new BigDecimal(mStartNetNumber.replace("KB/S", "").trim()).multiply(new BigDecimal(1.5)).setScale(2, BigDecimal.ROUND_HALF_UP) + " KB/S");
                if (null != mNetWorkSpeedUtils) {
                    mNetWorkSpeedUtils.cancelTask();
                }
                if (null != mLottieAnimationView) {
                    mLottieAnimationView.cancelAnimation();
                    mLottieAnimationView.clearAnimation();
                }
                if (null != mValueAnimator) {
                    mValueAnimator.cancel();
                }

                String speed=NumberUtils.mathRandom(25, 50);

                AppHolder.getInstance().setCleanFinishSourcePageId("network_acceleration_animation_page");


                Intent intent = new Intent()
                        .putExtra(ExtraConstant.TITLE, getString(R.string.network_quicken))
                        .putExtra(ExtraConstant.NUM,speed);

                StartFinishActivityUtil.Companion.gotoFinish(NetWorkActivity.this, intent);

                PreferenceUtil.saveSpeedNetworkTime();
                PreferenceUtil.saveSpeedNetworkValue(speed);
                EventBus.getDefault().post(new FunctionCompleteEvent(getString(R.string.network_quicken)));

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

    private String mStartNetNumber;
    private boolean isShow;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (!isShow) {
                        isShow = true;
                        mStartNetNumber = msg.obj.toString();
                        if (null != mNetNumTv && null != mStartNetNumber)
                        mNetNumTv.setText("现网速度： " + mStartNetNumber);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (null != mLottieAnimationView && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
            mLottieAnimationView.clearAnimation();
        }
        if (null != mValueAnimator) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public void netError() {

    }
}
