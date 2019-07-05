package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.presenter.PhoneAccessPresenter;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import java.util.ArrayList;

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

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                mPresenter.getAccessList();
            }
        });

    }

    public void getAccessList(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += firstJunkInfo.getTotalSize();
        Log.e("totalSizes",""+totalSizes);
    }

    @Override
    public void netError() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
