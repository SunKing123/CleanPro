package com.xiaoniu.cleanking.app.injector.component;

import com.xiaoniu.cleanking.app.injector.PerActivity;
import com.xiaoniu.cleanking.app.injector.module.ActivityModule;
import com.xiaoniu.cleanking.ui.main.activity.CleanInstallPackageActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
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
    void inject(FileManagerHomeActivity fileManagerHomeActivity);

    void inject(CleanInstallPackageActivity activity);
}

