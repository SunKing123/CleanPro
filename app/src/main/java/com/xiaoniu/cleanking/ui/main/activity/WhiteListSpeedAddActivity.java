package com.xiaoniu.cleanking.ui.main.activity;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.presenter.WhiteListSpeedAddPresenter;

/**
 * 加速白名单添加
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedAddActivity extends BaseActivity<WhiteListSpeedAddPresenter> {

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_list_speed_add;
    }

    @Override
    protected void initView() {

    }
}
