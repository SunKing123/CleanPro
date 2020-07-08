package com.xiaoniu.cleanking.ui.weather.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.xiaoniu.cleanking.ui.weather.di.module.WeatherForecastModule;
import com.xiaoniu.cleanking.ui.weather.contract.WeatherForecastContract;

import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.weather.activity.WeatherForecastActivity;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/08/2020 09:13
 */
@ActivityScope
@Component(modules = WeatherForecastModule.class, dependencies = AppComponent.class)
public interface WeatherForecastComponent {
    void inject(WeatherForecastActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        WeatherForecastComponent.Builder view(WeatherForecastContract.View view);

        WeatherForecastComponent.Builder appComponent(AppComponent appComponent);

        WeatherForecastComponent build();
    }
}