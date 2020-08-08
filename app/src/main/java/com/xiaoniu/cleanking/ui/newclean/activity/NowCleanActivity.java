package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanCleanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanResultFragment;
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener;
import com.xiaoniu.cleanking.ui.newclean.util.AlertDialogUtil;
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.core.content.ContextCompat;

/**
 * 一键清理主页面
 */
public class NowCleanActivity extends BaseActivity {
//    private CountEntity mCountEntity;
//    private LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups;
    private boolean isScan = false;
    private boolean isClean = true;
    private boolean isBackClick = false;
//    private int scanningFileCount = 0;
//    private LinkedHashMap<ScanningResultType, JunkGroup> junkTitleMap;
//    private LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap;

    public void setClean(boolean clean) {
        isClean = clean;
    }
 /*   public LinkedHashMap<ScanningResultType, JunkGroup> getJunkGroups() {
        return mJunkGroups;
    }*/

   /* public void setJunkGroups(LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups) {
        this.mJunkGroups = mJunkGroups;
    }*/

/*
    public void setCountEntity(CountEntity mCountEntity) {
        this.mCountEntity = mCountEntity;
    }*/

 /*   public CountEntity getCountEntity() {
        return mCountEntity;
    }*/

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
        // Umeng --- java.lang.NullPointerException: Attempt to invoke virtual method 'androidx.appcompat.widget.Toolbar com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity.getToolBar()' on a null object reference
        if (null != getToolBar()) {
            getToolBar().setVisibility(View.GONE); //不显示公共toobar
        }

        //如果是从推荐功能进来的，跳入扫描页面
        boolean fromRecommend=getIntent().getBooleanExtra("fromRecommend",false);
        if(!fromRecommend&&ScanDataHolder.getInstance().getScanState() > 0 && ScanDataHolder.getInstance().getmJunkGroups().size() > 0){
            scanFinish();
        } else {
            startScan();
        }

    }

    private void addClick(Intent intent) {
        if (intent != null) {
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)) {
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
        isScan = true;
        setLeftTitle(getString(R.string.scaning)); // xx
        AppHolder.getInstance().setCleanFinishSourcePageId("home_page");
        ScanFragment mScanFragment = ScanFragment.newInstance();
        replaceFragment(R.id.fl_content, mScanFragment, false);
    }

    /**
     * 扫描完成
     */
    public void scanFinish() {
        isScan = false;
        setLeftTitle("建议清理");
        AppHolder.getInstance().setCleanFinishSourcePageId("clean_up_scan_page");
        LinkedHashMap<ScanningResultType, JunkGroup> resultMap = ScanDataHolder.getInstance().getmJunkGroups();
        if (resultMap != null && resultMap.size() > 0) {
            long totalSize = 0;
            for (Map.Entry<ScanningResultType, JunkGroup> map : resultMap.entrySet()) {
                totalSize += map.getValue().mSize;
            }
            if (totalSize == 0) {
                showCleanResult();
            } else {
                ScanResultFragment scanResultFragment = ScanResultFragment.createFragment();
                replaceFragment(R.id.fl_content, scanResultFragment, false);
            }
        } else {
            showCleanResult();
        }
    }

    private void showCleanResult() {
        finish();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.tool_suggest_clean));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        PreferenceUtil.saveNowCleanTime();
        StartFinishActivityUtil.Companion.gotoFinish(this, bundle);
    }

    @Override
    protected void setListener() {
        mBtnLeft.setOnClickListener(v -> {
            backClick(true);
        });
    }

    /**
     * 返回事件
     *
     * @param isLeftBtn true toolbar返回事件 false 系统返回
     */
    public void backClick(boolean isLeftBtn) {
        if (isBackClick)
            return;
        if (isScan) {
            //btn返回或者系统返回（点击事件）
            StatisticsUtils.trackClick(isLeftBtn ? "return_back_click" : "system_return_back_click", "用户在清理扫描页点击返回", "home_page", "clean_up_scan_page");
            //TODO 停止清理
            AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "清理未完成，大量垃圾会影响手机使用。", "继续清理", "确认退出", new ClickListener() {
                @Override
                public void clickOKBtn() {
                    //扫描中弹框_继续扫描按钮
                    StatisticsUtils.trackClick("continue_cleaning_up_click", "用户在清理扫描页点击【继续清理】", "home_page", "clean_up_scan_page");
                    isBackClick = false;
                }

                @Override
                public void cancelBtn() {
                    //扫描中弹框_确认按钮
                    StatisticsUtils.trackClick("confirm_exit_click", "用户在清理扫描页点击【确认退出】", "home_page", "clean_up_scan_page");
                    finish();

                }
            }, Color.parseColor("#06C581"), Color.parseColor("#727375"));
        } else {
            if (isClean) {
                //清理页面—btn返回或者系统返回（点击事件）
                StatisticsUtils.trackClick(isLeftBtn ? "return_back_click" : "system_return_back_click", "用户点击【建议清理】返回", "clean_up_scan_page", "scanning_result_page");
                String resultSize = ScanDataHolder.getInstance().getmCountEntity().getResultSize() == null ? "" : ScanDataHolder.getInstance().getmCountEntity().getResultSize();
                AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", resultSize + "垃圾未清理，垃圾过多会造成手机卡顿！", "继续清理", "确认退出", new ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        //扫描中弹框_继续扫描按钮
                        StatisticsUtils.trackClick( "continue_cleaning_up_click", "用户在扫描结果页点击【继续清理】按钮", "clean_up_scan_page", "scanning_result_page");
                        isBackClick = false;
                    }

                    @Override
                    public void cancelBtn() {
                        //扫描中弹框_确认按钮
                        StatisticsUtils.trackClick("confirm_exit_click", "用户在扫描结果页点击【确认退出】按钮", "clean_up_scan_page", "scanning_result_page");
                        finish();
                    }
                }, Color.parseColor("#06C581"), Color.parseColor("#727375"));
            } else {
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

    public void setReadyCleanJunkList(LinkedHashMap<ScanningResultType, JunkGroup> junkTitleMap,
                                      LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap) {
//        this.junkTitleMap = junkTitleMap;
//        this.junkContentMap = junkContentMap;

         ScanDataHolder.getInstance().setJunkContentMap(junkContentMap);

        //切换到清理界面
        ScanCleanFragment scanCleanFragment = ScanCleanFragment.createFragment();
        replaceFragment(R.id.fl_content, scanCleanFragment, true);
    }

//    public LinkedHashMap<ScanningResultType, JunkGroup> getJunkTitleMap() {
//        return junkTitleMap;
//    }

    public LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> getJunkContentMap() {
        return ScanDataHolder.getInstance().getJunkContentMap();
    }


/*
    public void setScanningFileCount(int fileCount) {
        scanningFileCount = fileCount;
    }

    public int getScanningFileCount() {
        return scanningFileCount;
    }*/
}
