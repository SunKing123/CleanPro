package com.xiaoniu.cleanking.ui.main.activity;

import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

public class PhoneSuperPowerActivity extends SimpleActivity {

    private TextView mTvClean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power;
    }

    @Override
    protected void initView() {
        mTvClean = findViewById(R.id.tv_clean);

        mTvClean.setOnClickListener(v -> {
            startActivity(PhoneSuperPowerSavingActivity.class);
            finish();
        });
    }
}
