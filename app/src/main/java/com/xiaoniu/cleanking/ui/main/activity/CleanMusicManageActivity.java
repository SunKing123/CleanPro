package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Environment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanMusicManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMusicFilePresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 清理音乐文件
 * Created by lang.chen on 2019/7/2
 */
public class CleanMusicManageActivity extends BaseActivity<CleanMusicFilePresenter> implements CleanMusicManageAdapter.OnCheckListener {


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_del)
    Button mBtnDel;
    @BindView(R.id.check_all)
    ImageButton mCheckBoxAll;
    @BindView(R.id.ll_empty_view)
    LinearLayout mLLEmptyView;

    private CleanMusicManageAdapter mAdapter;

    /**
     *列表选项的 checkbox关联全选，如果是选择关联的路径 is ture ,else false;  如果为true 不做重复操作
     */
    private  boolean mIsCheckAll;
    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        String path = Environment.getExternalStorageDirectory().getPath();
        mPresenter.getMusicList(path);
    }


    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_file_manage;
    }

    @Override
    protected void initView() {
        mAdapter = new CleanMusicManageAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnCheckListener(this);

        mCheckBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsCheckAll){
                    mIsCheckAll=false;
                }else {
                    mIsCheckAll=true;
                }
                mCheckBoxAll.setSelected(mIsCheckAll);
                checkAll(mIsCheckAll);
                totalSelectFiles();
            }
        });
    }


    /**
     * 全选
     *
     * @param isCheck
     */
    private void checkAll(boolean isCheck) {
        List<MusciInfoBean> lists = mAdapter.getLists();

        for (MusciInfoBean appInfoBean : lists) {
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
            String title="确定删除此音乐？";
            DelDialogFragment dialogFragment = DelDialogFragment.newInstance(title);
            dialogFragment.show(getFragmentManager(), "");
            dialogFragment.setDialogClickListener(new DelDialogFragment.DialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    List<MusciInfoBean> lists = mAdapter.getLists();
                    //过滤选中的文件删除
                    List<MusciInfoBean> appInfoSelect = new ArrayList<>();
                    for (MusciInfoBean appInfoBean : lists) {
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


    public void updateDelFileView(List<MusciInfoBean> appInfoBeans) {
        totalSelectFiles();
        Toast.makeText(mContext, "成功删除" + FileSizeUtils.formatFileSize(totalSize), Toast.LENGTH_SHORT).show();
        //
        List<MusciInfoBean> listsNew = new ArrayList<>();
        List<MusciInfoBean> lists = mAdapter.getLists();
        //过滤删除的文件
        for (MusciInfoBean appInfoBean : lists) {
            boolean isConstain = false;
            for (MusciInfoBean appInfoBean1 : appInfoBeans) {
                if (appInfoBean.name.equals(appInfoBean1.name)) {
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
            if(null!=mLLEmptyView){
                mLLEmptyView.setVisibility(View.GONE);
            }
        }else {
            if(null!=mLLEmptyView){
                mLLEmptyView.setVisibility(View.VISIBLE);
            }
        }
        //更新缓存
        mPresenter.updateRemoveCache(appInfoBeans);


    }

    public void updateData(List<MusciInfoBean> musciInfoBeans) {
        mAdapter.clear();
        mAdapter.modifyList(musciInfoBeans);

        if(musciInfoBeans.size()>0){
            if(null!=mLLEmptyView){
                mLLEmptyView.setVisibility(View.GONE);
            }
        }else {
            if(null!=mLLEmptyView){
                mLLEmptyView.setVisibility(View.VISIBLE);
            }
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
        List<MusciInfoBean> lists = mAdapter.getLists();
        //文件总大小
        totalSize = 0L;
        boolean isCheckAll = true;
        for (MusciInfoBean appInfoBean : lists) {
            if (appInfoBean.path.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }
        mAdapter.notifyDataSetChanged();
        for (MusciInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            } else {
                isCheckAll = false;
            }
        }

        mIsCheckAll=isCheckAll;
        mCheckBoxAll.setSelected(mIsCheckAll);

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
     * 统计文件的大小
     */
    private void totalSelectFiles() {
        totalSize = 0L;
        List<MusciInfoBean> lists = mAdapter.getLists();
        for (MusciInfoBean appInfoBean : lists) {
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
}
