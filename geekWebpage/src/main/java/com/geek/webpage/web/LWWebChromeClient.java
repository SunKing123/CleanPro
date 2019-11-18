package com.geek.webpage.web;

import android.content.Context;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.apkfuns.jsbridge.JsBridge;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/6 13:32
 */
public class LWWebChromeClient extends WebChromeClient {
    private Context mContext;
    private WebViewListener mWebViewListener;
    private JsBridge mJsBridge;
    public LWWebChromeClient(Context context,WebViewListener webViewListener,JsBridge jsBridge){
        this.mContext = context;
        this.mWebViewListener = webViewListener;
        this.mJsBridge = jsBridge;
    }

    public void setWebViewListener(WebViewListener webViewListener){
        this.mWebViewListener = webViewListener;
    }
    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, JsPromptResult result) {
        if (mJsBridge != null && mJsBridge.callJsPrompt(message, result)) {
            return true;
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        //LogUtils.d(JsBridge.TAG, consoleMessage.message());
        return true;
    }
    public void onProgressChanged(WebView view, int newProgress) {
        if (mWebViewListener != null) {
            mWebViewListener.onLoad(view, newProgress);
            if (newProgress == 100) {
                mWebViewListener.onFinish();
            }
        }
    }
    public void onReceivedTitle(WebView view, String title) {
        if (mWebViewListener != null) {
            mWebViewListener.onSetTitle(view, title);
        }
    }
}
