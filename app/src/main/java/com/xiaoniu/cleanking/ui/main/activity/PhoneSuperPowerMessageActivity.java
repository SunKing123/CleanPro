package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.suke.widget.SwitchButton;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

/**
 * 超强省电通知
 */
public class PhoneSuperPowerMessageActivity extends BaseActivity implements View.OnClickListener {

    private SwitchButton mSBtnLowPower;
    private SwitchButton mSBtnNightPower;
    private SwitchButton mSBtnErrorPower;
    private ImageView mBack;
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
        mSBtnLowPower = findViewById(R.id.s_btn_low_power);
        mSBtnNightPower = findViewById(R.id.s_btn_night_power);
        mSBtnErrorPower = findViewById(R.id.s_btn_error_power);
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);

        mSBtnLowPower.setOnCheckedChangeListener((view, isChecked) -> {
           //TODO 低电量
            ToastUtils.showLong("");
        });
        mSBtnNightPower.setOnCheckedChangeListener((view, isChecked) -> {
            //TODO 夜间
            ToastUtils.showLong("");
        });
        mSBtnErrorPower.setOnCheckedChangeListener((view, isChecked) -> {
            //TODO 异常耗电
            ToastUtils.showLong("");
        });
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
