package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.suke.widget.SwitchButton;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

/**
 * 超强省电通知 设置
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
            PreferenceUtil.saveLower(isChecked);
            if (isChecked)
                StatisticsUtils.trackClick("Low_Voltage_Notification_Bar_Reminder_on_click", "““低电压通知栏提醒开”点击", "Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
            else
                StatisticsUtils.trackClick("Low_Voltage_Notification_Bar_Reminder_off_click", "““低电压通知栏提醒关”点击", "Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
        });
        mSBtnNightPower.setOnCheckedChangeListener((view, isChecked) -> {
            PreferenceUtil.saveNightPower(isChecked);
            if (isChecked)
                StatisticsUtils.trackClick("Night_power_saving_reminder_on_click", "““夜间省电提醒开”点击", "Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
            else
                StatisticsUtils.trackClick("Night_power_saving_reminder_off_click", "““夜间省电提醒关”点击","Super_Power_Saving_page", "Super_Power_Saving_Notice_page");

        });
        mSBtnErrorPower.setOnCheckedChangeListener((view, isChecked) -> {
            PreferenceUtil.saveErrorPower(isChecked);
            if (isChecked)
                StatisticsUtils.trackClick("Reminder_for_Abnormal_Electricity_Consumption_Elastic_Window_on_click", "““异常耗电弹窗提醒开”点击","Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
            else
                StatisticsUtils.trackClick("Reminder_for_Abnormal_Electricity_Consumption_Elastic_Window_off_click", "““异常耗电弹窗提醒关”点击","Super_Power_Saving_page", "Super_Power_Saving_Notice_page");

        });
        mSBtnLowPower.setChecked(PreferenceUtil.getLower());
        mSBtnNightPower.setChecked(PreferenceUtil.getNightPower());
        mSBtnErrorPower.setChecked(PreferenceUtil.getErrorPower());
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsUtils.trackClick("Super_Power_Saving_Notice_view_page", "“超强省电通知”浏览","Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                StatisticsUtils.trackClick("Super_Power_Saving_Notice_Return_click", "““超强省电通知返回”点击", "Super_Power_Saving_page", "Super_Power_Saving_Notice_page");
                finish();
                break;
        }
    }

}
