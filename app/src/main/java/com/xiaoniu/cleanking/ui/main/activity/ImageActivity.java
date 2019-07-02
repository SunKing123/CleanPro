package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 清理图片
 */
public class ImageActivity extends BaseActivity<ImageListPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    ImageShowAdapter imageAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_list;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    boolean isCheckeds = false;

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        mPresenter.getSdcardFiles();
        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recycle_view.isComputingLayout()) {
                    isCheckeds = isCheckeds ? false : true;
                    imageAdapter.setIsCheckAll(isCheckeds ? true : false);
                    cb_checkall.setBackgroundResource(isCheckeds ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(isCheckeds ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                }
            }
        });
        //删除图片
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tv_delete.isSelected())
                    return;
                mPresenter.alertBanLiveDialog(ImageActivity.this, new ImageListPresenter.ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        List<FileEntity> listF = new ArrayList<>();
                        for (int i = 0; i < imageAdapter.getListImage().size(); i++) {
                            if (imageAdapter.getListImage().get(i).getIsCheck())
                                listF.add(imageAdapter.getListImage().get(i));
                        }

                        for (int i = 0; i < listF.size(); i++) {
                            File f = new File(listF.get(i).getPath());
                            f.delete();
                        }
                        imageAdapter.deleteData(listF);
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });
            }
        });

    }


    /**
     * 扫描出的结果
     *
     * @param listFiles
     */
    public void scanSdcardResult(List<Map<String, String>> listFiles) {
        long imageSize = 0;
        List<FileEntity> listImages = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            if (listFiles.get(i) != null) {
                String filePath = listFiles.get(i).get("path");
                if (Arrays.asList(CleanAllFileScanUtil.imageFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setCheck(false);
                    fileEntity.setType(listFiles.get(i).get("type"));
                    fileEntity.setTime(listFiles.get(i).get("time"));
                    fileEntity.setSize(listFiles.get(i).get("size"));
                    fileEntity.setPath(listFiles.get(i).get("path"));
                    listImages.add(fileEntity);
                    imageSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                }
            }
        }
        tv_delete.setText("删除 " + CleanAllFileScanUtil.byte2FitSize(imageSize));
        imageAdapter = new ImageShowAdapter(ImageActivity.this, listImages);
        recycle_view.setLayoutManager(new GridLayoutManager(ImageActivity.this, 3));
        recycle_view.setAdapter(imageAdapter);
        imageAdapter.setmOnCheckListener(new ImageShowAdapter.onCheckListener() {
            @Override
            public void onCheck(List<FileEntity> listFile, int pos) {
                List<Boolean> listC = new ArrayList<>();
                for (int i = 0; i < listFile.size(); i++) {
                    listC.add(listFile.get(i).getIsCheck());
                }
                cb_checkall.setBackgroundResource(listC.contains(false) ? R.drawable.icon_unselect : R.drawable.icon_select);
                tv_delete.setBackgroundResource(listC.contains(true) ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                tv_delete.setSelected(listC.contains(true) ? true : false);
            }
        });
    }


    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
