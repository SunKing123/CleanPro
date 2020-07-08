package com.xiaoniu.cleanking.ui.weather.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.ArmBaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.xiaoniu.cleanking.ui.weather.contract.WeatherForecastContract;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/08/2020 09:13
 */
@ActivityScope
public class WeatherForecastModel extends ArmBaseModel implements WeatherForecastContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public WeatherForecastModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}