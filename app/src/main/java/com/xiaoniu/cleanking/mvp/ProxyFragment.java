package com.xiaoniu.cleanking.mvp;

class ProxyFragment<V extends IBaseView> extends ProxyImpl {

    ProxyFragment(V view) {
        super(view);
    }
}
