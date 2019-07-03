package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Environment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanVideoManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.CleanVideoManagePresenter;
import com.xiaoniu.cleanking.ui.main.widget.GrideManagerWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 清理视频文件
 * Created by lang.chen on 2019/7/3
 */
public class CleanVideoManageActivity extends BaseActivity<CleanVideoManagePresenter> implements CleanVideoManageAdapter.OnCheckListener {


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_del)
    Button mBtnDel;
    @BindView(R.id.check_all)
    AppCompatCheckBox mCheckBoxAll;
    @BindView(R.id.ll_empty_view)
    LinearLayout mLLEmptyView;

    private CleanVideoManageAdapter mAdapter;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        String path = Environment.getExternalStorageDirectory().getPath();
        mPresenter.getFlieList(path);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clean_video_manage;
    }

    @Override
    protected void initView() {

        mAdapter = new CleanVideoManageAdapter(this.getBaseContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                VideoInfoBean videoInfoBean = mAdapter.getLists().get(position);
                if (videoInfoBean.itemType == 0) {
                    return 3;
                }
                return 1;
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GrideManagerWrapper(DensityUtil.dp2px(10)));
        mAdapter.setOnCheckListener(this);

        mCheckBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @OnClick({R.id.img_back
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            finish();
        }
    }

    public void updateData(List<VideoInfoBean> infoBeans) {

        if(infoBeans.size()>0){
            mLLEmptyView.setVisibility(View.GONE);
        }else {
            mLLEmptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.clear();
        mAdapter.modifyList(infoBeans);
        mAdapter.notifyDataSetChanged();
    }

    public void updateDelFileView(List<VideoInfoBean> infoBeans) {

    }

    @Override
    public void onCheck(String id, boolean isChecked) {

    }
}
