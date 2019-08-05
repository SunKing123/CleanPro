package com.xiaoniu.cleanking.app.injector.component;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.app.injector.PerFragment;
import com.xiaoniu.cleanking.app.injector.module.FragmentModule;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgCameraFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgChatFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgSaveListFragment;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerFragment
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    RxFragment getFragment();

    void inject(CleanMainFragment cleanMainFragment);

    void inject(ToolFragment toolFragment);

    void inject(WXImgSaveListFragment fragmentComponent);

    void inject(WXImgCameraFragment wxImgCameraFragment);

    void inject(WXImgChatFragment wxImgChatFragment);
}
