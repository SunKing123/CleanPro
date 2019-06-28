package com.installment.mall.ui.main.presenter;


import com.installment.mall.base.RxPresenter;
import com.installment.mall.ui.main.activity.SplashActivity;
import com.installment.mall.ui.main.model.MainModel;
import com.installment.mall.utils.StatisticsUtils;
import com.installment.mall.utils.prefs.NoClearSPHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashPresenter extends RxPresenter<SplashActivity, MainModel> {

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
        StatisticsUtils.burying(StatisticsUtils.BuryEvent.ActivatingQuantity, new StatisticsUtils.OnResponseListener() {
            @Override
            public void onSuccess(String result) {
                mSPHelper.setIsFristStart(true);
            }

            @Override
            public void onError(String result) {

            }
        });
    }
}
