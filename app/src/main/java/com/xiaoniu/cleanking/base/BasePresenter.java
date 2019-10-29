package com.xiaoniu.cleanking.base;

/**
 * Presenter基类
 */
public interface BasePresenter<T extends BaseView,V extends BaseModel>{

    void attachView(T view);

    void detachView();
}
