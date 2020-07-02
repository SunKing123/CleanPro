package com.xiaoniu.cleanking.ui.login.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.xiaoniu.cleanking.ui.login.di.module.LoginWeiChatModule;
import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;

import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
@ActivityScope
@Component(modules = LoginWeiChatModule.class, dependencies = AppComponent.class)
public interface LoginWeiChatComponent {
    void inject(LoginWeiChatActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        LoginWeiChatComponent.Builder view(LoginWeiChatContract.View view);

        LoginWeiChatComponent.Builder appComponent(AppComponent appComponent);

        LoginWeiChatComponent build();
    }
}