package com.xiaoniu.cleanking.ui.usercenter.presenter;


import android.text.TextUtils;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.usercenter.activity.AboutActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class AboutPresenter extends RxPresenter<AboutActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public AboutPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 版本更新
     */
    public void queryAppVersion(int type, final OnCancelListener onCancelListener) {
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {


            @Override
            public void getData(AppVersion updateInfoEntity) {
                if (type == 1) {
                    mView.setShowVersion(updateInfoEntity);
                } else {
                    setAppVersion(updateInfoEntity);
                }

            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {

            }
        });
    }

    private UpdateAgent mUpdateAgent;

    public void setAppVersion(AppVersion result) {
        if (result != null) {
            //根据版本号判断是否需要更新
            String versionName = AndroidUtil.getAppVersionName();
            int versionCode = AndroidUtil.getVersionCode();
            //默认可以下载
            int code = 0;
            if (!TextUtils.isEmpty(result.code)) {
                code = Integer.parseInt(result.code);
            }
            if (!TextUtils.isEmpty(versionName) && !TextUtils.equals(versionName, result.getData().versionNumber) && !TextUtils.isEmpty(result.getData().downloadUrl)) {
                boolean isForced = false;
                if (TextUtils.equals(result.getData().forcedUpdate, "1")) {//强更
                    isForced = true;
                } else {//手动更新
                    isForced = false;
                }
                if (mUpdateAgent == null) {
                    mUpdateAgent = new UpdateAgent(mActivity, result, new OnCancelListener() {
                        @Override
                        public void onCancel() {
                        }
                    });
                    mUpdateAgent.check();
                }else{
                    mUpdateAgent.check();
                }


            } else {//清空版本信息状态
            }
        } else {
        }
    }
}
