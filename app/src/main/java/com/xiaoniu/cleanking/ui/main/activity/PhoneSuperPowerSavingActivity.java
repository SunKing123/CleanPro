package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.lang.ref.WeakReference;

import butterknife.OnClick;

/**
 * 超强省电
 */
public class PhoneSuperPowerSavingActivity extends SimpleActivity {

    AppBarLayout mAppBarLayout;
    TextView mTvNum;
    RelativeLayout mRlResultTop;
    ExpandableListView mEdList;
    RelativeLayout mRlResult;
    LinearLayout mLlBottom;
    LottieAnimationView mLottieAnimationStartView;

    private int num = 0;
    MyHandler mHandler = new MyHandler(this);

    class MyHandler extends Handler {
        WeakReference<Activity> mActivity;
        public MyHandler(Activity con){
            this.mActivity = new WeakReference<>(con);
        }
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1 ){
                showListAppInfo();
            }else if(msg.what == 2){
                num ++;
                mTvNum.setText(String.valueOf(num));
                if (num < 100) {
                    sendEmptyMessageDelayed(2, 30);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsUtils.trackClick("Super_Power_Saving_view_page", "超强省电", AppHolder.getInstance().getSourcePageId(), "Super_Power_Saving_page");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power_saving;
    }

    @Override
    protected void initView() {
        mAppBarLayout = findViewById(R.id.app_power_saving_bar_layout);
        mLlBottom = findViewById(R.id.ll_bottom);
        mRlResultTop = findViewById(R.id.rl_result_top);
        mRlResult = findViewById(R.id.rl_result);
        mTvNum = findViewById(R.id.tv_num);
        mEdList = findViewById(R.id.ed_list);
        mLottieAnimationStartView = findViewById(R.id.view_lottie_super_saving);

        showStartAnim();
        mHandler.sendEmptyMessageDelayed(1,5000);
        mHandler.sendEmptyMessageDelayed(2, 1000);
    }

    /**
     * 显示正在分析动画...
     */
    private void showStartAnim() {
        mLottieAnimationStartView.useHardwareAcceleration();
        mLottieAnimationStartView.useHardwareAcceleration();
        mLottieAnimationStartView.setImageAssetsFolder("images");
        mLottieAnimationStartView.setAnimation("data_super_power_saving.json");
        mLottieAnimationStartView.playAnimation();
        mLottieAnimationStartView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mLottieAnimationStartView.cancelAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 显示超省电应用信息
     */
    private void showListAppInfo() {
        if (mRlResult == null) return;
        mRlResult.setVisibility(View.GONE);
        mRlResultTop.setVisibility(View.VISIBLE);
        mEdList.setVisibility(View.VISIBLE);
        mLlBottom.setVisibility(View.VISIBLE);
        mAppBarLayout.setExpanded(true);
    }

    @OnClick({R.id.iv_back,R.id.icon_saving_right,R.id.iv_power,R.id.ll_bottom})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                StatisticsUtils.trackClick("Super_Power_Saving_Return_click", "“超强省电返回”点击", AppHolder.getInstance().getSourcePageId(), "Super_Power_Saving_page");
                finish();
                break;
            case R.id.iv_power:
                finish();
                break;
            case R.id.icon_saving_right:
                StatisticsUtils.trackClick("Super_Power_Saving_Notice_click", "“超强省电通知”点击", AppHolder.getInstance().getSourcePageId(), "Super_Power_Saving_page");
                startActivity(PhoneSuperPowerMessageActivity.class);
                finish();
                break;
            case R.id.ll_bottom:
                StatisticsUtils.trackClick("One_Touch_Optimize_click", "“一键优化”点击", AppHolder.getInstance().getSourcePageId(), "Super_Power_Saving_page");
                startActivity(PhoneSuperSavingNowActivity.class);
                break;
        }
    }
}
