package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanMusicManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.VideoPlayFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMusicFilePresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.io.File;
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
    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;

    @BindView(R.id.ll_empty_view)
    LinearLayout mLLEmptyView;

    private CleanFileLoadingDialogFragment mLoading;

    private CleanMusicManageAdapter mAdapter;

    /**
     * 列表选项的 checkbox关联全选，如果是选择关联的路径 is ture ,else false;  如果为true 不做重复操作
     */
    private boolean mIsCheckAll;

    //loading是否为首次弹窗
    private boolean isShowFirst = true;

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
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("music_cleaning_page_view_page", "音乐清理浏览");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("music_cleaning_page_view_page", "音乐清理浏览");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_file_manage;
    }

    @Override
    protected void initView() {
        mLoading = CleanFileLoadingDialogFragment.newInstance();
        mAdapter = new CleanMusicManageAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnCheckListener(this);

        mLLCheckAll.setOnClickListener(v -> {
            if (mIsCheckAll) {
                mIsCheckAll = false;
            } else {
                mIsCheckAll = true;
            }
            StatisticsUtils.trackClick("music_cleaning_all_election_click", "\"全选\"按钮点击", "file_cleaning_page", "music_cleaning_page");
            mCheckBoxAll.setSelected(mIsCheckAll);
            checkAll(mIsCheckAll);
            totalSelectFiles();
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

    /**
     * 设置空视图
     */
    private void setEmptyView(int size) {
        if (null == mLLEmptyView) {
            mLLEmptyView = findViewById(R.id.ll_empty_view);
        }
        if (size > 0) {
            if (null != mLLEmptyView) {
                mLLEmptyView.setVisibility(View.GONE);
            }
        } else {
            if (null != mLLEmptyView) {
                mLLEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }


    @OnClick({R.id.img_back
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            StatisticsUtils.trackClick("music_cleaning_back_click", "\"音乐清理\"返回按钮点击", "file_cleaning_page", "music_cleaning_page");

            finish();
        } else if (id == R.id.btn_del) { //删除文件
            StatisticsUtils.trackClick("music_cleaning_delete_click", "\"删除\"按钮点击", "file_cleaning_page", "music_cleaning_page");

            String title = String.format("确定删除这%s个音乐？", getSelectSize());
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
                    if (!isShowFirst) {
                        mLoading.setReportSuccess(0, "");
                    }
                    mLoading.show(getSupportFragmentManager(), "");

                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("music_cleaning_back_click", "\"音乐清理\"返回按钮点击", "file_cleaning_page", "music_cleaning_page");
        super.onBackPressed();
    }

    public void updateDelFileView(List<MusciInfoBean> appInfoBeans) {
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

        setEmptyView(listsNew.size());
        //更新缓存
        mPresenter.updateRemoveCache(appInfoBeans);


        mLoading.setReportSuccess(1, "成功删除" + FileSizeUtils.formatFileSize(totalSize));
        mBtnDel.postDelayed(() -> mLoading.dismissAllowingStateLoss(), 1500);

        totalSelectFiles();

    }


    /**
     * 获取选中的大小
     *
     * @return
     */
    private int getSelectSize() {
        int size = 0;
        List<MusciInfoBean> lists = mAdapter.getLists();
        for (MusciInfoBean musciInfoBean : lists) {
            if (musciInfoBean.isSelect) {
                size++;
            }
        }
        return size;
    }

    public void updateData(List<MusciInfoBean> musciInfoBeans) {
        mAdapter.clear();
        mAdapter.modifyList(musciInfoBeans);

        setEmptyView(musciInfoBeans.size());

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

        mIsCheckAll = isCheckAll;
        mCheckBoxAll.setSelected(mIsCheckAll);

        totalSelectFiles();

    }

    @Override
    public void play(MusciInfoBean musciInfoBean) {
        VideoPlayFragment videoPlayFragment = VideoPlayFragment.newInstance(musciInfoBean.name, FileSizeUtils.formatFileSize(musciInfoBean.packageSize)
                , "时长: " + MusicFileUtils.getPlayDuration2(musciInfoBean.path), "未知");
        videoPlayFragment.show(getFragmentManager(), "");
        videoPlayFragment.setDialogClickListener(new VideoPlayFragment.DialogClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                playAudio(musciInfoBean.path);
            }
        });
    }


    public void playAudio(String audioPath) {
        File file = new File(audioPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        AndroidUtil.fileUri(this, intent, file, "audio/mp3");
        startActivity(intent);

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
        if (totalSize > 0) {
            mBtnDel.setEnabled(true);
        } else {
            mBtnDel.setEnabled(false);
        }
    }

}
