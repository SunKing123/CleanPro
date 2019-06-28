package com.installment.mall.app.injector.component;

import com.installment.mall.app.injector.PerFragment;
import com.installment.mall.app.injector.module.FragmentModule;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerFragment
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

}
