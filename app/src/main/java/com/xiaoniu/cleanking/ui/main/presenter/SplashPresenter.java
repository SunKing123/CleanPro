package com.xiaoniu.cleanking.ui.main.presenter;


import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashPresenter extends RxPresenter<SplashADActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public SplashPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


    /**
     * 过审
     */
    public void getAuditSwitch() {

        Observable.create(new ObservableOnSubscribe<AuditSwitch>() {

            @Override
            public void subscribe(ObservableEmitter<AuditSwitch> e) throws Exception {
                mModel.queryAuditSwitch(new Common4Subscriber<AuditSwitch>() {

                    @Override
                    public void getData(AuditSwitch auditSwitch) {
                        e.onNext(auditSwitch);
                        e.onComplete();
                    }

                    @Override
                    public void showExtraOp(String code, String message) {
                        e.onError(new RuntimeException(message));
                    }

                    @Override
                    public void showExtraOp(String message) {
                        e.onError(new RuntimeException(message));
                    }

                    @Override
                    public void netConnectError() {
                        e.onError(new RuntimeException("网络异常"));
                    }
                });
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(6, TimeUnit.SECONDS)
                .subscribe(new Consumer<AuditSwitch>() {
                    @Override
                    public void accept(AuditSwitch auditSwitch) throws Exception {
                        mView.getAuditSwitch(auditSwitch);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("--------------","time out");
                        mView.getAuditSwitchFailure();
                    }
                });
    }


}
