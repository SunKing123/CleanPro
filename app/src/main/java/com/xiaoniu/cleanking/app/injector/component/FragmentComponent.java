package com.xiaoniu.cleanking.app.injector.component;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.app.injector.PerFragment;
import com.xiaoniu.cleanking.app.injector.module.FragmentModule;
import com.xiaoniu.cleanking.ui.newclean.fragment.MineFragment;
import com.xiaoniu.cleanking.ui.main.fragment.QQImgFragment;
import com.xiaoniu.cleanking.ui.main.fragment.QQVideoFragment;
import com.xiaoniu.cleanking.ui.main.fragment.ToolFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgCameraFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgChatFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXImgSaveListFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoCameraFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoChatFragment;
import com.xiaoniu.cleanking.ui.main.fragment.WXVideoSaveListFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.CleanFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewPlusCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.tool.wechat.fragment.WXFileFragment;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerFragment
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    RxFragment getFragment();

//    void inject(CleanMainFragment cleanMainFragment);

    void inject(NewCleanMainFragment newCleanMainFragment);

    void inject(NewPlusCleanMainFragment newPlusCleanMainFragment);

    void inject(ScanFragment scanFragment);

    void inject(CleanFragment cleanFragment);

    void inject(ToolFragment toolFragment);

    void inject(WXImgSaveListFragment fragmentComponent);

    void inject(WXImgCameraFragment wxImgCameraFragment);

    void inject(WXImgChatFragment wxImgChatFragment);

    void inject(WXVideoSaveListFragment wxVideoSaveListFragment);

    void inject(WXVideoChatFragment wxVideoChatFragment);

    void inject(WXVideoCameraFragment wxVideoCameraFragment);

    void inject(WXFileFragment wxVideoCameraFragment);

    void inject(QQImgFragment qqImgFragment);

    void inject(QQVideoFragment qqVideoFragment);

    void inject(MineFragment mineFragment);
}
