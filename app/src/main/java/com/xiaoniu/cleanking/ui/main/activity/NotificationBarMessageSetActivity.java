package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

public class NotificationBarMessageSetActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notification_bar_message_set;
    }

    @Override
    protected void initVariable(Intent intent) {
        setTitle(getString(R.string.tool_set));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }
}
