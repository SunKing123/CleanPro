package com.xiaoniu.cleanking.ui.usercenter.presenter;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class AboutPresenter extends RxPresenter<AboutActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public AboutPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 版本更新
     */
    public void queryAppVersion(int type, final OnCancelListener onCancelListener) {
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {


            @Override
            public void getData(AppVersion updateInfoEntity) {
                if (type == 1) {
                    mView.setShowVersion(updateInfoEntity);
                } else {
                    setAppVersion(updateInfoEntity);
                }

            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {

            }
        });
    }

    private UpdateAgent mUpdateAgent;

    public void setAppVersion(AppVersion result) {
        if (result != null && result.getData() != null) {
            //根据版本号判断是否需要更新
            String versionName = AndroidUtil.getAppVersionName();
            int versionCode = AndroidUtil.getVersionCode();
            //默认可以下载
            int code = 0;
            if (!TextUtils.isEmpty(result.code)) {
                code = Integer.parseInt(result.code);
            }
            if (!TextUtils.isEmpty(versionName) && !TextUtils.equals(versionName, result.getData().versionNumber) && !TextUtils.isEmpty(result.getData().downloadUrl)) {
                boolean isForced = false;
                if (TextUtils.equals(result.getData().forcedUpdate, "1")) {//强更
                    isForced = true;
                } else {//手动更新
                    isForced = false;
                }
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mActivity, result, new OnCancelListener() {
                        @Override
                        public void onCancel() {
                        }
                    });
                    mUpdateAgent.check();
                } else {
                    mUpdateAgent.check();
                }


            } else {//清空版本信息状态
            }
        } else {
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
            web.setThumb(new UMImage(mView, R.mipmap.applogo));
        } else {
            web.setThumb(new UMImage(mView, picurl));
        }
        //描述
        web.setDescription(content);
        ShareAction shareAction = new ShareAction(mView).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.e("ss", "ss");
                String pageName = "";
                if (AppManager.getAppManager().preActivityName().contains("MainActivity")) {
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
                System.out.println("----------------------------------"+throwable.getCause());
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
                    mView.showToast("分享成功");
                    break;
                case SHARE_CANCEL:
                    mView.showToast("已取消");
                    break;
                case SHARE_WECHAT:
                    mView.showToast("没有安装微信，请先安装应用");
                    break;
                case SHARE_QQ:
                    mView.showToast("没有安装QQ，请先安装应用");
                    break;
                case SHARE_SINA:
                    mView.showToast("没有安装新浪微博，请先安装应用");
                    break;
                default:
                    break;
            }
        }
    };
}
