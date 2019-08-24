package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 通知栏清理
 */
public class NotificationBarMessageActivity extends BaseActivity {

    private FrameLayout mFlClean;
    private TextView mTvClean;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_message;
    }

    @Override
    protected void initVariable(Intent intent) {
        setTitle(getString(R.string.tool_notification_clean));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mFlClean = findViewById(R.id.fl_clean_layout);
        mTvClean = findViewById(R.id.tv_clean);

    }

    @Override
    protected void setListener() {
        mTvClean.setOnClickListener(v -> {

        });
    }

    @Override
    protected void loadData() {

    }
}
