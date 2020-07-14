package com.xiaoniu.cleanking.ui.newclean.util;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.ui.main.bean.CheckUserTokenBean;
import com.xiaoniu.cleanking.ui.main.bean.ExitLoginBean;
import com.xiaoniu.cleanking.ui.main.bean.GuaGuaDoubleBean;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.newclean.interfice.RequestResultListener;
import com.xiaoniu.cleanking.ui.tool.notify.event.UserInfoEvent;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.payshare.AuthorizedLogin;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.schedulers.Schedulers;

import static com.xiaoniu.cleanking.utils.user.UserHelper.EXIT_SUCCESS;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/9
 * Describe: 公共请求用户金币的类
 */
public class RequestUserInfoUtil {
    /**
     * 获取用户金币并发送EventBus 通知
     */
    public static void getUserCoinInfo() {
        if (!UserHelper.init().isLogin()) {
            return;
        }
        ApiModule.getRetrofit().create(UserApiService.class)
                .getMinePageInfo().subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(new CommonSubscriber<MinePageInfoBean>() {
                    @Override
                    public void getData(MinePageInfoBean infoBean) {
                        if (infoBean != null && infoBean.getData() != null) {
                            UserInfoEvent event = new UserInfoEvent();
                            event.infoBean = infoBean.getData();
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    public void showExtraOp(String message) {

                    }

                    @Override
                    public void netConnectError() {

                    }
                });
    }

    /**
     * 用户/token校验
     */
    public static void checkUserToken(Activity activity) {
        if (TextUtils.isEmpty(UserHelper.init().getToken()) || TextUtils.isEmpty(UserHelper.init().getCustomerId())) {
            return;
        }
        ApiModule.getRetrofit().create(UserApiService.class)
                .checkUserTokenApi().subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(new CommonSubscriber<CheckUserTokenBean>() {
                    @Override
                    public void getData(CheckUserTokenBean tokenBean) {
                        if (tokenBean != null) {
                            boolean beanData = tokenBean.getData();
                            UserHelper.init().setCheckUserTokenResult(beanData);
                            if (!beanData) {//调退出登录
                                exitLogin(activity, null);
                            }
                        }
                    }

                    @Override
                    public void showExtraOp(String message) {

                    }

                    @Override
                    public void netConnectError() {

                    }
                });
    }

    /**
     * 退出登录
     *
     * @param activity
     * @param resultListener 请求结果回调，不需要刻意传空
     */
    public static void exitLogin(Activity activity, RequestResultListener resultListener) {
        if (TextUtils.isEmpty(UserHelper.init().getToken()) || TextUtils.isEmpty(UserHelper.init().getCustomerId())) {
            return;
        }
        ApiModule.getRetrofit().create(UserApiService.class)
                .exitLogin().subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(new CommonSubscriber<ExitLoginBean>() {
                    @Override
                    public void getData(ExitLoginBean exitLoginBean) {
                        if (exitLoginBean != null) {
                            if (activity != null) {
                                AuthorizedLogin.getInstance().delAuthorized(activity, SHARE_MEDIA.WEIXIN);
                            }
                            UserHelper.init().clearCurrentUserInfo();
                            EventBus.getDefault().post(EXIT_SUCCESS);
                            if (resultListener != null) {
                                resultListener.requestSuccess();
                            }
                        }
                    }

                    @Override
                    public void showExtraOp(String message) {
                        if (resultListener != null) {
                            resultListener.requestFail();
                        }
                    }

                    @Override
                    public void netConnectError() {
                        if (resultListener != null) {
                            resultListener.requestFail();
                        }
                    }
                });
    }

    /**
     * 广告双倍请求
     */
    public static void guaGuaBubbleDoubleRequest(Context mContext, String id, RequestResultListener resultListener) {
        if (!UserHelper.init().isLogin() || TextUtils.isEmpty(id)) {
            return;
        }
        ApiModule.getRetrofit().create(UserApiService.class)
                .guaGuaBubbleDouble(id).subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(new CommonSubscriber<GuaGuaDoubleBean>() {
                    @Override
                    public void getData(GuaGuaDoubleBean dataBean) {
                        RequestUserInfoUtil.getUserCoinInfo(); //更新UI金币信息；
                        int num = dataBean.getData().getGold();
                        if (resultListener != null) {
                            resultListener.requestSuccess();
                        }
                    }

                    @Override
                    public void showExtraOp(String message) {
                        if (resultListener != null) {
                            resultListener.requestFail();
                        }
                    }

                    @Override
                    public void netConnectError() {
                        if (resultListener != null) {
                            resultListener.requestFail();
                        }
                    }
                });
    }
}
