package com.xiaoniu.cleanking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

public class TestActivity extends BaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initVariable(Intent intent) {
        setTitle("超强省电");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showErrorView();
            }
        });

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }
}
