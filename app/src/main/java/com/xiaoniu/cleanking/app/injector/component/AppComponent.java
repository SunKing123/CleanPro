package com.xiaoniu.cleanking.app.injector.component;


import android.app.Application;

import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;

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
}

