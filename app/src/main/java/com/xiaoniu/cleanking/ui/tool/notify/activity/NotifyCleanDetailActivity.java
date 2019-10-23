package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.interfac.AnimationEnd;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.newclean.activity.CleanFinishAdvertisementActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.notify.adapter.NotifyCleanAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationCleanEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationSetEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.ResidentUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotifyCleanDetailActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private View mHeaderView;
    private TextView mTvNotificationCount;
    private NotifyCleanAdapter mNotifyCleanAdapter;
    private boolean mIsClearNotification;
    private TextView mTvDelete;

    private NotityCleanAnimView mCleanAnimView;

    private LinearLayout mTitleBar;
    private ImageView mIvBack;
    private ImageView mIvSet;
    private boolean isCleanFinish = false;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //所有应用所占内存大小

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
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        getAccessListBelow();
        mTitleBar = findViewById(R.id.title_bar);
        mRecyclerView = findViewById(R.id.notify_recyclerView);
        mTvDelete = findViewById(R.id.tv_delete);
        mIvBack = findViewById(R.id.iv_back_notity);
        mIvSet = findViewById(R.id.iv_set);
        mHeaderView = mInflater.inflate(R.layout.layout_notification_clean_header, null);
        mTvNotificationCount = mHeaderView.findViewById(R.id.tvNotificationCount);
        mTvNotificationCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));
        mHeaderView.findViewById(R.id.lay_notify_clean_tips).setVisibility(View.VISIBLE);
        mRecyclerView.setHeaderView(mHeaderView);
        hideToolBar();
        mCleanAnimView = findViewById(R.id.view_clean_anim);
        mCleanAnimView.setAnimationEnd(new AnimationEnd() {
            @Override
            public void onAnimationEnd() {
                showCleanFinishView();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //通知栏清理浏览"
        StatisticsUtils.trackClick("Notice_Bar_Cleaning_view_page", "\"通知栏清理\"浏览", "home_page", "Notice_Bar_Cleaning_page");
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> {
            finish();
            if (isCleanFinish) {
                //通知栏清理完成返回 点击",
                StatisticsUtils.trackClick("Notice_Bar_Cleaning_Completed_Return_click", "\"通知栏清理完成返回\"点击", "Notice_Bar_Cleaning_page", "Notice_Bar_Cleaning_Completed_page");
            } else {
                //通知栏清理返回 点击",
                StatisticsUtils.trackClick("Notice_Bar_Cleaning_Return_click", "\"通知栏清理返回\"点击", "home_page", "Notice_Bar_Cleaning_page");
            }
        });
        mIvSet.setOnClickListener(v -> {
            NotifyCleanSetActivity.start(NotifyCleanDetailActivity.this);
            //通知栏清理返回 点击",
            StatisticsUtils.trackClick("Notification_Bar_Cleaning_Settings_click", "\"通知栏清理设置\"点击", "home_page", "Notice_Bar_Cleaning_page");
        });

        mTvDelete.setOnClickListener(v -> {
            //通知栏清理
            AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.NOTITY);
            mIsClearNotification = true;
            NotifyCleanManager.getInstance().cleanAllNotification();
            EventBus.getDefault().post(new ResidentUpdateEvent(true));
            mNotifyCleanAdapter.clear();

            mCleanAnimView.setData(CleanUtil.formatShortFileSize(100), CleanAnimView.page_junk_clean);
            mCleanAnimView.setVisibility(View.VISIBLE);
            //清理动画
            mCleanAnimView.startTopAnim(false);
            //title bar
            showBarColor(getResources().getColor(R.color.color_06C581));
//            mCleanAnimView.setOnColorChangeListener(this::showBarColor);
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

        if (notificationList.size() <= 0) {
            showCleanFinishView();
        }
    }

    @Subscribe
    public void onEventMainThread(NotificationCleanEvent event) {
        if (!mIsClearNotification && event != null) {
            ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
            mNotifyCleanAdapter.setData(notificationList);
            mTvNotificationCount.setText(notificationList.size() + "");
            if (notificationList.size() <= 0) {
                showCleanFinishView();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(NotificationSetEvent event) {
        if (event != null && !event.isEnable()) {
            NotifyCleanManager.getInstance().cleanAllNotification();
            EventBus.getDefault().post(new ResidentUpdateEvent(false));
            mNotifyCleanAdapter.clear();
            showCleanFinishView();
        }
    }

    private void showCleanFinishView() {
//        isCleanFinish = true;
//        /*显示完成页*/
//        mCleanAnimView.setVisibility(View.VISIBLE);
//        showBarColor(getResources().getColor(R.color.color_06C581));
//        mCleanAnimView.setViewTrans();
        //通知栏清理完成浏览
        StatisticsUtils.trackClick("Notice_Bar_Cleaning_Completed_view_page", "\"通知栏清理完成\"浏览", "Notice_Bar_Cleaning_page", "Notice_Bar_Cleaning_Completed_page");

        //保存通知栏清理完成时间
        if (PreferenceUtil.getNotificationCleanTime()) {
            PreferenceUtil.saveNotificationCleanTime();
        }
        PreferenceUtil.saveCleanNotifyUsed(true);
        boolean isOpen = false;
        //solve umeng error --> SwitchInfoList.getData()' on a null object reference
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpen = switchInfoList.isOpen();
                }
            }
        }
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        if (isOpen && PreferenceUtil.getShowCount(getString(R.string.tool_notification_clean), mRamScale, mNotifySize, mPowerSize) < 3) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_notification_clean));
            startActivity(CleanFinishAdvertisementActivity.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_notification_clean));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            Intent intent = new Intent(this, NewCleanFinishActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        finish();
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void reduceSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }

                @Override
                public void totalSize(int p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    getAccessListBelow(strings);
                });
    }

    //低于Android O
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null) return;
        //悟空清理app加入默认白名单
        for (FirstJunkInfo firstJunkInfo : listInfo) {
            if (SpCacheConfig.APP_ID.equals(firstJunkInfo.getAppPackageName())) {
                listInfo.remove(firstJunkInfo);
            }
        }
        if (listInfo.size() != 0) {
            mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
        }
    }
}
