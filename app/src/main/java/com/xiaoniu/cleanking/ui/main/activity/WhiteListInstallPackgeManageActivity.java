package com.xiaoniu.cleanking.ui.main.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListInstallPackageAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListSpeedAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListInstallPManagePresenter;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListIntallPackagePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 安装包白名单管理
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListInstallPackgeManageActivity extends BaseActivity<WhiteListIntallPackagePresenter> implements WhiteListInstallPackageAdapter.OnCheckListener {


    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    private WhiteListInstallPackageAdapter mAdapter;
    @BindView(R.id.ll_install_empty_view)
    LinearLayout mLLEmptyView;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_install_package;
    }

    @Override
    protected void initView() {
        mAdapter = new WhiteListInstallPackageAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.modifyList(mPresenter.getData());
        mAdapter.setOnCheckListener(this);

        setStatusEmptyView();


    }

    @OnClick({R.id.img_back})
    public void onViewClick(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }
    }

    @Override
    public void onCheck(String id) {
        List<AppInfoBean> appInfoBeanList = mAdapter.getLists();
        List<AppInfoBean> appselcts = new ArrayList<>();
        for (AppInfoBean appInfoBean : appInfoBeanList) {
            if (id.equals(appInfoBean.name)) {
                appselcts.add(appInfoBean);
                appInfoBeanList.remove(appInfoBean);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
        mPresenter.updateCache(appselcts);
        setStatusEmptyView();
    }

    private  void setStatusEmptyView(){
        if (mAdapter.getLists().size() > 0 && null != mLLEmptyView) {
            mLLEmptyView.setVisibility(View.GONE);
        }else if(null!=mLLEmptyView){
            mLLEmptyView.setVisibility(View.VISIBLE);
        }
    }
}
