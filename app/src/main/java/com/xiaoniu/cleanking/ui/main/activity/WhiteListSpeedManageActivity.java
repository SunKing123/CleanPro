package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanMusicManageAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListSpeedAdapter;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListSpeedPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 加速白名单管理
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedManageActivity extends BaseActivity<WhiteListSpeedPresenter> {


    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

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
    }

    @OnClick({R.id.img_back, R.id.ll_add})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.ll_add) {
                startActivity(new Intent(this,WhiteListSpeedAddActivity.class));
        }
    }
}
