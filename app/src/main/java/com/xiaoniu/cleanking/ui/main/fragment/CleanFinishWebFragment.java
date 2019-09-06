package com.xiaoniu.cleanking.ui.main.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.utils.JavaInterface;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.NestedScrollWebView;
import com.xiaoniu.common.base.BaseFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CleanFinishWebFragment extends BaseFragment {

    private NestedScrollWebView mWebView;
    private boolean isError = false;
    private JavaInterface javaInterface;
    private LinearLayout mLayoutNotNet;

    public static CleanFinishWebFragment getInstance(){
        return new CleanFinishWebFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initVariable(Bundle arguments) {

    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        mWebView = contentView.findViewById(R.id.web_view);
        mLayoutNotNet = contentView.findViewById(R.id.layout_not_net);
        initWebView();
    }

    @Override
    protected void setListener() {
        mLayoutNotNet.setOnClickListener(v -> mWebView.loadUrl(PreferenceUtil.getWebViewUrl()));
    }

    @Override
    protected void loadData() {

    }

    public void initWebView() {
        javaInterface = new JavaInterface(getActivity(), mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        mWebView.loadUrl(PreferenceUtil.getWebViewUrl());
        mWebView.addJavascriptInterface(javaInterface, "cleanPage");
        javaInterface.setListener(() -> {

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //TODO 显示加载框
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //TODO 隐藏加载框
//                cancelLoadingDialog();
                if (!isError) {
                    if (mLayoutNotNet != null) {
                        mLayoutNotNet.setVisibility(GONE);
                    }

                    if (mWebView != null) {
                        mWebView.setVisibility(SPUtil.isInAudit() ? GONE : VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                //TODO 显示无网络UI
                if (mLayoutNotNet != null) {
                    mLayoutNotNet.setVisibility(VISIBLE);
                }
                if (mWebView != null) {
                    mWebView.setVisibility(GONE);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

}
