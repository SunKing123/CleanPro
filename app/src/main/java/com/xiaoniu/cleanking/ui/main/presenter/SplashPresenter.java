package com.xiaoniu.cleanking.ui.main.presenter;

import com.google.gson.Gson;
import com.ishumei.smantifraud.SmAntiFraud;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.BottoomAdList;
import com.xiaoniu.cleanking.ui.main.bean.InsideAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.SystemUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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

    //开关逻辑初始化；
    public void spDataInit(){
        PreferenceUtil.saveCleanAllUsed(false);
        PreferenceUtil.saveCleanJiaSuUsed(false);
        PreferenceUtil.saveCleanPowerUsed(false);
        PreferenceUtil.saveCleanNotifyUsed(false);
        PreferenceUtil.saveCleanWechatUsed(false);
        PreferenceUtil.saveCleanCoolUsed(false);
        PreferenceUtil.saveCleanGameUsed(false);
        if (PreferenceUtil.getScreenInsideTime()) {
            PreferenceUtil.saveRedPacketShowCount(1);
            PreferenceUtil.saveScreenInsideTime();
        } else {
            PreferenceUtil.saveRedPacketShowCount(PreferenceUtil.getRedPacketShowCount() + 1);
        }
        /*保存冷、热启动的次数*/
        InsideAdEntity inside = PreferenceUtil.getColdAndHotStartCount();
        if (DateUtils.isSameDay(inside.getTime(), System.currentTimeMillis())) {
            inside.setCount(inside.getCount() + 1);
        } else {
            inside.setCount(1);
        }
        inside.setTime(System.currentTimeMillis());
        PreferenceUtil.saveColdAndHotStartCount(inside);
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
    @Deprecated
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



    /**
     * 数美SDK初始化；
     */
    public void initShuMeiSDK() {
        // 如果 AndroidManifest.xml 中没有指定主进程名字，主进程名默认与 packagename 相同
        if (AppApplication.getInstance().getPackageName().equals(SystemUtils.getProcessName(AppApplication.getInstance()))) {

            SmAntiFraud.SmOption option = new SmAntiFraud.SmOption();
            //1.通用配置项
            option.setOrganization(Constant.SMANTIFRAUD_ORGANIZATION); //必填，组织标识，邮件中 organization 项
            option.setAppId(Constant.APPLICATION_IDENTIFICATION); //必填，应用标识，登录数美后台应用管理查看
            option.setPublicKey(Constant.SMANTIFRAUD_PUBLIC_KEY); //必填，加密 KEY，邮件中 android_public_key 附件内容
            option.setAinfoKey(Constant.SMANTIFRAUD_AINFOKEY); //必填，加密 KEY，邮件中 Android ainfo key 项
            option.setChannel(ChannelUtil.getChannel());//渠道代码
            //2.连接海外机房特殊配置项，仅供设备数据上报海外机房客户使用
            // option.setArea(SmAntiFraud.AREA_XJP); //连接新加坡机房客户使用此选项
            // option.setArea(SmAntiFraud.AREA_FJNY); //连接美国机房客户使用此选项
            //3.SDK 初始化
            SmAntiFraud.create(AppApplication.getInstance(), option);
        }
    }




}
