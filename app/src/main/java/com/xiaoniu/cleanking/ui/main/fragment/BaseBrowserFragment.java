package com.xiaoniu.cleanking.ui.main.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.geek.webpage.utils.NetkUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.Parameters;
import com.xiaoniu.cleanking.scheme.utils.UrlUtils;
import com.xiaoniu.cleanking.ui.main.model.GoldCoinDoubleModel;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.newclean.interfice.RequestResultListener;
import com.xiaoniu.cleanking.ui.newclean.presenter.ScratchCardAvdPresenter;
import com.xiaoniu.cleanking.ui.newclean.util.MyBaseWebViewClient;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.user.ShanYanManager;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statusview.StatusView;
import com.xiaoniu.statusview.StatusViewBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * Desc:
 * <p>
 * Author: AnYaBo
 * Date: 2019/7/4
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update Comments:
 *
 * @author anyabo
 */
public class BaseBrowserFragment extends SimpleFragment {
    @BindView(R.id.web_container)
    RelativeLayout mRootView;
    @BindView(R.id.load_iv)
    ImageView loadIv;
    @BindView(R.id.web_page_no_network)
    StatusView webPageNoNetwork;

    private String current_page_id;

    public boolean isShow;
    private AgentWeb mAgentWeb;

    //返回键 0 不可返回  1 可返回
    private String backable = "1";
    ScratchCardAvdPresenter cardAvdPresenter;
    String videoRequestJsonParams;

    public static BaseBrowserFragment newInstance(String jumpUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("url", jumpUrl);
        BaseBrowserFragment xBrowserFragment = new BaseBrowserFragment();
        xBrowserFragment.setArguments(bundle);
        return xBrowserFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_browser;
    }

    MyBaseWebViewClient baseWebViewClient;
    String url;

    @Override
    protected void initView() {
        url = getArguments().getString("url");
        cardAvdPresenter = new ScratchCardAvdPresenter(mActivity);
        Parameters parameter = UrlUtils.getParamsFromUrl(url);
        current_page_id = parameter.getParameter(SchemeConstant.AD_CURRENT_PAGE_ID);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.web_error_layout, null, false);
        errorView.findViewById(R.id.sv_error_retry).setOnClickListener(v -> {
            if (!AndroidUtil.isFastDoubleClick()) {
                getWebView().loadUrl(url);
            }
        });
        errorView.findViewById(R.id.text_go_to_setting).setOnClickListener(v -> {
            getContext().startActivity(new Intent(Settings.ACTION_SETTINGS));
        });
        baseWebViewClient = new MyBaseWebViewClient(this, cardAvdPresenter, getActivity(), loadIv, webPageNoNetwork);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRootView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(errorView)
                .setWebChromeClient(new CustomWebChromeClient())
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .setWebViewClient(baseWebViewClient)
                .addJavascriptInterface("native", new JsInterface())
                .addJavascriptInterface("android", new SdkJsInterface())
                .createAgentWeb()
                .ready()
                .go(url);
        //播放视频完成的回调
        cardAvdPresenter.setOnVideoPlayedListener(() -> {

//            LogUtils.e("==========================================================getXnData() JsonParams start");
//            LogUtils.e("h5下发的JsonParams: " + videoRequestJsonParams);
//            LogUtils.e("==========================================================getXnData() JsonParams  end");
//            getWebView().loadUrl("javascript:videoCallBack(" + videoRequestJsonParams + ")");
//            mActivity.finish();
            guaguaDouble();
        });
//        netWorkAbout();
    }

    /**
     * 刮刮翻倍
     */
    private void guaguaDouble() {
        String id = "";
        if (!TextUtils.isEmpty(videoRequestJsonParams)) {
            try {
                JSONObject jsonObject = new JSONObject(videoRequestJsonParams);
                id = jsonObject.optString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int cardIndex = cardAvdPresenter.cardIndex;
        int coinCount = cardAvdPresenter.coinCount;
        RequestUserInfoUtil.guaGuaBubbleDoubleRequest(getContext(), id, new RequestResultListener() {
            @Override
            public void requestSuccess(String coin) {
                String adId = cardAvdPresenter.isOpenThree() ? cardAvdPresenter.getSecondAdvId(cardIndex) : "";
                int coinC = TextUtils.isEmpty(coin) ? coinCount : Integer.parseInt(coin);
                GoldCoinDoubleModel model = new GoldCoinDoubleModel(adId, coinC, cardIndex, Points.ScratchCard.SUCCESS_PAGE,0);
                GoldCoinSuccessActivity.Companion.start(getContext(), model);
                GoldCoinDialog.dismiss();
                StatisticsUtils.scratchCardClick(Points.ScratchCard.VIDEO_PAGE_CLOSE_CLICK_CODE, Points.ScratchCard.VIDEO_PAGE_CLOSE_CLICK_NAME, cardIndex, "", Points.ScratchCard.VIDEO_PAGE);
                mActivity.finish();
            }

            @Override
            public void requestFail() {

            }
        });
    }

    private void netWorkAbout() {
        webPageNoNetwork.config(new StatusViewBuilder.Builder()
                .setOnErrorRetryClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AndroidUtil.isFastDoubleClick()) {
                            return;
                        }
                        //错误页面重试点击
                        if (getWebView() != null && checkNetWork()) {
                            getWebView().loadUrl(url);
                            new Handler().postDelayed(() -> {//延迟显示布局，否则会有那个网络无法加载的页面
                                webPageNoNetwork.setVisibility(View.GONE);
                                mRootView.setVisibility(View.VISIBLE);
                                getWebView().setVisibility(View.VISIBLE);
                                //修改有时不显示加载内容，显示空白
                                getWebView().bringToFront();
                            }, 1500);
                        } else {
                            ToastUtils.showShort("网络连接异常，请检查网络设置");
                        }
                    }
                }).build());
        checkNetWork();
    }

    private boolean checkNetWork() {
        if (NetkUtils.isConnected(getContext())) {

            return true;
        } else if (webPageNoNetwork.getVisibility() != View.VISIBLE) {
            webPageNoNetwork.showErrorView();
            webPageNoNetwork.setVisibility(View.VISIBLE);
            return false;
        }
        return false;
    }

    public void setVideoCallBackParams(String jsonParams) {
        this.videoRequestJsonParams = jsonParams;
    }

    /**
     * 统一调用h5刷新方法
     */
    public void refreshWebView() {
        try {
            if (null != getWebView()) {
                getWebView().loadUrl("javascript:refresh()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CustomWebChromeClient extends com.just.agentweb.WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (view != null && !TextUtils.isEmpty(view.getUrl())
                    && view.getUrl().contains(title)) {
                return;
            }
            if (getActivity() != null && mWebListener != null) {
                mWebListener.onReceivedTitle(title);
            }
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isShow = true;
            onResume();
        } else {
            isShow = false;
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
        cardAvdPresenter.destroy();
        if (baseWebViewClient != null) {
            baseWebViewClient.destroy();
        }
        super.onDestroy();
    }

    public WebView getWebView() {
        return mAgentWeb.getWebCreator().getWebView();
    }

    public boolean canGoBack() {
        return mAgentWeb.back();
    }

    private WebListener mWebListener;

    public void setWebListener(WebListener webListener) {
        mWebListener = webListener;
    }

    public interface WebListener {
        void onReceivedTitle(String title);
    }

    /**
     * @param jsonEvent
     */
    public void eventCallBack(String jsonEvent) {
        if (getWebView() != null) {
            getWebView().loadUrl("javascript:eventCallBack('" + jsonEvent + "')");
        }
    }

    public String getBackable() {
        return backable;
    }

    public class JsInterface {
        @JavascriptInterface
        public String getXnData() {
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
         * 路由
         * 身体数据  /usercenter/BodyDataActivity
         */
        @JavascriptInterface
        public void toPage(String routeString) {
            try {
                Log.e("zhc", "路由" + routeString);
                if (TextUtils.isEmpty(routeString)) {
                    return;
                }
                ARouter.getInstance().build(routeString).navigation();
            } catch (Exception e) {
                Log.e("zhc", "路由错误");
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

        @JavascriptInterface
        public void walletSuccess() {//提现成功
            RequestUserInfoUtil.getUserCoinInfo();
        }

        @JavascriptInterface
        public void fastBindPhone() {//绑定手机号操作
            new Handler().post(() -> {
                ShanYanManager.oneBindingOption(getContext());
            });

        }
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
