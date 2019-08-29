package com.xiaoniu.cleanking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.ToastUtils;

public class TestActivity extends BaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initVariable(Intent intent) {
        setLeftTitle("超强省电");

        addRightButton(R.drawable.cc_icon_none_file, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("haha");
            }
        });

        addRightButton(R.drawable.cc_icon_none_file, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("haha");
            }
        });
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
