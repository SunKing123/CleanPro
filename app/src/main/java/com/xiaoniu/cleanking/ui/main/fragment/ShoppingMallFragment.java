package com.xiaoniu.cleanking.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xiaoniu.cleanking.app.Constant.TAG_TURN_MAIN;

/**
 * 首页tab H5页面（商城页、生活页）
 * Created on 2018/3/21.
 */
public class ShoppingMallFragment extends SimpleFragment implements MainActivity.OnKeyBackListener {
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.layout_net_error)
    LinearLayout mLayoutNetError;
    @BindView(R.id.tv_refresh)
    TextView mTvRefresh;
    private String url = ApiModule.SHOPPING_MALL;
    private boolean isFirst = true;

    private boolean isSuccess = false;
    private boolean isError = false;
    private boolean mCanGoBack = true;
    private final int REQUEST_SDCARD = 638;
    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_SUCCESS:
//                    showToast("分享成功");
                    break;
                case SHARE_CANCEL:
//                    showToast("已取消");
                    break;
                case SHARE_WECHAT:
                    showToast("没有安装微信，请先安装应用");
                    break;
                case SHARE_QQ:
                    showToast("没有安装QQ，请先安装应用");
                    break;
                case SHARE_SINA:
                    showToast("没有安装新浪微博，请先安装应用");
                    break;

            }
        }
    };

    public static ShoppingMallFragment getIntance(String url) {
        ShoppingMallFragment fragment = new ShoppingMallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shopping_mall;
    }

    @Override
    protected void initView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            url = arguments.getString(Constant.URL, ApiModule.SHOPPING_MALL);
        }
        if (url.contains("?")) {
            url = url + "&xn_data=" + AndroidUtil.getXnData();
        } else {
            url = url + "?xn_data=" + AndroidUtil.getXnData();
        }
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new Javascript(), "HhhWebPage");
        //分享
        mWebView.addJavascriptInterface(new Javascript(), "sharePage");
        mWebView.addJavascriptInterface(new Javascript(), "guanJiaPage");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!isSuccess) {
                    showLoadingDialog();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                cancelLoadingDialog();
                if (!isError) {
                    isSuccess = true;
                    //回调成功后的相关操作
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                isSuccess = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                try {
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://chinamrgift.com.cn");
                        view.loadUrl(url, extraHeaders);
                    }
                } catch (Exception e) {
                    ToastUtils.showShort("请安装微信最新版!");
                    mWebView.goBack();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mCanGoBack = !"悟空清理商城".equals(title);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (isFirst) {
                mWebView.loadUrl(url);
                isFirst = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            mWebView.reload();
        }
    }

    private long firstTime;

    @Override
    public void onKeyBack() {
        if (mWebView.canGoBack() && mCanGoBack) {
            mWebView.goBack();
            firstTime = 0;
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - firstTime > 1500) {
                Toast.makeText(getActivity(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = currentTimeMillis;
            } else {
                SPUtil.setInt(getContext(), "turnask", 0);
//                AppManager.getAppManager().AppExit(getContext(), false);
            }
        }
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
        public void shareLink(String picurl, String linkurl, String title, String content, String activityEvtType) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);
            } else {
                share(picurl, linkurl, title, content, -1);
            }
        }

        @JavascriptInterface
        public void modularShareLink(String picurl, String linkurl, String title, String content, int type) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);
            } else {
                share(picurl, linkurl, title, content, type);
            }
        }

        @JavascriptInterface
        public void canGoBack(boolean canGoBack) {
            mCanGoBack = canGoBack;
        }
    }

    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public void share(String picurl, String linkurl, String title, String content, int type) {
        UMWeb web = new UMWeb(linkurl);//分享链接
        web.setTitle(title);//标题
        if (TextUtils.isEmpty(picurl)) {
            web.setThumb(new UMImage(getContext(), R.mipmap.logo_share));  //缩略图
        } else {
            web.setThumb(new UMImage(getContext(), picurl));
        }
        web.setDescription(content);//描述
        ShareAction shareAction = new ShareAction(getActivity()).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_SUCCESS);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                    handler.sendEmptyMessage(SHARE_WECHAT);
                } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                    handler.sendEmptyMessage(SHARE_QQ);
                } else if (share_media == SHARE_MEDIA.SINA) {
                    handler.sendEmptyMessage(SHARE_SINA);
                }

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_CANCEL);
            }
        });
        switch (type) {
            case -1:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE/*, SHARE_MEDIA.SINA*/);
                shareAction.open();
                break;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                shareAction.setPlatform(platform[type]);
                shareAction.share();
                break;
            default:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE/*, SHARE_MEDIA.SINA*/);
                shareAction.open();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getContext()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.tv_refresh)
    public void onTvRefreshClicked() {
        mLayoutNetError.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadUrl(url);
    }

    private void refresh() {
        mWebView.loadUrl(url);
    }

    private void showToast(String msg) {
        ToastUtils.showShort(msg);
    }
}
