package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListSpeedAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListSpeedPresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;

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
    private String mType;

    @BindView(R.id.tv_title_name)
    TextView mTvSubTitle;
    @BindView(R.id.tv_top_title)
    TextView mTvTopTitle;
    @BindView(R.id.tv_default)
    TextView tvDefault;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
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

        Intent intent = getIntent();
        if (intent != null){
            mType = intent.getStringExtra("type");
            if ("white_list".equals(mType)){
                //白名单
                mTvTopTitle.setText(getString(R.string.text_speed_white_list));
                mTvSubTitle.setText(getString(R.string.txt_white_list_speed_title));
                tvDefault.setText("加速白名单为空～");
            }else {
                //软件管理白名单
                mTvTopTitle.setText("软件管理白名单");
                mTvSubTitle.setVisibility(View.GONE);
                tvDefault.setText("软件白名单为空～");
            }
            //扫描已安装包的信息
            mPresenter.scanData(mType);
        }

        mAdapter = new WhiteListSpeedAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnCheckListener(this);
        setEmptyView();
    }




    @OnClick({R.id.img_back, R.id.ll_add})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.ll_add) {
            if(AndroidUtil.isFastDoubleClick())
                return;
            Intent intent = new Intent(this, WhiteListSpeedAddActivity.class);
            intent.putExtra("type",mType);
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            mPresenter.scanData(mType);
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
        mPresenter.updateCache(appselcts,mType);
        setEmptyView();
    }

    private void setEmptyView() {
        if (mLLEmptyView == null) return;
        if (mAdapter.getLists().size() > 0) {
            mLLEmptyView.setVisibility(View.GONE);
            mLLHead.setVisibility(View.VISIBLE);
        } else {
            mLLEmptyView.setVisibility(View.VISIBLE);
            mLLHead.setVisibility(View.GONE);
        }
    }

    public void refData(){
        mAdapter.clear();
        mAdapter.modifyList(mPresenter.getData());
        setEmptyView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
