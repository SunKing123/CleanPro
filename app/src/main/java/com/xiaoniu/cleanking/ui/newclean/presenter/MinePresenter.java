package com.xiaoniu.cleanking.ui.newclean.presenter;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.newclean.contact.MineFragmentContact;
import com.xiaoniu.cleanking.ui.newclean.model.NewMineModel;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common3Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
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


    //更新金币列表
    public void refBullList() {
        if (AppHolder.getInstance().getAuditSwitch())
            return;
        mModel.getGoleGonfigs(new Common3Subscriber<BubbleConfig>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleConfig bubbleConfig) {
                LogUtils.i("zz--refBullList()---" + new Gson().toJson(bubbleConfig));
                mView.setBubbleView(bubbleConfig);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper((RxFragment) mView));
    }
}
