package com.xiaoniu.cleanking.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JavaInterface {

    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;
    @Inject
    UserApiService mService;
    private Activity mActivity;
    WebView mWebView;
    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public JavaInterface(Activity activity, WebView mWebView) {
        mActivity = activity;
        this.mWebView = mWebView;
    }

    @JavascriptInterface
    public void toOtherPage(String url) {
        if (mActivity instanceof UserLoadH5Activity) {
            mActivity.finish();
        }
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
        StatisticsUtils.trackClick("Sharing_coupons_click", "分享领优惠券", "", "");

        //保存分享次数
        PreferenceUtil.saveShareNum();
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
                ToastUtils.showShort("分享成功");
                addShareSuccessRequest();

                if (share_media == SHARE_MEDIA.WEIXIN) {
//                    StatisticsUtils.trackClick("Wechat_friends_click", "微信好友", "", "Sharing_page");
                } else if (SHARE_MEDIA.WEIXIN_CIRCLE == share_media) {
//                    StatisticsUtils.trackClick("Circle_of_friends_click", "朋友圈", "", "Sharing_page");
                } else if (share_media == SHARE_MEDIA.QZONE) {
                    StatisticsUtils.trackClick("qq_space_click", "QQ空间", "", "Sharing_page");
                } else if (SHARE_MEDIA.QQ == share_media) {
                    StatisticsUtils.trackClick("qq_friends_click", "QQ好友", "", "Sharing_page");
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                mActivity.runOnUiThread(() -> {
                    if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                        Toast.makeText(mActivity, "没有安装微信，请先安装应用", Toast.LENGTH_SHORT).show();
                    } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                        Toast.makeText(mActivity, "没有安装QQ，请先安装应用", Toast.LENGTH_SHORT).show();
                    } else if (share_media == SHARE_MEDIA.SINA) {
                        Toast.makeText(mActivity, "没有安装新浪微博，请先安装应用", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtils.showShort("已取消");
            }
        });

        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
        shareAction.open();

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_WECHAT:
                    ToastUtils.showShort("没有安装微信，请先安装应用");
                    break;
                case SHARE_QQ:
                    ToastUtils.showShort("没有安装QQ，请先安装应用");
                    break;
                case SHARE_SINA:
                    ToastUtils.showShort("没有安装新浪微博，请先安装应用");
                    break;
                default:
                    break;
            }
        }
    };
    onShareSuccessListener listener;

    public void setListener(onShareSuccessListener listener) {
        this.listener = listener;
    }

    public interface onShareSuccessListener {
        void shareSuccess();
    }

    public void shareSuccessRequest(Common4Subscriber<BaseEntity> commonSubscriber) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
//        map.put("channel", AndroidUtil.getMarketId());
//        map.put("appVersion", AndroidUtil.getAppVersionName());
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        new ApiModule(AppApplication.getInstance()).provideHomeService().shareSuccess(body).compose(RxUtil.<BaseEntity>rxSchedulerHelper((RxAppCompatActivity) mActivity))
                .subscribeWith(commonSubscriber);
    }

    //d分享成功增加领券接口
    public void addShareSuccessRequest() {
        shareSuccessRequest(new Common4Subscriber<BaseEntity>() {


            @Override
            public void getData(BaseEntity auditSwitch) {
                mWebView.reload();
                if (listener != null) listener.shareSuccess();
            }

            @Override
            public void showExtraOp(String code, String message) {
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }
}
