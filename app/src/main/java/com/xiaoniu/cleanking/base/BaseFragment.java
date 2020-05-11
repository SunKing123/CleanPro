package com.xiaoniu.cleanking.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.app.injector.component.DaggerFragmentComponent;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.app.injector.module.FragmentModule;
import com.xiaoniu.common.utils.ToastUtils;

import javax.inject.Inject;

/**
 * Created by codeest on 2016/8/2.
 * MVP Fragment基类
 */
public abstract class BaseFragment<T extends BasePresenter> extends SimpleFragment implements BaseView {


    private boolean mViewCreated = false;   // 标示View 是否已经创建
    private boolean mSupportLazy = false;   // 默认不支持惰加载
    private boolean mIsLoaded = false;      // 是否已经惰加载过数据
    private boolean mVisibleToUser = false;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mSupportLazy) {
            if (mVisibleToUser && !mIsLoaded) {
                mIsLoaded = true;
                loadData(); //用于第一个fragment
            }
        } else {
            mIsLoaded = true;
            loadData();
        }
    }

    /**
     * 该方法会先于onCreate和onCreateView 执行,适合ViewPager切换情况
     * 且每次都在Fragment可见或不可见时调用一次，所以需要判断是否创建View
     * 该方案与onHiddenChanged方案同时只有一个生效，不会有冲突
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            onVisibleToUser();
        }
        if (mSupportLazy && isVisibleToUser && mViewCreated && !mIsLoaded) {
            mIsLoaded = true;
            loadData();
        }
    }

    /**
     * 适合自己控制Fragment显示隐藏情况，要想实现懒加载，必须先hide所有Fragment，然后再show需要的显示的fragment，
     * 如果使用该方案实现懒加载，必须按要求实现逻辑，否则loadData方法不会调用
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisibleToUser();
        }
        if (mSupportLazy) {
            if (!hidden && mViewCreated && !mIsLoaded) {
                mIsLoaded = true;
                loadData();
            }
        }
    }

    /*每次Fragment可见都会调用*/
    protected void onVisibleToUser() {
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

    protected void setSupportLazy(boolean supportLazy) {
        mSupportLazy = supportLazy;
    }

    /**
     * 和lazyLoadData选择性使用
     */
    protected void loadData() {

    }

    protected void refresh() {

    }
}