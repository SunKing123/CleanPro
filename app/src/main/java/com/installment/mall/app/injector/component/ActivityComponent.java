package com.installment.mall.app.injector.component;

import com.installment.mall.app.injector.PerActivity;
import com.installment.mall.app.injector.module.ActivityModule;
import com.installment.mall.ui.main.activity.MainActivity;
import com.installment.mall.ui.main.activity.SplashActivity;
import com.installment.mall.ui.usercenter.activity.UserLoadH5Activity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    RxAppCompatActivity getActivity();

    void inject(MainActivity mainActivity);

    void inject(UserLoadH5Activity userLoadH5Activity);

    void inject(SplashActivity splashActivity);
}

