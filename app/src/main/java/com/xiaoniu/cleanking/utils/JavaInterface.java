package com.xiaoniu.cleanking.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;

public class JavaInterface {

    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;

    private Activity mActivity;

    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public JavaInterface(Activity activity) {
        mActivity = activity;
    }

    @JavascriptInterface
    public void toOtherPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "");
        bundle.putBoolean(Constant.NoTitle, false);
        ARouter.getInstance().build(RouteConstants.USER_LOAD_H5_ACTIVITY).with(bundle).navigation();

    }

    @JavascriptInterface
    public void onTitleClick(String id, String name) {
        StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", "home_page", "information_page", id, name);
    }

    @JavascriptInterface
    public void showDialog(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }

        mActivity.runOnUiThread(() -> {
            //提示对话框
            final Dialog dialog = new Dialog(mActivity, R.style.custom_dialog);
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_webview, null);

            ImageView imageView = view.findViewById(R.id.image_view);

            ImageUtil.display(url, imageView);

            dialog.setContentView(view);
            if (dialog.isShowing()) {
                return;
            }
            dialog.show();
        });

    }

    /**
     * 分享
     */
    @JavascriptInterface
    public void shareLink(String picurl, String linkurl, String title, String content) {
        //分享链接
        UMWeb web = new UMWeb(linkurl);
        //标题
        web.setTitle(title);
        if (TextUtils.isEmpty(picurl)) {
            //缩略图
            web.setThumb(new UMImage(mActivity, R.mipmap.logo_share));
        } else {
            web.setThumb(new UMImage(mActivity, picurl));
        }
        //描述
        web.setDescription(content);
        ShareAction shareAction = new ShareAction(mActivity).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                            Toast.makeText(mActivity, "没有安装微信，请先安装应用", Toast.LENGTH_SHORT).show();
                        } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                            Toast.makeText(mActivity, "没有安装QQ，请先安装应用", Toast.LENGTH_SHORT).show();
                        } else if (share_media == SHARE_MEDIA.SINA) {
                            Toast.makeText(mActivity, "没有安装新浪微博，请先安装应用", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });

        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
        shareAction.open();

    }

}
