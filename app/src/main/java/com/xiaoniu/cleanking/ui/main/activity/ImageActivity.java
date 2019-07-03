package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.db.FileDBManager;
import com.xiaoniu.cleanking.utils.db.FileTableManager;
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
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.cb_checkall)
    TextView cb_checkall;
    @BindView(R.id.line_none)
    LinearLayout line_none;
    ImageShowAdapter imageAdapter;
    List<Integer> listSelect = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_list;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        tv_delete.setSelected(false);
        cb_checkall.setSelected(false);
        getImageList();
        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recycle_view.isComputingLayout()) {
                    cb_checkall.setSelected(!cb_checkall.isSelected());
                    tv_delete.setSelected(cb_checkall.isSelected());
                    imageAdapter.setIsCheckAll(cb_checkall.isSelected() ? true : false);
                    cb_checkall.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    tv_delete.setBackgroundResource(cb_checkall.isSelected() ? R.drawable.delete_select_bg : R.drawable.delete_unselect_bg);
                    compulateDeleteSize();
                }
            }
        });
        //删除图片
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tv_delete.isSelected())
                    return;
                List<FileEntity> listF = new ArrayList<>();
                for (int i = 0; i < imageAdapter.getListImage().size(); i++) {
                    if (imageAdapter.getListCheck().contains(i))
                        listF.add(imageAdapter.getListImage().get(i));
                }

                mPresenter.alertBanLiveDialog(ImageActivity.this, listF.size(), new ImageListPresenter.ClickListener() {
                    @Override
                    public void clickOKBtn() {
//                        for (int i = 0; i < listF.size(); i++) {
//                            File f = new File(listF.get(i).getPath());
//                            f.delete();
//                        }
                        //数据库删除选中的文件
                        mPresenter.deleteFromDatabase(listF, imageAdapter);
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //删除成功
    public void deleteSuccess(List<FileEntity> listF) {
        tv_delete.setSelected(false);
        tv_delete.setText("删除");
        imageAdapter.deleteData(listF);
        line_none.setVisibility(imageAdapter.getListImage().size() == 0 ? View.VISIBLE : View.GONE);
        recycle_view.setVisibility(imageAdapter.getListImage().size() == 0 ? View.GONE : View.VISIBLE);
    }

    //计算删除文件大小
    public void compulateDeleteSize() {
        List<FileEntity> listF = new ArrayList<>();
        for (int i = 0; i < imageAdapter.getListImage().size(); i++) {
            if (imageAdapter.getListCheck().contains(i))
                listF.add(imageAdapter.getListImage().get(i));
        }
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += NumberUtils.getLong(listF.get(i).getSize());
        }
        tv_delete.setText(deleteSize == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSize(deleteSize));
    }

    /**
     * 扫描出的结果
     */
    public void getImageList() {
        List<FileEntity> listImages = CleanAllFileScanUtil.clean_image_list;
        imageAdapter = new ImageShowAdapter(ImageActivity.this, listImages, listSelect);
        recycle_view.setLayoutManager(new GridLayoutManager(ImageActivity.this, 3));
        recycle_view.setAdapter(imageAdapter);
        line_none.setVisibility(listImages.size() == 0 ? View.VISIBLE : View.GONE);
        recycle_view.setVisibility(listImages.size() == 0 ? View.GONE : View.VISIBLE);
        imageAdapter.setmOnCheckListener(new ImageShowAdapter.onCheckListener() {
            @Override
            public void onCheck(List<FileEntity> listFile, int pos) {

                cb_checkall.setBackgroundResource(imageAdapter.getListCheck().size() == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(imageAdapter.getListCheck().size()==0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
                tv_delete.setSelected(imageAdapter.getListCheck().size()==0 ? false : true);
                compulateDeleteSize();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 205) {
            List<Integer> listInt = (List<Integer>) data.getSerializableExtra("selectList");
            imageAdapter.setListCheck(listInt);
            tv_delete.setBackgroundResource(imageAdapter.getListCheck().size()==0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
            tv_delete.setSelected(imageAdapter.getListCheck().size()==0 ? false : true);
            compulateDeleteSize();
            cb_checkall.setBackgroundResource(imageAdapter.getListCheck().size() == imageAdapter.getListImage().size() ? R.drawable.icon_select : R.drawable.icon_unselect);
        }
    }

    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
