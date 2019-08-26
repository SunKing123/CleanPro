package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

public class NotifyCleanSetActivity extends BaseActivity {

    public static void start(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, NotifyCleanSetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

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
