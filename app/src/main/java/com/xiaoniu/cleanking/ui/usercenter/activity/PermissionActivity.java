package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.BindView;

/**
 * 权限列表页面
 */
public class PermissionActivity extends SimpleActivity {

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @Override
    public int getLayoutId() {
        return R.layout.activity_permision;
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
