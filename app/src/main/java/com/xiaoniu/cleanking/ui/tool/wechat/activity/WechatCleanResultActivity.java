package com.xiaoniu.cleanking.ui.tool.wechat.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.widget.CleanAnimView;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.BindView;

public class WechatCleanResultActivity extends SimpleActivity {

    @BindView(R.id.acceview)
    CleanAnimView mCleanAnimView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxclean_result;
    }

    @Override
    protected void initView() {
        showBarColor(getResources().getColor(R.color.color_FD6F46));
        CountEntity countEntity = CleanUtil.formatShortFileSize(getIntent().getExtras().getLong("data"));
        startCleanAnim(countEntity);
        mCleanAnimView.setOnColorChangeListener(this::showBarColor);
    }

    /**
     * 开始清理动画
     *
     * @param countEntity
     */
    public void startCleanAnim(CountEntity countEntity) {
        mCleanAnimView.setTitle("微信清理");
        mCleanAnimView.setIcon(R.mipmap.icon_wx_cleaned, DeviceUtils.dip2px(49), DeviceUtils.dip2px(49));
        mCleanAnimView.setData(countEntity, CleanAnimView.page_file_wxclean);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.startTopAnim(true);
        mCleanAnimView.setListener(new CleanAnimView.onBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }


}
