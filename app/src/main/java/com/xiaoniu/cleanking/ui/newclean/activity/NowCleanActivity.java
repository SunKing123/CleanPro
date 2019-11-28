package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.fragment.CleanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener;
import com.xiaoniu.cleanking.ui.newclean.util.AlertDialogUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.HashMap;

/**
 * 1.2.1 版本更新 建议清理页面
 */
public class NowCleanActivity extends BaseActivity {
    private CountEntity mCountEntity;
    private HashMap<Integer, JunkGroup> mJunkGroups;
    private boolean isScan = false;
    private boolean isClean = true;
    private ScanFragment mScanFragment;
    private CleanFragment mCleanFragment;
    private boolean isBackClick = false;

    public void setClean(boolean clean) {
        isClean = clean;
    }

    public HashMap<Integer, JunkGroup> getJunkGroups() {
        return mJunkGroups;
    }

    public void setJunkGroups(HashMap<Integer, JunkGroup> mJunkGroups) {
        this.mJunkGroups = mJunkGroups;
    }

    public void setCountEntity(CountEntity mCountEntity) {
        this.mCountEntity = mCountEntity;
    }

    public CountEntity getCountEntity() {
        return mCountEntity;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_now_clean;
    }

    @Override
    protected void initVariable(Intent intent) {
        addClick(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForWindow(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        startScan();
    }

    private void addClick(Intent intent) {
        if (intent != null){
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)){
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_clean_click");
                StatisticsUtils.trackClick("toggle_clean_click", "常驻通知栏点击清理", "", "toggle_page");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addClick(intent);
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        // Umeng --- java.lang.NullPointerException: Attempt to invoke virtual method 'android.support.v7.widget.Toolbar com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity.getToolBar()' on a null object reference
        if(null != getToolBar()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getToolBar().setBackgroundColor(getResources().getColor(R.color.color_4690FD));
            } else {
                getToolBar().setBackgroundColor(getResources().getColor(R.color.color_FD6F46));
            }
        }

        isScan = true;
        setLeftTitle("扫描中");
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        mScanFragment = ScanFragment.newInstance();
        getToolBar().setVisibility(View.GONE);//不显示公共toobar
        replaceFragment(R.id.fl_content, mScanFragment, false);
    }

    /**
     * 扫描完成、跳转诊断页
     */
    public void scanFinish(){
        isScan = false;
        setLeftTitle("建议清理");
        AppHolder.getInstance().setCleanFinishSourcePageId("clean_up_scan_page");
        mCleanFragment = CleanFragment.newInstance();
        getToolBar().setVisibility(View.GONE);          //不显示公共toobar
        replaceFragment(R.id.fl_content, mCleanFragment, false);
    }

    @Override
    protected void setListener() {
        mBtnLeft.setOnClickListener(v -> {
           backClick(true);
        });
    }

    /**
     * 返回事件
     * @param isLeftBtn  true toolbar返回事件 false 系统返回
     */
    public void backClick(boolean isLeftBtn) {
        if (isBackClick)
            return;
        if (isScan) {
            //btn返回或者系统返回（点击事件）
            StatisticsUtils.trackClick(isLeftBtn ? "return_back_click" : "system_return_back_click", "用户在清理扫描页点击返回", "home_page", "clean_up_scan_page");
            //TODO 停止清理
            if (mScanFragment != null)
                mScanFragment.stopScan();
            AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "清理未完成，大量垃圾会影响手机使用。", "继续清理", "确认退出", new ClickListener() {
                @Override
                public void clickOKBtn() {
                    //扫描中弹框_继续扫描按钮
                    StatisticsUtils.trackClick( "continue_cleaning_up_click", "用户在清理扫描页点击【继续清理】", "home_page", "clean_up_scan_page");
                    isBackClick = false;
                    if (mScanFragment != null)
                        mScanFragment.startScan();
                }

                @Override
                public void cancelBtn() {
                    //扫描中弹框_确认按钮
                    StatisticsUtils.trackClick( "confirm_exit_click", "用户在清理扫描页点击【确认退出】", "home_page", "clean_up_scan_page");
                    finish();

                }
            },Color.parseColor("#06C581"), Color.parseColor("#727375"));
        }else {
            //TODO 停止清理
            if (isClean) {
                //清理页面—btn返回或者系统返回（点击事件）
                StatisticsUtils.trackClick(isLeftBtn ? "return_back_click" : "system_return_back_click", "用户点击【建议清理】返回", "clean_up_scan_page", "scanning_result_page");
                if (mCleanFragment != null)
                    mCleanFragment.stopClean();
                AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "正在清理，退出将中断", "继续清理", "确认退出", new ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        //扫描中弹框_继续扫描按钮
                        StatisticsUtils.trackClick( "continue_cleaning_up_click", "用户在扫描结果页点击【继续清理】按钮", "clean_up_scan_page", "scanning_result_page");
                        isBackClick = false;
                        //TODO 继续清理
                        if (mCleanFragment != null)
                            mCleanFragment.reStartClean();
                    }

                    @Override
                    public void cancelBtn() {
                        //扫描中弹框_确认按钮
                        StatisticsUtils.trackClick( "confirm_exit_click", "用户在扫描结果页点击【确认退出】按钮", "clean_up_scan_page", "scanning_result_page");
                        finish();
                    }
                },Color.parseColor("#06C581"), Color.parseColor("#727375"));
            }else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        backClick(false);
        isBackClick = true;
    }

    @Override
    protected void loadData() {

    }
}
