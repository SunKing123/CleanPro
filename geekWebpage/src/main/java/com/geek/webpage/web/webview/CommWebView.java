package com.geek.webpage.web.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.apkfuns.jsbridge.JsBridge;
import com.geek.webpage.web.LWWebChromeClient;
import com.geek.webpage.web.LWWebviewClient;
import com.geek.webpage.web.WebViewListener;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/7 20:55
 */
public class CommWebView extends LinearLayout {
    /**
     * 是否可以返回上⼀一个⻚页⾯面，默认可以返回上⼀一个⻚页⾯面
     */
    private boolean isCanBack = true;
    /**
     * 当前⽹网⻚页的标题
     */
    private String webTitle = "";
    /**
     * 当前url
     */
    private String curWebUrl = "";
    /**
     * 回调器器
     */
    private WebViewListener webViewListener;
    /**
     * 采⽤用addview(webview)的⽅方式添加到线性布局，可以及时销毁webview
     */
    private LWWebView webview;
    private Context context;
    private boolean isTransparent = false;
    private JsBridge jsBridge;
    private LWWebviewClient mLWWebviewClient;
    private LWWebChromeClient mLWWebChromeClient;
    public CommWebView(Context context) {
        this(context, null);
    }
    public CommWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CommWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.jsBridge = JsBridge.loadModule();
        initConfig(context);
    }
    /**
     * 初始化参数配置
     *
     * @param context
     */
    private void initConfig(final Context context) {
        if(null == context)
            return;
        webview = new LWWebView(context);
        transparent();
        mLWWebviewClient = new LWWebviewClient(getContext(),webViewListener,jsBridge,null);
        mLWWebChromeClient = new LWWebChromeClient(getContext(),webViewListener,jsBridge);
        webview.setWebViewClient(mLWWebviewClient);
        webview.setWebChromeClient(mLWWebChromeClient);
        setVisibility(View.VISIBLE);
        requestFocus();//请求获取焦点，防⽌止view不不能打开输⼊入法问题
        requestFocusFromTouch();//请求获取焦点，防⽌止view不不能打开输⼊入法问题
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webview.setLayoutParams(params);
        addView(webview);
    }
    private void transparent() {
        if (isTransparent) {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setBackgroundColor(0);
            webview.setBackgroundColor(0);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
        transparent();
    }
    /**
     * 销毁当前的⻚页⾯面
     */
    public void onDestroy() {
        if (webview != null) {
            webview.removeAllViews();
            try {
                webview.destroy();
            } catch (Throwable t) {
            }
            webview = null;
        }
        curWebUrl = "";
    }
    /**
     * 开始回调
     *
     * @param webViewListener
     */
    public CommWebView setWebViewListener(WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
        if (mLWWebviewClient != null){
            mLWWebviewClient.setWebViewListener(webViewListener);
        }
        if (mLWWebChromeClient != null){
            mLWWebChromeClient.setWebViewListener(webViewListener);
        }
        loadWebUrl(curWebUrl);
        return this;
    }
    /**
     * 判断是否可以返回上⼀一个⻚页⾯面
     *
     * @return
     */
    public boolean isCanBack() {
        return isCanBack;
    }
    /**
     * 设置是否可以返回上⼀一个⻚页⾯面
     *
     * @param canBack
     */
    public CommWebView setCanBack(boolean canBack) {
        isCanBack = canBack;
        return this;
    }
    /**
     * 获取webview
     *
     * @return
     */
    public WebView getWebview() {
        return webview;
    }
    /**
     * 设置当前需要加载的url
     *
     * @param curWebUrl
     */
    public CommWebView setCurWebUrl(String curWebUrl) {
        this.curWebUrl = curWebUrl;
        return this;
    }
    /**
     * 加载⽹网⻚页
     *
     * @param url
     */
    public CommWebView loadWebUrl(String url) {
        curWebUrl = url;//记录当前的url
        webview.loadUrl(curWebUrl);//webview加载url
        return this;
    }
    /**
     * 判断是否可以返回上⼀一个⻚页⾯面
     *
     * @return
     */
    public boolean canGoBack() {
        return webview.canGoBack();
    }
    /**
     * 返回到上⼀一个⻚页⾯面
     */
    public void goBack() {
        webview.goBack();
    }
    /**
     * 刷新
     */
    public void refresh() {
        loadWebUrl(curWebUrl);
    }
    public void release(){
        if (jsBridge == null)
            return;
        jsBridge.release();
    }
}