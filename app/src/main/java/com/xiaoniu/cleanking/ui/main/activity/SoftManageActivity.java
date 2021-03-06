package com.xiaoniu.cleanking.ui.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.InstallPackageManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.SoftManagePresenter;
import com.xiaoniu.common.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 软件管理
 * Created by lang.chen on 2019/7/9
 */
public class SoftManageActivity extends BaseActivity<SoftManagePresenter> implements InstallPackageManageAdapter.OnCheckListener {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.check_all)
    ImageButton mCheckBoxAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    private InstallPackageManageAdapter mAdapter;
    /**
     * 列表选项的 checkbox关联全选，如果是选择关联的路径 is ture ,else false;  如果为true 不做重复操作
     */
    private boolean mIsCheckAll;

    private MonitorSysReceiver mMonitorSysReceiver;

    @Override
    public void inject(ActivityComponent activityComponent) {

        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_soft_manage;
    }

    @Override
    protected void initView() {

        mAdapter = new InstallPackageManageAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnCheckListener(this);
        mAdapter.setIsShowSubTitle(false);

        mCheckBoxAll.setOnClickListener(v -> {
            if (mIsCheckAll) {
                mIsCheckAll = false;
            } else {
                mIsCheckAll = true;
            }
            mCheckBoxAll.setSelected(mIsCheckAll);
            checkAll(mIsCheckAll);
            totalSelectFiles();
        });

        mPresenter.scanData();

    }





    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        mMonitorSysReceiver = new MonitorSysReceiver();
        registerReceiver(mMonitorSysReceiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        totalSelectFiles();
    }

    public void updateData(List<AppInfoBean> lists) {
        runOnUiThread(() -> {
                mAdapter.clear();
                mAdapter.modifyList(lists);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMonitorSysReceiver);
    }

    @OnClick({R.id.img_back
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            finish();
        } else if (id == R.id.btn_del) { //删除文件

            List<AppInfoBean> lists = mAdapter.getLists();
            for (AppInfoBean appInfoBean : lists) {
                if (appInfoBean.isSelect) {
                    AppUtils.uninstallNormal(getBaseContext(), appInfoBean.packageName);
                }
            }
        }

    }

    /**
     * 全选
     *
     * @param isCheck
     */
    private void checkAll(boolean isCheck) {
        List<AppInfoBean> lists = mAdapter.getLists();

        for (AppInfoBean appInfoBean : lists) {
            appInfoBean.isSelect = isCheck;
        }
        mAdapter.notifyDataSetChanged();

    }


    private int mTotalSize;

    @Override
    public void onCheck(String id, boolean isChecked) {
        List<AppInfoBean> lists = mAdapter.getLists();
        //文件总大小
        mTotalSize = 0;
        boolean isCheckAll = true;
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.packageName.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }

        mAdapter.notifyDataSetChanged();
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                mTotalSize++;
            } else {
                isCheckAll = false;
            }
        }

        mIsCheckAll = isCheckAll;
        mCheckBoxAll.setSelected(mIsCheckAll);

        totalSelectFiles();
    }

    /**
     * 统计文件的大小
     */
    private void totalSelectFiles() {
        mTotalSize = 0;
        List<AppInfoBean> lists = mAdapter.getLists();
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                mTotalSize++;
            }
        }
        if (mTotalSize > 0) {
            mBtnDel.setText(String.format("卸载%s款", mTotalSize));
            mBtnDel.setSelected(true);
            mBtnDel.setClickable(true);
        } else {
            mBtnDel.setText("卸载");
            mBtnDel.setSelected(false);
            mBtnDel.setClickable(false);
        }
    }


    /**
     * 自定义接收卸载广播
     */
    private class MonitorSysReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                mPresenter.scanData();
                mAdapter.clear();
                mAdapter.modifyList(mPresenter.getData());
            }
        }
    }
}
