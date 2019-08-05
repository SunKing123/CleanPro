package com.xiaoniu.cleanking.ui.tool.wechat.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.FileCopyProgressDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanImgPresenter;
import com.xiaoniu.cleanking.ui.tool.wechat.presenter.WXCleanFilePresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;

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
public class WXFileFragment extends BaseFragment<WXCleanFilePresenter> {

    @BindView(R.id.list_view)
    ExpandableListView mListView;
    private WXImgChatAdapter mAdapter;


    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    private  boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;
    private FileCopyProgressDialogFragment mProgress;;

    public static WXFileFragment newInstance() {
        WXFileFragment wxImgChatFragment = new WXFileFragment();

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

    @Override
    protected void initView() {
        mAdapter = new WXImgChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mLoading=CleanFileLoadingDialogFragment.newInstance();
        mProgress=FileCopyProgressDialogFragment.newInstance();
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                List<FileTitleEntity> lists = mAdapter.getList();
                for (int i = 0; i < lists.size(); i++) {
                    if (i == groupPosition) {
                        FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                        if (fileTitleEntity.isExpand) {
                            fileTitleEntity.isExpand = false;
                        } else {
                            fileTitleEntity.isExpand = true;
                        }
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        mLLCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsCheckAll){
                    mIsCheckAll=false;
                }else {
                    mIsCheckAll=true;
                }
                mLLCheckAll.setSelected(mIsCheckAll);
                setSelectStatus(mIsCheckAll);
                setDelBtnSize();
            }
        });

//        mAdapter.setOnCheckListener(new WXImgChatAdapter.OnCheckListener() {
//            @Override
//            public void onCheck(int groupPosition, int position, boolean isCheck) {
//                setSelectChildStatus(groupPosition,position,isCheck);
//                setDelBtnSize();
//            }
//        });

    }


    /**
     * 改变子视图的选择状态
     * @param isCheck
     */
    private  void setSelectChildStatus(int groupPosition,int position,boolean isCheck){
        List<FileTitleEntity> lists = mAdapter.getList();
        for(int i=0;i<lists.size();i++) {
            if (i == groupPosition) {
                //是否选择所有
                boolean isCheckAll=true;
                FileTitleEntity fileTitleEntity = lists.get(groupPosition);
                for (FileChildEntity file : fileTitleEntity.lists) {
                    if (file.isSelect == false) {
                        isCheckAll =false;
                    }
                }

                fileTitleEntity.isSelect=isCheckAll;
                mAdapter.notifyDataSetChanged();
                break;
            }
        }


    }
    private void setSelectStatus(boolean isCheck){
        List<FileTitleEntity> lists = mAdapter.getList();
        for(FileTitleEntity fileTitleEntity :lists){
            if(fileTitleEntity.lists.size()>0){
                fileTitleEntity.isSelect=isCheck;

                for(FileChildEntity file: fileTitleEntity.lists){
                    file.isSelect=isCheck;
                }
            }

        }
        mAdapter.notifyDataSetChanged();
    }

    private long totalSelectSize() {
        long size = 0L;
        List<FileTitleEntity> lists = mAdapter.getList();
        for(FileTitleEntity fileTitleEntity :lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    size+=file.size;
                }
            }
        }
        return size;
    }

    private  void setDelBtnSize(){
        long size=totalSelectSize();
        if(size>0){
            mBtnDel.setSelected(true);
            mBtnDel.setEnabled(true);
            mBtnDel.setText("删除"+FileSizeUtils.formatFileSize(size));
        }else {
            mBtnDel.setSelected(false);
            mBtnDel.setEnabled(false);
            mBtnDel.setText("删除");
        }

    }

    /**
     * 移除列表中元素
     * @param paths 根据路径和父类id是否相等
     */
   public void updateDelFileView(List<FileChildEntity> paths){
       List<FileTitleEntity> listsNew=new ArrayList<>();

        List<FileTitleEntity> lists=mAdapter.getList();
        for(int i=0;i<lists.size();i++){
            FileTitleEntity fileTitleEntity=lists.get(i);

            FileTitleEntity fileTitle=FileTitleEntity.copyObject(fileTitleEntity.id,fileTitleEntity.title
                    ,fileTitleEntity.type,fileTitleEntity.size,fileTitleEntity.isExpand,fileTitleEntity.isSelect);
            for(FileChildEntity fileChildEntity: fileTitleEntity.lists){
                if(fileChildEntity.isSelect==false){
                    fileTitle.lists.add(fileChildEntity);
                }
            }
            listsNew.add(fileTitle);
        }
        mLoading.dismissAllowingStateLoss();
        ToastUtils.show("删除成功");
         mPresenter.totalFileSize(listsNew);
        mAdapter.clear();
        mAdapter.modifyData(listsNew);
    }

    /**
     * 获取选中删除的元素
     * @return
     */
    public List<FileChildEntity> getDelFile(){
        List<FileChildEntity> files=new ArrayList<>();
        List<FileTitleEntity> lists=mAdapter.getList();
        for(FileTitleEntity fileTitleEntity:lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    files.add(file);
                }
            }
        }
        return  files;
    }
    @OnClick({R.id.btn_del,R.id.btn_save})
    public void onClickView(View view){
        int ids=view.getId();
        switch (ids){
            case R.id.btn_del:

                String title=String.format("确定删除这%s个图片?",getSelectSize());
                DelDialogStyleFragment dialogFragment = DelDialogStyleFragment.newInstance(title);
                FragmentManager fm = getActivity().getFragmentManager();
                dialogFragment.show(fm,"");
                dialogFragment.setDialogClickListener(new DelDialogStyleFragment.DialogClickListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                        mLoading.show(getActivity().getSupportFragmentManager(),"");
                        List<FileChildEntity> files=getDelFile();
                        mPresenter.delFile(files);
                    }
                });

                break;
            //保存到手机
            case R.id.btn_save:

                List<File> lists=getSelectFiles();
                if(lists.size()==0){
                    ToastUtils.show("未选中照片");
                }else {
                    FragmentManager fmProgress= getActivity().getFragmentManager();
                    mProgress.show(fmProgress,"");
                    //导入图片
                    mPresenter.copyFile(lists);
                }

                break;
        }
    }


    /**
     * 导入成功
     * @param progress
     */
    public void copySuccess(int progress){

        mProgress.setValue(progress);
        if(progress>=100){
            ToastUtils.show("保存成功，请至手机相册查看");
            mProgress.dismissAllowingStateLoss();
        }
    }


    private  int getSelectSize(){
        int size = 0;
        List<FileTitleEntity> lists = mAdapter.getList();

        for(FileTitleEntity fileTitleEntity: lists){
            for(FileChildEntity file:fileTitleEntity.lists){
                if(file.isSelect){
                    size+=1;
                }
            }
        }
        return  size;
    }

    private  List<File> getSelectFiles(){
        List<File> files=new ArrayList<>();
        List<FileTitleEntity> lists = mAdapter.getList();

        for(FileTitleEntity fileTitleEntity: lists){
            for(FileChildEntity fileChildEntity:fileTitleEntity.lists){
                if(fileChildEntity.isSelect){
                    File File =new File(fileChildEntity.path);
                    files.add(File);
                }
            }
        }
        return  files;
    }



    /**
     * 更新微信聊天图片
     * @param lists
     */
    public void updateImgChat(List<FileTitleEntity> lists) {
        mAdapter.modifyData(lists);
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
