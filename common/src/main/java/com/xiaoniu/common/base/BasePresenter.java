package com.xiaoniu.common.base;

import android.os.Bundle;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public abstract class BasePresenter<V extends BaseView> {
    protected V mView;

    public void attachView(V view) {
        mView = view;
    }

    public void detachView() {
        mView = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onDestroy() {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }
}
