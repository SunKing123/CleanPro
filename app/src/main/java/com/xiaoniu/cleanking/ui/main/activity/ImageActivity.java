package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.ImageShowAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.presenter.FileManagerHomePresenter;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.EventBusTags;
import com.xiaoniu.cleanking.utils.MessageEvent;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 文件管理首页
 */
public class ImageActivity extends BaseActivity<ImageListPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    ImageShowAdapter imageAdapter;

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
        mPresenter.getSdcardFiles();
    }


    /**
     * 扫描出的结果
     *
     * @param listFiles
     */
    public void scanSdcardResult(List<Map<String, String>> listFiles) {
        long imageSize = 0;
        List<Map<String, String>> listImages = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            if (listFiles.get(i) != null) {
                String filePath = listFiles.get(i).get("path");
                if (Arrays.asList(CleanAllFileScanUtil.imageFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
                    {
                        listImages.add(listFiles.get(i));
                        imageSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                    }
                }
            }
        }
        imageAdapter = new ImageShowAdapter(ImageActivity.this, listImages);
        recycle_view.setLayoutManager(new GridLayoutManager(ImageActivity.this, 3));
        recycle_view.setAdapter(imageAdapter);
    }


    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
