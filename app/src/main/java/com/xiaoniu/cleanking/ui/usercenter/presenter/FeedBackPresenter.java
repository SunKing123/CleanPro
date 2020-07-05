package com.xiaoniu.cleanking.ui.usercenter.presenter;


import android.text.TextUtils;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.usercenter.activity.FeedBackActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class FeedBackPresenter extends RxPresenter<FeedBackActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public FeedBackPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


}
