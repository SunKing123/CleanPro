package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneThinPresenter;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机瘦身
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinActivity extends BaseActivity<PhoneThinPresenter> {

    //扫描路径
    private String mPath = Environment.getExternalStorageDirectory().getPath();

    //可用空间大小
    public static final String PARAMS_SPACE_SIZE_AVAILABLE = "params_space_size_available";

    //占内存百分比
    @BindView(R.id.txt_space_size)
    TextView mTxtSpaceSize;
    //
    @BindView(R.id.txt_scan_content)
    TextView mTxtScanContent;
    @BindView(R.id.img_progress)
    ImageView mImgProgress;
    @BindView(R.id.img_progress_system)
    ImageView mImgProgressSystem;

    @BindView(R.id.progress_video)
    ProgressBar mProgressVideo;
    @BindView(R.id.progress_system)
    ProgressBar mProgressSystem;
    private long mTotalSize;

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_thin;
    }

    @Override
    protected void initView() {
        mTotalSize = mPresenter.queryStorageSize(mPath);

        mPresenter.scanFile(mPath);
    }

    private long mCurrentTime;

    public void updateText(String path, long size) {

        if (!TextUtils.isEmpty(path)) {

            if (System.currentTimeMillis() - mCurrentTime > 50) {
                if (null != mTxtScanContent) {
                    mTxtScanContent.setText(String.format("正在扫描:%s", path));
                }
                if (null != mTxtSpaceSize) {
                    mCurrentTime = System.currentTimeMillis();
                    mTxtSpaceSize.setText(mPresenter.accuracy(size, mTotalSize, 0));
                }
            }

        }

    }


    @OnClick({R.id.img_back})
    public void onViewClick(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        }
    }

    /**
     * 扫描完成
     */
    public void onComplete() {
        if (null != mProgressVideo) {
            mProgressVideo.setVisibility(View.GONE);
        }
        if (null != mProgressSystem) {
            mProgressSystem.setVisibility(View.GONE);
        }
        if (null != mImgProgress) {
            mImgProgress.setVisibility(View.VISIBLE);
        }
        if (null != mImgProgressSystem) {
            mImgProgressSystem.setVisibility(View.VISIBLE);
        }
        if (null != mTxtScanContent) {
            mTxtScanContent.setText("扫描完成");
        }
        if (null != mImgProgressSystem) {
            mImgProgressSystem.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PhoneThinActivity.this, PhoneThinResultActivity.class);
                    long fileTotalSize = mPresenter.getFileSize();
                    intent.putExtra(PARAMS_SPACE_SIZE_AVAILABLE, mPresenter.accuracy(fileTotalSize, mTotalSize, 0));
                    startActivity(intent);
                    finish();
                }
            }, 500);

        }
    }
}
