package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.newclean.fragment.CleanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener;
import com.xiaoniu.cleanking.ui.newclean.util.AlertDialogUtil;
import com.xiaoniu.common.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //注册订阅者
        EventBus.getDefault().register(this);
        startScan();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        isScan = true;
        setCenterTitle("扫描中");
        mScanFragment = ScanFragment.newInstance();
        replaceFragment(R.id.fl_content, mScanFragment, false);
    }

    /**
     * 扫描完成
     */
    public void scanFinish(){
        //
        isScan = false;
        setCenterTitle("");
        setLeftTitle("建议清理");
        mCleanFragment = CleanFragment.newInstance();
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
    private void backClick(boolean isLeftBtn) {
        if (isBackClick)
            return;
        // TODO 添加埋点，弹出待确认提示框
        if (isScan) {
            //TODO 停止清理
            if (mScanFragment != null)
                mScanFragment.stopScan();
            AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "清理未完成，大量垃圾会影响手机使用。", "确认退出", "继续清理", new ClickListener() {
                @Override
                public void clickOKBtn() {
                    finish();
                }

                @Override
                public void cancelBtn() {
                    isBackClick = false;
                    if (mScanFragment != null)
                        mScanFragment.startScan();
                }
            },Color.parseColor("#06C581"), Color.parseColor("#727375"));
        }else {
            //TODO 停止清理
            if (isClean) {
                if (mCleanFragment != null)
                    mCleanFragment.stopClean();
                AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "正在清理，退出将中断", "确认退出", "继续清理", new ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        finish();
                    }

                    @Override
                    public void cancelBtn() {
                        isBackClick = false;
                        //TODO 继续清理
                        if (mCleanFragment != null)
                            mCleanFragment.starClean();
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

    @Subscribe
    public void onEventClean(CleanEvent cleanEvent){
        scanFinish();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注册订阅者
        EventBus.getDefault().unregister(this);
    }
}
