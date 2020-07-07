package com.xiaoniu.cleanking.ui.newclean.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.launcher.ARouter;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.WebChromeClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.databinding.FragmentYuleBinding;
import com.xiaoniu.cleanking.ui.newclean.util.YuLeWebViewClient;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:娱乐刮刮乐页面==加载h5页面
 */
public class YuLeFragment extends SimpleFragment {
    FragmentYuleBinding mBinding;
    //返回键 0 不可返回  1 可返回
    private String backable = "1";
    private String url = "http://192.168.85.61:9999/html/activitiesHtml/scratchCards/cardList.html";
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
        mBinding.refreshUrl.setOnClickListener(view -> {
            mAgentWeb.getWebCreator().getWebView().loadUrl(url);
            Log.e("snow","=="+AndroidUtil.getXnData());
        });
    }

    private void initWebView() {
        mAgentWeb = AgentWeb.with(getActivity())
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
                .go(url);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden) {
        //放在initView中无效
        StatusBarCompat.translucentStatusBarForImage(getActivity(), true, true);
//        }
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
        super.onDestroyView();
    }
}
