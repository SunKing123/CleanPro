package com.xiaoniu.cleanking.ui.main.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.WXVideoChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.CleanFileLoadingDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogStyleFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.FileCopyProgressDialogFragment;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.VideoPlayFragment;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanSaveListPresenter;
import com.xiaoniu.cleanking.ui.main.presenter.WXVideoCleanSaveListPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.MusicFileUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 微信图片保存图片
 * Created by lang.chen on 2019/8/1
 */
public class WXVideoSaveListFragment extends BaseFragment<WXVideoCleanSaveListPresenter> {
    private static final int REQUEST_CODE_IMG_VIEW = 0x1022;
    @BindView(R.id.list_view_save_list)
    ExpandableListView mListView;

    @BindView(R.id.ll_check_all)
    LinearLayout mLLCheckAll;
    @BindView(R.id.btn_del)
    Button mBtnDel;

    private boolean mIsCheckAll;
    private CleanFileLoadingDialogFragment mLoading;
    private FileCopyProgressDialogFragment mProgress;
    ;

    private WXVideoChatAdapter mAdapter;

    private int mGroupPosition;

    public static WXVideoSaveListFragment newInstance() {
        WXVideoSaveListFragment instance = new WXVideoSaveListFragment();
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
        mLoading = CleanFileLoadingDialogFragment.newInstance();
        mProgress = FileCopyProgressDialogFragment.newInstance();
        mAdapter = new WXVideoChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
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
        mPresenter.init(mContext);
        mPresenter.start();

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
            }
        });

        mAdapter.setOnCheckListener(new WXVideoChatAdapter.OnCheckListener() {
            @Override
            public void onCheck(int groupPosition, int position, boolean isCheck) {
                setSelectChildStatus(groupPosition);
                setDelBtnSize();
            }

            @Override
            public void onCheckImg(int groupPosition, int position) {
                mGroupPosition = groupPosition;
                Intent intent = new Intent(mActivity, PreviewImageActivity.class);
                intent.putExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, position);
                CleanAllFileScanUtil.clean_image_list = wrapperImg(groupPosition, position);
                startActivityForResult(intent, REQUEST_CODE_IMG_VIEW);
            }

            @Override
            public void onCheckVideo(int groupPosition, int position) {
                List<FileTitleEntity> lists= mAdapter.getList();
                if(groupPosition<lists.size()){
                    FileTitleEntity fileTitleEntity=lists.get(groupPosition);
                    //获取子集
                    List<FileChildEntity> flieChilds=fileTitleEntity.lists;
                    if(position<flieChilds.size()){
                        FileChildEntity fileChildEntity=flieChilds.get(position);
                        play(fileChildEntity.name,fileChildEntity.path,fileChildEntity.size);
                    }
                }
            }
        });

    }

    public void play(String name,String path,long lenth) {
        VideoPlayFragment videoPlayFragment=VideoPlayFragment.newInstance(name,FileSizeUtils.formatFileSize(lenth)
                ,"时长: "+ MusicFileUtils.getPlayDuration2(path),"未知");
        FragmentManager fm = getActivity().getFragmentManager();
        videoPlayFragment.show(fm,"");
        videoPlayFragment.setDialogClickListener(new VideoPlayFragment.DialogClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                playAudio(path);
            }
        });
    }

    public void playAudio(String audioPath) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //解决8.0以上问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file:///" + audioPath);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

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
        if (lists.size() > 0) {
            List<FileChildEntity> listChilds = lists.get(groupPosition).lists;
            for (FileChildEntity fileChildEntity : listChilds) {
                FileEntity fileEntity = new FileEntity(String.valueOf(fileChildEntity.size), fileChildEntity.path);
                fileEntity.isSelect = fileChildEntity.isSelect;
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
                }else {
                    for (FileChildEntity file : fileTitleEntity.lists) {
                        if (file.isSelect == false) {
                            isCheckAll = false;
                        }
                    }

                    fileTitleEntity.isSelect = isCheckAll;
                }
            }
        }
        mAdapter.notifyDataSetChanged();

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
        mAdapter.notifyDataSetChanged();

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
            mAdapter.notifyDataSetChanged();
            setSelectChildStatus(mGroupPosition);
            setDelBtnSize();

        }
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
        mAdapter.notifyDataSetChanged();
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
        ToastUtils.show("删除成功");
        setDelBtnSize();
        setSelectChildStatus();
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
            case R.id.btn_save:
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
