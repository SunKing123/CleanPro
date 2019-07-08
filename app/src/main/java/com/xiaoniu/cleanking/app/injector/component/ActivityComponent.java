package com.xiaoniu.cleanking.app.injector.component;

import com.xiaoniu.cleanking.app.injector.PerActivity;
import com.xiaoniu.cleanking.app.injector.module.ActivityModule;
import com.xiaoniu.cleanking.ui.main.activity.CleanBigFileActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanInstallPackageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanMusicManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.CleanVideoManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.ImageActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneCoolingActivity;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListInstallPackgeManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedAddActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedManageActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.FeedBackActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import dagger.Component;

/**
 * Created by tie on 2017/3/20.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    RxAppCompatActivity getActivity();

    void inject(MainActivity mainActivity);

    void inject(UserLoadH5Activity userLoadH5Activity);

    void inject(SplashActivity splashActivity);

    void inject(FileManagerHomeActivity fileManagerHomeActivity);

    void inject(ImageActivity imageListActivity);

    void inject(CleanInstallPackageActivity activity);

    void inject(CleanMusicManageActivity cleanMusicManageActivity);
    void inject(PreviewImageActivity previewImageActivity);

    void inject(CleanVideoManageActivity cleanVideoManageActivity);


    void inject(WhiteListInstallPackgeManageActivity whiteListInstallPackgeManageActivity);

    void inject(WhiteListSpeedAddActivity whiteListSpeedAddActivity);

    void inject(WhiteListSpeedManageActivity whiteListSpeedManageActivity);
    void inject(AboutActivity aboutActivity);
    void inject(PhoneAccessActivity phoneAccessActivity);
    void inject(FeedBackActivity feedBackActivity);

    void inject(PhoneCoolingActivity phoneCoolingActivity);

    void inject(QuestionReportActivity questionReportActivity);

    void inject(CleanBigFileActivity cleanBigFileActivity);
}

