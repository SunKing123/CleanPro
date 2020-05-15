package com.xiaoniu.cleanking.mvp;

public interface IBasePresenter {

    void attach(IBaseView view);

    void detach();
}
