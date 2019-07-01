package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.FileManagerHomePresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.TimeUtil;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_7A7B7C), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_7A7B7C), false);
        }
        //查询手机存储使用率
        mPresenter.getSpaceUse(tv_spaceinfos, circleProgressView);

        //监听进度条进度
        circleProgressView.setOnAnimProgressListener(new CircleProgressView.OnAnimProgressListener() {
            @Override
            public void valueUpdate(int progress) {
                tv_percent_num.setText("" + progress);
            }
        });
        //扫描
        mPresenter.scanSdcardFiles();
    }

    /**
     * 扫描出的结果
     *
     * @param listFiles
     */
    public void scanSdcardResult(List<File> listFiles) {
        Log.e("qwerty", "listFiles:" + listFiles.size());
        long imageSize = 0;
        long videoSize = 0;
        long musicSize = 0;
        long apkSize = 0;
        List<File> listImages = new ArrayList<>();
        List<File> listVideos = new ArrayList<>();
        List<File> listMusics = new ArrayList<>();
        List<File> listApks = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            if (listFiles.get(i) != null) {
                if (listFiles.get(i).getAbsolutePath().endsWith(".mp4")) {
                    listVideos.add(listFiles.get(i));
                    videoSize += listFiles.get(i) == null ? 0 : listFiles.get(i).length();
                } else if (listFiles.get(i).getAbsolutePath().endsWith(".apk")) {
                    listApks.add(listFiles.get(i));
                    apkSize += listFiles.get(i) == null ? 0 : listFiles.get(i).length();
                } else if (listFiles.get(i).getAbsolutePath().endsWith(".mp3")) {
                    listMusics.add(listFiles.get(i));
                    musicSize += listFiles.get(i) == null ? 0 : listFiles.get(i).length();
                } else {
                    listImages.add(listFiles.get(i));
                    imageSize += listFiles.get(i) == null ? 0 : listFiles.get(i).length();
                }
            }
        }
        tvImageSize.setText(CleanAllFileScanUtil.byte2FitSize(imageSize));
        tvVideoSize.setText(CleanAllFileScanUtil.byte2FitSize(videoSize));
        tvMusicSize.setText(CleanAllFileScanUtil.byte2FitSize(musicSize));
        tvApkSize.setText(CleanAllFileScanUtil.byte2FitSize(apkSize));

    }

    @Override
    public void netError() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
