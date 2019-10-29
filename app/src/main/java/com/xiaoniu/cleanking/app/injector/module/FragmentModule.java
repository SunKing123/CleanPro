package com.xiaoniu.cleanking.app.injector.module;

import android.content.Context;

import com.xiaoniu.cleanking.app.injector.ContextLife;
import com.xiaoniu.cleanking.app.injector.PerFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tie on 2017/3/20.
 */
@Module
public class FragmentModule {

    private RxFragment mFragment;

    public FragmentModule(RxFragment rxFragment) {
        mFragment = rxFragment;
    }

    @Provides
    @PerFragment
    @ContextLife(value = "RxActivity")
    Context provideContext() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    RxFragment provideFragment() {
        return mFragment;
    }
}
