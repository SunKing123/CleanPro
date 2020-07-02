package com.xiaoniu.cleanking.ui.login.di.component;

import dagger.BindsInstance;
import dagger.Component;
import com.jess.arms.di.component.AppComponent;

import com.xiaoniu.cleanking.ui.login.di.module.BindPhoneModule;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;

import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.login.activity.BindPhoneActivity;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:03
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = BindPhoneModule.class, dependencies = AppComponent.class)
public interface BindPhoneComponent {
    void inject(BindPhoneActivity activity);
    @Component.Builder
    interface Builder {
        @BindsInstance
        BindPhoneComponent.Builder view(BindPhoneContract.View view);
        BindPhoneComponent.Builder appComponent(AppComponent appComponent);
        BindPhoneComponent build();
    }
}