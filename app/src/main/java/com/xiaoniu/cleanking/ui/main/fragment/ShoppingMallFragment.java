package com.xiaoniu.cleanking.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.just.agentweb.AgentWeb;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;

/**
 * 首页tab H5页面（商城页、生活页）
 * Created on 2018/3/21.
 */
public class ShoppingMallFragment extends SimpleFragment implements MainActivity.OnKeyBackListener {
    @BindView(R.id.web_container)
    RelativeLayout mRootView;
    private String url = ApiModule.SHOPPING_MALL;
    private boolean isFirst = true;
    private boolean isFirstPause = true;

    private AgentWeb mAgentWeb;
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
        mAgentWeb.getWebCreator().getWebView().getSettings().setTextZoom(100);
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
            if (getActivity() != null) {
                mCanGoBack = !"清理管家极速版商城".equals(title);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mAgentWeb.getWebLifeCycle().onResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
            }
            if (isFirst) {
                getWebView().loadUrl(url);
                isFirst = false;
                hideBadgeView();
            } else {
                refreshList(10);
            }
        } else {
            if (!isFirstPause) {
                mAgentWeb.getWebLifeCycle().onPause();
            } else {
                isFirstPause = false;
            }

        }
    }

    public WebView getWebView() {
        return mAgentWeb.getWebCreator().getWebView();
    }

    public void refreshList(int num) {
        if (hideBadgeView()) {
            WebView webView = getWebView();
            if (webView != null) {
                webView.loadUrl("javascript:refreshList(" + num + ")");
            }
        }
    }

    private boolean hideBadgeView() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.isBadgeViewShow()) {
                mainActivity.hideBadgeView();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isHidden()) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        NiuDataAPI.onPageEnd("information_iew_page", "信息页面");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        NiuDataAPI.onPageStart("information_iew_page", "信息页面");
    }

    private long firstTime;

    @Override
    public void onKeyBack() {
        if (getWebView().canGoBack() && mCanGoBack) {
            getWebView().goBack();
            firstTime = 0;
        } else {
//            long currentTimeMillis = System.currentTimeMillis();
//            if (currentTimeMillis - firstTime > 1500) {
//                Toast.makeText(getActivity(), "再按一次退出程序",
//                        Toast.LENGTH_SHORT).show();
//                firstTime = currentTimeMillis;
//            } else {
//                SPUtil.setInt(getContext(), "turnask", 0);
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
//            }
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
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
            StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", "select_page", "information_page", id, name);
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

    private void refresh() {
        getWebView().loadUrl(url);
    }

    private void showToast(String msg) {
        ToastUtils.showShort(msg);
    }
}
