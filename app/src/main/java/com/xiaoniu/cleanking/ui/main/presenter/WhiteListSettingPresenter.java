package com.xiaoniu.cleanking.ui.main.presenter;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSettingActivity;
import com.xiaoniu.cleanking.ui.main.bean.ExitLoginBean;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.ToastUtils;

import javax.inject.Inject;


public class WhiteListSettingPresenter extends RxPresenter<WhiteListSettingActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public WhiteListSettingPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 退出登录
     */
    public void exitUserLogin() {
        mModel.exitLogin(new CommonSubscriber<ExitLoginBean>() {

            @Override
            public void getData(ExitLoginBean exitLoginBean) {
                if (mView != null) {
                    mView.exitLoginResult(exitLoginBean);
                }
            }

            @Override
            public void showExtraOp(String message) {
                ToastUtils.showShort(message);
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort("网络连接失败，请稍后重试！");
            }
        });
    }
}
