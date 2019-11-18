package com.geek.webpage.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JsBridge;
import com.geek.webpage.web.coolindicator.CoolIndicator;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/6 13:28
 */
public class LWWebviewClient extends WebViewClient {
    private Context mContext;
    private WebViewListener mWebViewListener;
    private JsBridge mJsBridge;
    private CoolIndicator mCoolIndicator;

    public LWWebviewClient(Context context, WebViewListener webViewListener, JsBridge jsBridge, CoolIndicator coolIndicator) {
        this.mContext = context;
        this.mWebViewListener = webViewListener;
        this.mJsBridge = jsBridge;
        this.mCoolIndicator = coolIndicator;
    }

    private Uri stringToUrl(String string) {
        try {
            return Uri.parse(string);
        } catch (Exception e) {
            return null;
        }
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.mWebViewListener = webViewListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("XiLei", "url=" + url);
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") ||
                url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String
            description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (mWebViewListener != null) {
            mWebViewListener.onError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        super.onReceivedSslError(view, handler, error);
        handler.proceed(); // 接受所有证书
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mCoolIndicator != null) {
            mCoolIndicator.start();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mWebViewListener != null) {
            mWebViewListener.onFinish();
        }
        if (mJsBridge != null) {
            mJsBridge.injectJs(view);
        }
        if (mCoolIndicator != null) {
            mCoolIndicator.complete();
        }
    }
}
