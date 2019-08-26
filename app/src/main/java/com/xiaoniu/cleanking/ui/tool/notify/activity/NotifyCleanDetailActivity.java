package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.notify.adapter.NotifyCleanAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class NotifyCleanDetailActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private View mHeaderView;
    private TextView mTvNotificationCount;
    private NotifyCleanAdapter mNotifyCleanAdapter;
    private boolean mIsClearNotification;
    private int mCurrentCleanCount;
    private TextView mTvDelete;

    public static void startNotificationCleanActivity(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, NotifyCleanDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_clean;
    }

    @Override
    protected void initVariable(Intent intent) {
        EventBus.getDefault().register(this);
        setTitle("通知栏清理");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.notify_recyclerView);
        mTvDelete = findViewById(R.id.tv_delete);
        mHeaderView = mInflater.inflate(R.layout.layout_notification_clean_header, null);
        mTvNotificationCount = (TextView) mHeaderView.findViewById(R.id.tvNotificationCount);
        mHeaderView.findViewById(R.id.lay_notify_clean_tips).setVisibility(View.VISIBLE);
        mRecyclerView.setHeaderView(mHeaderView);
    }

    @Override
    protected void setListener() {
        //设置按钮
        addRightButton(R.mipmap.icon_set, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NotifyCleanSetActivity.start(NotifyCleanDetailActivity.this);
                return true;
            }
        });

        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsClearNotification = true;
                /*todo 清理动画*/

            }
        });
    }

    @Override
    protected void loadData() {
        ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
        mNotifyCleanAdapter = new NotifyCleanAdapter(mContext, notificationList);
        mRecyclerView.setAdapter(mNotifyCleanAdapter);
        mTvNotificationCount.setText(notificationList.size() + "");
    }

    @Subscribe
    public void onEventMainThread(NotificationUpdateEvent event) {
        if (!mIsClearNotification && event != null) {
            ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
            mNotifyCleanAdapter.setData(notificationList);
            mTvNotificationCount.setText(notificationList.size() + "");
            mCurrentCleanCount = mCurrentCleanCount + event.cleanCount;
            if (notificationList.size() <= 0) {
                /*todo 清理完成页*/
            }
        }
    }
}
