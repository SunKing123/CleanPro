package com.xiaoniu.cleanking.app.injector.component;

import com.xiaoniu.cleanking.app.injector.PerFragment;
import com.xiaoniu.cleanking.app.injector.module.FragmentModule;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerFragment
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

}
