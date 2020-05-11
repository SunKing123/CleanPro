package com.xiaoniu.cleanking.mvp;

class ProxyActivity<V extends IBaseView> extends ProxyImpl {

    ProxyActivity(V view) {
        super(view);
    }
}
