package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashHotPresenter extends RxPresenter<SplashADHotActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public SplashHotPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void getSwitchInfoList(){
        mModel.getSwitchInfoList(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                if(null != switchInfoList)
                    AppHolder.getInstance().setSwitchInfoMap(switchInfoList.getData());
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {
                if (null != ApplicationDelegate.getAppDatabase() && null != ApplicationDelegate.getAppDatabase().adInfotDao()) {
                    AppHolder.getInstance().setSwitchInfoMap(ApplicationDelegate.getAppDatabase().adInfotDao().getAll());
                }
            }
        });
    }
}
