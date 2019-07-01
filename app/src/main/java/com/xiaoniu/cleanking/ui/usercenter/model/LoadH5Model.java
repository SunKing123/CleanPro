package com.xiaoniu.cleanking.ui.usercenter.model;

import com.xiaoniu.cleanking.base.BaseModel;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

/**
 * Created by fengpeihao on 2017/6/14.
 */

public class LoadH5Model extends BaseModel {

    private final RxAppCompatActivity mRxAppCompatActivity;

    @Inject
    public LoadH5Model(RxAppCompatActivity rxAppCompatActivity) {
        mRxAppCompatActivity = rxAppCompatActivity;
    }
}
