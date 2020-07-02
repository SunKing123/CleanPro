package com.xiaoniu.cleanking.ui.login.di.component;

import dagger.BindsInstance;
import dagger.Component;
import com.jess.arms.di.component.AppComponent;

import com.xiaoniu.cleanking.ui.login.di.module.BindPhoneManualModule;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneManualContract;

import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.login.activity.BindPhoneManualActivity;   


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = BindPhoneManualModule.class, dependencies = AppComponent.class)
public interface BindPhoneManualComponent {
    void inject(BindPhoneManualActivity activity);
    @Component.Builder
    interface Builder {
        @BindsInstance
        BindPhoneManualComponent.Builder view(BindPhoneManualContract.View view);
        BindPhoneManualComponent.Builder appComponent(AppComponent appComponent);
        BindPhoneManualComponent build();
    }
}