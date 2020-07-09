package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.localpush.LocalPushUtils;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.StartFinishActivityUtil;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.roundedimageview.RoundedImageView;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 超强省电中...
 */
public class PhoneSuperSavingNowActivity extends BaseActivity implements View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private ImageView mBack;
    private TextView mBtnCancel;
    private TextView mTvNum;
    private RelativeLayout mRlResult;
    private LinearLayout mLlResultTop;
    private JavaInterface javaInterface;
    private LinearLayout mLayoutNotNet;
    private FrameLayout mFlAnim;
    private ImageView mIvAnimationStartView;
    private LottieAnimationView mLottieAnimationFinishView;
    private boolean isError = false;
    private MyHandler mHandler = new MyHandler(this);
    private int num;
    private TextView mTvAllNum;
    private List<MultiItemInfo> mSelectedList;
    private View mLayIconAnim;
    private RoundedImageView mIvIcon1;
    private RoundedImageView mIvIcon2;
    private boolean isFinish = false;
    private int mTime = 800;

    String sourcePage = "";
    String currentPage = "";
    String sysReturnEventName = "";
    String returnEventName = "";

    String viewPageEventCode = "";
    String viewPageEventName = "";
    private boolean mIsFinish; //是否点击了返回键

    private class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                showFinishAnim();
            } else if (msg.what == 2) {
                num--;
                mTvNum.setText(String.valueOf(num));
                if (num > 0) {
                    sendEmptyMessageDelayed(2, mTime);
                } else {
                    sendEmptyMessageDelayed(1, mTime);
                }
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_phone_super_saving_now;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
        num = intent.getIntExtra("processNum", 0);
        mSelectedList = PhoneSuperPowerDetailActivity.sSelectedList;
        if (mSelectedList != null && mSelectedList.size() > 0) {
            mTime = 3000 / mSelectedList.size();
        }
        PhoneSuperPowerDetailActivity.sSelectedList = null;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        viewPageEventCode = "powersave_animation_page_view_page";
        viewPageEventName = "用户在省电动画页浏览";
        sourcePage = "powersave_scan_result_page";
        currentPage = "powersave_animation_page";
        sysReturnEventName = "用户在省电动画页返回";
        returnEventName = "用户在省电动画页返回";
        mAppBarLayout = findViewById(R.id.app_power_saving_bar_layout);
        mBack = findViewById(R.id.iv_back);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mTvNum = findViewById(R.id.tv_num);
        mTvAllNum = findViewById(R.id.tvAllNum);

        mRlResult = findViewById(R.id.rl_result);
        mLlResultTop = findViewById(R.id.viewt_finish);
        mLayoutNotNet = findViewById(R.id.layout_not_net);
        mIvAnimationStartView = findViewById(R.id.view_lottie_super_saving_sleep);
        mFlAnim = findViewById(R.id.fl_anim);
        mLottieAnimationFinishView = findViewById(R.id.view_lottie);

        mLayIconAnim = findViewById(R.id.layIconAnim);
        mIvIcon1 = findViewById(R.id.ivIcon1);
        mIvIcon2 = findViewById(R.id.ivIcon2);

        mTvNum.setText(String.valueOf(num));
        mTvAllNum.setText("/" + String.valueOf(num));
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mLayoutNotNet.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        if (num <= 0) {
            showCleanFinishUI();
        } else {
            showStartAnim();
            showIconAnim();
            mHandler.sendEmptyMessageDelayed(2, mTime);
        }
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showIconAnim() {
        mLayIconAnim.setVisibility(View.VISIBLE);
        mLayIconAnim.post(() -> {
            Bitmap bitmap = getNextImg();
            if (bitmap != null) {
                mIvIcon1.setImageBitmap(bitmap);
                playIconAnim1(mIvIcon1);
            } else {
                mIvIcon1.setVisibility(View.GONE);
            }

            bitmap = getNextImg();
            if (bitmap != null) {
                mIvIcon2.setImageBitmap(bitmap);
                playIconAnim2(mIvIcon2);
            } else {
                mIvIcon2.setVisibility(View.GONE);
            }
        });
    }

    private void playIconAnim1(final ImageView ivIcon) {
        ivIcon.setVisibility(View.VISIBLE);
        float distance = DisplayUtils.dip2px(40);
        ValueAnimator anim1 = ValueAnimator.ofFloat(0f, distance);
        anim1.setDuration(mTime);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());

        ivIcon.setTranslationY(0);
        ivIcon.setScaleX(1);
        ivIcon.setScaleY(1);
        ivIcon.setAlpha(1f);
        anim1.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            float percent = currentValue / distance;
            ivIcon.setScaleX(1 - percent);
            ivIcon.setScaleY(1 - percent);

            if (1 - percent <= 0.5) {
                percent = 0.5f;
            }
            ivIcon.setAlpha(1 - percent);
            ivIcon.setTranslationY(-currentValue);
        });

        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Bitmap bitmap = getNextImg();
                if (bitmap != null) {
                    ivIcon.setImageBitmap(bitmap);
                    playIconAnim2(ivIcon);
                } else {
                    ivIcon.setVisibility(View.GONE);
                }

            }
        });
        anim1.start();
    }

    private void playIconAnim2(final ImageView ivIcon) {
        ivIcon.setVisibility(View.VISIBLE);
        float distance = DisplayUtils.dip2px(40);
        ivIcon.setTranslationY(distance);
        ivIcon.setScaleX(0);
        ivIcon.setScaleY(0);
        ivIcon.setAlpha(0.5f);

        ValueAnimator anim2 = ValueAnimator.ofFloat(distance, 0f);
        anim2.setDuration(mTime);
        ivIcon.setPivotX(0.5f * ivIcon.getMeasuredWidth());
        ivIcon.setPivotY(0.5f * ivIcon.getMeasuredHeight());
        anim2.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            float percent = currentValue / distance;
            ivIcon.setScaleX(1 - percent);
            ivIcon.setScaleY(1 - percent);
            float alpha = 1 - percent;
            if (alpha <= 0.5) {
                alpha = 0.5f;
            }
            ivIcon.setAlpha(alpha);
            ivIcon.setTranslationY(currentValue);
        });

        anim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                playIconAnim1(ivIcon);
            }
        });

        anim2.start();
    }

    private int mCurImgIndex = 0;

    private Bitmap getNextImg() {
        if (mSelectedList != null && mCurImgIndex < mSelectedList.size()) {
            MultiItemInfo itemInfo = mSelectedList.get(mCurImgIndex);
            if (itemInfo instanceof PowerChildInfo) {
                mCurImgIndex++;
                PowerChildInfo childInfo = (PowerChildInfo) itemInfo;
                Bitmap icon = AppUtils.getAppIcon(this, childInfo.packageName);
                if (icon == null) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.clean_icon_apk);
                }
                return icon;
            }
        }
        return null;
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showStartAnim() {
        //获取背景，并将其强转成AnimationDrawable
        AnimationDrawable animationDrawable = (AnimationDrawable) mIvAnimationStartView.getBackground();
        //判断是否在运行
        if (!animationDrawable.isRunning()) {
            //开启帧动画
            animationDrawable.start();
        }
    }

    /**
     * 完成动画
     */
    public void showFinishAnim() {
        viewPageEventCode = "powersave_finish_annimation_page_view_page";
        viewPageEventName = "省电完成动画展示页浏览";
        sourcePage = "powersave_animation_page";
        currentPage = "powersave_finish_annimation_page";
        sysReturnEventName = "省电完成动画展示页返回";
        returnEventName = "省电完成动画展示页返回";
        NiuDataAPI.onPageStart(viewPageEventCode, viewPageEventName);
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, viewPageEventCode, viewPageEventName);

        SPUtil.setLastPowerCleanTime(System.currentTimeMillis());
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);

        if (mIvAnimationStartView != null)
            mIvAnimationStartView.setVisibility(View.INVISIBLE);

//        mLottieAnimationFinishView.useHardwareAcceleration();
        mLottieAnimationFinishView.setImageAssetsFolder("images");
        mLottieAnimationFinishView.setAnimation("data_clean_finish.json");
        mLottieAnimationFinishView.playAnimation();
        mLottieAnimationFinishView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mRlResult.setVisibility(View.GONE);
                mFlAnim.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showCleanFinishUI();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void showCleanFinishUI() {
        if (mIsFinish) return;
        //保存超强省电 省电完成时间
        if (PreferenceUtil.getPowerCleanTime()) {
            PreferenceUtil.savePowerCleanTime();
        }

        EventBus.getDefault().post(new FunctionCompleteEvent( getString(R.string.tool_super_power_saving)));

        LocalPushUtils.getInstance().updateLastUsedFunctionTime(SpCacheConfig.KEY_FUNCTION_POWER_SAVING);
        PreferenceUtil.saveCleanPowerUsed(true);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        AppHolder.getInstance().setCleanFinishSourcePageId("powersave_finish_annimation_page");

        Intent intent=new Intent()
                .putExtra(ExtraConstant.TITLE, getString(R.string.tool_super_power_saving));
        StartFinishActivityUtil.Companion.gotoFinish(this, intent);

        finish();
    }

    private void showFinishWebview() {
        isFinish = true;

        mLottieAnimationFinishView.setVisibility(View.GONE);
        mFlAnim.setVisibility(View.GONE);
        mRlResult.setVisibility(View.GONE);
        mAppBarLayout.setExpanded(true);
        mLlResultTop.setVisibility(View.VISIBLE);
        int startHeight = ScreenUtils.getFullActivityHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, 0);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mRlResult.getLayoutParams();
        anim.addUpdateListener(valueAnimator -> {
            rlp.topMargin = (int) valueAnimator.getAnimatedValue();
            if (mRlResult != null)
                mRlResult.setLayoutParams(rlp);
        });
        anim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //省电完成 返回点击
                mIsFinish = true;
                if (isFinish)
                    StatisticsUtils.trackClick("Super_Power_Saving_Completion_Return_click", "\"超强省电完成返回\"点击", "Super_Power_Saving_page", "Super_Power_Saving_Completion_page");
            case R.id.btn_cancel:
                StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
                finish();
                break;
            case R.id.layout_not_net:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mIsFinish = true;
        StatisticsUtils.trackClick("system_return_click", returnEventName, sourcePage, currentPage);
        super.onBackPressed();
    }

}
