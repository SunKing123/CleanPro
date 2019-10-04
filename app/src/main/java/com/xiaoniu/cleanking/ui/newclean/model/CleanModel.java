package com.xiaoniu.cleanking.ui.newclean.model;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.base.BaseModel;

import javax.inject.Inject;

public class CleanModel extends BaseModel {

    private final RxFragment mRxFragment;

    @Inject
    public CleanModel(RxFragment rxFragment) {
        mRxFragment = rxFragment;
    }

}
