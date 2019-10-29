package com.xiaoniu.cleanking.app.injector.module;

import com.xiaoniu.cleanking.app.injector.PerActivity;
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
