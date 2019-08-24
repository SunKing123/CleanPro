package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

/**
 * 通知栏清理
 */
public class NotificationBarMessageActivity extends BaseActivity {

    private FrameLayout mFlClean;
    private TextView mTvClean;
    private TextView mTvTitle;
    private ImageView mIvRight;
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

        mTvTitle.setText(getString(R.string.tool_notification_clean));

    }

    @Override
    protected void setListener() {
        mTvClean.setOnClickListener(v -> {
            finish();
        });
        mIvRight.setOnClickListener(v -> startActivity(null));
    }

    @Override
    protected void loadData() {

    }
}
