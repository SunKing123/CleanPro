package com.xiaoniu.cleanking.ui.usercenter.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.xiaoniu.cleanking.ui.usercenter.contract.AboutInfoContract;
import com.xiaoniu.cleanking.ui.usercenter.model.AboutInfoModel;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
@Module
public abstract class AboutInfoModule {

    @Binds
    abstract AboutInfoContract.Model bindAboutInfoModel(AboutInfoModel model);
}