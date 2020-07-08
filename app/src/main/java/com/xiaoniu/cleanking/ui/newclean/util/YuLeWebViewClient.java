package com.xiaoniu.cleanking.ui.newclean.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ImageView;

import com.just.agentweb.WebViewClient;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xiaoniu.common.utils.ToastUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/7
 * Describe:
 */
public class YuLeWebViewClient extends WebViewClient {
    AnimationsContainer.FrameseAnim animaDra;
    ImageView mLoadImg;

    public YuLeWebViewClient(Activity activity, ImageView mLoadImg) {
        if (activity == null) {
            return;
        }
        this.mLoadImg = mLoadImg;
        animaDra = AnimationsContainer.getInstance(R.array.loading_coin, 100).createAnim(mLoadImg);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //接受所有网站的证书 Android5.0 WebView中Http和Https混合问题 显示空白
        handler.proceed();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (animaDra != null) {
            mLoadImg.setVisibility(VISIBLE);
            animaDra.start();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (animaDra != null) {
            mLoadImg.setVisibility(GONE);
            animaDra.stop();
        }
//            mAgentWeb.getIndicatorController().offerIndicator().hide();//失效
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("snow", "========shouldOverrideUrlLoading====" + url);
        if (url.contains("alipays://") || url.contains("weixin://") || url.contains("alipayqr://")) {
            try {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setData(Uri.parse(url));
                view.getContext().startActivity(it);
            } catch (Exception e) {
                ToastUtils.showShort(url.contains("weixin://") ? "您未安装微信" : "您未安装支付宝");
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}
