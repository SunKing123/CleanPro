package com.xiaoniu.cleanking.ui.main.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseAgentWebActivity;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * @author XiLei
 * @date 2019/10/18.
 * description：
 */
public class AgentWebViewActivity extends BaseAgentWebActivity {

    private TextView mTitleTextView;
    private String mWebFrom;
    private String sourcePage;
    private String currentPage;
    private String eventName;
    private String eventCode;
    private String eventCloseName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        if (null != getIntent().getStringExtra(ExtraConstant.WEB_FROM)) {
            mWebFrom = getIntent().getStringExtra(ExtraConstant.WEB_FROM);
        }
        sourcePage = AppHolder.getInstance().getCleanFinishSourcePageId();
        LinearLayout mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        Toolbar mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!TextUtils.isEmpty(mWebFrom)) {
            if (mWebFrom.equals("SplashADActivity")) {
                currentPage = "clod_splash_custom_ad_page";
                eventCode = "clod_splash_custom_ad_page_view_page";
                eventName = "冷启动打底广告页浏览";
                eventCloseName = "冷启动打底广告页关闭点击";
            }
            if (mWebFrom.equals("SplashADHotActivity")) {
                currentPage = "hot_splash_custom_ad_page";
                eventCode = "hot_splash_custom_ad_page_view_page";
                eventName = "热启动打底广告页浏览";
                eventCloseName = "热启动打底广告页关闭点击";
            }
            if (mWebFrom.equals("FinishActivity")) {
                currentPage = "success_custom_page";
                eventCode = "success_custom_page_view_page";
                eventName = "结果页打底广告页浏览";
                eventCloseName = "结果页打底广告页关闭点击";
            }
            if (mWebFrom.equals("LockActivity")) {
                currentPage = "lock_screen_custom_page";
                eventCode = "lock_screen_custom_page_view_page";
                eventName = "锁屏页打底广告页浏览";
                eventCloseName = "锁屏页打底广告页关闭点击";
            }
        } else {
            currentPage = "interactive_advertising_page";
            eventCode = "interactive_advertising_page_view_page";
            eventName = "互动式广告页浏览";
            eventCloseName = "互动式广告关闭点击";
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsUtils.trackClick("close_click", eventCloseName, sourcePage, currentPage);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart(eventCode, eventName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, eventCode, eventName);
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.container);
    }

    @Override
    public void onBackPressed() {
        StatisticsUtils.trackClick("system_return_click", eventCloseName, sourcePage, currentPage);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getIndicatorColor() {
        return Color.parseColor("#ff0000");
    }

    @Override
    protected void setTitle(WebView view, String title) {
        super.setTitle(view, title);
        if (!TextUtils.isEmpty(title)) {
            if (title.length() > 10) {
                title = title.substring(0, 10).concat("...");
            }
        }
        mTitleTextView.setText(title);
    }

    @Override
    protected int getIndicatorHeight() {
        return 3;
    }

    @Nullable
    @Override
    protected String getUrl() {
        return getIntent().getStringExtra(ExtraConstant.WEB_URL);
    }


}
