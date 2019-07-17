package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.base.UmengEnum;
import com.xiaoniu.cleanking.base.UmengUtils;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.bean.UpdateInfoEntity;
import com.xiaoniu.cleanking.ui.usercenter.presenter.LoadH5Presenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 通用加载h5页面类
 *
 * @author tie
 */
@Route(path = RouteConstants.USER_LOAD_H5_ACTIVITY)
public class UserLoadH5Activity extends BaseActivity<LoadH5Presenter> {

    @BindView(R.id.webview)
    WebView mWebView;
    String title;
    String url;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.title_bar)
    RelativeLayout mTitleBar;
    @BindView(R.id.img_help)
    ImageView mImgHelp;
    @BindView(R.id.layout_net_error)
    LinearLayout mLayoutNetError;
    boolean noTitleBar;
    public static final int REQUEST_REFRESH = 5341;
    public static final int REQUEST_NOTIFICATION = 5342;
    public static final int REQUEST_GUANJIA = 5343;
    public static final int REQUEST_APP_UPDATE = 5344;
    String phone = "";

    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;

    public static final String JK_PAGE = "jk_page";
    public static final String HD_PAGE = "hd_page";
    public static final String ZH_PAGE = "zh_page";
    public static final String TIE_PAGE = "tie_page";
    public static final String WODE_PAGE = "wode_page";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
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
                default:
                    break;
            }
        }
    };
    private boolean isParam;
    private String eventId;
    private Bundle bundle;
    private String productId;
    private String isRefresh;
    private String appId;
    private Map<String, Map<String, Object>> productRegisterMonitorMap;

    private boolean isError = false;
    /**
     * 版本更新信息
     */
    private UpdateInfoEntity.DataBean mUpdateInfo;
    /**
     * 应用更新代理
     */
    private UpdateAgent mUpdateAgent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    protected void initView() {
        source_page="h5";
        bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString(Constant.Title);
            url = bundle.getString(Constant.URL);
            //是否添加xn_data参数
            isParam = bundle.getBoolean("isParam", true);
            eventId = bundle.getString("eventId");
            productId = bundle.getString("productId");
            isRefresh = bundle.getString("isRefresh");
            appId = bundle.getString("appId");
            //是否显示titleBar
            noTitleBar = bundle.getBoolean(Constant.NoTitle, false);
            this.mUpdateInfo = bundle.getParcelable("update_info");
        }
        if (noTitleBar) {
            //不显示titleBar
            mTitleBar.setVisibility(View.GONE);
        } else {
            mTitleBar.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
        //判断是否是刘海屏
//        boolean haveLiuhai = NotchUtils.hasNotchScreen(this);
        if (isParam) {
            if (url.contains("?")) {
//                url = url + "&xn_data=" + AndroidUtil.getXnData() + "&haveLiuhai=" + haveLiuhai;
            } else {
//                url = url + "?xn_data=" + AndroidUtil.getXnData() + "&haveLiuhai=" + haveLiuhai;
            }
            mImgHelp.setVisibility(View.GONE);
        } else {
            mImgHelp.setVisibility(View.VISIBLE);
        }
        showLoadingDialog();
        mWebView.loadUrl(url);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        // 为图片添加放大缩小功能
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.addJavascriptInterface(new JsInterface(), "mapPage");
        mWebView.addJavascriptInterface(new JsInterface(), "kefuPage");
        mWebView.addJavascriptInterface(new JsInterface(), "backPage");
        //分享
        mWebView.addJavascriptInterface(new JsInterface(), "sharePage");
        mWebView.addJavascriptInterface(new JsInterface(), "guanJiaPage");
        //活动
        mWebView.addJavascriptInterface(new JsInterface(), "shandaiPage");
        mWebView.addJavascriptInterface(new JsInterface(), "HhhWebPage");


        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(UserLoadH5Activity.this.title) && tvTitle != null) {
                    tvTitle.setText(title);
                }
            }
        });

        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("flashloan://www.xulu.com")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("tel:")) {
                    phone = url;
                    makePhoneCall(phone);
                    return true;
                }

                // ------  对alipays:相关的scheme处理 -------
                if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                        UserLoadH5Activity.this.finish();
                        setResult(RESULT_OK);
                    } catch (Exception e) {
                        new AlertDialog.Builder(UserLoadH5Activity.this)
                                .setMessage("未检测到支付宝客户端，请安装后重试。")
                                .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                        startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                        UserLoadH5Activity.this.finish();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserLoadH5Activity.this.finish();
                            }
                        }).show();
                    }
                    return true;
                }
                // ------- 处理结束 -------

                //--------微信-------
                try {
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://paytest.1an.com");
                        view.loadUrl(url, extraHeaders);
                    }
                } catch (Exception e) {
                    ToastUtils.showShort("请安装微信最新版!");
                    mWebView.goBack();
                }
                //------微信处理结束--------
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                cancelLoadingDialog();
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(UserLoadH5Activity.this.title) && tvTitle != null) {
                    tvTitle.setText(title);
                }
                if (!isError) {
                    if (mLayoutNetError != null) {
                        mLayoutNetError.setVisibility(View.GONE);
                    }
                    if (mWebView != null) {
                        mWebView.setVisibility(View.VISIBLE);
                    }
                }
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                isError = true;
                if (mLayoutNetError != null) {
                    mLayoutNetError.setVisibility(View.VISIBLE);
                }
                if (mWebView != null) {
                    mWebView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();// 接受所有网站的证书
            }
        };
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    if (bundle.containsKey(Constant.TAG) && "1".equals(bundle.get(Constant.TAG).toString())) {
                        startActivity(new Intent(UserLoadH5Activity.this, MainActivity.class));
                    } else {
                        finish();
                    }

                    if (ApiModule.ZhiMaXinYong.equals(url)) {
                        UmengUtils.event(UserLoadH5Activity.this, UmengEnum.kaihu_zhima_fanhui);
                    }
                }
            }
        });
        mWebView.setWebViewClient(webViewClient);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                if (bundle.containsKey(Constant.TAG) && "1".equals(bundle.get(Constant.TAG).toString())) {
                    startActivity(new Intent(UserLoadH5Activity.this, MainActivity.class));
                } else {
                    finish();
                }
                if (ApiModule.ZhiMaXinYong.equals(url)) {
                    UmengUtils.event(UserLoadH5Activity.this, UmengEnum.kaihu_zhima_fanhui);
                }
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void netError() {

    }

    @OnClick(R.id.img_help)
    public void onImgHelpClicked() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.Title, "帮助中心");
        bundle.putString(Constant.URL, ApiModule.Base_H5_Host + "/FlashLoanH5/html/page/my/help/renzheng.html");
        startActivity(UserLoadH5Activity.class, bundle);
    }

    @OnClick(R.id.layout_net_error)
    public void onTvRefreshClicked() {
        mWebView.loadUrl(url);
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    /**
     * 打电话权限校验
     *
     * @param phone 号码
     */
    public void makePhoneCall(String phone) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            callPhone(phone);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //拨打电话，  拨号权限
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(phone);
            }
        }
        //版本更新，写入sd卡权限
        if (requestCode == REQUEST_APP_UPDATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadApp();
            }
        }
    }

    /**
     * 拨打电话
     *
     * @param phone 电话号码
     */
    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    /**
     * 下载新版本应用
     */
    private void downloadApp() {

    }

    /**
     * android 6.0请求写入sd权限
     */
    public void requestPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(UserLoadH5Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserLoadH5Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        } else {
            //开始更新
            if (requestCode == REQUEST_APP_UPDATE) {
                downloadApp();
            }
            if (requestCode == REQUEST_GUANJIA) {

            }
        }
    }


    String downloadGjurl = "";

    /**
     * H5交互的类
     */
    public class JsInterface {

        @JavascriptInterface
        public void downApp(String android_url, String yyb_url) {
            if (AndroidUtil.isAppInstalled("com.xulu.loanmanager")) {
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.xulu.loanmanager");
                startActivity(intent);
            } else {
                downloadGjurl = yyb_url;
                requestPermission(REQUEST_GUANJIA);
            }
        }

        /**
         * 关闭页面
         */
        @JavascriptInterface
        public void backLink() {
            finish();
        }

        /**
         * 分享
         */
        @JavascriptInterface
        public void shareLink(String picurl, String linkurl, String title, String content, String activityEvtType) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(UserLoadH5Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(UserLoadH5Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserLoadH5Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 341);
            } else {
                share(picurl, linkurl, title, content, -1);
            }
        }

        @JavascriptInterface
        public void modularShareLink(String picurl, String linkurl, String title, String content, int type) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(UserLoadH5Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(UserLoadH5Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserLoadH5Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 341);
            } else {
                share(picurl, linkurl, title, content, type);
            }
        }

        /**
         * 页面跳转逻辑
         *
         * @param pageType
         */
        @JavascriptInterface
        public void pageLink(String pageType) {

        }




        /**
         * 打开新的h5页面
         */
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
        public void toOtherPage_gj(String url, String productId, String isRefresh) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, url);
            bundle.putString(Constant.Title, "");
            bundle.putString("productId", UserLoadH5Activity.this.productId);
            bundle.putString("isRefresh", isRefresh);
            bundle.putBoolean(Constant.NoTitle, false);
            startActivityForResult(UserLoadH5Activity.class, bundle, REQUEST_REFRESH);
        }
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingDialog = null;
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    public void finish() {
        if ("1".equals(isRefresh)) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (mWebView.getUrl().equals(url)) {
                super.onBackPressed();
            } else {
                mWebView.goBack();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 芝麻信用认证
     */
    public void zhiMaCreditSuccess() {
        finish();
    }

    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public void share(String picurl, String linkurl, String title, String content, int type) {
        //分享链接
        UMWeb web = new UMWeb(linkurl);
        //标题
        web.setTitle(title);
        if (TextUtils.isEmpty(picurl)) {
            //缩略图
            web.setThumb(new UMImage(UserLoadH5Activity.this, R.mipmap.logo_share));
        } else {
            web.setThumb(new UMImage(UserLoadH5Activity.this, picurl));
        }
        //描述
        web.setDescription(content);
        ShareAction shareAction = new ShareAction(UserLoadH5Activity.this).withMedia(web);
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
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
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
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
                shareAction.open();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH && resultCode == RESULT_OK) {
            mWebView.reload();
        }
//        if (REQUEST_NOTIFICATION == requestCode) {
//            if (null != this.mUpdateAgent) {
//                this.mUpdateAgent.check(false);
//            }
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
