package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 1.2.1 版本清理
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
        setLeftTitle("建议清理");
    }

    @Override
    protected void setListener() {
        mBtnLeft.setOnClickListener(v -> {
            Toast.makeText(this,"点击 click",Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    @Override
    protected void loadData() {

    }
}
