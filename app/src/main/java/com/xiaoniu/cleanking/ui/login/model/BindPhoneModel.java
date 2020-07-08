package com.xiaoniu.cleanking.ui.login.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.ArmBaseModel;
import com.xiaoniu.cleanking.api.CommonApiService;
import com.xiaoniu.cleanking.ui.login.bean.RequestPhoneBean;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:03
 * ================================================
 */
@ActivityScope
public class BindPhoneModel extends ArmBaseModel implements BindPhoneContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public BindPhoneModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<RequestPhoneBean> getPhoneNumFromShanYan(String token) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .getPhoneNumFromShanYanApi(token))
                .flatMap(new Function<Observable<RequestPhoneBean>, ObservableSource<RequestPhoneBean>>() {
                    @Override
                    public ObservableSource<RequestPhoneBean> apply(@NonNull Observable<RequestPhoneBean> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }
}