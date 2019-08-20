package com.xiaoniu.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.common_lib.R;

/**
 * Created by wangbaozhong on 2017/5/25.
 */

public class SimpleWebActivity extends BaseActivity {
    public WebView mWebView;
    public ProgressBar mProgressBar;
    public static final String KEY_URL = "url";//网页url
    public static final String KEY_TITLE = "title";//标题内容
    public String url;
    public String title;

    public static void startActivity(Activity context, String url, String title) {
        if (context != null) {
            Intent intent = new Intent(context, SimpleWebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, url);
            bundle.putString(KEY_TITLE, title);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_simple_web;
    }

    @Override
    protected void initVariable() {
        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString(KEY_URL);
            title = getIntent().getExtras().getString(KEY_TITLE);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        setTitle(title);
        setLeftButton(R.drawable.back_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void loadData() {
        setWebSettings();
        setWebViewClient();
        setWebChromeClient();
        loadUrl(url);
    }

    private void setWebSettings() {
        WebSettings settings = mWebView.getSettings();
        /*设置支持Javascript交互*/
        settings.setJavaScriptEnabled(true);

        /*设置自适应屏幕，两者合用*/
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        /*缩放操作*/
        settings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        /*缓存模式
        * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        * LOAD_CACHE_ONLY: 只读取本地缓存数据
        * LOAD_NO_CACHE:只从网络获取数据.
        * LOAD_CACHE_ELSE_NETWORK：没网，则从本地获取，即离线加载。
        * */
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        settings.setDatabaseEnabled(true);   //开启 database storage API 功能
        settings.setAppCacheEnabled(true);//开启 Application Caches 功能

        /*其他设置*/
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //页面自适应手机屏幕
        settings.setAllowFileAccess(true); //设置可以访问本地html文件
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 设置允许JS弹窗
//        settings.setUserAgentString("");
    }

    private void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //在WebView中打开
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                /*加载出错界面，比如404 view.loadUrl("file:///android_assets/error_handle.html");*/
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
               /* webView默认是不处理https请求的，页面显示空白，需要进行如下设置*/
                handler.proceed();  //表示等待证书响应
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }
        });

    }

    /*辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等*/
    private void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            /*是否拦截JS的提示框，默认false不拦截(同onJsAlert，onJsConfirm)*/
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
               /* 可用于java和js交互场景，可以根据需要定制协议，是难点*/
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            /*获取Web页中的标题*/
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(SimpleWebActivity.this.title)) {
                    setTitle(title);
                }
            }

            /*获得网页的加载进度并显示*/
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    /*JS通过WebView调用 Android 代码
    * 将Java对象映射为JS对象,在JS里可以使用该对象
    * object：java对象，name：对象名称
    * 4.2以下版本有漏洞，请使用onJsPrompt拦截提示框的方式进行交互
    * */
    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String name) {
        mWebView.addJavascriptInterface(object, name);
    }

    /**
     * 加载网页,Url可以是网络地址：http://xxxx,
     * 或者是本地assets目录html文件：file:///android_asset/xxx.html
     * 加载手机本地的html页面:content://xxx/sdcard/test.html
     */
    public void loadUrl(final String url) {
        mWebView.loadUrl(url);
    }

    /*清除缓存数据*/
    public void clearCache() {
        //清除网页访问留下的缓存,针对整个应用程序的webview
        mWebView.clearCache(true);

        //清除当前webview访问的历史记录
        mWebView.clearHistory();

        //仅仅清除自动完成填充的表单数据
        mWebView.clearFormData();
    }

    /**
     * 允许webview 内部返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            clearCache();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

    }
}
