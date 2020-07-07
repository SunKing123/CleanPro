package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common3Subscriber;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tie on 2017/5/15.
 */
public class CleanFinishPresenter extends RxPresenter<NewCleanFinishActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public CleanFinishPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 插屏广告开关
     */
    public void getScreenSwitch() {
        mModel.getScreentSwitch(new Common4Subscriber<InsertAdSwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InsertAdSwitchInfoList switchInfoList) {
                AppHolder.getInstance().setInsertAdSwitchInfoList(switchInfoList);
                mView.getScreenSwitchSuccess();
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
     * 获取金币
     */
    public void getGoldCoin() {
        mModel.getGoleGonfigs(new Common3Subscriber<BubbleConfig>() {
            @Override
            public void showExtraOp(String code, String message) {  //关心错误码；
                ToastUtils.showShort(message);
            }

            @Override
            public void getData(BubbleConfig bubbleConfig) {
                if (bubbleConfig != null && bubbleConfig.getData().size() > 0) {
                    for (BubbleConfig.DataBean item : bubbleConfig.getData()) {
                        if (item.getLocationNum() == 5) {
                            showGetGoldCoinDialog(item.getGoldCount());
                            break;
                        }
                    }
                }

            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
                ToastUtils.showShort(R.string.notwork_error);
            }
        }, RxUtil.<ImageAdEntity>rxSchedulerHelper(mView));
    }


    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
//                    mView.cancelLoadingDialog();
                    mView.getAccessListBelow(strings);
                });
    }


    //显示内部插屏广告
    public void showInsideScreenDialog() {
        if (mActivity == null) {
            return;
        }
        AdRequestParams params = new AdRequestParams.Builder()
                .setActivity(mActivity).setAdId(MidasConstants.FINISH_INSIDE_SCREEN_ID).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdShow(AdInfo adInfo) {
                super.onAdShow(adInfo);
                LogUtils.e("====完成页内部插屏广告展出======");
            }
        });
    }

    //金币领取广告弹窗
    private void showGetGoldCoinDialog(int coinCount) {
        GoldCoinDialogParameter bean = new GoldCoinDialogParameter();
        bean.dialogType = 3;
        bean.obtainCoinCount = coinCount;
        bean.adId = MidasConstants.FINISH_GET_GOLD_COIN;
        bean.fromType = GoldCoinDialogParameter.FROM_FINISH_COMPLETE;
        bean.context = mActivity;
        bean.advCallBack = new AbsAdCallBack() {

        };
        bean.onDoubleClickListener = (v) -> {

            if (!AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO)) {
                ToastUtils.showLong("网络异常");
                GoldCoinDialog.dismiss();
                return;
            }
            ViewGroup viewGroup = (ViewGroup) mView.getWindow().getDecorView();
            AdRequestParams params = new AdRequestParams.Builder().
                    setActivity(mActivity).
                    setViewContainer(viewGroup).
                    setAdId(MidasConstants.CLICK_GET_DOUBLE_COIN_BUTTON).build();
            MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
                @Override
                public void onShowError(int i, String s) {
                    super.onShowError(i, s);
                    ToastUtils.showLong("网络异常");
                    GoldCoinDialog.dismiss();
                }

                @Override
                public void onAdError(AdInfo adInfo, int i, String s) {
                    super.onAdError(adInfo, i, s);
                    ToastUtils.showLong("网络异常");
                    GoldCoinDialog.dismiss();
                }

                @Override
                public void onAdVideoComplete(AdInfo adInfo) {
                    super.onAdVideoComplete(adInfo);
                    Intent intent = new Intent(mActivity, GoldCoinSuccessActivity.class);
                    intent.putExtra(GoldCoinSuccessActivity.COIN_NUM, coinCount * 2);
                    if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
                        intent.putExtra(GoldCoinSuccessActivity.AD_ID, MidasConstants.GET_DOUBLE_GOLD_COIN_SUCCESS);
                    }
                    mActivity.startActivity(intent);
                    GoldCoinDialog.dismiss();

                }
            });

        };
        GoldCoinDialog.showGoldCoinDialog(bean);
    }


}
