package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.newclean.activity.InsertScreenFinishActivity;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
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

    /**
     * 插屏广告开关
     */
    public void getScreentSwitch() {
        mModel.getScreentSwitch(new Common4Subscriber<InsertAdSwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InsertAdSwitchInfoList switchInfoList) {
                mView.getSwitchInfoListSuccess(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
                mView.getSwitchInfoListFail(message);
            }

            @Override
            public void netConnectError() {
                mView.getSwitchInfoListConnectError();
            }
        });
    }

}
