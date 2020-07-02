package com.xiaoniu.cleanking.ui.login.model;

import android.app.Application;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;

import com.jess.arms.di.scope.ActivityScope;
import javax.inject.Inject;

import com.jess.arms.mvp.ArmBaseModel;
import com.xiaoniu.cleanking.ui.login.contract.BindPhoneContract;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 18:03
 * ================================================
 */
@ActivityScope
public class BindPhoneModel extends ArmBaseModel implements BindPhoneContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public BindPhoneModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}