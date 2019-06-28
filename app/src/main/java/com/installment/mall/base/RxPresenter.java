package com.installment.mall.base;
import javax.inject.Inject;

/**
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */
public class RxPresenter<T extends BaseView, V extends BaseModel> implements BasePresenter<T,V> {

    @Inject
    protected V mModel;

    protected T mView;

    public RxPresenter() {
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {

    }
}
