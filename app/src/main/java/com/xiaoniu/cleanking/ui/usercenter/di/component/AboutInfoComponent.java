package com.xiaoniu.cleanking.ui.usercenter.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.xiaoniu.cleanking.ui.usercenter.di.module.AboutInfoModule;
import com.xiaoniu.cleanking.ui.usercenter.contract.AboutInfoContract;

import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutInfoActivity;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
@ActivityScope
@Component(modules = AboutInfoModule.class, dependencies = AppComponent.class)
public interface AboutInfoComponent {
    void inject(AboutInfoActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        AboutInfoComponent.Builder view(AboutInfoContract.View view);

        AboutInfoComponent.Builder appComponent(AppComponent appComponent);

        AboutInfoComponent build();
    }
}