package com.xiaoniu.cleanking.ui.newclean.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.launcher.ARouter;
import com.geek.webpage.utils.NetkUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.WebChromeClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.H5Urls;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.databinding.FragmentYuleBinding;
import com.xiaoniu.cleanking.ui.newclean.util.YuLeWebViewClient;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.statusview.StatusViewBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.xiaoniu.cleanking.utils.user.UserHelper.EXIT_SUCCESS;
import static com.xiaoniu.cleanking.utils.user.UserHelper.LOGIN_SUCCESS;

/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:娱乐刮刮乐页面==加载h5页面
 */
public class YuLeFragment extends SimpleFragment {
    FragmentYuleBinding mBinding;
    //返回键 0 不可返回  1 可返回
    private String backable = "1";
//    private String url = "http://192.168.85.61:9999/html/activitiesHtml/scratchCards/cardList.html";
//    private String url = "http://192.168.85.61:9999/html/activitiesHtml/scratchCards/scratch.html?id=27&rondaId=4&awardType=1&hitCode=1&num=1222222&remark=&cardType=12312312&goldSectionNum=16&actRdNum=02:00&needRefresh=1&currentPageId=scratch_card_activity_page";
//    private String url = "https://www.baidu.com/";

    AgentWeb mAgentWeb;

    public static YuLeFragment getInstance() {
        return new YuLeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_yule;
    }

    @Override
    protected void initView() {
        mBinding = DataBindingUtil.bind(getView());
        initWebView();
        EventBus.getDefault().register(this);
        mBinding.refreshUrl.setOnClickListener(view -> {
//            Intent intent = new Intent(getContext(), BrowserActivity.class);
//            intent.putExtra(Constant.URL,url);
//            startActivity(intent);
            mAgentWeb.getWebCreator().getWebView().loadUrl(H5Urls.SCRATCHCARDS_URL);
            Log.e("snow", "==" + AndroidUtil.getXnData());
        });
        netWorkAbout();
    }

    private void netWorkAbout() {
        mBinding.webPageNoNetwork.config(new StatusViewBuilder.Builder()
                .setOnErrorRetryClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AndroidUtil.isFastDoubleClick()) {
                            return;
                        }
                        //错误页面重试点击
                        if (getWebView() != null && checkNetWork()) {
                            getWebView().loadUrl(H5Urls.SCRATCHCARDS_URL);
                            new Handler().postDelayed(() -> {//延迟显示布局，否则会有那个网络无法加载的页面
                                mBinding.webPageNoNetwork.setVisibility(View.GONE);
                                mBinding.webFragment.setVisibility(View.VISIBLE);
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
        } else if (mBinding.webPageNoNetwork.getVisibility() != View.VISIBLE) {
            mBinding.webPageNoNetwork.showErrorView();
            mBinding.webPageNoNetwork.setVisibility(View.VISIBLE);
            return false;
        }
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("fragment", "onHiddenChanged()  hidden=" + hidden);

        if (!hidden) {
//            getWebView().loadUrl(H5Urls.SCRATCHCARDS_URL);
            getWebView().loadUrl("javascript:refresh()");
            NiuDataAPI.onPageStart("home_page_view_page", "刮刮卡浏览");
            StatusBarCompat.translucentStatusBarForImage(getActivity(), true, true);
        } else {
            NiuDataAPI.onPageEnd("home_page_view_page", "刮刮卡浏览");
        }
    }

    private void initWebView() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mBinding.webFragment, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.common_view_no_network, R.id.no_network_tv)
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .setWebViewClient(new YuLeWebViewClient(getActivity(), mBinding.loadIv))
                .setWebChromeClient(mWebChromeClient)
                .addJavascriptInterface("native", new JsInterface())
                .addJavascriptInterface("android", new SdkJsInterface())
                .createAgentWeb()
                .ready()
                .go(H5Urls.SCRATCHCARDS_URL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUserInfo(String string) {
        if (LOGIN_SUCCESS.equals(string) || EXIT_SUCCESS.equals(string) || "refreshGuaGuaLeH5".equals(string)) {
//            mBinding.phoneNumTv.setText("UserHelper.init().getPhoneNum()");
            getWebView().loadUrl(H5Urls.SCRATCHCARDS_URL);
        }
    }

    public WebView getWebView() {
        return mAgentWeb.getWebCreator().getWebView();
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };

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
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
