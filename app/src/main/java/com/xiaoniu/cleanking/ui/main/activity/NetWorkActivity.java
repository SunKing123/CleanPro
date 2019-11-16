package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.presenter.NetWorkPresenter;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NetWorkSpeedUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.ArrayList;

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

    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private FileQueryUtils mFileQueryUtils;
    private ValueAnimator mValueAnimator;
    private boolean mIsOpen;

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
        initLottieYinDao();
        mFileQueryUtils = new FileQueryUtils();
        if (Build.VERSION.SDK_INT < 26) {
            mPresenter.getAccessListBelow();
        }
        if (null != mFileQueryUtils) {
            mPowerSize = mFileQueryUtils.getRunningProcess().size();
        }
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPresenter.getSwitchInfoList();
    }

    private void initLottieYinDao() {
        if (!mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setAnimation("wangluo.json");
            mLottieAnimationView.setImageAssetsFolder("images_network");
            mLottieAnimationView.playAnimation();
        }
        Log.d("XiLei", "mLottieAnimationView.getDuration()=" + mLottieAnimationView.getDuration());
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
                Log.d("XiLei", "mLottieAnimationView.getDuration()111=" + animation.getDuration());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("XiLei", "mLottieAnimationView.getDuration()222=" + animation.getDuration());
                if (null != mHandler) {
                    new NetWorkSpeedUtils(NetWorkActivity.this, mHandler).startShowNetSpeed();
                }
                mLottieAnimationView.cancelAnimation();
                mLottieAnimationView.clearAnimation();
                mValueAnimator.cancel();
                try {
                    Thread.sleep(1000);
                    goFinishActivity();
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    private void goFinishActivity() {
        boolean isOpen = false;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_NET.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        if (isOpen && PreferenceUtil.getShowCount(this, getString(R.string.network_quicken), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.network_quicken));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.network_quicken));
            bundle.putString("num", NumberUtils.mathRandom(25, 50));
            startActivity(NewCleanFinishActivity.class, bundle);
        }
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0 || null == mFileQueryUtils) return;
        mRamScale = mFileQueryUtils.computeTotalSize(listInfo);
    }

    private boolean isShow;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (!isShow) {
                        Log.d("XiLei", "msg.obj.toString()=" + msg.obj.toString());
                        isShow = true;
                        mNetNumTv.setText("现网速度： " + msg.obj.toString());
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

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        if (null == list || null == list.getData() || list.getData().size() <= 0)
            return;
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {
            if (PositionId.KEY_NET_SCREEN.equals(switchInfoList.getConfigKey())) {
                mIsOpen = switchInfoList.isOpen();
            }
        }
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {
//        ToastUtils.showShort(getString(R.string.net_error));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
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