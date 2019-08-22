package com.xiaoniu.cleanking.ui.main.activity;

import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 超强省电
 */
public class PhoneSuperPowerSavingActivity extends SimpleActivity {

    AppBarLayout mAppBarLayout;
    TextView mTvNum;
    @BindView(R.id.rl_result_top)
    RelativeLayout mRlResultTop;
    @BindView(R.id.ed_list)
    ExpandableListView mEdList;
    @BindView(R.id.rl_result)
    RelativeLayout mRlResult;
    @BindView(R.id.view_lottie_super_saving)
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
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power_saving;
    }

    @Override
    protected void initView() {
        mAppBarLayout = findViewById(R.id.app_power_saving_bar_layout);
        mTvNum = findViewById(R.id.tv_num);
        showStartAnim();
        mHandler.sendEmptyMessageDelayed(1,5000);
        mHandler.sendEmptyMessageDelayed(2, 30);
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
    }

    /**
     * 显示超省电应用信息
     */
    private void showListAppInfo() {
        if (mRlResult == null) return;
        mRlResult.setVisibility(View.GONE);
        mRlResultTop.setVisibility(View.VISIBLE);
        mEdList.setVisibility(View.VISIBLE);
        mAppBarLayout.setExpanded(true);
    }

    @OnClick({R.id.iv_back,R.id.icon_saving_right,R.id.iv_power})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_power:
                startActivity(PhoneSuperSavingNowActivity.class);
                finish();
                break;
            case R.id.icon_saving_right:
                startActivity(PhoneSuperPowerMessageActivity.class);
                finish();
                break;
        }
    }
}
