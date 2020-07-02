package com.xiaoniu.cleanking.ui.login.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.xiaoniu.cleanking.ui.login.contract.LoginWeiChatContract;
import com.xiaoniu.cleanking.ui.login.model.LoginWeiChatModel;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
@Module
public abstract class LoginWeiChatModule {

    @Binds
    abstract LoginWeiChatContract.Model bindLoginWeiChatModel(LoginWeiChatModel model);
}