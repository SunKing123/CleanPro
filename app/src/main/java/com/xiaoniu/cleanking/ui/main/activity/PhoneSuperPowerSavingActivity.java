package com.xiaoniu.cleanking.ui.main.activity;

import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

import butterknife.OnClick;

public class PhoneSuperPowerSavingActivity extends SimpleActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power_saving;
    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.iv_back)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
