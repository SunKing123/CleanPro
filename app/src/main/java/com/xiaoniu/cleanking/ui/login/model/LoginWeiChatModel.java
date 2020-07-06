package com.xiaoniu.cleanking.ui.login.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.ArmBaseModel;
import com.xiaoniu.cleanking.api.CommonApiService;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;
import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
@ActivityScope
public class LoginWeiChatModel extends ArmBaseModel implements LoginWeiChatContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public LoginWeiChatModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<LoginDataBean> loginWithWeiChat(RequestBody body) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .loginWeiChatApi(body))
                .flatMap(new Function<Observable<LoginDataBean>, ObservableSource<LoginDataBean>>() {
                    @Override
                    public ObservableSource<LoginDataBean> apply(@NonNull Observable<LoginDataBean> listObservable) throws Exception {
                        return listObservable;
                    }
                });
//        return Observable.just(mRepositoryManager
//                .obtainRetrofitService(LoginService.class)
//                .loginWeiChatApi());
    }

    @Override
    public Observable<LoginDataBean> bindingWeiChat(RequestBody body) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .bindingWeiChatApi(body))
                .flatMap(new Function<Observable<LoginDataBean>, ObservableSource<LoginDataBean>>() {
                    @Override
                    public ObservableSource<LoginDataBean> apply(@NonNull Observable<LoginDataBean> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }
}