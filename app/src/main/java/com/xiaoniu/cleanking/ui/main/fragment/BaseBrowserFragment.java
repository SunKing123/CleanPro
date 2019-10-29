package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.just.agentweb.AgentWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;

import butterknife.BindView;

/**
 * Desc:
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/4
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 * @author anyabo
 */
public class BaseBrowserFragment extends SimpleFragment {
    @BindView(R.id.web_container)
    RelativeLayout mRootView;

    private AgentWeb mAgentWeb;

    public static BaseBrowserFragment newInstance(String jumpUrl){
        Bundle bundle = new Bundle();
        bundle.putString("url",jumpUrl);
        BaseBrowserFragment xBrowserFragment = new BaseBrowserFragment();
        xBrowserFragment.setArguments(bundle);
        return xBrowserFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_browser;
    }

    @Override
    protected void initView() {
        String url = getArguments().getString("url");
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRootView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.web_error_layout, R.id.layout_not_net)
                .setWebChromeClient(new CustomWebChromeClient())
//                .setWebViewClient(new MyBaseWebViewClient(getActivity()))
                .createAgentWeb()
                .ready()
                .go(url);
    }


    private class CustomWebChromeClient extends com.just.agentweb.WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (view != null && !TextUtils.isEmpty(view.getUrl())
                    && view.getUrl().contains(title)) {
                return;
            }
            if (getActivity() != null && mWebListener != null){
                mWebListener.onReceivedTitle(title);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            onResume();
        }else {
            onPause();
        }
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    public WebView getWebView(){
        return mAgentWeb.getWebCreator().getWebView();
    }

    public boolean canGoBack(){
        return mAgentWeb.back();
    }

    private WebListener mWebListener;

    public void setWebListener(WebListener webListener) {
        mWebListener = webListener;
    }

    public interface WebListener{
        void onReceivedTitle(String title);
    }

}
