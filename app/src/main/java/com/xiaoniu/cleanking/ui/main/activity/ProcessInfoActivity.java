package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ProcessInfoAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Administrator
 */
@Route(path = RouteConstants.PROCESS_INFO_ACTIVITY)
public class ProcessInfoActivity extends SimpleActivity {

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_process_info;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }

        List<FirstJunkInfo> list = PhoneCoolingActivity.mRunningProcess;

        if (list != null) {
            mTextTitle.setText(list.size() + "个运行的应用");
        }

        initData(list);
    }

    private void initData(List<FirstJunkInfo> list) {
        ProcessInfoAdapter processIconAdapter = new ProcessInfoAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(processIconAdapter);
        processIconAdapter.setData(list);
    }

    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        StatisticsUtils.trackClick("Running_application_return_click","\"运行的应用\"返回","temperature_result_display_page","Running_applications_page ");
        finish();
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("Running_application_return_click","\"运行的应用\"返回","temperature_result_display_page","Running_applications_page ");
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatisticsUtils.trackClick("Running_applications_view_page","\"运行的应用\"浏览","temperature_result_display_page","Running_applications_page ");
    }
}
