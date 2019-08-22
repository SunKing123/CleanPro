package com.xiaoniu.cleanking.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.common.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * 超强省电中...
 */
public class PhoneSuperSavingNowActivity extends BaseActivity implements View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private ImageView mBack;
    private TextView mBtnCancel;
    private TextView mTvNum;
    private RelativeLayout mRlResult;
    private RelativeLayout mRlResultTop;
    private NestedScrollWebView mNestedScrollWebView;
    private JavaInterface javaInterface;
    private LinearLayout mLayoutNotNet;
    private LottieAnimationView mLottieAnimationStartView;
    private boolean isError = false;

    private MyHandler mHandler = new MyHandler(this);

    private int num = 31;
    class MyHandler extends Handler {
        WeakReference<Activity> mActivity;
        public MyHandler(Activity con){
            this.mActivity = new WeakReference<>(con);
        }
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1 ){
                mRlResult.setVisibility(View.GONE);
                mAppBarLayout.setExpanded(true);
                mRlResultTop.setVisibility(View.VISIBLE);
                mNestedScrollWebView.setVisibility(View.VISIBLE);
            }else if(msg.what == 2){
                num --;
                mTvNum.setText(String.valueOf(num));
                if (num > 0) {
                    sendEmptyMessageDelayed(2, 90);
                }else {
                    sendEmptyMessageDelayed(1, 800);
                }
            }
        }
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_phone_super_saving_now;
    }

    @Override
    protected void initVariable(Intent intent) {
        hideToolBar();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAppBarLayout = findViewById(R.id.app_power_saving_bar_layout);
        mBack = findViewById(R.id.iv_back);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mTvNum = findViewById(R.id.tv_num);
        mRlResult = findViewById(R.id.rl_result);
        mRlResultTop = findViewById(R.id.rl_result_top);
        mNestedScrollWebView = findViewById(R.id.web_view);
        mLayoutNotNet = findViewById(R.id.layout_not_net);
        mLottieAnimationStartView = findViewById(R.id.view_lottie_super_saving_sleep);
        initWebView();
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mLayoutNotNet.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        showStartAnim();
        mHandler.sendEmptyMessageDelayed(1,5000);
        mHandler.sendEmptyMessageDelayed(2, 60);
    }

    /**
     * 正在休眠应用减少耗电...
     */
    private void showStartAnim() {
        mLottieAnimationStartView.useHardwareAcceleration();
        mLottieAnimationStartView.useHardwareAcceleration();
        mLottieAnimationStartView.setImageAssetsFolder("images");
        mLottieAnimationStartView.setAnimation("data_super_power_saving.json");
        mLottieAnimationStartView.playAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.layout_not_net:
                mNestedScrollWebView.loadUrl(PreferenceUtil.getWebViewUrl());
                break;
        }
    }

    public void initWebView() {
        javaInterface = new JavaInterface(this, mNestedScrollWebView);
        WebSettings settings = mNestedScrollWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mNestedScrollWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mNestedScrollWebView.addJavascriptInterface(javaInterface, "cleanPage");
        javaInterface.setListener(() -> {

        });
        mNestedScrollWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                cancelLoadingDialog();
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(View.GONE);
                    }
                    if (mNestedScrollWebView != null) {
//                        mNestedScrollWebView.setVisibility(SPUtil.isInAudit() ? View.GONE : View.VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                if (mLayoutNotNet != null) {
                    mLayoutNotNet.setVisibility(View.VISIBLE);
                }
                if (mNestedScrollWebView != null) {
                    mNestedScrollWebView.setVisibility(View.GONE);
                }
            }
        });

        mNestedScrollWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }
}
