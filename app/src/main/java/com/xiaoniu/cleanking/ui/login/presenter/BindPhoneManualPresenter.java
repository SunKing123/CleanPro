package com.xiaoniu.cleanking.ui.login.presenter;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.login.bean.BindPhoneBean;
import com.xiaoniu.cleanking.ui.login.bean.IsPhoneBindBean;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneManualContract;

import java.util.HashMap;
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
 * Created by MVPArmsTemplate on 07/02/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class BindPhoneManualPresenter extends BasePresenter<BindPhoneManualContract.Model, BindPhoneManualContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    AppManager mAppManager;

    @Inject
    public BindPhoneManualPresenter(BindPhoneManualContract.Model model, BindPhoneManualContract.View rootView) {
        super(model, rootView);
    }

    public void checkPhoneBinded(String phoneNum) {
        mModel.checkPhoneBinded(phoneNum).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<IsPhoneBindBean>(mErrorHandler) {
                    @Override
                    public void onNext(IsPhoneBindBean loginDataBean) {
                        if (mRootView != null) {
                            mRootView.getIsPhoneBindedSuccess(loginDataBean);
                        }
                    }
                });
    }

    public void phoneBind(String phoneNum, String code) {
        Map<String, Object> requestMap = new HashMap<>();
        Gson gson = new Gson();
        requestMap.put("phoneNum", phoneNum);
        requestMap.put("bizType", "phoneNum-手机号绑定");
        requestMap.put("msgCode", code);
        String json = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.phoneBind(body).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BindPhoneBean>(mErrorHandler) {
                    @Override
                    public void onNext(BindPhoneBean loginDataBean) {
                        if (mRootView != null) {
                            mRootView.getBindPhoneSuccess(loginDataBean);
                        }
                    }
                });
    }

    public void getCode(String phoneNum) {
        Map<String, Object> requestMap = new HashMap<>();
        Gson gson = new Gson();
        requestMap.put("phoneNum", phoneNum);
        requestMap.put("bizType", "wkBindPhone");
        String json = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        mModel.sendMsg(body).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BaseEntity>(mErrorHandler) {
                    @Override
                    public void onNext(BaseEntity msd) {
                        if (mRootView != null) {
                            mRootView.getCodeSuccess();
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
