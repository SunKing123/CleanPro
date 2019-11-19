package com.geek.webpage.web.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.apkfuns.jsbridge.JsBridge;
import com.geek.webpage.R;
import com.geek.webpage.entity.WebPageEntity;
import com.geek.webpage.eventbus.BaseEventBus;
import com.geek.webpage.eventbus.BaseEventBusConstant;
import com.geek.webpage.utils.NetkUtils;
import com.geek.webpage.utils.StatusBarUtils;
import com.geek.webpage.utils.WebPageConstants;
import com.geek.webpage.web.LWWebChromeClient;
import com.geek.webpage.web.LWWebviewClient;
import com.geek.webpage.web.WebViewListener;
import com.geek.webpage.web.coolindicator.CoolIndicator;
import com.geek.webpage.web.webview.LWWebView;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * H5页面加载<p>
 *
 * @author anhuiqing
 * @since 2019/6/18 12:37
 */
public class BaseWebpageActivity extends AppCompatActivity implements WebViewListener {
    private static final String TAG = BaseWebpageActivity.class.getSimpleName();
    private JsBridge mJsBridge;
    private CoolIndicator mCoolIndicator;
    private LWWebView mLWWebView;
    private ImageView mLeftIv;
    protected TextView mTitleTv;
    private ImageView mRightIv;
    private RelativeLayout noNetWork;
    private Button refreshBtn;
    private FrameLayout webLayout;
    private LinearLayout mTitleBarLayout;
    protected WebPageEntity webPageEntity;
    protected RelativeLayout webBar;

    protected int pageType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        EventBus.getDefault().register(this);
        initTitleBar();
        initData();
    }


    protected void initTitleBar() {
        if (getIntent() != null) {
            webPageEntity = (WebPageEntity) getIntent().getSerializableExtra(WebPageConstants.WEBPAGE_ENTITY);
            if (webPageEntity == null) {
                webPageEntity = new WebPageEntity();
            }
        }
        mTitleBarLayout = findViewById(R.id.webpage_title_bar);
        webBar = findViewById(R.id.rl_web_bar);
        mLeftIv = findViewById(R.id.titlebar_left_iv);
        mTitleTv = findViewById(R.id.titlebar_title_tv);
        mRightIv = findViewById(R.id.titlebar_right_iv);
        noNetWork = findViewById(R.id.web_page_no_network);
        refreshBtn = findViewById(R.id.refresh_net);
        webLayout = findViewById(R.id.web_page_web);
        mLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        mTitleTv.setText(webPageEntity.title);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLWWebView != null) {
                    isNetworkAvailable();
                    mLWWebView.loadUrl(webPageEntity.url);
                }
            }
        });
        if (webPageEntity.isShowTitleBar) {
            mTitleBarLayout.setVisibility(View.VISIBLE);
        } else {
            mTitleBarLayout.setVisibility(View.GONE);
        }
    }

    public void setTitleBarRes(@DrawableRes int titleBg, @DrawableRes int backRes) {
        if (mTitleBarLayout != null) {
            mTitleBarLayout.setBackgroundResource(titleBg);
            StatusBarUtils.setStatusBarState(this, R.id.status_bar_view, true, titleBg);
        }
        if (mLeftIv != null) {
            mLeftIv.setImageResource(backRes);
        }
    }

    protected void initData() {
        mJsBridge = JsBridge.loadModule();
        mCoolIndicator = findViewById(R.id.jsbridge_cool_indicator);
        mLWWebView = findViewById(R.id.jsbridge_lw_webview);
        mCoolIndicator.setMax(100);
        mTitleTv.setText(webPageEntity.title);
        mLWWebView.loadUrl(webPageEntity.url);
        mLWWebView.setWebChromeClient(new LWWebChromeClient(this, this, mJsBridge));
        mLWWebView.setWebViewClient(new LWWebviewClient(this, this, mJsBridge, mCoolIndicator));
    }


    @Override
    public void onLoad(WebView view, int newProgress) {
        mCoolIndicator.setVisibility(View.VISIBLE);
        mCoolIndicator.setProgress(newProgress);
        mCoolIndicator.clearAnimation();
        mCoolIndicator.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onFinish() {
        Animation fadeOut = AnimationUtils.loadAnimation(BaseWebpageActivity.this, android.R.anim.fade_out);
        fadeOut.setFillBefore(true);
        fadeOut.setFillAfter(true);
        fadeOut.setFillEnabled(true);
        mCoolIndicator.startAnimation(fadeOut);
        mCoolIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSetTitle(WebView view, String title) {
//添加⼀一下，过滤掉⾃自定义title不不为空的情况
//添加⼀一个容错处理理，把http或者https为开头的全部过滤掉
        //|| URLUtil.isHttpsUrl(title) ||
        //                URLUtil.isHttpsUrl(title) || isSpecialTitle(title)
        if (TextUtils.isEmpty(title)) {
            mTitleTv.setText(webPageEntity.title);
            return;
        }
        mTitleTv.setText(title);
    }

    @Override
    public void onError(WebView view, int errorCode, String description, String failingUrl) {
//        mTitleBarView.getTitleView().setText(getResources().getString(R.string.online_e
//                rror_fail));
//        mWebViewLayout.setVisibility(View.GONE);
//        mOnlineErrLayout.setVisibility(View.VISIBLE);
        mTitleTv.setText(webPageEntity.title);
    }

    private static final String REGEX_TITLE = "[&?.=/]";

    private boolean isSpecialTitle(String title) {
        Pattern p = Pattern.compile(REGEX_TITLE);
        Matcher m = p.matcher(title);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exit();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exit() {
        if (mLWWebView != null && mLWWebView.canGoBack()) {
            mLWWebView.goBack();
        } else {
            BaseWebpageActivity.this.finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPause() {
        super.onPause();
        mLWWebView.onPause();
        mLWWebView.pauseTimers();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        mLWWebView.resumeTimers();
        mLWWebView.onResume();
        isNetworkAvailable();
    }

    private void isNetworkAvailable() {
        webLayout.setVisibility(!NetkUtils.isConnected(this) ? View.GONE : View.VISIBLE);
        noNetWork.setVisibility(NetkUtils.isConnected(this) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLWWebView.destroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Subscriber(mode = ThreadMode.MAIN)
    public void receiverMessage(BaseEventBus baseEvent) {
        switch (baseEvent.action) {
            case BaseEventBusConstant.WEB_PAGE_FINISH:
                finish();
                break;
            case BaseEventBusConstant.WEB_PAGE_GO_BACK:
                exit();
                break;
            case BaseEventBusConstant.WEB_PAGE_TASK_TYPE:
                pageType = 1;
                break;
            case BaseEventBusConstant.WEB_PAGE_ACTIVITY_TYPE:
                pageType = 2;
                break;
        }
    }

    public void release() {
        if (mJsBridge == null)
            return;
        mJsBridge.release();
    }
}