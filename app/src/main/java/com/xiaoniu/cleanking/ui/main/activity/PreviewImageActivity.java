package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImagePreviewAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.PreviewImagePagerAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.Image;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.ui.main.presenter.ImagePreviewPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.xiaoniu.cleanking.utils.ExtraConstant.PREVIEW_IMAGE_SELECT;

/**
 * 预览图片
 */
public class PreviewImageActivity extends BaseActivity<ImagePreviewPresenter> implements ViewPager.OnPageChangeListener {

    @BindView(R.id.preview_image_vp_content)
    ViewPager mViewPager;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_pos)
    TextView tv_pos;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    @BindView(R.id.tv_selectimage)
    TextView tvSelectImage;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    PreviewImagePagerAdapter previewImagePagerAdapter;
    ImagePreviewAdapter adapter;
    private List<FileEntity> mImageArrayList;
    int selectPos = 0;

    @Override
    public int getLayoutId() {
        return R.layout.common_activity_preview_image;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.black), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.black), false);
        }

        Intent intent = getIntent();
//        mImageArrayList = intent.getParcelableArrayListExtra(ExtraConstant.PREVIEW_IMAGE_DATA);
        mImageArrayList = CleanAllFileScanUtil.clean_image_list;
        int position = intent.getIntExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, 0);

        if (null == mImageArrayList) {
            mImageArrayList = new ArrayList<>();
        }
        adapter = new ImagePreviewAdapter(PreviewImageActivity.this, mImageArrayList, position);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PreviewImageActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(adapter);


        previewImagePagerAdapter = new PreviewImagePagerAdapter(this, mImageArrayList);
        mViewPager.setAdapter(previewImagePagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(position);
        //position == 0 时   不会调用onPageSelected方法
        if (position == 0) {
            setPosition(position);
        }


        computeDeleteSize();

        adapter.setmOnCheckListener(new ImagePreviewAdapter.onCheckListener() {
            @Override
            public void onCheck(int pos) {
                adapter.setSelectPosition(pos);
                mViewPager.setCurrentItem(pos);
                if (pos == 0) {
                    setPosition(pos);
                }
            }
        });
        tvSelectImage.setSelected(false);
        //顶部选中此张图
        tvSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectImage.setSelected(!tvSelectImage.isSelected());
                tvSelectImage.setBackgroundResource(tvSelectImage.isSelected() ? R.drawable.icon_select : R.drawable.icon_unselect);
               adapter.getListImage().get(selectPos).setIsSelect(tvSelectImage.isSelected());
                if (adapter != null)
                    adapter.setSelectPosition(selectPos);
                computeDeleteSize();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToActivity();
                finish();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<FileEntity> listF = new ArrayList<>();
                List<FileEntity> listData=adapter.getListImage();
                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).getIsSelect())
                        listF.add(adapter.getListImage().get(i));
                }
                if (listF.size() == 0)
                    return;
                mPresenter.alertBanLiveDialog(PreviewImageActivity.this, listF.size(), new ImagePreviewPresenter.ClickListener() {
                    @Override
                    public void clickOKBtn() {
//                        for (int i = 0; i < listF.size(); i++) {
//                            File f = new File(listF.get(i).getPath());
//                            f.delete();
//                        }
                        //数据库删除选中的文件
                        mPresenter.deleteFromDatabase(listF);
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });

            }
        });
    }


    public void computeDeleteSize() {
        List<FileEntity> listF = new ArrayList<>();
        List<FileEntity> listData=adapter.getListImage();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getIsSelect())
                listF.add(listData.get(i));
        }

        tvSelectCount.setText("已选:" + listF.size() + "张");
        long deleteSize = 0;
        for (int i = 0; i < listF.size(); i++) {
            deleteSize += NumberUtils.getLong(listF.get(i).getSize());
        }
        tvDelete.setText(listF.size() == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSize(deleteSize));
        tvDelete.setBackgroundResource(listF.size() == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
    }

    @Override
    public void netError() {

    }

    //删除成功
    public void deleteSuccess(List<FileEntity> listF) {
        tvDelete.setText("删除");

        adapter.deleteData(listF);

        mImageArrayList.removeAll(listF);
        previewImagePagerAdapter = new PreviewImagePagerAdapter(this, mImageArrayList);
        mViewPager.setAdapter(previewImagePagerAdapter);
        computeDeleteSize();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setPosition(position);
        selectPos = position;
        tvSelectImage.setBackgroundResource(adapter.getListImage().get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
        tvSelectImage.setSelected(adapter.getListImage().get(position).getIsSelect());
        if (adapter != null)
            adapter.setSelectPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setPosition(int position) {
        tv_pos.setText((position + 1) + "/" + mImageArrayList.size());
    }


    @Override
    public void onBackPressed() {
        backToActivity();
        super.onBackPressed();
    }

    public void backToActivity(){
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        intent1.putExtras(bundle);
        setResult(205, intent1);
        CleanAllFileScanUtil.clean_image_list =mImageArrayList;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
