//package com.xiaoniu.cleanking.ui.main.activity;
//
//import android.text.TextUtils;
//import android.view.View;
//import android.webkit.WebView;
//
//
//import com.xiaoniu.cleanking.R;
//import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
//import com.xiaoniu.cleanking.base.BaseActivity;
//import com.xiaoniu.cleanking.ui.main.fragment.BaseBrowserFragment;
//
//import butterknife.BindView;
//
///**
// * Desc:H5页面
// * <p>
// * Author: AnYaBo
// * Date: 2019/7/4
// * Copyright: Copyright (c) 2016-2020
// * Company: @小牛科技
// * Update Comments:
// */
//public class BrowserActivity extends BaseActivity {
//
//    private String mJumpUrl;
//    private String mJumpUri;
//
////    @BindView(R.id.title_bar)
////    TitleBar mTitleBar;
//
//    private BaseBrowserFragment mBrowserFragment;
//
//
//    @Override
//    public void netError() {
//
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_browser;
//    }
//
//    @Override
//    protected void initView() {
//        mJumpUrl = getIntent().getStringExtra(ExtraConstant.EXTRA_URL);
//        mBrowserFragment = BaseBrowserFragment.newInstance(mJumpUrl);
//        initTitleOptions(mBrowserFragment);
//        getSupportFragmentManager().
//                beginTransaction().replace(R.id.browser_fragment_container,
//                mBrowserFragment).commitAllowingStateLoss();
//    }
//
//    private void initTitleOptions(BaseBrowserFragment xBrowserFragment){
//
//
//
//    }
//
//
//
//    @Override
//    public void onBackPressed() {
//        if (!mBrowserFragment.canGoBack()){
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public void inject(ActivityComponent activityComponent) {
//        activityComponent.inject(this);
//    }
//}
