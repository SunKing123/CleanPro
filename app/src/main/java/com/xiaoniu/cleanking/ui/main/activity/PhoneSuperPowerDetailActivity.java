package com.xiaoniu.cleanking.ui.main.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.adapter.SuperPowerCleanAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.bean.PowerGroupInfo;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.widget.BattaryView;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class PhoneSuperPowerDetailActivity extends BaseActivity implements View.OnClickListener {

    private XRecyclerView mRecyclerView;
    private TextView mTvClean;
    private ImageView mIvBack;
    private ImageView mIvSet;
    private View mHeaderView;
    private LottieAnimationView mPowerLottieAnimationView;
    private BattaryView mBvView;
    private TextView mTvNum;
    private RelativeLayout mRlResult;
    private LinearLayout mLlBottom;
    private LottieAnimationView mLottieAnimationStartView;

    private SuperPowerCleanAdapter mPowerCleanAdapter;
    private int mSelectedCount;
    private int mPowerSavingTime;

    public static List<MultiItemInfo> sSelectedList;
    private TextView tvHour;
    private TextView tvMini;
    private TextView tvUnitHour;
    private LinearLayout mLlTime;
    private LinearLayout mLlPowerLow;
    private TextView mTvAfterUpdate;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_phone_super_power_detail;
    }

    @Override
    protected void initVariable(Intent intent) {
        mPowerCleanAdapter = new SuperPowerCleanAdapter(PhoneSuperPowerDetailActivity.this);
        PhoneSuperPowerDetailActivity.sSelectedList = null;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.power_recyclerView);
        /*title*/
        mTvClean = findViewById(R.id.tv_super_power);
        mIvBack = findViewById(R.id.iv_back);
        mIvSet = findViewById(R.id.icon_saving_right);

        /*扫描动画*/
        mLlBottom = findViewById(R.id.ll_bottom);
        mRlResult = findViewById(R.id.rl_result);
        mTvNum = findViewById(R.id.tv_num);
        mLottieAnimationStartView = findViewById(R.id.view_lottie_super_saving);

        mHeaderView = mInflater.inflate(R.layout.layout_power_clean_header, mRecyclerView, false);
        mBvView = mHeaderView.findViewById(R.id.bv_view);
        mPowerLottieAnimationView = mHeaderView.findViewById(R.id.view_lottie_super_saving_power);
        tvHour = mHeaderView.findViewById(R.id.tvHour);
        tvMini = mHeaderView.findViewById(R.id.tvMini);
        tvUnitHour = mHeaderView.findViewById(R.id.tv_unit_hour);
        mLlPowerLow = mHeaderView.findViewById(R.id.ll_power_low);
        mLlTime = mHeaderView.findViewById(R.id.ll_time);
        mTvAfterUpdate = mHeaderView.findViewById(R.id.tv_after_update);

        mRecyclerView.setHeaderView(mHeaderView);
        mRecyclerView.setAdapter(mPowerCleanAdapter);
        hideToolBar();
        showStartAnim();
        mHandler.sendEmptyMessageDelayed(1, 5000);
        mHandler.sendEmptyMessageDelayed(2, 1000);

        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //注册接收器以获取电量信息
        registerReceiver(broadcastReceiver,intentFilter);
    }

    private BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量，如未获取具体数值，则默认为0
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            //获取最大电量，如未获取到具体数值，则默认为100
            int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int power = (batteryLevel * 100 / batteryScale);
            //显示电量
            if (power < 10){
                mTvAfterUpdate.setVisibility(View.GONE);
                mLlTime.setVisibility(View.GONE);
                mLlPowerLow.setVisibility(View.VISIBLE);
            }else {
                if (mPowerSavingTime < 60) {
                    tvMini.setText(mPowerSavingTime + "");
                    tvHour.setVisibility(View.GONE);
                    tvUnitHour.setVisibility(View.GONE);
                }else {

                }
                mTvAfterUpdate.setVisibility(View.VISIBLE);
                mLlTime.setVisibility(View.VISIBLE);
                mLlPowerLow.setVisibility(View.GONE);

                int hour = 1 + new Random().nextInt(3);
                tvHour.setText(hour + "");
                int mini = 1 + new Random().nextInt(59);
                tvMini.setText(mini + "");
            }
            mBvView.setBattaryPercent(power);
        }
    };

    @Override
    protected void setListener() {
        mPowerCleanAdapter.setmOnCheckListener(info -> {
            mSelectedCount = mPowerCleanAdapter.getSelectedData().size();
            if (mSelectedCount > 0) {
                mTvClean.setText("一键优化 " + mSelectedCount);
            } else {
                mTvClean.setText("一键优化");
            }
        });

        mIvBack.setOnClickListener(this);
        mIvSet.setOnClickListener(this);
        mTvClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                StatisticsUtils.trackClick("Super_Power_Saving_Return_click", "“超强省电返回”点击", "home_page", "Super_Power_Saving_page");
                finish();
                break;
            case R.id.icon_saving_right:
                StatisticsUtils.trackClick("Super_Power_Saving_Notice_click", "“超强省电通知”点击", "home_page", "Super_Power_Saving_page");
                startActivity(new Intent(mContext, PhoneSuperPowerMessageActivity.class));
                break;
            case R.id.tv_super_power:
                StatisticsUtils.trackClick("One_Touch_Optimize_click", "“一键优化”点击", "home_page", "Super_Power_Saving_page");
                sSelectedList = mPowerCleanAdapter.getSelectedData();

                for (int i = 0; i < sSelectedList.size(); i++) {
                    MultiItemInfo itemInfo = sSelectedList.get(i);
                    if (itemInfo instanceof PowerChildInfo) {
                        PowerChildInfo childInfo = (PowerChildInfo) itemInfo;
                        CleanUtil.killAppProcesses(childInfo.packageName, 0);
                    }
                }

                Intent intent = new Intent(mContext, PhoneSuperSavingNowActivity.class);
                intent.putExtra("processNum", sSelectedList.size());
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void loadData() {
        FileQueryUtils mFileQueryUtils = new FileQueryUtils();
        ArrayList<MultiItemInfo> mPowerList = new ArrayList<>();

        ArrayList<FirstJunkInfo> runningProcess;
        if (Build.VERSION.SDK_INT >= 26) {
            runningProcess = getProcessAbove();
        } else {
            runningProcess = mFileQueryUtils.getRunningProcess();
        }
        HashSet<String> whitelist = getDefaultHoldApp();
        PowerGroupInfo killGroupInfo = new PowerGroupInfo();
        killGroupInfo.type = 0;
        killGroupInfo.title = "耗电应用";
        killGroupInfo.isExpanded = true;
        PowerGroupInfo holdGroupInfo = new PowerGroupInfo();
        holdGroupInfo.type = 1;
        holdGroupInfo.title = "建议继续运行";

        for (int i = 0; i < runningProcess.size(); i++) {
            FirstJunkInfo junkInfo = runningProcess.get(i);
            if (junkInfo != null) {
                PowerChildInfo childInfo = new PowerChildInfo();
                childInfo.appName = junkInfo.getAppName();
                childInfo.packageName = junkInfo.getAppPackageName();

                if (whitelist.contains(childInfo.packageName)) {
                    holdGroupInfo.addItemInfo(childInfo);
                } else {
                    childInfo.selected = 1;
                    mSelectedCount++;
                    killGroupInfo.addItemInfo(childInfo);
                }
            }
        }

        if (killGroupInfo.hasChild()) {
            mPowerList.add(killGroupInfo);
        }

        if (holdGroupInfo.hasChild()) {
            mPowerList.add(holdGroupInfo);
        }
        mPowerCleanAdapter.setData(mPowerList);

        if (mSelectedCount > 0) {
            mTvClean.setText("一键优化 " + mSelectedCount);
        } else {
            mTvClean.setText("一键优化");
        }
        mPowerSavingTime = mSelectedCount * 5;
    }

    private HashSet<String> getDefaultHoldApp() {
        HashSet<String> whitelist = new HashSet<>();
        whitelist.add("com.tencent.mm");
        whitelist.add("com.tencent.mobileqq");
        whitelist.add("com.tencent.wework");
        whitelist.add("com.xiaoniu.cleanking");
        return whitelist;
    }

    //Android O以上用这个方法获取最近使用情况
    @TargetApi(22)
    public ArrayList<FirstJunkInfo> getProcessAbove() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }
        List<UsageStats> lists = usageStatsManager.queryUsageStats(4, System.currentTimeMillis() - 86400000, System.currentTimeMillis());
        ArrayList arrayList = new ArrayList();
        HashSet<String> packages = new HashSet<>();
        if (!(lists == null || lists.size() == 0)) {
            for (UsageStats usageStats : lists) {
                if (!(usageStats == null || usageStats.getPackageName() == null || usageStats.getPackageName().contains("com.cleanmaster.mguard_cn"))) {
                    FirstJunkInfo runningAppProcessInfo = new FirstJunkInfo();
                    runningAppProcessInfo.setAppPackageName(usageStats.getPackageName());
                    runningAppProcessInfo.setAppName(AppUtils.getAppName(this, usageStats.getPackageName()));
                    if (!AppUtils.isSystemApp(this, usageStats.getPackageName())) {
                        if (!packages.contains(usageStats.getPackageName())) {
                            packages.add(usageStats.getPackageName());
                            arrayList.add(runningAppProcessInfo);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    private int num = 0;
    private MyHandler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                showListAppInfo();
            } else if (msg.what == 2) {
                num++;
                mTvNum.setText(String.valueOf(num));
                if (num < 100) {
                    sendEmptyMessageDelayed(2, 30);
                }
            }
        }
    }

    /**
     * 显示超省电应用信息
     */
    private void showListAppInfo() {
        long lastTime = SPUtil.getLastPowerCleanTime();
        long curTime = System.currentTimeMillis();
        if (curTime - lastTime < 3 * 60 * 1000) {
            Intent intent = new Intent(mContext, PhoneSuperSavingNowActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        mRlResult.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLlBottom.setVisibility(View.VISIBLE);
        showPowerAnim();
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
    private void showPowerAnim() {
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
}
