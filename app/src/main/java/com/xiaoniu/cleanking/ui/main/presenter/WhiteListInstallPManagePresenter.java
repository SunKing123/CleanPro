package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListInstallPackgeManageActivity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import javax.inject.Inject;

/**
 * Created by lang.chen on 2019/7/4
 */
public class WhiteListInstallPManagePresenter extends RxPresenter<WhiteListInstallPackgeManageActivity, MainModel> {

    RxAppCompatActivity activity;

    @Inject
    public WhiteListInstallPManagePresenter(RxAppCompatActivity activity) {
        this.activity = activity;
    }

}
