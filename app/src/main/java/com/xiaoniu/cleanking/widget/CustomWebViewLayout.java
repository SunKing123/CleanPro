package com.xiaoniu.cleanking.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.ToastUtils;

import static com.jess.arms.utils.ArmsUtils.startActivity;

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
//        initWebView();
        animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(load_iv);
    }

    private void initWebView() {
//        mAgentWeb = AgentWeb.with((Activity) mContext)
//                .setAgentWebParent(webFragment, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
////                .closeIndicator()
//                .useDefaultIndicator()
//                .setMainFrameErrorView(R.layout.common_view_no_network, R.id.no_network_tv)
//                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
//                .setWebViewClient(new YuLeWebViewClient((Activity) mContext, load_iv))
//                .setWebChromeClient(mWebChromeClient)
//                .addJavascriptInterface("native", new JsInterface())
//                .addJavascriptInterface("android", new SdkJsInterface())
//                .createAgentWeb()
//                .ready();
        mAgentWeb = AgentWeb.with((Activity) mContext)
                .setAgentWebParent(webFragment, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
//                .closeIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .addJavascriptInterface("native", new JsInterface())
                .addJavascriptInterface("android", new SdkJsInterface())
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .createAgentWeb()
                .ready();
        mAgentWeb.get().getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
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
//        getAgentWeb().getWebCreator().getWebView().loadUrl(url);
        mAgentWeb.go(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!loadingStatus) {
                if (animaDra != null) {
                    load_iv.setVisibility(VISIBLE);
                    animaDra.start();
                }
            }
            loadingStatus = true;
            Log.e("snow", "======onPageStarted=====");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (loadingStatus) {
                if (animaDra != null) {
                    load_iv.setVisibility(GONE);
                    animaDra.stop();
                }
            }
            Log.e("snow", "==onPageFinished====onPageFinished=====");
            loadingStatus = false;
//            mAgentWeb.getIndicatorController().offerIndicator().hide();//失效
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
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
    private boolean loadingStatus;

    public class JsInterface {
        @JavascriptInterface
        public String getXnData() {
            return AndroidUtil.getXnData();
        }

        /**
         * 路由
         * 身体数据  /usercenter/BodyDataActivity
         */
        @JavascriptInterface
        public void toPage(String routeString) {
            try {
                if (TextUtils.isEmpty(routeString)) {
                    return;
                }
                ARouter.getInstance().build(routeString).navigation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取登陆状态
         */
        @JavascriptInterface
        public String getLogin() {
            if (UserHelper.init().isWxLogin()) {
                return "2";
            }
            if (UserHelper.init().isLogin()) {
                return "1";
            }
            return "0";
        }
    }

    public WebView getWebView() {
        return mAgentWeb.get().getWebCreator().getWebView();
    }

    /**
     * 闲玩H5交互的类
     */
    public class SdkJsInterface {

        @JavascriptInterface
        public void CheckInstall(String packageName) {
            getWebView().post(new Runnable() {
                @Override
                public void run() {
                    String js = "javascript:CheckInstall_Return(" + (AndroidUtil.isAppInstalled(packageName) ? "1)" : "0)");
                    getWebView().loadUrl(js);
                }
            });
        }

        @JavascriptInterface
        public void OpenAPP(final String packageName) {
            // 根据包名,实现启动对应App功能
            if (AndroidUtil.isAppInstalled(packageName)) {
                PackageManager packageManager = getContext().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                startActivity(intent);
            }
        }

        @JavascriptInterface
        public void InstallAPP(final String url) {
            // 根据下载地址url,实现下载并提示安装功能
//            MyToaste.getInstance(AppApplication.getInstance()).toastShort("下载安装App");
//            XianWanDownLoadManager.getInstance().downloadApp(getWebView(), url, getActivity());
        }

        @JavascriptInterface
        public void Browser(final String url) {
            //根据下载地址url,实现在浏览器中打开url功能
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
