package com.xiaoniu.cleanking.ui.main.presenter;


import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashActivity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
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
