package com.geek.webpage.web;

import android.webkit.WebView;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/6 13:17
 */
public interface  WebViewListener {
    void onLoad(WebView view, int newProgress);
    void onFinish();
    void onSetTitle(WebView view, String title);
    void onError(WebView view, int errorCode,
                 String description, String failingUrl);
}
