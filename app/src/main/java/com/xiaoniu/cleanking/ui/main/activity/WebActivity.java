package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.hellogeek.permission.statusbarcompat.StatusBarCompat;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ui.main.activity
 * @ClassName: WebActivity
 * @Description: webView
 * @Author: LiDing
 * @CreateDate: 2020/5/22 15:18
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/22 15:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class WebActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.loadUrl(getIntent().getData().toString());
        StatusBarCompat.translucentStatusBarForImage(this, false, true);
    }
}
