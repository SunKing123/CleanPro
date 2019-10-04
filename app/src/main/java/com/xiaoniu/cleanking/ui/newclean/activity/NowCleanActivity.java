package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.newclean.fragment.CleanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
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
        setCenterTitle("扫描中");
        replaceFragment(R.id.fl_content, ScanFragment.newInstance(), false);
    }

    /**
     * 扫描完成
     */
    public void scanFinish(){
        setCenterTitle("");
        setLeftTitle("建议清理");
        replaceFragment(R.id.fl_content, CleanFragment.newInstance(), false);
    }

    @Override
    protected void setListener() {
        mBtnLeft.setOnClickListener(v -> {
            // TODO 添加埋点，弹出待确认提示框
            finish();
        });

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
