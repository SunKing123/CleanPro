package com.xiaoniu.cleanking.app.injector.module;

import android.app.Application;
import android.content.Context;

import com.xiaoniu.cleanking.utils.prefs.ImplPreferencesHelper;
import com.xiaoniu.cleanking.utils.prefs.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tie on 2017/3/20.
 */
@Module
public class AppModule {

    private Application mApp;

    public AppModule(Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    Context getApp() {
        return mApp;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(ImplPreferencesHelper implPreferencesHelper) {
        return implPreferencesHelper;
    }
}
