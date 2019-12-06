package com.geek.webpage.web.model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.geek.webpage.web.WebViewListener;
import com.geek.webpage.web.views.LWWebViewDialog;

/**
 * H5弹窗管理器<p>
 *
 * @author anhuiqing
 * @since 2019/6/7 21:10
 */
public class WebDialogManager {
    private final String TAG = WebDialogManager.class.getSimpleName();
    private static WebDialogManager instance = new WebDialogManager();

    public static WebDialogManager getInstance() {
        return instance;
    }

    private WebDialogManager() {
    }

    private LWWebViewDialog dialog;

    public void showWebDialog(final Activity activity, Context context, String urlStr) {
        if (null == activity || activity.isFinishing()) return;
        if (null == context || TextUtils.isEmpty(urlStr)) {
            if (null != mFinishInterface && !activity.isFinishing()) {
                mFinishInterface.finishActivity();
            }
            return;
        }

        dialog = new LWWebViewDialog(context);
        dialog.loadUrl(urlStr);
        dialog.getWebView().setWebViewListener(new WebViewListener() {
            @Override
            public void onLoad(WebView view, int newProgress) {
                if (null != activity && !activity.isFinishing()) {
                    if (newProgress == 100) {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.show();
                        }
                    }
                } else {
                    if (null != mFinishInterface) {
                        mFinishInterface.finishActivity();
                    }
                }

            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onSetTitle(WebView view, String title) {

            }

            @Override
            public void onError(WebView view, int errorCode, String description, String failingUrl) {
                dismissWebDialog();
            }
        });

        DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dismissWebDialog();
                    return true;
                }
                return false;
            }
        };
        dialog.setOnKeyListener(keylistener);
    }

    public void dismissWebDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (null != mFinishInterface) {
                mFinishInterface.finishActivity();
            }
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface FinishInterface {
        void finishActivity();
    }

    FinishInterface mFinishInterface;

    public void setFinishInterface(FinishInterface finishInterface) {
        mFinishInterface = finishInterface;
    }
}
