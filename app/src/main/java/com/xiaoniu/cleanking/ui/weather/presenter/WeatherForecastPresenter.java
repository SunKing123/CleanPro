package com.xiaoniu.cleanking.ui.weather.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.xiaoniu.cleanking.ui.weather.contract.WeatherForecastContract;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/08/2020 09:13
 */
@ActivityScope
public class WeatherForecastPresenter extends BasePresenter<WeatherForecastContract.Model, WeatherForecastContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    AppManager mAppManager;

    @Inject
    public WeatherForecastPresenter(WeatherForecastContract.Model model, WeatherForecastContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }
}
