package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.adapter.PowerExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.BattaryView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.OnClick;

/**
 * 超强省电
 */
public class PhoneSuperPowerSavingActivity extends SimpleActivity {

    private AppBarLayout mAppBarLayout;
    private TextView mTvNum;
    private RelativeLayout mRlResultTop;
    private ExpandableListView mEdList;
    private RelativeLayout mRlResult;
    private LinearLayout mLlBottom;
    private LottieAnimationView mLottieAnimationStartView;
    private LottieAnimationView mPowerLottieAnimationView;

    private BattaryView mBvView;
    private CountEntity countEntity;
    private HashMap<Integer, JunkGroup> mJunkGroups = new HashMap<>();
    private PowerExpandableListViewAdapter mAdapter;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsUtils.trackClick("Super_Power_Saving_view_page", "超强省电", "Super_Power_Saving_click", "Super_Power_Saving_page");
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
        mBvView = findViewById(R.id.bv_view);
        mLottieAnimationStartView = findViewById(R.id.view_lottie_super_saving);
        mPowerLottieAnimationView = findViewById(R.id.view_lottie_super_saving_power);

        mEdList.setGroupIndicator(null);
        mEdList.setChildIndicator(null);
        mEdList.setDividerHeight(0);
        mEdList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            JunkGroup junkGroup = mJunkGroups.get(groupPosition);
            if (junkGroup != null) {
                junkGroup.isExpand = !junkGroup.isExpand();
                mAdapter.notifyDataSetChanged();
            }
            return false;
        });
        mAdapter = new PowerExpandableListViewAdapter(this, mEdList);
        mAdapter.setOnItemSelectListener(() -> {

        });

        mEdList.setAdapter(mAdapter);
        mJunkGroups = CleanMainFragment.mJunkGroups;
        mAdapter.setData(mJunkGroups);

        for (int i = 0; i < mJunkGroups.size(); i++) {
            mEdList.expandGroup(i);
        }
        countEntity = CleanUtil.formatShortFileSize(getTotalSize());

        showStartAnim();
        mHandler.sendEmptyMessageDelayed(1,5000);
        mHandler.sendEmptyMessageDelayed(2, 1000);

        mBvView.setBattaryPercent(70);
    }

    private long getTotalSize() {
        long size = 0L;
        for (JunkGroup group : mJunkGroups.values()) {
            for (FirstJunkInfo firstJunkInfo : group.mChildren) {
                if (firstJunkInfo.isAllchecked()) {
                    size += firstJunkInfo.getTotalSize();
                }
            }
        }
        return size;
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
     * 电池动画
     */
    private void showPowerAnim(){
        mPowerLottieAnimationView.useHardwareAcceleration();
        mPowerLottieAnimationView.useHardwareAcceleration();
        mPowerLottieAnimationView.setImageAssetsFolder("images");
        mPowerLottieAnimationView.setAnimation("data_plan.json");
        mPowerLottieAnimationView.playAnimation();
        mPowerLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mPowerLottieAnimationView.playAnimation();
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
        showPowerAnim();
    }

    @OnClick({R.id.iv_back, R.id.icon_saving_right, R.id.ll_bottom})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                StatisticsUtils.trackClick("Super_Power_Saving_Return_click", "“超强省电返回”点击", "Super_Power_Saving_click", "Super_Power_Saving_page");
                finish();
                break;
            case R.id.icon_saving_right:
                StatisticsUtils.trackClick("Super_Power_Saving_Notice_click", "“超强省电通知”点击", "Super_Power_Saving_click", "Super_Power_Saving_page");
                startActivity(PhoneSuperPowerMessageActivity.class);
                finish();
                break;
            case R.id.ll_bottom:
                StatisticsUtils.trackClick("One_Touch_Optimize_click", "“一键优化”点击", "Super_Power_Saving_click", "Super_Power_Saving_page");
                startActivity(PhoneSuperSavingNowActivity.class);
                break;
        }
    }
}
