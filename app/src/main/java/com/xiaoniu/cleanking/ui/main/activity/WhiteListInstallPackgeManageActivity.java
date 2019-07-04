package com.xiaoniu.cleanking.ui.main.activity;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListInstallPManagePresenter;

/**
 * 安装包白名单管理
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListInstallPackgeManageActivity extends BaseActivity<WhiteListInstallPManagePresenter> {


    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_install_package;
    }

    @Override
    protected void initView() {

    }
}
