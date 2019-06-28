package com.installment.mall.app.injector.component;


import android.app.Application;

import com.installment.mall.api.BigDataApiService;
import com.installment.mall.api.UserApiService;
import com.installment.mall.app.injector.module.ApiModule;
import com.installment.mall.app.injector.module.AppModule;
import com.installment.mall.utils.prefs.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@Singleton
@Component(modules = {ApiModule.class, AppModule.class})
public interface AppComponent {

    void inject(Application mApplication);

    UserApiService getApiUserService();

    PreferencesHelper getPreferencesHelper();

    BigDataApiService getBigDataApiService();
}

