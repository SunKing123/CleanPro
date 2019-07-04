package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Environment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanVideoManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanVideoManagePresenter;
import com.xiaoniu.cleanking.ui.main.widget.GrideManagerWrapper;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
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
                checkAll(isChecked);
            }
        });
    }

    /**
     * 全选
     *
     * @param isCheck
     */
    private void checkAll(boolean isCheck) {
        List<VideoInfoBean> lists = mAdapter.getLists();

        for (VideoInfoBean appInfoBean : lists) {
            appInfoBean.isSelect = isCheck;
        }
        mAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.img_back
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            finish();
        } else if (id == R.id.btn_del) { //删除文件
            String title="确定删除此视频？";
            DelDialogFragment dialogFragment = DelDialogFragment.newInstance(title);
            dialogFragment.show(getFragmentManager(), "");
            dialogFragment.setDialogClickListener(new DelDialogFragment.DialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    List<VideoInfoBean> lists = mAdapter.getLists();
                    //过滤选中的文件删除
                    List<VideoInfoBean> appInfoSelect = new ArrayList<>();
                    for (VideoInfoBean appInfoBean : lists) {
                        if (appInfoBean.isSelect) {
                            appInfoSelect.add(appInfoBean);
                            //从列表中移除即将要删除文件
                            // lists.remove(appInfoBean);
                        }
                    }
                    mPresenter.delFile(appInfoSelect);

                }
            });
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

    public void updateDelFileView(List<VideoInfoBean> appInfoBeans) {
        totalSelectFiles();
        Toast.makeText(mContext, "成功删除" + FileSizeUtils.formatFileSize(totalSize), Toast.LENGTH_SHORT).show();
        //
        List<VideoInfoBean> listsNew = new ArrayList<>();
        List<VideoInfoBean> lists = mAdapter.getLists();
        //过滤删除的文件
        for (VideoInfoBean appInfoBean : lists) {
            boolean isConstain = false;
            for (VideoInfoBean appInfoBean1 : appInfoBeans) {
                if (appInfoBean1.itemType==1 && appInfoBean.path.equals(appInfoBean1.path)) {
                    isConstain = true;
                }
            }
            //没有包含
            if (isConstain == false) {
                listsNew.add(appInfoBean);
            }
        }
        mAdapter.clear();
        mAdapter.modifyList(listsNew);

        if(listsNew.size()>0){
            mLLEmptyView.setVisibility(View.GONE);
        }else {
            mLLEmptyView.setVisibility(View.VISIBLE);
        }
        //更新缓存
        mPresenter.updateRemoveCache(appInfoBeans);


    }

    /**
     * 统计文件的大小
     */
    private void totalSelectFiles() {
        totalSize = 0L;
        List<VideoInfoBean> lists = mAdapter.getLists();
        for (VideoInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            }
        }
        if (totalSize > 0) {
            mBtnDel.setText("删除" + FileSizeUtils.formatFileSize(totalSize));
            mBtnDel.setSelected(true);
            mBtnDel.setClickable(true);
        } else {
            mBtnDel.setText("删除");
            mBtnDel.setSelected(false);
            mBtnDel.setClickable(false);
        }
    }
    
    /**
     * 每次选择列表选择都去底部大小
     * <p>
     * 1.列表有选择更新底部按钮，并且统计文件大小
     */
    private long totalSize = 0L;
    
    @Override
    public void onCheck(String id, boolean isChecked) {
        List<VideoInfoBean> lists = mAdapter.getLists();
        //文件总大小
        totalSize = 0L;
        boolean isCheckAll = true;
        for (VideoInfoBean appInfoBean : lists) {
            if(appInfoBean.itemType==0){
                continue;
            }
            if (appInfoBean.path.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }

        for (VideoInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            } else {
                isCheckAll = false;
            }
        }

        //mCheckBoxAll.setChecked(isCheckAll);
        if (totalSize > 0) {
            mBtnDel.setText("删除" + FileSizeUtils.formatFileSize(totalSize));
            mBtnDel.setSelected(true);
            mBtnDel.setClickable(true);
        } else {
            mBtnDel.setText("删除");
            mBtnDel.setSelected(false);
            mBtnDel.setClickable(false);
        }

    }
}
