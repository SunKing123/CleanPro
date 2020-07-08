package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.ui.main.fragment.BaseBrowserFragment;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/7
 * Describe:刮刮乐二级页面
 */
public class BrowserActivity extends BaseActivity {
    private BaseBrowserFragment mBrowserFragment;

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser_webview;
    }

    @Override
    protected void initView() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_fff7f8fa), true);
//        } else {
//            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_fff7f8fa), false);
//        }
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            String url = extras.getString(Constant.URL);
            mBrowserFragment = BaseBrowserFragment.newInstance(url);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    mBrowserFragment).commitAllowingStateLoss();
        }
    }
}
