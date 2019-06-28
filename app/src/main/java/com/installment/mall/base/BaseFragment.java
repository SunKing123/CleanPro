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
 * Created by codeest on 2016/8/2.
 * MVP Fragment基类
 */
public abstract class BaseFragment<T extends BasePresenter> extends SimpleFragment implements BaseView {

    @Inject
    protected T mPresenter;

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

    protected void refresh() {

    }
}