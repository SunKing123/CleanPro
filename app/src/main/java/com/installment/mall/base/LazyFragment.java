package com.installment.mall.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.installment.mall.app.injector.component.DaggerFragmentComponent;
import com.installment.mall.app.injector.component.FragmentComponent;
import com.installment.mall.app.injector.module.FragmentModule;
import com.installment.mall.hotfix.ApplicationDelegate;
import com.installment.mall.utils.ToastUtils;

import javax.inject.Inject;

/**
 * Created by fengpeihao on 2018/5/7.
 */

public abstract class LazyFragment<T extends BasePresenter> extends SimpleFragment implements BaseView {
    @Inject
    protected T mPresenter;
    protected boolean isFragmentCreated = false;
    protected boolean isVisibleToUser = false;
    protected boolean isFirstShow = true;

    private void initInjector() {
        FragmentComponent fragmentComponent = DaggerFragmentComponent.builder()
                .appComponent(ApplicationDelegate.getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();

        inject(fragmentComponent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initInjector();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onViewCreated(view, savedInstanceState);
        isFragmentCreated = true;
        if (isVisibleToUser && isFirstShow) {
            isFirstShow = false;
            lazyInit();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isFragmentCreated && isFirstShow) {
            isFirstShow = false;
            lazyInit();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isFragmentCreated && isFirstShow) {
            isFirstShow = false;
            lazyInit();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.showShort(msg);
        }
    }

    protected abstract void inject(FragmentComponent fragmentComponent);

    protected abstract void lazyInit();

    protected void refresh() {

    }
}
