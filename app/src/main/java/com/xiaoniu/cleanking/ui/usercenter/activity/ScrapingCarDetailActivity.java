package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.jess.arms.utils.LogUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.ui.newclean.bean.H5EventBean;
import com.xiaoniu.cleanking.ui.newclean.util.ScrapingCardDataUtils;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statusview.StatusView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/7
 * Describe:刮刮乐详情页面
 */
public class ScrapingCarDetailActivity extends BaseActivity {
    String url;
    @BindView(R.id.web_container)
    RelativeLayout mRootView;
    @BindView(R.id.load_iv)
    ImageView loadIv;
    @BindView(R.id.web_page_no_network)
    StatusView webPageNoNetwork;
    AgentWeb mAgentWeb;
    //返回键 0 不可返回  1 可返回
    private String backable = "1";
    AnimationsContainer.FrameseAnim animaDra;

    @Override
    public void inject(ActivityComponent activityComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scraping_car_detail;
    }

    @Override
    protected void initView() {
        StatusBarCompat.translucentStatusBarForImage(this, true, true);
//        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            url = extras.getString(Constant.URL);
        }
        LogUtils.debugInfo("跳转刮刮卡详情===获取到的===" + url);
        animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(loadIv);
        initWebView();
//        new ScratchCardAvdPresenter(this).preLoadAd();
    }

    private void initWebView() {
        View errorView = LayoutInflater.from(this).inflate(R.layout.web_error_layout, null, false);
        errorView.findViewById(R.id.sv_error_retry).setOnClickListener(v -> {
            if (!AndroidUtil.isFastDoubleClick()) {
                getWebView().loadUrl(url);
            }
        });
        errorView.findViewById(R.id.text_go_to_setting).setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        });
        AgentWebConfig.clearDiskCache(this);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRootView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(errorView)
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .addJavascriptInterface("native", new JsInterface())
//                .addJavascriptInterface("android", SdkJsInterface())
                .createAgentWeb()
                .ready()
                .go(url);
    }


    @Override
    public void onBackPressed() {
        if ("0".equals(backable)) {
            H5EventBean h5EventBean = new H5EventBean();
            h5EventBean.setEventCode("0");
            if (getWebView() != null) {
                getWebView().loadUrl("javascript:eventCallBack('" + new Gson().toJson(h5EventBean) + "')");
            }
        } else {
            super.onBackPressed();
        }
    }

    public WebView getWebView() {
        if (mAgentWeb == null || mAgentWeb.getWebCreator() == null) {
            initView();
        }
        return mAgentWeb.getWebCreator().getWebView();
    }
    @Override
    protected void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }
    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        EventBus.getDefault().post("refreshGuaGuaLeH5");
    }

    public class JsInterface {
        @JavascriptInterface
        public String getXnData() {
            LogUtils.debugInfo("snow","调用getXnData");
            return AndroidUtil.getXnData();
        }

        /**
         * 返回键 0 不可返回  1 可返回
         * 只支持二级页面，一级页面不要调用
         *
         * @return
         */
        @JavascriptInterface
        public void setBackable(String type) {
            backable = type;
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

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            LogUtils.debugInfo("跳转刮刮卡详情===onPageStarted==");
            showLoadAnim();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (ScrapingCardDataUtils.init().parseUrl(ScrapingCarDetailActivity.this, url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//              super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtils.debugInfo("跳转刮刮卡详情===onPageFinished==");
            if (animaDra != null && loadIv != null) {
                animaDra.stop();
                loadIv.setVisibility(View.GONE);
            }
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
            if (newProgress == 100) {
                if (animaDra != null && loadIv != null) {
                    animaDra.stop();
                    loadIv.setVisibility(View.GONE);
                }
            }
            LogUtils.debugInfo("==newProgress==========" + newProgress);
        }
    };

    //加载动画
    private void showLoadAnim() {
        if (loadIv != null && animaDra != null) {
            loadIv.setVisibility(View.VISIBLE);
            animaDra.start();
        }
    }
}
