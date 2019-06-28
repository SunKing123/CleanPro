package com.installment.mall.base;

/**
 * Presenter基类
 */
public interface BasePresenter<T extends BaseView,V extends BaseModel>{

    void attachView(T view);

    void detachView();
}
