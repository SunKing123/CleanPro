package com.xiaoniu.cleanking.ui.main.presenter;

import com.xiaoniu.cleanking.base.BaseView;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashPresenter extends RxPresenter<BaseView, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    private int tryCount = 0;
    private int maxTryCount = 3;

    @Inject
    public SplashPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    public void setFirstStart() {
    }

    //过审
    public void getAuditSwitch() {
//        mModel.queryAuditSwitch(new Common4Subscriber<AuditSwitch>() {
//
//
//            @Override
//            public void getData(AuditSwitch auditSwitch) {
//                mView.getAuditSwitch(auditSwitch);
//            }
//
//            @Override
//            public void showExtraOp(String code, String message) {
//                mView.getAuditSwitch(null);
//            }
//
//            @Override
//            public void showExtraOp(String message) {
//                mView.getAuditSwitch(null);
//            }
//
//            @Override
//            public void netConnectError() {
//                mView.getAuditSwitch(null);
//            }
//        });
    }
}
