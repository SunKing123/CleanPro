package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.presenter.FileManagerHomePresenter;
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
public class FileManagerHomeActivity extends BaseActivity<FileManagerHomePresenter> {

    @BindView(R.id.circle_progress)
    CircleProgressView circleProgressView;
    @BindView(R.id.tv_spaceinfos)
    TextView tv_spaceinfos;
    @BindView(R.id.tv_percent_num)
    TextView tv_percent_num;
    @BindView(R.id.tv_image_size)
    TextView tvImageSize;
    @BindView(R.id.tv_video_size)
    TextView tvVideoSize;
    @BindView(R.id.tv_music_size)
    TextView tvMusicSize;
    @BindView(R.id.tv_apk_size)
    TextView tvApkSize;
    @BindView(R.id.view_imagearea)
    View viewImagearea;
    long times1 = 0;


    @Override
    public int getLayoutId() {
        return R.layout.activity_filemanager_home;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_7A7B7C), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_7A7B7C), false);
        }
        //查询手机存储使用率
        mPresenter.getSpaceUse(tv_spaceinfos, circleProgressView);
        //获取sdcard文件，从数据库查询
        mPresenter.getSdcardFiles();
        //监听进度条进度
        circleProgressView.setOnAnimProgressListener(new CircleProgressView.OnAnimProgressListener() {
            @Override
            public void valueUpdate(int progress) {
                tv_percent_num.setText("" + progress);
            }
        });
    }


    /**
     * 扫描出的结果
     *
     * @param listFiles
     */
    public void scanSdcardResult(List<Map<String, String>> listFiles) {
        if (listFiles.size() == 0) {
            showLoadingDialog();
            return;
        }
        cancelLoadingDialog();
        Log.e("qwerty", "listFiles:" + listFiles.size());
        long imageSize = 0;
        long videoSize = 0;
        long musicSize = 0;
        long apkSize = 0;
        List<FileEntity> listImages = new ArrayList<>();
        List<Map<String, String>> listVideos = new ArrayList<>();
        List<Map<String, String>> listMusics = new ArrayList<>();
        List<Map<String, String>> listApks = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            if (listFiles.get(i) != null) {
                String filePath = listFiles.get(i).get("path");
                if (Arrays.asList(CleanAllFileScanUtil.videoFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
                    listVideos.add(listFiles.get(i));
                    videoSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                } else if (Arrays.asList(CleanAllFileScanUtil.apkFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
                    listApks.add(listFiles.get(i));
                    apkSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                } else if (Arrays.asList(CleanAllFileScanUtil.musicFormat).contains(filePath.substring(filePath.lastIndexOf('.'), filePath.length()))) {
                    listMusics.add(listFiles.get(i));
                    musicSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                } else {
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setSize(listFiles.get(i).get("size"));
                    fileEntity.setTime(listFiles.get(i).get("time"));
                    fileEntity.setPath(listFiles.get(i).get("path"));
                    fileEntity.setType(listFiles.get(i).get("type"));
                    listImages.add(fileEntity);
                    imageSize += listFiles.get(i) == null ? 0 : NumberUtils.getLong(listFiles.get(i).get("size"));
                }
            }
        }
        tvImageSize.setText(CleanAllFileScanUtil.byte2FitSize(imageSize));
        tvVideoSize.setText(CleanAllFileScanUtil.byte2FitSize(videoSize));
        tvMusicSize.setText(CleanAllFileScanUtil.byte2FitSize(musicSize));
        tvApkSize.setText(CleanAllFileScanUtil.byte2FitSize(apkSize));

        viewImagearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileManagerHomeActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 数据库更新成功
     */
    @Subscribe
    public void updateFileManager(MessageEvent messageEvent) {
        if (EventBusTags.UPDATE_FILE_MANAGER.equals(messageEvent.getType())) {
            //获取sdcard文件，从数据库查询
            Log.e("更新数据成功", "更新数据成功");
            mPresenter.getSdcardFiles();
        }
    }

    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
