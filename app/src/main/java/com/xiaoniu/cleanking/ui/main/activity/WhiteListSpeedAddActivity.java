package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WhiteListAddAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListSpeedAddPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 加速白名单添加
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedAddActivity extends BaseActivity<WhiteListSpeedAddPresenter> implements WhiteListAddAdapter.OnCheckListener {

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    private WhiteListAddAdapter mAdapter;
    @BindView(R.id.btn_add)
    Button mBtnAdd;
    @BindView(R.id.check_all)
    ImageButton mCheckBoxAll;
    @BindView(R.id.ll_check_all)
    LinearLayout mCheckAll;
    @BindView(R.id.tv_sub_title)
    TextView mTvSubTitle;
    @BindView(R.id.tv_add_title)
    TextView mTvAddTitle;

    private String mType;
    /**
     *列表选项的 checkbox关联全选，如果是选择关联的路径 is ture ,else false;  如果为true 不做重复操作
     */
    private  boolean mIsCheckAll;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_speed_add;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null){
            mType = intent.getStringExtra("type");
            if ("white_list".equals(mType)){
                //白名单
                mTvAddTitle.setText(getString(R.string.text_add_speed_white_list));
                mTvSubTitle.setText(getString(R.string.txt_white_list_speed_title));
            }else {
                //软件管理白名单
                mTvAddTitle.setText(getString(R.string.text_add_soft_white_list));
                mTvSubTitle.setVisibility(View.GONE);
            }
        //扫描已安装包的信息
        mPresenter.scanData(mType);
        }
        mAdapter = new WhiteListAddAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.modifyList(mPresenter.getData());
        mAdapter.setOnCheckListener(this);
        mCheckAll.setOnClickListener(v -> {
            if(mIsCheckAll){
                mIsCheckAll=false;
            }else {
                mIsCheckAll=true;
            }
            mCheckBoxAll.setSelected(mIsCheckAll);
            checkAll(mIsCheckAll);
            totalSelectFiles();
        });

    }


    /**
     * 选择所有
     * @param isCheck
     */
    private void checkAll(boolean isCheck) {
        List<AppInfoBean> lists = mAdapter.getLists();

        for (AppInfoBean appInfoBean : lists) {
            appInfoBean.isSelect = isCheck;
        }
        mAdapter.notifyDataSetChanged();

    }
    @OnClick({R.id.img_back,R.id.btn_add})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }else if(ids==R.id.btn_add){

            //从列表中中出所有选中的APP
            List<AppInfoBean> lists=mAdapter.getLists();
            List<AppInfoBean> appInfoSelect=new ArrayList<>();
            for(AppInfoBean appInfoBean: lists){
                if(appInfoBean.isSelect){
                    appInfoSelect.add(appInfoBean);
                }
            }
            mPresenter.addWhiteList(appInfoSelect,mType);
            Intent intent=new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onCheck(String id, boolean isChecked) {
        List<AppInfoBean> lists = mAdapter.getLists();

        boolean isCheckAll = true;
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.packageName.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }

        mAdapter.notifyDataSetChanged();
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {

            } else {
                isCheckAll = false;
            }
        }
        totalSelectFiles();
        mIsCheckAll=isCheckAll;
        mCheckBoxAll.setSelected(mIsCheckAll);
    }

    /**
     * 统计文件的大小
     */
    private  int totalSize;
    private  void totalSelectFiles(){
        totalSize=0;
        List<AppInfoBean> lists = mAdapter.getLists();
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize++;
            }
        }
        if (totalSize > 0) {
            mBtnAdd.setText(String.format("添加(%s)",String.valueOf(totalSize)));
            mBtnAdd.setSelected(true);
            mBtnAdd.setClickable(true);
        } else {
            mBtnAdd.setText("添加");
            mBtnAdd.setSelected(false);
            mBtnAdd.setClickable(false);
        }
    }
}
