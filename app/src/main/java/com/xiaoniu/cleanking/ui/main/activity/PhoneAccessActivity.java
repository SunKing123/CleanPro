package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.adapter.PhoneAccessAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 手机加速--一键清理内存页面
 */
public class PhoneAccessActivity extends BaseActivity<PhoneAccessPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    PhoneAccessAdapter imageAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_phone_access;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    @Override
    public void initView() {
        getImageList();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                mPresenter.getAccessList();
            }
        });

    }

    public void getImageList() {
        List<FileEntity> listImages = CleanAllFileScanUtil.clean_image_list == null ? new ArrayList<>() : CleanAllFileScanUtil.clean_image_list.subList(0, 20);
        imageAdapter = new PhoneAccessAdapter(PhoneAccessActivity.this, listImages);
        recycle_view.setLayoutManager(new LinearLayoutManager(PhoneAccessActivity.this));
        recycle_view.setAdapter(imageAdapter);
        recycle_view.setVisibility(listImages.size() == 0 ? View.GONE : View.VISIBLE);
        imageAdapter.setmOnCheckListener(new PhoneAccessAdapter.onCheckListener() {
            @Override
            public void onCheck(List<FileEntity> listFile, int pos) {
                int selectCount = 0;
                for (int i = 0; i < listFile.size(); i++) {
                    if (listFile.get(i).getIsSelect()) {
                        selectCount++;
                    }
                }
//                cb_checkall.setBackgroundResource(selectCount == listFile.size() ? R.drawable.icon_select : R.drawable.icon_unselect);
                tv_delete.setBackgroundResource(selectCount == 0 ? R.drawable.delete_unselect_bg : R.drawable.delete_select_bg);
                tv_delete.setSelected(selectCount == 0 ? false : true);
//                compulateDeleteSize();
            }
        });
    }


    public void getAccessList(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += firstJunkInfo.getTotalSize();
        Log.e("totalSizes", "" + totalSizes);
    }

    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
