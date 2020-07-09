package com.xiaoniu.cleanking.ui.newclean.presenter;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.newclean.contact.MineFragmentContact;
import com.xiaoniu.cleanking.ui.newclean.model.NewMineModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.common.utils.ToastUtils;

import javax.inject.Inject;

/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:
 */
public class MinePresenter extends RxPresenter<MineFragmentContact.View, NewMineModel> {
    private Context mContext;

    @Inject
    public MinePresenter() {
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    //废弃==暂时不掉用了，通过统一地方调用
    public void getMinePageInfo() {
        mModel.getMinePageInfo(new CommonSubscriber<MinePageInfoBean>() {
            @Override
            public void getData(MinePageInfoBean minePageInfoBean) {
                if (mView != null) {
                    mView.getInfoDataSuccess(minePageInfoBean);
                }
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
     * 版本更新
     */
    public void queryAppVersion(int type, final OnCancelListener onCancelListener) {
        mModel.queryAppVersion(new Common4Subscriber<AppVersion>() {
            @Override
            public void getData(AppVersion updateInfoEntity) {
                if (type == 2) {
                    setAppVersion(updateInfoEntity);
                }
            }

            @Override
            public void showExtraOp(String code, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
        if (result != null && result.getData() != null) {
            if (mUpdateAgent == null) {
                mUpdateAgent = new UpdateAgent((Activity) mContext, result, () -> {
                });
            }
            if (result.getData().isPopup) {
                mUpdateAgent.check();
            } else {
                ToastUtils.showShort("当前已是最新版本");
            }
        } else {
            ToastUtils.showShort("当前已是最新版本");
        }
    }
}
