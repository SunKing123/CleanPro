package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 1.2.1 版本更新 建议清理页面
 */
public class NowCleanActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_now_clean;
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //开始扫描
        startScan();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        setCenterTitle("扫描中");

    }

    @Override
    protected void setListener() {
        mBtnLeft.setOnClickListener(v -> {
            // TODO 添加埋点，弹出待确认提示框

            finish();
        });

    }

    @Override
    protected void loadData() {

    }

    /**
     * 扫描完成
     */
    public void scanFinish(){
        setLeftTitle("建议清理");

    }
}
