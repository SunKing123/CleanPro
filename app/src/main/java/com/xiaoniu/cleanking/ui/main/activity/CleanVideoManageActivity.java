package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.imagepicker.util.Utils;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanVideoManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.VideoPlayFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanVideoManagePresenter;
import com.xiaoniu.cleanking.ui.main.widget.GrideManagerWrapper;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    ImageButton mCheckBoxAll;
    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.ll_video_empty_view)
    LinearLayout mLLEmptyView;

    private CleanVideoManageAdapter mAdapter;
    private  boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;

    //loading是否为首次弹窗
    private  boolean isShowFirst=true;
    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
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
        String path = Environment.getExternalStorageDirectory().getPath();
        mPresenter.getFlieList(path);
        mLoading=CleanFileLoadingDialogFragment.newInstance();

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
        mRecyclerView.addItemDecoration(new GrideManagerWrapper(Utils.dp2px(mContext,8)));
        mAdapter.setOnCheckListener(this);

        mLLCheckAll.setOnClickListener(v -> {
            if(mIsCheckAll){
                mIsCheckAll=false;
            }else {
                mIsCheckAll=true;
                StatisticsUtils.trackClick("all_election_click","\"全选\"点击","file_cleaning_page","video_cleaning_page");

            }
            mCheckBoxAll.setSelected(mIsCheckAll);
            checkAll(mIsCheckAll);
            totalSelectFiles();
        });


    }




    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("video_cleaning_view_page","视频清理浏览");
    }


    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("video_cleaning_view_page","视频清理浏览");
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

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("video_cleaning_back_click","\"视频清理\"返回按钮点击","file_cleaning_page","video_cleaning_page");
        super.onBackPressed();
    }

    @OnClick({R.id.img_back
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            StatisticsUtils.trackClick("video_cleaning_back_click","\"视频清理\"返回按钮点击","file_cleaning_page","video_cleaning_page");

            finish();
        } else if (id == R.id.btn_del) { //删除文件
            StatisticsUtils.trackClick("clean_click","\"清理\"点击","file_cleaning_page","video_cleaning_page");

            String title=String.format("确定删除这%s个视频?",getSelectSize());
            DelDialogFragment dialogFragment = DelDialogFragment.newInstance(title);
            dialogFragment.show(getFragmentManager(), "");
            dialogFragment.setDialogClickListener(new DelDialogFragment.DialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    if(!isShowFirst){
                        mLoading.setReportSuccess(0,"");
                    }
                    mLoading.show(getSupportFragmentManager(),"");
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

    /**
     * 获取选中的大小
     */
    public int getSelectSize(){
        int size=0;
        List<VideoInfoBean> lists=mAdapter.getLists();
        for(VideoInfoBean videoInfoBean: lists){
            if(videoInfoBean.isSelect){
                size++;
            }
        }
        return  size;
    }
    public void updateData(List<VideoInfoBean> infoBeans) {
        setEmptyView(infoBeans.size());
        mAdapter.clear();
        mAdapter.modifyList(infoBeans);
        mAdapter.notifyDataSetChanged();
    }

    public void updateDelFileView(List<VideoInfoBean> appInfoBeans) {

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

        //删除标题日期去重，找出相对于的日期子集，如果不存在子集删除标题
        Set<String> tempDate=new HashSet<>();
        for(VideoInfoBean bean:appInfoBeans){
            tempDate.add(bean.date);
        }

        for(String date:tempDate){
            boolean isConstain = false;
            VideoInfoBean videoInfoBeanTitle=null;
            for(VideoInfoBean videoInfoBean:listsNew){
                if(videoInfoBean.itemType==1 && date.equals(videoInfoBean.date)){
                    isConstain=true;
                }
                if(videoInfoBean.itemType==0 && date.equals(videoInfoBean.date)){
                    videoInfoBeanTitle=videoInfoBean;
                }

            }

            //不包含子文件日期标题删除
            if(isConstain==false &&null!=videoInfoBeanTitle){
                listsNew.remove(videoInfoBeanTitle);
            }
        }

        mAdapter.clear();
        mAdapter.modifyList(listsNew);
        setEmptyView(listsNew.size());
        //更新缓存
        mPresenter.updateRemoveCache(appInfoBeans);

        mLoading.setReportSuccess(1,"成功删除" + FileSizeUtils.formatFileSize(totalSize));
        mBtnDel.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoading.dismissAllowingStateLoss();
            }
        },1500);
        totalSelectFiles();


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
        if(totalSize>0){
            mBtnDel.setEnabled(true);
        }else {
            mBtnDel.setEnabled(false);
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
        //是否全选
        boolean isCheckAll = true;
        for (VideoInfoBean appInfoBean : lists) {
            if(appInfoBean.itemType==0){
                continue;
            }
            //itemType==1 为图片 0 为 标题
            if (appInfoBean.itemType==1 && appInfoBean.path.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }

        mAdapter.notifyDataSetChanged();
        for (VideoInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            } else if(appInfoBean.isSelect==false && appInfoBean.itemType==1){
                isCheckAll = false;
            }
        }

        mIsCheckAll=isCheckAll;
        mCheckBoxAll.setSelected(mIsCheckAll);
        totalSelectFiles();
    }

    @Override
    public void play(VideoInfoBean appInfoBean) {
        try {
            VideoPlayFragment videoPlayFragment=VideoPlayFragment.newInstance(appInfoBean.name,FileSizeUtils.formatFileSize(appInfoBean.packageSize)
                    ,"时长: "+ MusicFileUtils.getPlayDuration2(appInfoBean.path),"未知");
            videoPlayFragment.show(getFragmentManager(),"");
            videoPlayFragment.setDialogClickListener(new VideoPlayFragment.DialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    playAudio(appInfoBean.path);
                }
            });
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }


    /**
     * 播放视频
     * @param audioPath
     */
    public void playAudio(String audioPath) {
        try {
            File file = new File(audioPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            AndroidUtil.fileUri(this, intent, file, "video/*");
            startActivity(intent);
        } catch (Exception e) {

        }
    }
    /**
     * 设置空视图
     * */
    private void setEmptyView(int size) {
        if(null==mLLEmptyView){
            mLLEmptyView=findViewById(R.id.ll_video_empty_view);
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
}
