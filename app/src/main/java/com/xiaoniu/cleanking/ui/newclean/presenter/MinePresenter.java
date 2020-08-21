package com.xiaoniu.cleanking.ui.newclean.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack;
import com.xiaoniu.cleanking.ui.login.activity.LoginWeiChatActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListData;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.contact.MineFragmentContact;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.newclean.model.NewMineModel;
import com.xiaoniu.cleanking.ui.newclean.util.RequestUserInfoUtil;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common3Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.net.ErrorCode;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;
import com.xiaoniu.cleanking.utils.update.listener.OnCancelListener;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

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
    //获取任务列表
    public void refDaliyTask() {
        if (AppHolder.getInstance().getAuditSwitch())
            return;
        mModel.getDaliyTaskList(new Common3Subscriber<DaliyTaskListData>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(DaliyTaskListData daliyTaskListData) {
                LogUtils.i("zz--refDaliyTask()---" + new Gson().toJson(daliyTaskListData));
                mView.setTaskData(daliyTaskListData);
            }

            @Override
            public void showExtraOp(String message) {
                mView.setTaskData(null);
            }

            @Override
            public void netConnectError() {
                mView.setTaskData(null);
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper((RxFragment) mView));
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
                mView.setBubbleView(null);
            }

            @Override
            public void netConnectError() {
                mView.setBubbleView(null);
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper((RxFragment) mView));
    }

    /**
     * 激励视频广告
     */
    public void rewardVideoAd(int localNum) {
        if (!AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_MINE_LIMIT_AWARD_PAG, PositionId.DRAW_ONE_CODE)) {
            ToastUtils.showLong("网络异常");
            return;
        }
        MidasRequesCenter.requestAndShowAd((Activity) mContext, AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_MINE_LIMIT_AWARD_PAG, PositionId.DRAW_ONE_CODE), new VideoAbsAdCallBack() {
            @Override
            public void onAdLoadError(String errorCode, String errorMsg) {
                super.onAdLoadError(errorCode, errorMsg);
                ToastUtils.showLong("网络异常");
                GoldCoinDialog.dismiss();
            }

            @Override
            public void onAdVideoComplete(AdInfoModel adInfoModel) {
                super.onAdVideoComplete(adInfoModel);
                if (!((Activity) mContext).isFinishing()) {
                    GoldCoinDialog.dismiss();
                }
            }

            @Override
            public void onAdClose(AdInfoModel adInfo, boolean isComplete) {
                super.onAdClose(adInfo, isComplete);
//                try {
//                    org.json.JSONObject exJson = new org.json.JSONObject();
//                    exJson.put("position_id", dataBean.getData().getLocationNum());
//                    StatisticsUtils.trackClick("incentive_video_ad_click", "首页金币翻倍激励视频广告关闭点击", "home_page_gold_coin_pop_up_window_incentive_video_page", "home_page_gold_coin_pop_up_window_incentive_video_page", exJson);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                if (!((Activity) mContext).isFinishing()) {
                    if (isComplete) {
                        bullCollect(localNum);
                    }
                    GoldCoinDialog.dismiss();
                }
            }
        });
    }


    //领取金币
    public void bullCollect(int locationNum) {
        mModel.goleCollect(new Common3Subscriber<BubbleCollected>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码
                if (TextUtils.equals(code, ErrorCode.LOGIN_EXCEPTION)) {
                    ((Activity) mView).startActivity(new Intent(((Activity) mView), LoginWeiChatActivity.class));
                }
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleCollected bubbleConfig) {
                //实时更新金币信息
                RequestUserInfoUtil.getUserCoinInfo();
                if (null != bubbleConfig && null != bubbleConfig.getData()) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("position_id", bubbleConfig.getData().getLocationNum());
//                    map.put("gold_number", bubbleConfig.getData().getGoldCount());
//                    StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", "首页金币领取弹窗金币发放数", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", map);
                    mView.bubbleCollected(bubbleConfig);
                }

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper((RxFragment) mView), locationNum);
    }


    //金币领取广告弹窗
    public void showGetGoldCoinDialog(BubbleCollected dataBean) {
        GoldCoinDialogParameter bean = new GoldCoinDialogParameter();
        bean.dialogType = 1;
        bean.obtainCoinCount = dataBean.getData().getGoldCount();
        bean.doubleNums = 0;//关闭翻倍按钮;
        bean.isDouble = false;
        //广告位1开关控制
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_MINE_LIMIT_AWARD_PAG, PositionId.DRAW_TWO_CODE)) {
            bean.adId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_MINE_LIMIT_AWARD_PAG, PositionId.DRAW_TWO_CODE);
//            Map<String, Object> mapJson = new HashMap<>();
//            mapJson.put("position_id", String.valueOf(dataBean.getData().getLocationNum()));
//            StatisticsUtils.customTrackEvent("ad_request_sdk_1", "首页金币领取弹窗上广告发起请求", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", mapJson);
        }
        bean.totalCoinCount = dataBean.getData().getTotalGoldCount();
        //翻倍回调
        bean.onDoubleClickListener = (v) -> { };
        //弹框关闭回调
        bean.closeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     try {
                    org.json.JSONObject exJson = new org.json.JSONObject();
                    exJson.put("position_id", dataBean.getData().getLocationNum());
                    StatisticsUtils.trackClick("close_click", "弹窗关闭点击", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window", exJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        };
        //bean.adVideoId = MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON;
        bean.context = (Activity) mContext;
        GoldCoinDialog.showGoldCoinDialog(bean);
//        StatisticsUtils.customTrackEvent("home_page_gold_coin_pop_up_window_custom", "首页金币领取弹窗曝光", "home_page_gold_coin_pop_up_window", "home_page_gold_coin_pop_up_window");
//        adPrevData(AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_HOME_GOLD_PAGE, PositionId.DRAW_THREE_CODE));//位置三预加载
    }
}
