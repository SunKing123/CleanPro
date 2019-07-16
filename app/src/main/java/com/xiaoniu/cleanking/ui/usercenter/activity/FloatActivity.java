package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.os.Build;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

/**
 */
public class FloatActivity extends SimpleActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_float;
    }


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
    }

}
