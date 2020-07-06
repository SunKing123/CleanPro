package com.xiaoniu.cleanking.ui.login.presenter;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
@ActivityScope
public class LoginWeiChatPresenter extends BasePresenter<LoginWeiChatContract.Model, LoginWeiChatContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    AppManager mAppManager;

    @Inject
    public LoginWeiChatPresenter(LoginWeiChatContract.Model model, LoginWeiChatContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 微信登录
     * @param map
     */
    public void loginWithWeiChat(Map<String, Object> map) {
        map.put("userType", 1);
        Gson gson = new Gson();
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.loginWithWeiChat(body).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<LoginDataBean>(mErrorHandler) {
                    @Override
                    public void onNext(LoginDataBean loginDataBean) {
                        if (mRootView != null) {
                            mRootView.dealLoginResult(1,loginDataBean);
                        }
                    }
                });
    }

    /**
     * 绑定微信到游客账户
     * @param map
     */
    public void bindingWeiChat(Map<String, Object> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.bindingWeiChat(body).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<LoginDataBean>(mErrorHandler) {
                    @Override
                    public void onNext(LoginDataBean loginDataBean) {
                        if (mRootView != null) {
                            mRootView.dealLoginResult(2,loginDataBean);
                        }
                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }
}
