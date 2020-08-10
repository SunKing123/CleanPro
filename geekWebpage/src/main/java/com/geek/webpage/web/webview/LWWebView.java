package com.geek.webpage.web.webview;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.geek.webpage.utils.WebPageConstants;
import com.geek.webpage.web.WebViewListener;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/6 13:18
 */
public class LWWebView extends WebView implements DownloadListener {

    private WebViewListener mWebViewListener;

    public LWWebView(Context context) {
        super(getFixedContext(context));
        initDatas();
    }

    public LWWebView(Context context,WebViewListener webViewListener) {
        super(getFixedContext(context));
        initDatas();
    }

    public LWWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
        initDatas();
    }

    public LWWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
        initDatas();
    }

    public LWWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(getFixedContext(context), attrs, defStyleAttr, privateBrowsing);
        initDatas();
    }

    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.createConfigurationContext(new Configuration());
        } else {
            return context;
        }
    }
    private void initDatas(){
        //setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        WebSettings mWebViewSettings = getSettings();
        mWebViewSettings.setAppCacheEnabled(true);
        mWebViewSettings.setAllowFileAccess(true);

        //设置UA
        String ua = mWebViewSettings.getUserAgentString();
        mWebViewSettings.setUserAgentString(ua + " " + WebPageConstants.UA); // 设置ua
        // 网页内容的宽度是否可大于WebView控件的宽度
        mWebViewSettings.setLoadWithOverviewMode(false);
        // 保存表单数据
        //mWebViewSettings.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        mWebViewSettings.setSupportZoom(true);
        mWebViewSettings.setBuiltInZoomControls(true);
        mWebViewSettings.setDisplayZoomControls(false);
        // 启动应用缓存
        mWebViewSettings.setAppCacheEnabled(true);
        // 设置缓存模式
        mWebViewSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 开启 DOM storage API 功能
        mWebViewSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        mWebViewSettings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebViewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 告诉WebView启用JavaScript执行。默认的是false。
        mWebViewSettings.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        //mWebViewSettings.setBlockNetworkImage(false);
        // 排版适应屏幕
        mWebViewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // WebView是否支持多个窗口。
        mWebViewSettings.setSupportMultipleWindows(true);
        mWebViewSettings.setTextZoom(100);

        setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速

        setDownloadListener(this);

    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

}