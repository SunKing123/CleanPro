package com.installment.mall.base;


import android.text.TextUtils;

import com.installment.mall.app.injector.component.ActivityComponent;
import com.installment.mall.app.injector.component.DaggerActivityComponent;
import com.installment.mall.app.injector.module.ActivityModule;
import com.installment.mall.hotfix.ApplicationDelegate;
import com.installment.mall.utils.ToastUtils;

import javax.inject.Inject;

/**
 * MVP activity基类
 */
public abstract class BaseActivity<T extends BasePresenter> extends SimpleActivity implements BaseView {

    @Inject
    protected T mPresenter;

    private void initInjector() {
        ActivityComponent activityComponent = DaggerActivityComponent.builder()
                .appComponent(ApplicationDelegate.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();

        inject(activityComponent);
        /**
         *
         */
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

    protected void refresh() {

    }
}