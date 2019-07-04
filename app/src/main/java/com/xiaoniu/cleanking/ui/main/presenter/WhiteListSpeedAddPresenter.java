package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedAddActivity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import javax.inject.Inject;

/**
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListSpeedAddPresenter extends RxPresenter<WhiteListSpeedAddActivity, MainModel> {

    RxAppCompatActivity activity;

    @Inject
    public WhiteListSpeedAddPresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }
}
