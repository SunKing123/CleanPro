package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.presenter.FileManagerHomePresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.EventBusTags;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.MessageEvent;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.iv_back)
    ImageView iv_back;


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
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        //查询手机存储使用率
        mPresenter.getSpaceUse(tv_spaceinfos, circleProgressView);
        mPresenter.getPhotos(FileManagerHomeActivity.this);
        //监听进度条进度
        circleProgressView.setOnAnimProgressListener(new CircleProgressView.OnAnimProgressListener() {
            @Override
            public void valueUpdate(int progress) {
                tv_percent_num.setText("" + progress);
            }
        });
    }


    public void getPhotoInfo(List<FileEntity> listPhoto) {
        long imageSize = 0;
        for (FileEntity fileEntity : listPhoto) {
            imageSize += fileEntity == null ? 0 : NumberUtils.getLong(fileEntity.getSize());
        }
        tvImageSize.setText(CleanAllFileScanUtil.byte2FitSize(imageSize));
        viewImagearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileManagerHomeActivity.this, ImageActivity.class);
                CleanAllFileScanUtil.clean_image_list = listPhoto;
                startActivity(intent);
            }
        });
    }


    @Override
    public void netError() {

    }

    @OnClick({R.id.view_clean_video, R.id.view_clean_music, R.id.view_clean_install_apk})
    public void onClickView(View view) {
        int ids = view.getId();
        if (ids == R.id.view_clean_install_apk) {
            //跳转到安装包清理
            startActivity(new Intent(this, CleanInstallPackageActivity.class));
        } else if (ids == R.id.view_clean_music) {
            //跳转到音乐清理
            startActivity(new Intent(this, CleanMusicManageActivity.class));
        } else if (ids == R.id.view_clean_video) {
            //跳转到视频清理
            startActivity(new Intent(this, CleanVideoManageActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long videoSize=mPresenter.getVideoTotalSize();
        long musicSize=mPresenter.getMusicTotalSize();
        long apkSize=mPresenter.getAPKTotalSize();
        if(null!=tvVideoSize){
            tvVideoSize.setText(FileSizeUtils.formatFileSize(videoSize));
        }
        if(null!=tvMusicSize){
            tvMusicSize.setText(FileSizeUtils.formatFileSize(musicSize));
        }
        if(null!=tvApkSize){
            tvApkSize.setText(FileSizeUtils.formatFileSize(apkSize));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
