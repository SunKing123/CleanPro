package com.xiaoniu.cleanking.ui.main.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanSaveListPresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信图片保存图片
 * Created by lang.chen on 2019/8/1
 */
public class WXImgSaveListFragment extends BaseFragment<WXCleanSaveListPresenter> {

    @BindView(R.id.list_view_save_list)
    ExpandableListView mListView;

    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    private  boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;

    private WXImgChatAdapter mAdapter;

    public static WXImgSaveListFragment newInstance() {
        WXImgSaveListFragment instance = new WXImgSaveListFragment();
        return instance;
    }


    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.wx_img_save_list;
    }

    @Override
    protected void initView() {
        mLoading=CleanFileLoadingDialogFragment.newInstance();
        mAdapter=new WXImgChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                List<FileTitleEntity> lists=mAdapter.getList();
                for(int i=0;i<lists.size();i++){
                    if(i==groupPosition){
                        FileTitleEntity fileTitleEntity=lists.get(groupPosition);
                        if(fileTitleEntity.isExpand){
                            fileTitleEntity.isExpand=false;
                        }else {
                            fileTitleEntity.isExpand=true;
                        }
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });
        mPresenter.init(mContext);
        mPresenter.start();

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

        mAdapter.setOnCheckListener(new WXImgChatAdapter.OnCheckListener() {
            @Override
            public void onCheck(int groupPosition, int position, boolean isCheck) {
                setSelectChildStatus(groupPosition,position,isCheck);
                setDelBtnSize();
            }
        });

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
            mBtnDel.setText("删除"+ FileSizeUtils.formatFileSize(size));
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
            case R.id.btn_save:
                break;
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


    public void updateImgSaveList(List<FileTitleEntity> lists) {

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
