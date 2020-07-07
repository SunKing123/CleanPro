package com.xiaoniu.cleanking.ui.login.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.ArmBaseModel;
import com.xiaoniu.cleanking.api.CommonApiService;
import com.xiaoniu.cleanking.base.BaseEntity;
import com.xiaoniu.cleanking.ui.login.bean.BindPhoneBean;
import com.xiaoniu.cleanking.ui.login.bean.IsPhoneBindBean;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneManualContract;

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
 * Created by MVPArmsTemplate on 07/02/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class BindPhoneManualModel extends ArmBaseModel implements BindPhoneManualContract.Model{
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public BindPhoneManualModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<IsPhoneBindBean> checkPhoneBinded(String phoneNum) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .checkPhoneBindedApi(phoneNum))
                .flatMap(new Function<Observable<IsPhoneBindBean>, ObservableSource<IsPhoneBindBean>>() {
                    @Override
                    public ObservableSource<IsPhoneBindBean> apply(@NonNull Observable<IsPhoneBindBean> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }

    @Override
    public Observable<BindPhoneBean> phoneBind(RequestBody body) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .phoneBindApi(body))
                .flatMap(new Function<Observable<BindPhoneBean>, ObservableSource<BindPhoneBean>>() {
                    @Override
                    public ObservableSource<BindPhoneBean> apply(@NonNull Observable<BindPhoneBean> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }

    @Override
    public Observable<BaseEntity> sendMsg(RequestBody body) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonApiService.class)
                .sendMsgApi(body))
                .flatMap(new Function<Observable<BaseEntity>, ObservableSource<BaseEntity>>() {
                    @Override
                    public ObservableSource<BaseEntity> apply(@NonNull Observable<BaseEntity> listObservable) throws Exception {
                        return listObservable;
                    }
                });
    }
}