package com.xiaoniu.cleanking.ui.usercenter.model;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.ArmBaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.xiaoniu.cleanking.api.CommonApiService;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.api.cache.CommonCache;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.usercenter.contract.AboutInfoContract;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
@ActivityScope
public class AboutInfoModel extends ArmBaseModel implements AboutInfoContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public AboutInfoModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<AppVersion> getVersion(Context ctx) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .queryAppVersion())
                .flatMap(new Function<Observable<AppVersion>, ObservableSource<AppVersion>>() {
                    @Override
                    public ObservableSource<AppVersion> apply(@NonNull Observable<AppVersion> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }
}