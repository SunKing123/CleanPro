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
    private ArrayList<Image> mImageArrayList;
    int selectPos = 0;
    List<Integer> listSelect = new ArrayList<>();

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
        mImageArrayList = intent.getParcelableArrayListExtra(ExtraConstant.PREVIEW_IMAGE_DATA);
        int position = intent.getIntExtra(ExtraConstant.PREVIEW_IMAGE_POSITION, 0);
        listSelect.addAll((List) intent.getSerializableExtra(ExtraConstant.PREVIEW_IMAGE_SELECT));

        if (null == mImageArrayList) {
            mImageArrayList = new ArrayList<>();
        }
         previewImagePagerAdapter = new PreviewImagePagerAdapter(this, mImageArrayList);
        mViewPager.setAdapter(previewImagePagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(position);
        //position == 0 时   不会调用onPageSelected方法
        if (position == 0) {
            setPosition(position);
        }

        adapter = new ImagePreviewAdapter(PreviewImageActivity.this, mImageArrayList, position, listSelect);
        computeDeleteSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PreviewImageActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(adapter);
        adapter.setmOnCheckListener(new ImagePreviewAdapter.onCheckListener() {
            @Override
            public void onCheck(int pos) {
                adapter.setSelectPosition(pos, listSelect);
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
                if (tvSelectImage.isSelected()) {
                    if (!listSelect.contains(selectPos))
                        listSelect.add(selectPos);
                } else {
                    if (listSelect.contains(selectPos))
                        listSelect.remove(listSelect.indexOf(selectPos));
                }
                if (adapter != null)
                    adapter.setSelectPosition(selectPos, listSelect);
                computeDeleteSize();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listSelect.size() == 0)
                    return;
                List<Image> listF = new ArrayList<>();
                for (int i = 0; i < adapter.getListImage().size(); i++) {
                    if (listSelect.contains(i))
                        listF.add(adapter.getListImage().get(i));
                }
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
        tvSelectCount.setText("已选:" + listSelect.size() + "张");
        long sizes = 0;
        for (int i = 0; i < adapter.getListImage().size(); i++) {
            if (listSelect.contains(i))
                sizes += adapter.getListImage().get(i).getSize();
        }
        tvDelete.setText(listSelect.size() == 0 ? "删除" : "删除 " + CleanAllFileScanUtil.byte2FitSize(sizes));
        tvDelete.setBackgroundResource(listSelect.size() == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
    }

    @Override
    public void netError() {

    }
    //删除成功
    public void deleteSuccess(List<Image> listF) {
        tvDelete.setText("删除");
        for(int i=0;i<adapter.getListImage().size();i++){
           if(listF.contains(adapter.getListImage().get(i))){
               listSelect.remove(listSelect.indexOf(i));
           }
        }
        adapter.deleteData(listF);

        mImageArrayList.removeAll(listF);
        previewImagePagerAdapter = new PreviewImagePagerAdapter(this, mImageArrayList);
        mViewPager.setAdapter(previewImagePagerAdapter);
        computeDeleteSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setPosition(position);
        selectPos = position;
        tvSelectImage.setBackgroundResource(listSelect.contains(position) ? R.drawable.icon_select : R.drawable.icon_unselect);
        tvSelectImage.setSelected(listSelect.contains(position));
        if (adapter != null)
            adapter.setSelectPosition(position, listSelect);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setPosition(int position) {
        tv_pos.setText((position + 1) + "/" + mImageArrayList.size());
    }

    @Override
    public void finish() {
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectList", (Serializable) listSelect);
        intent1.putExtras(bundle);
        setResult(205, intent1);
        super.finish();
    }
}
