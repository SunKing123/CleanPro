package com.xiaoniu.cleanking.base;


import android.text.TextUtils;

import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerActivityComponent;
import com.xiaoniu.cleanking.app.injector.module.ActivityModule;
import com.xiaoniu.common.utils.ToastUtils;

import javax.inject.Inject;

/**
 * MVP activity基类
 */
public abstract class BaseActivity<T extends BasePresenter> extends SimpleActivity implements BaseView {

    @Inject
    protected T mPresenter;
    public String source_page;

    private void initInjector() {
        ActivityComponent activityComponent = DaggerActivityComponent.builder()
                .appComponent(ApplicationDelegate.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();

        inject(activityComponent);
    }

    /**
     * 注入
     */
    public abstract void inject(ActivityComponent activityComponent);

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        initInjector();
        if (mPresenter != null)
            mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.showShort(msg);
        }
    }
}