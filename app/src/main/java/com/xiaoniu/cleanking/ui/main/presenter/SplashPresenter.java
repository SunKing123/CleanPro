package com.xiaoniu.cleanking.ui.main.presenter;

import com.comm.jksdk.GeekAdSdk;
import com.comm.jksdk.http.utils.LogUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.CheckAdConfigUpdateEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

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
                PreferenceUtil.getInstants().save(Constant.SWITCH_INFO, new Gson().toJson(switchInfoList));
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
                PreferenceUtil.getInstants().save(Constant.SWITCH_INFO, new Gson().toJson(switchInfoList));
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
        if(null==mActivity)
            return;
        LogUtils.i("-cgName-----冷启动进入前台");
        mModel.checkAdConfigUpdate("1",new Common4Subscriber<CheckAdConfigUpdateEntity>() {

            @Override
            public void showExtraOp(String code, String message) {
                Logger.i("zz--checkAd--"+code+message);
            }

            @Override
            public void getData(CheckAdConfigUpdateEntity configUpdateEntity) {
                Logger.i("zz--checkAd--"+configUpdateEntity.getData().getIsUpdate());
            }

            @Override
            public void showExtraOp(String message) {
                Logger.i("zz--checkAd--showExtraOp"+message);
            }

            @Override
            public void netConnectError() {
                Logger.i("zz--checkAd--netConnectError");
            }
        });
        GeekAdSdk.refAdConfig(mActivity);
        //1.4.5版本走本地配置
    /*    GeekAdSdk.requestConfig(new ConfigListener() {
            @Override
            public void adSuccess(List<ConfigBean.AdListBean> configList) {
                String config = JsonUtils.encode(configList);
                String aa = config.substring(0, config.length() / 2);//
                LogUtils.i("config:" + aa);
            }

            @Override
            public void adError(int errorCode, String errorMsg) {
                LogUtils.i(errorCode + "----config--error---:" + errorMsg);

            }
        });*/


    }

    /**
     * 打底广告
     */
    public void getBottomAdList() {
        mModel.getBottomAdList(new Common4Subscriber<BottoomAdList>() {

            @Override
            public void getData(BottoomAdList auditSwitch) {
                if (null == auditSwitch.getData() || auditSwitch.getData().size() <= 0) return;
                AppHolder.getInstance().setBottomAdList(auditSwitch.getData());
                PreferenceUtil.getInstants().save(SpCacheConfig.BOTTOM_AD_LIST, new Gson().toJson(auditSwitch));
                mView.getBottomAdListSuccess();
            }

            @Override
            public void showExtraOp(String code, String message) {
                mView.getBottomAdListSuccess();
            }

            @Override
            public void showExtraOp(String message) {
                mView.getBottomAdListSuccess();
            }

            @Override
            public void netConnectError() {
                mView.getBottomAdListSuccess();
            }
        });
    }
}
