package com.geek.webpage.web.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.geek.webpage.R;
import com.geek.webpage.utils.ScreenUtils;
import com.geek.webpage.web.webview.CommWebView;

/**
 * H5网页dialog<p>
 *
 * @author anhuiqing
 * @since 2019/6/7 20:54
 */
public class LWWebViewDialog extends Dialog {
    private Context mContext;
    private CommWebView webView;
    private int screenWidth, screenHeight;

    public LWWebViewDialog(@NonNull Context context) {
        this(context, R.style.Red_Dialog);
    }

    public LWWebViewDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initPix();
        init();
    }

    private void init() {
        webView = new CommWebView(mContext);
        webView.setTransparent(true);
        setContentView(webView);
        setMargin(false);
//        OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode== KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        };
//        this.setOnKeyListener(keylistener);
//        this.setCancelable(false);
    }


    /**
     * 获取webview
     *
     * @return
     */
    public CommWebView getWebView() {
        return webView;
    }


    public void setMargin(boolean isReset) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        try {
            dialogWindow.setGravity(Gravity.CENTER);
        } catch (IllegalArgumentException e) {//如果出现当前acitivty 销毁，结束当前dialog
            e.printStackTrace();
            dismiss();
        }
        if (isReset) {
            lp.width = screenWidth;
            lp.height = screenHeight; // 高度
        } else {
            lp.width = 0; //宽度
            lp.height = 0; // 高度
        }
    }

    @Override
    public void show() {
        setMargin(true);
        super.show();
    }

    /**
     * 加载url
     *
     * @param url
     */
    public void loadUrl(String url) {
        webView.setCurWebUrl(url);
        webView.refresh();
    }

    public void initPix() {
        screenWidth = ScreenUtils.getScreenWidth(mContext);
        screenHeight = ScreenUtils.getScreenHeight(mContext);
    }

    @Override
    public void dismiss() {
        if (webView != null) {
            webView.onDestroy();
        }
        super.dismiss();
    }
}

