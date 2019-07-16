package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListSpeedAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListSpeedPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 加速白名单管理
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedManageActivity extends BaseActivity<WhiteListSpeedPresenter> implements WhiteListSpeedAdapter.OnCheckListener {


    public static final int REQUEST_CODE_UPDATE = 0x1101;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty_view)
    LinearLayout mLLEmptyView;
    @BindView(R.id.ll_head)
    LinearLayout mLLHead;

    private WhiteListSpeedAdapter mAdapter;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        mPresenter.scanData();
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_speed;
    }

    @Override
    protected void initView() {
        mAdapter = new WhiteListSpeedAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.modifyList(mPresenter.getData());
        mAdapter.setOnCheckListener(this);
        setEmptyView();
    }

    @OnClick({R.id.img_back, R.id.ll_add})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.ll_add) {
            Intent intent = new Intent(this, WhiteListSpeedAddActivity.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            mPresenter.scanData();
            mAdapter.clear();
            mAdapter.modifyList(mPresenter.getData());
            setEmptyView();
        }
    }

    @Override
    public void onCheck(String id) {
        List<AppInfoBean> appInfoBeanList = mAdapter.getLists();
        List<AppInfoBean> appselcts = new ArrayList<>();
        for (AppInfoBean appInfoBean : appInfoBeanList) {
            if (id.equals(appInfoBean.packageName)) {
                appselcts.add(appInfoBean);
                appInfoBeanList.remove(appInfoBean);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
        mPresenter.updateCache(appselcts);
        setEmptyView();
    }

    private void setEmptyView() {
        if (mAdapter.getLists().size() > 0) {
            mLLEmptyView.setVisibility(View.GONE);
            mLLHead.setVisibility(View.VISIBLE);
        } else {
            mLLEmptyView.setVisibility(View.VISIBLE);
            mLLHead.setVisibility(View.GONE);

        }
    }
}
