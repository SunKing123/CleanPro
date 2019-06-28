package com.installment.mall.app.injector.module;

import com.installment.mall.app.injector.PerActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tie on 2017/3/20.
 */
@Module
public class ActivityModule {

    private RxAppCompatActivity mActivity;

    public ActivityModule(RxAppCompatActivity compatActivity) {
        mActivity = compatActivity;
    }

    @Provides
    @PerActivity
    RxAppCompatActivity getActivity(){
        return mActivity;
    }
}
