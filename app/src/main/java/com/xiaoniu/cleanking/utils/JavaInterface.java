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
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

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
        StatisticsUtils.trackClickUrlH5("banner_share_page", "广告点击", AppHolder.getInstance().getSourcePageId(), "banner_share_page", url);
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
        StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", AppHolder.getInstance().getSourcePageId(), "information_page", id, name);
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
        StatisticsUtils.trackClick("Sharing_coupons_click", "分享领优惠券", AppHolder.getInstance().getSourcePageId(), AppHolder.getInstance().getOtherSourcePageId());

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
                String eventWeixinCode;
                String eventWeixinCircleCode;
                String eventQZoneCode;
                String eventQQCode;
                String eventSinaCode;
                String sourcePage;
                String currentPage;

                switch (AppHolder.getInstance().getOtherSourcePageId()) {
                    case SpCacheConfig.ONKEY:
                        //一键加速
                        eventWeixinCode = "once_accelerate_Wechat_friends_click";
                        eventWeixinCircleCode = "once_accelerate_Circle_of_friends_click ";
                        eventQZoneCode = "once_accelerate_qq_space_click";
                        eventQQCode = "once_accelerate_qq_friends_click";
                        eventSinaCode = "once_accelerate_Weibo_Sharing_click";
                        sourcePage = "once_accelerate_page";
                        currentPage = "One_click_acceleration_Clean_up_page";
                        break;
                    case SpCacheConfig.PHONE_CLEAN:
                        //手机清理
                        eventWeixinCode = "mobile_cleaning_scan_cleaing_Wechat_friends_click";
                        eventWeixinCircleCode = "mobile_cleaning_scan_cleaing_Circle_of_friends_click ";
                        eventQZoneCode = "mobile_cleaning_scan_cleaing_qq_space_click";
                        eventQQCode = "mobile_cleaning_scan_cleaing_qq_friends_click";
                        eventSinaCode = "mobile_cleaning_scan_cleaing_Weibo_Sharing_click";
                        sourcePage = "mobile_cleaning_scan_cleaing_page";
                        currentPage = "mobile_cleaning_scan_clean_up_page";
                        break;
                    case SpCacheConfig.PHONE_COOLING:
                        //手机降温
                        eventWeixinCode = "detecting_mobile_temperature_Wechat_friends_click";
                        eventWeixinCircleCode = "detecting_mobile_temperature_Circle_of_friends_click";
                        eventQZoneCode = "detecting_mobile_temperature_qq_space_click";
                        eventQQCode = "detecting_mobile_temperature_qq_friends_click";
                        eventSinaCode = "detecting_mobile_temperature_Weibo_Sharing_click";
                        sourcePage = "detecting_mobile_temperature_page";
                        currentPage = "Mobile_Cooling_Completion_Page";
                        break;
                    case SpCacheConfig.QQ_CLEAN:
                        //QQ清理
                        eventWeixinCode = "qq_cleaning_Wechat_friends_click ";
                        eventWeixinCircleCode = "qq_cleaning_Circle_of_friends_click";
                        eventQZoneCode = "qq_cleaning_qq_space_click";
                        eventQQCode = "qq_cleaning_qq_friends_click";
                        eventSinaCode = "qq_cleaning_Weibo_Sharing_click";
                        sourcePage = "qq_cleaning_page";
                        currentPage = "QQ_Clean_Up_Page";
                        break;
                    case SpCacheConfig.WETCHAT_CLEAN:
                        //微信清理
                        eventWeixinCode = "wechat_cleaning_Wechat_friends_click";
                        eventWeixinCircleCode = "wechat_cleaning_Circle_of_friends_click";
                        eventQZoneCode = "wechat_cleaning_qq_space_click";
                        eventQQCode = "wechat_cleaning_qq_friends_click";
                        eventSinaCode = "wechat_cleaning_Weibo_Sharing_click";
                        sourcePage = "wechat_cleaning_page";
                        currentPage = "Wechat_Clean_Up_Page";
                        break;
                    case SpCacheConfig.NOTITY:
                        //通知栏清理
                        eventWeixinCode = "Notice_Bar_Cleaning_Wechat_friends_click ";
                        eventWeixinCircleCode = "Notice_Bar_Cleaning_Circle_of_friends_click ";
                        eventQZoneCode = "Notice_Bar_Cleaning_qq_space_click";
                        eventQQCode = "Notice_Bar_Cleaning_qq_friends_click";
                        eventSinaCode = "Notice_Bar_Cleaning_Weibo_Sharing_click";
                        sourcePage = "Notice_Bar_Cleaning_page";
                        currentPage = "Notice_Bar_Cleaning_Completed_page";
                        break;
                    case SpCacheConfig.SUPER_POWER_SAVING:
                        //超强省电
                        eventWeixinCode = "Super_Power_Saving_Wechat_friends_click ";
                        eventWeixinCircleCode = "Super_Power_Saving_Circle_of_friends_click";
                        eventQZoneCode = "Super_Power_Saving_qq_space_click";
                        eventQQCode = "Super_Power_Saving_qq_friends_click";
                        eventSinaCode = "Super_Power_Saving_Weibo_Sharing_click";
                        sourcePage = "Super_Power_Saving_page";
                        currentPage = "Super_Power_Saving_Completion_page";
                        break;
                    case SpCacheConfig.BANNER:
                        //banner
                        eventWeixinCode = "banner_Wechat_friends_click  ";
                        eventWeixinCircleCode = "banner_Circle_of_friends_click";
                        eventQZoneCode = "banner_qq_space_click";
                        eventQQCode = "banner_qq_friends_click";
                        eventSinaCode = "banner_Weibo_Sharing_click";
                        sourcePage = "home_page";
                        currentPage = "banner_share_page";
                        break;
                    default:
                        eventWeixinCode = "home_page_clean_up_Wechat_friends_click";
                        eventWeixinCircleCode = "home_page_clean_up_Circle_of_friends_click";
                        eventQZoneCode = "home_page_clean_up_qq_space_click";
                        eventQQCode = "home_page_clean_up_qq_friends_click";
                        eventSinaCode = "home_page_clean_up_Weibo_Sharing_click";
                        sourcePage = "home_page";
                        currentPage = "home_page_clean_up_page";
                        break;
                }

                if (share_media == SHARE_MEDIA.WEIXIN) {
                    StatisticsUtils.trackClick(eventWeixinCode, "\"微信好友\"点击", sourcePage, currentPage);
                } else if (SHARE_MEDIA.WEIXIN_CIRCLE == share_media) {
                    StatisticsUtils.trackClick(eventWeixinCircleCode, "\"朋友圈\"点击", sourcePage, currentPage);
                } else if (share_media == SHARE_MEDIA.QZONE) {
                    StatisticsUtils.trackClick(eventQZoneCode, "\"QQ空间\"点击", sourcePage, currentPage);
                } else if (SHARE_MEDIA.QQ == share_media) {
                    StatisticsUtils.trackClick(eventQQCode, "\"qq好友\"点击", sourcePage, currentPage);
                } else if (SHARE_MEDIA.SINA == share_media) {
                    StatisticsUtils.trackClick(eventSinaCode, "\"微博分享\"点击", sourcePage, currentPage);
                }
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                ToastUtils.showShort("分享成功");
                addShareSuccessRequest();
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
