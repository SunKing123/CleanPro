package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

public class PhoneSuperPowerMessageActivity extends BaseActivity implements View.OnClickListener {
    ImageView mBack;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_phone_super_power_messge;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mBack = findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
