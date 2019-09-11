package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.suke.widget.SwitchButton;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.InstalledApp;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.adapter.NotifySettingAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationSettingInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationSetEvent;
import com.xiaoniu.cleanking.utils.PackageUtils;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NotifyCleanSetActivity extends BaseActivity {

    private XRecyclerView mRecyclerView;
    private SwitchButton mSwitchButton;
    private NotifySettingAdapter mNotifySettingAdapter;
    private List<NotificationSettingInfo> mAppList;
    private View mHeaderView;
    private TextView mTvSetDesc;

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
        mAppList = new ArrayList<>();
        setLeftTitle(getString(R.string.tool_set));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.set_recyclerView);
        mHeaderView = mInflater.inflate(R.layout.layout_notification_setting_header, mRecyclerView, false);
        mSwitchButton = mHeaderView.findViewById(R.id.switch_button);
        mTvSetDesc = mHeaderView.findViewById(R.id.tvSetDesc);
        mRecyclerView.setHeaderView(mHeaderView);

        if (SPUtil.isCleanNotificationEnable()) {
            mSwitchButton.setChecked(true);
            mTvSetDesc.setVisibility(View.VISIBLE);
        } else {
            mSwitchButton.setChecked(false);
            mTvSetDesc.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        mSwitchButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                StatisticsUtils.trackClick("Open_Notice_Bar_to_Clean_Up_on_click", "“开启通知栏清理开“点击","Notice_Bar_Cleaning_page", "Notification_Bar_Cleaning_Settings_page");
                SPUtil.setCleanNotificationEnable(true);
                mTvSetDesc.setVisibility(View.VISIBLE);
                mNotifySettingAdapter.setData(mAppList);
            } else {
                StatisticsUtils.trackClick("Open_Notice_Bar_to_Clean_Up_off_click", "“开启通知栏清理关“点击","Notice_Bar_Cleaning_page", "Notification_Bar_Cleaning_Settings_page");
                SPUtil.setCleanNotificationEnable(false);
                mTvSetDesc.setVisibility(View.GONE);
                mNotifySettingAdapter.clear();
                EventBus.getDefault().post(new NotificationSetEvent(false));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //通知栏清理完成浏览
        StatisticsUtils.trackClick("Notification_Bar_Cleaning_Settings_view_page", "”通知栏清理设置”浏览","Notice_Bar_Cleaning_page", "Notification_Bar_Cleaning_Settings_page");
    }

    @Override
    protected void loadData() {
        mNotifySettingAdapter = new NotifySettingAdapter(mContext);
        mRecyclerView.setAdapter(mNotifySettingAdapter);
        prepareData();
        if (SPUtil.isCleanNotificationEnable()) {
            mNotifySettingAdapter.setData(mAppList);
        }
    }

    private void prepareData() {
        if (mAppList.size() == 0) {
            ConcurrentHashMap<String, InstalledApp> installedApps = PackageUtils.getInstalledApps();
            Set<String> actualWhitelist = SPUtil.getActualWhitelist();

            List<NotificationSettingInfo> blackList = new ArrayList<>();
            List<NotificationSettingInfo> whitelist = new ArrayList<>();

            Set<Map.Entry<String, InstalledApp>> entries = installedApps.entrySet();
            NotificationSettingInfo data = null;
            InstalledApp value;
            for (Map.Entry<String, InstalledApp> next : entries) {
                if (next == null || next.getKey() == null || (value = next.getValue()) == null) {
                    continue;
                }

                data = new NotificationSettingInfo();
                data.pkg = next.getKey();
//                data.icon = AppUtils.getAppIcon(this, value.packageName);
                data.appName = value.appName;
                data.selected = actualWhitelist.contains(data.pkg);

                if (!SpCacheConfig.APP_ID.equals(value.packageName)) {
                    if (data.selected) {
                        whitelist.add(data);
                    } else {
                        blackList.add(data);
                    }
                }
            }

            mAppList.addAll(whitelist);
            mAppList.addAll(blackList);
        }
    }
}
