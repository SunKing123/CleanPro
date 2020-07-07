package com.xiaoniu.cleanking.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.common.utils.ToastUtils;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/3
 * Describe:公共的webview，在xml中实现即可，自带加载动画
 * 需要实现
 * mAgentWeb.getWebLifeCycle().onResume();
 * mAgentWeb.getWebLifeCycle().onPause();
 * mAgentWeb.getWebLifeCycle().onDestroy();
 */
public class CustomWebViewLayout extends FrameLayout {
    private Context mContext;

    public CustomWebViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    AgentWeb.PreAgentWeb mAgentWeb;
    AnimationsContainer.FrameseAnim animaDra;
    ImageView load_iv;
    FrameLayout webFragment;

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_webview, null, false);
        addView(view);
        webFragment = view.findViewById(R.id.webFragment);
        load_iv = view.findViewById(R.id.load_iv);
        initWebView();
        animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(load_iv);
    }

    private void initWebView() {
        mAgentWeb = AgentWeb.with((Activity) mContext)
                .setAgentWebParent(webFragment, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
//                .useDefaultIndicator()
                .closeIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .createAgentWeb()
                .ready();
        animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(load_iv);
    }

    public AgentWeb getAgentWeb() {
        if (mAgentWeb == null) {
            initWebView();
        }
        return mAgentWeb.get();
    }

    public void loadUrl(String url) {
        if (mAgentWeb == null) {
            initWebView();
        }
        mAgentWeb.go(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (animaDra != null) {
                load_iv.setVisibility(VISIBLE);
                animaDra.start();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (animaDra != null) {
                load_iv.setVisibility(GONE);
                animaDra.stop();
            }
//            mAgentWeb.getIndicatorController().offerIndicator().hide();//失效
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("alipays://") || url.contains("weixin://") || url.contains("alipayqr://")) {
                try {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.setData(Uri.parse(url));
                    view.getContext().startActivity(it);
                } catch (Exception e) {
                    ToastUtils.showShort(url.contains("weixin://") ? "您未安装微信" : "您未安装支付宝");
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };

}
