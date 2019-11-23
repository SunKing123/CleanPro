package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.newclean.activity.InsertScreenFinishActivity;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class InsertScreenFinishPresenter extends RxPresenter<InsertScreenFinishActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public InsertScreenFinishPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

}
