package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.tool.notify.adapter.NotifyCleanAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
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

    private NotityCleanAnimView mCleanAnimView;

    private LinearLayout mTitleBar;
    private ImageView mIvBack;
    private ImageView mIvSet;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mTitleBar = findViewById(R.id.title_bar);
        mRecyclerView = findViewById(R.id.notify_recyclerView);
        mTvDelete = findViewById(R.id.tv_delete);
        mIvBack = findViewById(R.id.iv_back_notity);
        mIvSet = findViewById(R.id.iv_set);
        mHeaderView = mInflater.inflate(R.layout.layout_notification_clean_header, null);
        mTvNotificationCount = mHeaderView.findViewById(R.id.tvNotificationCount);
        mHeaderView.findViewById(R.id.lay_notify_clean_tips).setVisibility(View.VISIBLE);
        mRecyclerView.setHeaderView(mHeaderView);
        hideToolBar();
        mCleanAnimView = findViewById(R.id.view_clean_anim);
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> finish());
        mIvSet.setOnClickListener(v -> NotifyCleanSetActivity.start(NotifyCleanDetailActivity.this));

        mTvDelete.setOnClickListener(v -> {
            mIsClearNotification = true;
            mCleanAnimView.setData(CleanUtil.formatShortFileSize(100), CleanAnimView.page_junk_clean);
            mCleanAnimView.setVisibility(View.VISIBLE);
            //清理动画
            mCleanAnimView.startTopAnim(false);
            //title bar
            showBarColor(getResources().getColor(R.color.color_FD6F46));
            mCleanAnimView.setOnColorChangeListener(this::showBarColor);
        });
    }


    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        mTitleBar.setBackgroundColor(animatedValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

    @Override
    protected void loadData() {
        ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
        mNotifyCleanAdapter = new NotifyCleanAdapter(mContext);
        mNotifyCleanAdapter.setData(notificationList);
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
