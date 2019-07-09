package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneThinResultPresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 手机瘦身结果页
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinResultActivity extends BaseActivity<PhoneThinResultPresenter> {

    @BindView(R.id.txt_video_size)
    TextView mTxtVideoSize;
    @BindView(R.id.txt_space_size)
    TextView mTxtSpaceSize;

    @BindView(R.id.txt_install_size)
    TextView mTxtInstallSize;//安装应用大小
    @BindView(R.id.txt_soft_size)
    TextView mTxtSoftSize;//软件大小
    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_thin_result;
    }

    @Override
    protected void initView() {
        mTxtVideoSize.setText(FileSizeUtils.formatFileSize(mPresenter.getVideoTotalSize()));

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            String size=bundle.getString(PhoneThinActivity.PARAMS_SPACE_SIZE_AVAILABLE,"0");
            mTxtSpaceSize.setText(size);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.scanData();
    }

    @OnClick({R.id.img_back, R.id.ll_video, R.id.ll_soft})
    public void onViewClick(View view) {
        int ids = view.getId();
        if (ids == R.id.img_back) {
            finish();
        } else if (ids == R.id.ll_video) {
            startActivity(new Intent(this, CleanVideoManageActivity.class));
        } else if (ids == R.id.ll_soft) {
            startActivity(new Intent(this,SoftManageActivity.class));
        }
    }

    /**
     * 更新软件数据
     * @param size 安装应用大小
     * @param totalSize  应用总大小
     */
    public void updateData(int size,long totalSize){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=mTxtInstallSize){
                    mTxtInstallSize.setText(String.format("已安装%s款",size));
                }
                if(null!=mTxtSoftSize){
                    mTxtSoftSize.setText(FileSizeUtils.formatFileSize(totalSize));
                }

            }
        });
    }
}
