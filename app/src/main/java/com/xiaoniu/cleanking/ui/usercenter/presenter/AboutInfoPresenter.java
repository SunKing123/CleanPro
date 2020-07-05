package com.xiaoniu.cleanking.ui.usercenter.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import javax.inject.Inject;

import com.jess.arms.utils.RxLifecycleUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.usercenter.contract.AboutInfoContract;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
@ActivityScope
public class AboutInfoPresenter extends BasePresenter<AboutInfoContract.Model, AboutInfoContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    AppManager mAppManager;

    @Inject
    public AboutInfoPresenter(AboutInfoContract.Model model, AboutInfoContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }


    /**
     * 版本更新
     */
    public void queryAppVersion(Activity ctx, int type) {
        mModel.getVersion(ctx).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<AppVersion>(mErrorHandler) {
                    @Override
                    public void onNext(AppVersion appVersion) {
                        if (type == 1) {
                            mRootView.setShowVersion(appVersion);
                        } else {
                            setAppVersion(ctx, appVersion);
                        }
                    }
                });
//        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {
//
//
//            @Override
//            public void getData(AppVersion updateInfoEntity) {
//                if (type == 1) {
//                    mView.setShowVersion(updateInfoEntity);
//                } else {
//                    setAppVersion(updateInfoEntity);
//                }
//
//            }
//
//            @Override
//            public void showExtraOp(String code, String message) {
//                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void showExtraOp(String message) {
//            }
//
//            @Override
//            public void netConnectError() {
//
//            }
//        });
    }


    private UpdateAgent mUpdateAgent;

    public void setAppVersion(Activity ctx, AppVersion result) {
        if (result != null && result.getData() != null) {
            if (result.getData().isPopup)
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(ctx, result, () -> {
                    });
                    mUpdateAgent.check();
                } else {
                    mUpdateAgent.check();
                }
        }
    }

    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;
    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public void share(String picurl, String linkurl, String title, String content, int type) {
        //分享链接
        UMWeb web = new UMWeb(linkurl);
        //标题
        web.setTitle(title);
        if (TextUtils.isEmpty(picurl)) {
            //缩略图
            web.setThumb(new UMImage(mRootView.getActivity(), R.mipmap.applogo));
        } else {
            web.setThumb(new UMImage(mRootView.getActivity(), picurl));
        }
        //描述
        web.setDescription(content);
        ShareAction shareAction = new ShareAction(mRootView.getActivity()).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.e("ss", "ss");
                String pageName = "";
                if (com.xiaoniu.cleanking.app.AppManager.getAppManager().preActivityName().contains("MainActivity")) {
                    pageName = "mine_page";
                }

                if (share_media == SHARE_MEDIA.WEIXIN) {
                    StatisticsUtils.trackClick("Wechat_friends_click", "微信好友", pageName, "Sharing_page");
                } else if (SHARE_MEDIA.WEIXIN_CIRCLE == share_media) {
                    StatisticsUtils.trackClick("Circle_of_friends_click", "朋友圈", pageName, "Sharing_page");
                }
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_SUCCESS);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                System.out.println("----------------------------------" + throwable.getCause());
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
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
                shareAction.open();
//                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
//                shareAction.open();
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
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
                shareAction.open();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_SUCCESS:
                    ToastUtils.showShort("分享成功");
                    break;
                case SHARE_CANCEL:
//                    mView.showToast("已取消");
                    break;
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
}
