package com.xiaoniu.cleanking.app.injector.component;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
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
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinResultActivity;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.activity.QuestionReportActivity;
import com.xiaoniu.cleanking.ui.main.activity.SoftManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListInstallPackgeManageActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedAddActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedManageActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanAudActivity;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanFileActivity;
import com.xiaoniu.cleanking.ui.tool.qq.activity.QQCleanHomeActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanAudActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanFileActivity;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.FeedBackActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.PermissionActivity;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;

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

    void inject(SplashADActivity splashADActivity);

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

    void inject(PermissionActivity permissionActivity);

    void inject(PhoneThinActivity phoneThinActivity);

    void inject(PhoneThinResultActivity phoneThinResultActivity);

    void inject(SoftManageActivity softManageActivity);

    void inject(WechatCleanHomeActivity wechatCleanHomeActivity);

    void inject(WechatCleanAudActivity wechatCleanAudActivity);

    void inject(WechatCleanFileActivity wechatCleanFileActivity);

    void inject(QQCleanHomeActivity qqCleanHomeActivity);

    void inject(QQCleanFileActivity qqCleanFileActivity);

    void inject(QQCleanAudActivity qqCleanAudActivity);

    void inject(SplashADHotActivity splashADHotActivity);

    void inject(NewCleanFinishActivity newCleanFinishActivity);
}

