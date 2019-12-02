package com.xiaoniu.cleanking.ui.main.presenter;

import android.util.Log;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.bean.ConfigBean;
import com.comm.jksdk.config.listener.ConfigListener;
import com.comm.jksdk.utils.JsonUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashPresenter extends RxPresenter<SplashADActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public SplashPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 冷启动、热启动、完成页广告开关
     */
    public void getSwitchInfoList() {
        mModel.getSwitchInfoList(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                mView.getSwitchInfoListSuccess(switchInfoList);
                AppHolder.getInstance().setSwitchInfoList(switchInfoList);
                Log.d("XiLei", "AppHolder.getInstance().setSwitchInfoList=" + AppHolder.getInstance().getSwitchInfoList().getData().size());
            }

            @Override
            public void showExtraOp(String message) {
                mView.getSwitchInfoListFail();
            }

            @Override
            public void netConnectError() {
                mView.getSwitchInfoListFail();
            }
        });
    }
    /**
     * 冷启动、热启动、完成页广告开关
     */
    public void getSwitchInfoListNew() {
        mModel.getSwitchInfoList(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                AppHolder.getInstance().setSwitchInfoList(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }

    /**
     * 过审
     */
    public void getAuditSwitch() {
        mModel.queryAuditSwitch(new Common4Subscriber<AuditSwitch>() {

            @Override
            public void getData(AuditSwitch auditSwitch) {
                mView.getAuditSwitch(auditSwitch);
            }

            @Override
            public void showExtraOp(String code, String message) {
                mView.getAuditSwitchFail();
            }

            @Override
            public void showExtraOp(String message) {
                mView.getAuditSwitchFail();
            }

            @Override
            public void netConnectError() {
                mView.getAuditSwitchFail();
            }
        });
    }

    //获取广告配置
    public void geekAdSDKConfig() {
        GeekAdSdk.requestConfig(new ConfigListener() {
            @Override
            public void adSuccess(List<ConfigBean.AdListBean> configList) {
                String config = JsonUtils.encode(configList);
                String aa = config.substring(0, config.length() / 2);
                LogUtils.i("config:" + aa);
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                LogUtils.i(errorCode + "----config--error---:" + errorMsg);

            }
        });
    }
}
