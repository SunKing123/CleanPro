package com.xiaoniu.cleanking.ui.main.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
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
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter2;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelFileSuccessFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.FileCopyProgressDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.MFullDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanImgPresenter;

import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 微信聊天图片清理
 * Created by lang.chen on 2019/8/1
 * <p>
 * 图片分类
 */
public class WXImgChatFragment extends BaseFragment<WXCleanImgPresenter> {


    private  static  final  int REQUEST_CODE_IMG_VIEW=0x1021;
    @BindView(R.id.list_view)
    ExpandableStickyListHeadersListView mListView;
    private WXImgChatAdapter2 mAdapter;

//    @BindView(R.id.scroll_view)
//    ObservableScrollView mScrollView;

    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    private boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;
    private FileCopyProgressDialogFragment mProgress;

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

        List<FileTitleEntity> adaterLists = mAdapter.getList();
        List<FileChildEntity> fileChildEntities = mPresenter.listsChat.get(groupPosition).lists;
        int startSize = mAdapter.getList().get(groupPosition).lists.size();
        if (startSize < fileChildEntities.size()) {

            for (int i = startSize; i < fileChildEntities.size(); i++) {
                if (i <= startSize + 3) {
                    adaterLists.get(groupPosition).lists.add(fileChildEntities.get(i));
                } else {
                    break;
                }
            }

            mMyHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int itemCount = adaterLists.get(groupPosition).lists.size();
                    mAdapter.getWXImgAdapter().notifyItemRangeChanged(startSize+1, itemCount,"");
                    mIsLoading = false;
                }
            }, 0);
        }
    }



    private  int mOfferY=0;
    @Override
    protected void initView() {
        mMyHandler=new MyHandler();
        mAdapter = new WXImgChatAdapter2(getContext());
        mListView.setAdapter(mAdapter);
        mLoading = CleanFileLoadingDialogFragment.newInstance();
        mProgress = FileCopyProgressDialogFragment.newInstance();
//        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                List<FileTitleEntity> lists = mAdapter.getList();
//                boolean isExpand=false;
//
//                for (int i = 0; i < lists.size(); i++) {
//                    if (i == groupPosition) {
//                        FileTitleEntity fileTitleEntity = lists.get(groupPosition);
//                        if (fileTitleEntity.isExpand) {
//                            fileTitleEntity.isExpand = false;
//                        } else {
//                            fileTitleEntity.isExpand = true;
//                        }
//                        isExpand=fileTitleEntity.isExpand;
//                        break;
//                    }
//                }

//                if(isExpand){

        mListView.setOnStickyHeaderOffsetChangedListener(new StickyListHeadersListView.OnStickyHeaderOffsetChangedListener() {
            @Override
            public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
                Log.i("test","offset"+offset);
            }
        });


        mListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if(mListView.isHeaderCollapsed(headerId)){
                    mListView.expand(headerId);
                }else {
                    mListView.collapse(headerId);
                }
                WXImgChatAdapter2.ViewHolderChild viewHolderChild=(WXImgChatAdapter2.ViewHolderChild)mAdapter.mViewChild;

                if(null!=viewHolderChild ){

                }
//                viewHolderChild.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        Log.i("test", "onScrollStateChanged" + mOfferY);
//                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                        // 当不滚动时
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            //获取最后一个完全显示的ItemPosition
//                            int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
//                            int totalItemCount = manager.getItemCount();
//
//                            // 判断是否滚动到底部，并且是向右滚动
//                            if (lastVisibleItem == (totalItemCount - 1) && mOfferY>0) {
//                                //加载更多功能的代码
//                                scollPage(itemPosition);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        mOfferY=dy;
//                        Log.i("test", "y=" + mOfferY);
//                    }
//                });


            }
        });



//                WXImgChatAdapter.ViewHolderChild viewHolderChild=(WXImgChatAdapter.ViewHolderChild)(mAdapter.mViewChild);
//                if(null!=viewHolderChild && null!= viewHolderChild.mRecyclerView){
//                    viewHolderChild.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                        @Override
//                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                            super.onScrolled(recyclerView, dx, dy);
//                            Log.i("test","recycleVIew");
//                        }
//
//                        @Override
//                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                            super.onScrollStateChanged(recyclerView, newState);
//                        }
//                    });
//
//                }
//                    mListView.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            float startY=0;
//                            switch (event.getAction()){
//                                case MotionEvent.ACTION_DOWN:
//                                    startY=event.getY();
//                                    break;
//                                case  MotionEvent.ACTION_MOVE:
//                                    mOfferY=(int)(event.getY()-startY);
//                                    break;
//                            }
//                            return false;
//                        }
//                    });
//
////                }else {
////                    mListView.setOnScrollListener(null);
////                }
//
//                //mAdapter.notifyDataSetChanged();
//               // mMyHandler.sendEmptyMessage(1);
//                return false;
//            }
//        });
//
//


        mLLCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });



        mAdapter.setOnCheckListener(new WXImgChatAdapter2.OnCheckListener() {
            @Override
            public void onCheck(int groupPosition, int position, boolean isCheck) {
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
                }else {
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
        for (FileTitleEntity fileTitleEntity : lists) {
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
        List<FileTitleEntity> lists = mAdapter.getList();
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
     * 移除列表中元素
     *
     * @param paths 根据路径和父类id是否相等
     */
    public void updateDelFileView(List<FileChildEntity> paths) {
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
            listsNew.add(fileTitle);
        }
        mLoading.dismissAllowingStateLoss();
        mPresenter.totalFileSize(listsNew);
        mAdapter.clear();
        mAdapter.modifyData(listsNew);
        setDelBtnSize();
        setSelectChildStatus();

        FragmentManager fm = getActivity().getFragmentManager();
        String totalSize=FileSizeUtils.formatFileSize(getDelTotalFileSize(paths));
        String fileSize=String.valueOf(paths.size());
        DelFileSuccessFragment.newInstance(totalSize,fileSize).show(fm,"");

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
                    ToastUtils.show("未选中照片");
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
            ToastUtils.show("保存成功，请至手机相册查看");
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
        List<FileTitleEntity> lists = mAdapter.getList();

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
        List<FileTitleEntity> lists = mAdapter.getList();

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
//        List<FileTitleEntity> fileCopyEntitys=new ArrayList<>();
//        for(int i=0;i<lists.size();i++){
//            FileTitleEntity fileParent=lists.get(i);
//            FileTitleEntity fileTitleEntity=FileTitleEntity.copyObject(fileParent.id,fileParent.title,fileParent.type
//                    ,fileParent.size,fileParent.isExpand,fileParent.isSelect);
//
//            List<FileChildEntity> listsNew=new ArrayList<>();
//            int count=0;
//            if(fileParent.lists.size()>30){
//                count=30;
//            }else {
//                count=fileParent.lists.size();
//            }
//            for(int j=0;j<count;j++){
//                FileChildEntity childEntity=fileParent.lists.get(j);
//                listsNew.add(childEntity);
//            }
//            fileTitleEntity.lists.addAll(listsNew);
//            fileCopyEntitys.add(fileTitleEntity);
//        }
//        mAdapter.modifyData(fileCopyEntitys);
        mAdapter.modifyData(lists);

        SharedPreferences sharedPreferences =getContext().getSharedPreferences(SpCacheConfig.CACHES_FILES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong(Constant.WX_CACHE_SIZE_IMG,totalFileSize(lists));
        editor.commit();


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
