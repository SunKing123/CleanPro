package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneThinResultPresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

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
}
