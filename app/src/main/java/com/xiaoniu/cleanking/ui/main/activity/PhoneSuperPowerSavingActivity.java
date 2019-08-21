package com.xiaoniu.cleanking.ui.main.activity;

import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class PhoneSuperPowerSavingActivity extends SimpleActivity {

    @BindView(R.id.app_power_saving_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_num)
    TextView mTvNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_super_power_saving;
    }

    @Override
    protected void initView() {
        mAppBarLayout.setExpanded(false);
        mTvNum.setText("80");
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
