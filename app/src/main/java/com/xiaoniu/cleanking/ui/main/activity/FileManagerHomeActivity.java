package com.xiaoniu.cleanking.ui.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.event.FileCleanSizeEvent;
import com.xiaoniu.cleanking.ui.main.presenter.FileManagerHomePresenter;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.FileSizeUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.CircleProgressView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
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

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                StatisticsUtils.trackClick("file_clean_back_click", "返回按钮", "home_page", "file_cleaning_page");
            }
        });
    }


    public void getPhotoInfo(List<FileEntity> listPhoto) {
        long imageSize = 0;
        for (FileEntity fileEntity : listPhoto) {
            imageSize += fileEntity == null ? 0 : NumberUtils.getLong(fileEntity.getSize());
        }
        if (imageSize > 0) {
            tvImageSize.setText(CleanAllFileScanUtil.byte2FitSize(imageSize));
        }else {
            tvImageSize.setText("");
        }
        viewImagearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileManagerHomeActivity.this, ImageActivity.class);
                for (FileEntity fileEntity : listPhoto)
                    fileEntity.setIsSelect(false);
                CleanAllFileScanUtil.clean_image_list = listPhoto;
                startActivity(intent);
                StatisticsUtils.trackClick("picture_cleaning_page_click", "图片清理", "home_page", "file_cleaning_page");
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
            StatisticsUtils.trackClick("Installation_pack _pleaning_page", "安装包清理", "home_page", "file_cleaning_page");
        } else if (ids == R.id.view_clean_music) {
            //跳转到音乐清理
            startActivity(new Intent(this, CleanMusicManageActivity.class));
            StatisticsUtils.trackClick("music_cleaning_page", "音乐清理", "home_page", "file_cleaning_page");
        } else if (ids == R.id.view_clean_video) {
            //跳转到视频清理
            startActivity(new Intent(this, CleanVideoManageActivity.class));
            StatisticsUtils.trackClick("video_cleaning _page", "视频清理", "home_page", "file_cleaning_page");
        }
    }


    /**
     * 文件扫描后更新size
     */
    @Subscribe
    public void onUpdateSize(FileCleanSizeEvent fileCleanSizeEvent) {

        long videoSize = mPresenter.getVideoTotalSize();
        long musicSize = mPresenter.getMusicTotalSize();
        long apkSize = mPresenter.getAPKTotalSize();
        if (null != tvVideoSize && videoSize > 0) {
            tvVideoSize.setText(FileSizeUtils.formatFileSize(videoSize));
        }
        if (null != tvMusicSize && musicSize > 0) {
            tvMusicSize.setText(FileSizeUtils.formatFileSize(musicSize));
        }
        if (null != tvApkSize && apkSize > 0) {
            tvApkSize.setText(FileSizeUtils.formatFileSize(apkSize));
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getPhotos(FileManagerHomeActivity.this);
        long videoSize = mPresenter.getVideoTotalSize();
        long musicSize = mPresenter.getMusicTotalSize();
        long apkSize = mPresenter.getAPKTotalSize();


        if (null != tvVideoSize && videoSize > 0) {
            tvVideoSize.setText(FileSizeUtils.formatFileSize(videoSize));
        }else if(null!=tvVideoSize){
            tvVideoSize.setText("");
        }

        if (null != tvMusicSize && musicSize > 0) {
            tvMusicSize.setText(FileSizeUtils.formatFileSize(musicSize));
        }else if(null!=tvMusicSize){
            tvMusicSize.setText("");
        }
        if (null != tvApkSize && apkSize > 0) {
            tvApkSize.setText(FileSizeUtils.formatFileSize(apkSize));
        }else if(null!=tvApkSize){
            tvApkSize.setText("");
        }

        NiuDataAPI.onPageStart("file_clean_page_view_page", "文件清理");
    }

    public void checkPermission() {
        String permissionsHint = "需要打开文件读写权限";
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        new RxPermissions(FileManagerHomeActivity.this).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //开始更新

                } else {
                    if (hasPermissionDeniedForever(FileManagerHomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ToastUtils.show(permissionsHint);
                    }
                }
            }
        });
    }

    /**
     * 是否有权限被永久拒绝
     *
     * @param activity   当前activity
     * @param permission 权限
     * @return
     */
    private static boolean hasPermissionDeniedForever(Activity activity, String permission) {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPI.onPageEnd("file_clean_page_view_page", "文件清理");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
