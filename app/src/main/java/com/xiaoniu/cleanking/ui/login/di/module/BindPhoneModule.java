package com.xiaoniu.cleanking.ui.login.di.module;

import dagger.Binds;
import dagger.Module;

import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;
import com.xiaoniu.cleanking.ui.login.model.BindPhoneModel;


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
@Module
public abstract class BindPhoneModule {

    @Binds
    abstract BindPhoneContract.Model bindBindPhoneModel(BindPhoneModel model);
}