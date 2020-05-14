package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

/**
 * 权限列表页面
 */
public class PermissionDetailActivity extends Activity {

//    @BindView(R.id.iv_back)
//    ImageView iv_back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new View(this));
//        initView();
    }

    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
