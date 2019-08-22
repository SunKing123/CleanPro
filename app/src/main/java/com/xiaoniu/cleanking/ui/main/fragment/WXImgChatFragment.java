package com.xiaoniu.cleanking.ui.main.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CommonLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelFileSuccessFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.FileCopyProgressDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MFullDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanImgPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信聊天图片清理
 * Created by lang.chen on 2019/8/1
 * <p>
 * 图片分类
 */
public class WXImgChatFragment extends BaseFragment<WXCleanImgPresenter> {


    private  static  final  int REQUEST_CODE_IMG_VIEW=0x1021;
    @BindView(R.id.list_view)
    ExpandableListView mListView;
    private WXImgChatAdapter mAdapter;

    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    @BindView(R.id.ll_empty_view)
    LinearLayout mEmptyView;

    private boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;
    private FileCopyProgressDialogFragment mProgress;
    private CommonLoadingDialogFragment mLoadingProgress;
    private  int mGroupPosition;

    private  MyHandler mMyHandler;

    public static WXImgChatFragment newInstance() {
        WXImgChatFragment wxImgChatFragment = new WXImgChatFragment();

        return wxImgChatFragment;
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
        mPresenter.init(getContext());
        mPresenter.start();
    }

    @Override
    public void netError() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wx_img_chat;
    }



    private boolean mIsLoading;


    private void scollPage(int groupPosition) {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;

        Log.i("test","scollPage()");
        List<FileTitleEntity> adaterLists = mAdapter.getList();
        List<FileChildEntity> fileChildEntities = mPresenter.listsChat.get(groupPosition).lists;
        int startSize = mAdapter.getList().get(groupPosition).lists.size();
        if (startSize < fileChildEntities.size()) {

            for (int i = startSize; i < fileChildEntities.size(); i++) {
                if (i <= startSize + 29) {
                    adaterLists.get(groupPosition).lists.add(fileChildEntities.get(i));
                } else {
                    break;
                }
            }

            mMyHandler.postDelayed(() -> {
                int itemCount = adaterLists.get(groupPosition).lists.size();
                mAdapter.getWXImgAdapter().notifyItemRangeChanged(startSize+1, itemCount,"");
                mIsLoading = false;
            }, 0);
        }
    }



    private  int mOfferY=0;
    @Override
    protected void initView() {

        mMyHandler=new MyHandler();

        mLoadingProgress=CommonLoadingDialogFragment.newInstance();
        mLoadingProgress.show(getFragmentManager(),"");

        mAdapter = new WXImgChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mLoading = CleanFileLoadingDialogFragment.newInstance();
        mProgress = FileCopyProgressDialogFragment.newInstance();

        mListView.setOnGroupCollapseListener(groupPosition -> {
            List<FileTitleEntity> lists = mAdapter.getList();
            boolean isExpand = false;

            for (int i = 0; i < lists.size(); i++) {
                if (i == groupPosition) {
                    FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                    if (fileTitleEntity.isExpand) {
                        fileTitleEntity.isExpand = false;
                    } else {
                        fileTitleEntity.isExpand = true;
                    }
                    isExpand = fileTitleEntity.isExpand;
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();

        });

        mListView.setOnGroupExpandListener(groupPosition -> {
            List<FileTitleEntity> lists = mAdapter.getList();


            boolean isExpand = false;
            for (int i = 0; i < lists.size(); i++) {
                if (i == groupPosition) {
                    FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                    if (fileTitleEntity.isExpand) {
                        fileTitleEntity.isExpand = false;
                    } else {
                        fileTitleEntity.isExpand = true;
                    }
                    isExpand = fileTitleEntity.isExpand;
                }else {
                    mListView.collapseGroup(i);
                }
            }
            mAdapter.notifyDataSetChanged();

            mListView.setSelectedGroup(groupPosition);
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if((scrollState== SCROLL_STATE_IDLE || scrollState==SCROLL_STATE_FLING) && mOfferY>0){
                        scollPage(groupPosition);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if(firstVisibleItem+visibleItemCount==totalItemCount){
                        mOfferY=1;
                    }else {
                        mOfferY=-1;
                    }

                }
            });


        });



        mLLCheckAll.setOnClickListener(v -> {
            if (mIsCheckAll) {
                mIsCheckAll = false;
            } else {
                mIsCheckAll = true;
            }
            mLLCheckAll.setSelected(mIsCheckAll);
            setSelectStatus(mIsCheckAll);
            setDelBtnSize();

            StatisticsUtils.trackClick("picture_cleaning_all_election_click","\"全选\"按钮点击"
                    ,"wechat_cleaning_page","wechat_picture_cleaning_page");

        });



        mAdapter.setOnCheckListener(new WXImgChatAdapter.OnCheckListener() {
            @Override
            public void onCheck(int groupPosition, int position, boolean isCheck) {


                setSelectChildStatus(groupPosition);
                setDelBtnSize();

            }

            @Override
            public void onCheckAll(int groupPosition, int position, boolean isCheck) {
                //更新副本
                List<FileTitleEntity>  lists= mPresenter.listsChat;
                for(int i=0;i<lists.size();i++){
                    List<FileChildEntity> files= lists.get(groupPosition).lists;
                    for(FileChildEntity fileChildEntity:files){
                        fileChildEntity.isSelect=isCheck;
                    }

                }
                setSelectChildStatus(groupPosition);
                setDelBtnSize();
            }

            @Override
            public void onCheckImg(int groupPosition, int position) {
                mGroupPosition=groupPosition;
                Intent intent = new Intent(mActivity, PreviewImageActivity.class);
                intent.putExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, position);
                CleanAllFileScanUtil.clean_image_list = wrapperImg(groupPosition,position);
                startActivityForResult(intent, REQUEST_CODE_IMG_VIEW);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_IMG_VIEW) {
                List<FileEntity> listTemp = new ArrayList<>();
                listTemp.addAll(CleanAllFileScanUtil.clean_image_list);
                refreshData(listTemp);
            }

    }


    /**
     * 查看图片后刷新数据
     * @param fileEntities
     */
    private  void  refreshData(List<FileEntity> fileEntities){

            List<FileTitleEntity> lists=mAdapter.getList();

            List<FileChildEntity> listsNew=new ArrayList<>();
            if(lists.size()>0){
              List<FileChildEntity> fileChildEntities=  lists.get(mGroupPosition).lists;

              for(FileChildEntity fileChildEntity:fileChildEntities){

                  boolean isAdd=false;
                  for(FileEntity fileEntity:fileEntities){
                      if(fileEntity.path.equals(fileChildEntity.path)){
                          fileChildEntity.isSelect=fileEntity.isSelect;
                          isAdd=true;
                      }
                  }
                  if(isAdd){
                      listsNew.add(fileChildEntity);
                  }
              }
                fileChildEntities.clear();;

                fileChildEntities.addAll(listsNew);
                mPresenter.totalFileSize(lists);
                mMyHandler.sendEmptyMessage(1);
                setSelectChildStatus(mGroupPosition);
                setDelBtnSize();


            }
    }

    /**
     * 照片查看选择集合包装
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    private List<FileEntity> wrapperImg(int groupPosition, int childPosition) {
        List<FileEntity> wrapperLists = new ArrayList<>();

        List<FileTitleEntity> lists = mAdapter.getList();
        if(lists.size()>0){
            List<FileChildEntity> listChilds=lists.get(groupPosition).lists;
            for(FileChildEntity fileChildEntity: listChilds){
                FileEntity fileEntity=new FileEntity(String.valueOf(fileChildEntity.size),fileChildEntity.path);
                fileEntity.isSelect=fileChildEntity.isSelect;
                wrapperLists.add(fileEntity);
            }
        }
        return wrapperLists;
    }



    /**
     * 改变子视图的选择状态
     */
    private void setSelectChildStatus(int groupPosition) {
        List<FileTitleEntity> lists = mAdapter.getList();

        for (int i = 0; i < lists.size(); i++) {
            if (i == groupPosition) {
                //是否选择所有
                boolean isCheckAll = true;
                FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                if(fileTitleEntity.lists.size()==0){
                    fileTitleEntity.isSelect = false;
                    mMyHandler.sendEmptyMessage(1);
                    break;
                } else {
                    for (FileChildEntity file : fileTitleEntity.lists) {
                        if (file.isSelect == false) {
                            isCheckAll = false;
                        }
                    }

                    fileTitleEntity.isSelect = isCheckAll;
                    mMyHandler.sendEmptyMessage(1);
                    break;

                }
            }
        }


    }

    /**
     * 改变父类文件选择状态
     */
    private void setSelectChildStatus() {
        List<FileTitleEntity> lists = mAdapter.getList();

        for (int i = 0; i < lists.size(); i++) {
            //是否选择所有
            boolean isCheckAll = true;
            FileTitleEntity fileTitleEntity = lists.get(i);
            if (fileTitleEntity.lists.size() == 0) {
                fileTitleEntity.isSelect = false;
            } else {
                for (FileChildEntity file : fileTitleEntity.lists) {
                    if (file.isSelect == false) {
                        isCheckAll = false;
                    }
                }

                fileTitleEntity.isSelect = isCheckAll;
            }

        }
        mMyHandler.sendEmptyMessage(1);

    }


    private void setSelectStatus(boolean isCheck) {
        List<FileTitleEntity> lists = mAdapter.getList();
        List<FileTitleEntity> listsLocal=mPresenter.listsChat;
        for (FileTitleEntity fileTitleEntity : lists) {
            if (fileTitleEntity.lists.size() > 0) {
                fileTitleEntity.isSelect = isCheck;

                for (FileChildEntity file : fileTitleEntity.lists) {
                    file.isSelect = isCheck;
                }
            }

        }

        for (FileTitleEntity fileTitleEntity : listsLocal) {
            if (fileTitleEntity.lists.size() > 0) {
                fileTitleEntity.isSelect = isCheck;

                for (FileChildEntity file : fileTitleEntity.lists) {
                    file.isSelect = isCheck;
                }
            }

        }
        mMyHandler.sendEmptyMessage(1);
    }

    private long totalSelectSize() {
        long size = 0L;
        //List<FileTitleEntity> lists = mAdapter.getList();

        List<FileTitleEntity> lists=mPresenter.listsChat;

        for (FileTitleEntity fileTitleEntity : lists) {
            for (FileChildEntity file : fileTitleEntity.lists) {
                if (file.isSelect) {
                    size += file.size;
                }
            }
        }
        return size;
    }


    private void setDelBtnSize() {
        long size = totalSelectSize();
        if (size > 0) {
            mBtnDel.setSelected(true);
            mBtnDel.setEnabled(true);
            mBtnDel.setText("删除" + FileSizeUtils.formatFileSize(size));
        } else {
            mBtnDel.setSelected(false);
            mBtnDel.setEnabled(false);
            mBtnDel.setText("删除");
        }

    }

    /**
     * 删除本地
     * @param paths
     */
    public void updateDeLocal(List<FileChildEntity> paths){
        List<FileTitleEntity> lists=mPresenter.listsChat;
        for(FileChildEntity file: paths){

            for(FileTitleEntity fileTitleEntity:lists){
                for(int i=0;i<fileTitleEntity.lists.size();i++){
                      FileChildEntity fileChildEntity=fileTitleEntity.lists.get(i);
                      if(file.path.equals(fileChildEntity.path)){
                          fileTitleEntity.lists.remove(fileChildEntity);
                      }
                }
            }

        }
    }
    /**
     * 移除列表中元素
     *
     * @param paths 根据路径和父类id是否相等
     */
    public void updateDelFileView(List<FileChildEntity> paths) {
        //mPresenter.listsChat.removeAll(paths);
        updateDeLocal(paths);
        List<FileTitleEntity> listsNew = new ArrayList<>();

        List<FileTitleEntity> lists = mAdapter.getList();
        for (int i = 0; i < lists.size(); i++) {
            FileTitleEntity fileTitleEntity = lists.get(i);

            FileTitleEntity fileTitle = FileTitleEntity.copyObject(fileTitleEntity.id, fileTitleEntity.title
                    , fileTitleEntity.type, fileTitleEntity.size, fileTitleEntity.isExpand, fileTitleEntity.isSelect);
            for (FileChildEntity fileChildEntity : fileTitleEntity.lists) {
                if (fileChildEntity.isSelect == false) {
                    fileTitle.lists.add(fileChildEntity);
                }
            }
            if(fileTitleEntity.size !=0) {
                listsNew.add(fileTitle);
            }
        }
        mLoading.dismissAllowingStateLoss();
        mPresenter.totalFileSize(listsNew);
        mAdapter.clear();
        mAdapter.modifyData(listsNew);
        setDelBtnSize();
        setSelectChildStatus();
        if(totalFileSizeL(mAdapter.getList())==0){
            mEmptyView.setVisibility(View.VISIBLE);
        }
        FragmentManager fm = getActivity().getFragmentManager();
        long delSize=getDelTotalFileSize(paths);
        String totalSize=FileSizeUtils.formatFileSize(delSize);
        String fileSize=String.valueOf(paths.size());
        DelFileSuccessFragment.newInstance(totalSize,fileSize).show(fm,"");

        //更新缓存
        SharedPreferences sharedPreferences =getContext().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long totalSizeCache=sharedPreferences.getLong(Constant.WX_CACHE_SIZE_IMG,0l);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong(Constant.WX_CACHE_SIZE_IMG,(totalSizeCache-delSize));
        editor.commit();




    }

    public long getDelTotalFileSize(List<FileChildEntity> paths){
        long size=0L;
        for(FileChildEntity fileChildEntity:paths){
                size+=fileChildEntity.size;

        }
        return  size;
    }
    /**
     * 获取选中删除的元素
     *
     * @return
     */
    public List<FileChildEntity> getDelFile() {
        List<FileChildEntity> files = new ArrayList<>();
        List<FileTitleEntity> lists = mAdapter.getList();
        for (FileTitleEntity fileTitleEntity : lists) {
            for (FileChildEntity file : fileTitleEntity.lists) {
                if (file.isSelect) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    @OnClick({R.id.btn_del, R.id.btn_save})
    public void onClickView(View view) {
        int ids = view.getId();
        switch (ids) {
            case R.id.btn_del:
                StatisticsUtils.trackClick("picture_cleaning_delete_click","\"删除\"按钮点击"
                        ,"wechat_cleaning_page","wechat_picture_cleaning_page");

                String title = String.format("确定删除这%s个图片?", getSelectSize());
                DelDialogStyleFragment dialogFragment = DelDialogStyleFragment.newInstance(title);
                FragmentManager fm = getActivity().getFragmentManager();
                dialogFragment.show(fm, "");
                dialogFragment.setDialogClickListener(new DelDialogStyleFragment.DialogClickListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                        mLoading.show(getActivity().getSupportFragmentManager(), "");
                        List<FileChildEntity> files = getDelFile();
                        mPresenter.delFile(files);
                    }
                });

                break;
            //保存到手机
            case R.id.btn_save:
                StatisticsUtils.trackClick("Save_to_cell_phone_click","\"保存到手机\"点击"
                        ,"wechat_cleaning_page","wechat_picture_cleaning_page");

                List<File> lists = getSelectFiles();
                if (lists.size() == 0) {
                    ToastUtils.showShort("未选中照片");
                } else {
                    FragmentManager fmProgress = getActivity().getFragmentManager();
                    mProgress.show(fmProgress, "");
                    //导入图片
                    mPresenter.copyFile(lists);
                }

                break;
        }
    }


    /**
     * 导入成功
     *
     * @param progress
     */
    public void copySuccess(int progress) {

        mProgress.setValue(progress);
        if (progress >= 100) {
            ToastUtils.showShort("保存成功，请至手机相册查看");
            mProgress.dismissAllowingStateLoss();
        }
    }


    /**
     * 导出失败
     */
    public void onCopyFaile(){
        if(null!=mProgress){
            mProgress.dismissAllowingStateLoss();
        }
        FragmentManager fm = getActivity().getFragmentManager();
        MFullDialogStyleFragment.newInstance().show(fm,"");
    }



    private int getSelectSize() {
        int size = 0;
        //List<FileTitleEntity> lists = mAdapter.getList();

        List<FileTitleEntity> lists =mPresenter.listsChat;

        for (FileTitleEntity fileTitleEntity : lists) {
            for (FileChildEntity file : fileTitleEntity.lists) {
                if (file.isSelect) {
                    size += 1;
                }
            }
        }
        return size;
    }

    private List<File> getSelectFiles() {
        List<File> files = new ArrayList<>();
        //List<FileTitleEntity> lists = mAdapter.getList();
        List<FileTitleEntity> lists =mPresenter.listsChat;
        for (FileTitleEntity fileTitleEntity : lists) {
            for (FileChildEntity fileChildEntity : fileTitleEntity.lists) {
                if (fileChildEntity.isSelect) {
                    File File = new File(fileChildEntity.path);
                    files.add(File);
                }
            }
        }
        return files;
    }


    /**
     * 更新微信聊天图片
     *
     * @param lists
     */
    public void updateImgChat(List<FileTitleEntity> lists) {
        if (mListView == null || lists == null) return;
        //cancelLoadingDialog();
        if(null!=mLoadingProgress){
            mLoadingProgress.dismissAllowingStateLoss();
        }
        List<FileTitleEntity> fileCopyEntitys=new ArrayList<>();
        for(int i=0;i<lists.size();i++){
            FileTitleEntity fileParent=lists.get(i);
            FileTitleEntity fileTitleEntity=FileTitleEntity.copyObject(fileParent.id,fileParent.title,fileParent.type
                    ,fileParent.size,fileParent.isExpand,fileParent.isSelect);

            List<FileChildEntity> listsNew=new ArrayList<>();
            int count=0;
            if(fileParent.lists.size()>30){
                count=30;
            }else {
                count=fileParent.lists.size();
            }
            for(int j=0;j<count;j++){
                FileChildEntity childEntity=fileParent.lists.get(j);
                listsNew.add(childEntity);
            }
            fileTitleEntity.lists.addAll(listsNew);
            fileCopyEntitys.add(fileTitleEntity);
        }
        mAdapter.modifyData(fileCopyEntitys);

        if (fileCopyEntitys.size() > 0) {
            //默认展开最后一条
            mListView.expandGroup(fileCopyEntitys.size()-1);
            mListView.setSelectedGroup(0);
        }

        if(totalFileSizeL(lists)==0){
            mEmptyView.setVisibility(View.VISIBLE);
        }

        SharedPreferences sharedPreferences =getContext().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        long totalSize=sharedPreferences.getLong(Constant.WX_CACHE_SIZE_IMG,totalFileSize(lists));
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong(Constant.WX_CACHE_SIZE_IMG,(totalSize+totalFileSize(lists)));
        editor.commit();


    }


    public   long totalFileSizeL(List<FileTitleEntity> lists){
        if(null==lists ||  lists.size()==0){
            return 0L;
        }

        long size=0L;

        for(FileTitleEntity fileTitleEntity: lists) {
            size+=fileTitleEntity.size;

        }

        return  size;
    }

    public   long totalFileSize(List<FileTitleEntity> lists){
        if(null==lists ||  lists.size()==0){
            return 0L;
        }

        long size=0L;

        for(FileTitleEntity fileTitleEntity: lists) {
            size+=fileTitleEntity.size;

        }

        return  size;
    }


    private  class  MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case 1:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    /**
     * 更新系统相册
     * @param file
     */
    public void updateDIM(File file){
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
