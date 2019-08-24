package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 通知栏清理
 */
public class NotificationBarMessageActivity extends BaseActivity {

    private LinearLayout mFlClean;
    private TextView mTvClean;
    private TextView mTvTitle;
    private ImageView mIvRight;
    private ImageView mIvBack;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_message;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mFlClean = findViewById(R.id.fl_clean_layout);
        mTvClean = findViewById(R.id.tv_clean);
        mTvTitle = findViewById(R.id.tv_title);
        mIvRight = findViewById(R.id.iv_set);
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle.setText(getString(R.string.tool_notification_clean));

    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> finish());
        mTvClean.setOnClickListener(v -> {
            finish();
        });
        mIvRight.setOnClickListener(v -> startActivity(new Intent(this,NotificationBarMessageSetActivity.class)));
    }

    @Override
    protected void loadData() {

    }
}
