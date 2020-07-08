package com.xiaoniu.cleanking.ui.weather.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.xiaoniu.cleanking.ui.weather.contract.WeatherForecastContract;
import com.xiaoniu.cleanking.ui.weather.model.WeatherForecastModel;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/08/2020 09:13
 */
@Module
public abstract class WeatherForecastModule {

    @Binds
    abstract WeatherForecastContract.Model bindWeatherForecastModel(WeatherForecastModel model);
}