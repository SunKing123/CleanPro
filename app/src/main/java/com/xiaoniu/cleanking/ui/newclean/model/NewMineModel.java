package com.xiaoniu.cleanking.ui.newclean.model;


import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.api.UserApiService;
import com.xiaoniu.cleanking.base.BaseModel;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;

import javax.inject.Inject;


public class NewMineModel extends BaseModel {

    private final RxFragment mRxFragment;
    @Inject
    UserApiService mService;

    @Inject
    public NewMineModel(RxFragment rxFragment) {
        mRxFragment = rxFragment;
    }

    public void queryAppVersion(Common4Subscriber<AppVersion> commonSubscriber) {
//        mService.queryAppVersion().compose(RxUtil.<AppVersion>rxSchedulerHelper(mRxFragment)).subscribeWith(commonSubscriber);
    }
}
