package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.just.agentweb.AgentWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.constant.RouteConstants;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页tab H5页面（商城页、生活页）
 * Created on 2018/3/21.
 */
@Route(path = RouteConstants.NEWS_LOAD_ACTIVITY)
public class NewsLoadActivity extends SimpleActivity implements MainActivity.OnKeyBackListener {
    @BindView(R.id.web_container)
    RelativeLayout mRootView;
    @BindView(R.id.tv_title)
    TextView mTextTitle;
    private String url = ApiModule.SHOPPING_MALL;
    private boolean isFirst = true;
    private boolean isFirstPause = true;

    private AgentWeb mAgentWeb;
    private boolean mCanGoBack = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_load;
    }

    @Override
    protected void initView() {
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            url = arguments.getString(Constant.URL, ApiModule.SHOPPING_MALL);
        }
        initWebView();
    }

    private void initWebView() {

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRootView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.web_error_layout, R.id.layout_not_net)
                .setWebChromeClient(new CustomWebChromeClient())
                .createAgentWeb()
                .ready()
                .go(url);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("cleanPage", new Javascript());
        mAgentWeb.getJsInterfaceHolder().addJavaObject("sharePage", new Javascript());
    }

    private class CustomWebChromeClient extends com.just.agentweb.WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (view != null && !TextUtils.isEmpty(view.getUrl())
                    && view.getUrl().contains(title)) {
                return;
            }
            if (!isDestroyed()) {
                mCanGoBack = !"清理极速管家商城".equals(title);
            }
            if (mTextTitle != null) {
                mTextTitle.setText("新闻热点");
            }
        }
    }

    public WebView getWebView() {
        return mAgentWeb.getWebCreator().getWebView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAgentWeb.getWebLifeCycle().onPause();
        NiuDataAPI.onPageEnd("information_iew_page", "信息页面");
    }

    @Override
    public void onResume() {
        super.onResume();
        mAgentWeb.getWebLifeCycle().onResume();
        NiuDataAPI.onPageStart("information_iew_page", "信息页面");
    }

    private long firstTime;

    @Override
    public void onKeyBack() {
        if (getWebView().canGoBack() && mCanGoBack) {
            getWebView().goBack();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb.back()) {
            getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    public class Javascript {

        @JavascriptInterface
        public void toOtherPage(String url) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, url);
            bundle.putString(Constant.Title, "");
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);
        }

        @JavascriptInterface
        public void onTitleClick(String id, String name) {
            StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", "home_page", "information_page", id, name);
        }


        @JavascriptInterface
        public void canGoBack(boolean canGoBack) {
            mCanGoBack = canGoBack;
        }
    }

    @OnClick(R.id.back)
    public void onBackClick() {
        if (mAgentWeb.back()) {
            getWebView().goBack();
        } else {
            finish();
        }
    }

    private void refresh() {
        getWebView().loadUrl(url);
    }

    private void showToast(String msg) {
        ToastUtils.showShort(msg);
    }
}
