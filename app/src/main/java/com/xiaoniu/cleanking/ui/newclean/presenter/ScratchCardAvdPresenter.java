package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.CMAbsAdCallBack;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.ad.entity.AdInfo;

/**
 * Created by xinxiaolong on 2020/7/6.
 * email：xinxiaolong123@foxmail.com
 */
public class ScratchCardAvdPresenter {

    //广告id资源前缀
    public static final String ADV_FIRST_PREFIX = "scratch_card_first";
    public static final String ADV_SECOND_PREFIX = "scratch_card_second";
    public static final String ADV_VIDEO_PREFIX = "scratch_card_video";

    //开关是否初始化过
    private static boolean hasInit = false;
    //刮刮卡广告开关
    private static boolean isOpenOne;
    //激励视频广告开关
    private static boolean isOpenTwo;
    //翻倍广告开关
    private static boolean isOpenThree;

    private Activity activity;
    private int cardIndex;
    private int coinCount;
    private GoldCoinDialogParameter parameter;

    public ScratchCardAvdPresenter(Activity activity) {
        initOnOff();
        this.activity = activity;
    }

    private boolean isOpenOne() {
        return isOpenOne;
    }

    private boolean isOpenTwo() {
        return isOpenTwo;
    }

    private boolean isOpenThree() {
        return isOpenThree;
    }

    public void showDialog(int cardIndex, int coinCount) {
        log("================================================刮刮卡调用弹框 showDialog()  cardIndex=" + cardIndex + "    coinCount=" + coinCount);
        if (activity == null) {
            log("activity 对象为空，不能弹框");
            return;
        }
        this.cardIndex = cardIndex;
        this.coinCount = coinCount;
        if (parameter == null) {
            parameter = new GoldCoinDialogParameter();
            parameter.context = activity;
            parameter.advCallBack = new CardAdCallBack(ADV_FIRST_PREFIX);
            parameter.fromType = GoldCoinDialogParameter.FROM_SCRATCH_CARD;
            parameter.onDoubleClickListener = v -> handlerDoubleClick();
            parameter.closeClickListener = v -> handlerCloseClick();
        }
        parameter.adId = isOpenOne() ? getFirstAdvId(cardIndex) : "";
        parameter.obtainCoinCount = coinCount;

        GoldCoinDialog.showGoldCoinDialog(parameter);
        StatisticsUtils.scratchCardCustom(Points.ScratchCard.WINDOW_UP_EVENT_CODE, Points.ScratchCard.WINDOW_UP_EVENT_NAME, cardIndex, "", Points.ScratchCard.WINDOW_PAGE);
    }

    //点击翻倍按钮事件
    private void handlerDoubleClick() {
        if (isOpenTwo()) {
            loadVideoAdv(getVideoAdvId(cardIndex));
        } else {
            handlerVideoAdvError();
        }
        StatisticsUtils.scratchCardClick(Points.ScratchCard.WINDOW_DOUBLE_CLICK_EVENT_CODE, Points.ScratchCard.WINDOW_DOUBLE_CLICK_EVENT_NAME, cardIndex, "", Points.ScratchCard.WINDOW_PAGE);
    }

    //点击关闭按钮事件
    private void handlerCloseClick() {
        StatisticsUtils.scratchCardClick(Points.ScratchCard.WINDOW_CLOSE_CLICK_CODE, Points.ScratchCard.WINDOW_CLOSE_CLICK_NAME, cardIndex, "", Points.ScratchCard.WINDOW_PAGE);
    }

    /**
     * 两个刮刮卡刮完显示的广告id
     */
    private String getFirstAdvId(int cardIndex) {
        return getAdvId(ADV_FIRST_PREFIX, cardIndex);
    }

    /**
     * 获取翻倍完成的广告id
     */
    public String getSecondAdvId(int cardIndex) {
        return getAdvId(ADV_SECOND_PREFIX, cardIndex);
    }

    /**
     * 点击金币翻倍展示的激励视频广告id
     */
    private String getVideoAdvId(int cardIndex) {
        return getAdvId(ADV_VIDEO_PREFIX, cardIndex);
    }

    //加载激励视屏广告
    private void loadVideoAdv(String advId) {
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(advId).setActivity(activity)
                .setViewContainer((ViewGroup) activity.getWindow().getDecorView()).build();

        MidasRequesCenter.requestAd(params, new CardAdCallBack(ADV_VIDEO_PREFIX));
    }

    private String getAdvId(String resNamePrefix, int index) {
        String allResourceName = resNamePrefix + index;
        String advId = getAdvId(activity, allResourceName);
        log("================================================resNamePrefix=" + resNamePrefix + "   index=" + index + "   广告id=" + advId + "");
        return advId;
    }

    class CardAdCallBack extends CMAbsAdCallBack {
        String resNamePrefix;
        boolean videoPlayed = false;

        public CardAdCallBack(String resNamePrefix) {
            this.resNamePrefix = resNamePrefix;
        }

        @Override
        public void onShowError(int i, String s) {
            super.onShowError(i, s);
            log("onShowError()====" + resNamePrefix + "====" + s);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    handlerVideoAdvError();
                    break;
            }
        }

        @Override
        public void onAdShow(AdInfo adInfo) {
            super.onAdShow(adInfo);
            log("onAdShow()====" + resNamePrefix);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    GoldCoinDialog.dismiss();
                    break;
            }
        }

        @Override
        public void onAdClicked(AdInfo adInfo) {
            super.onAdClicked(adInfo);
            log("onAdClicked()====" + resNamePrefix);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    break;
            }
        }

        @Override
        public void onAdClose(AdInfo adInfo) {
            super.onAdClose(adInfo);
            log("onAdClose()====" + resNamePrefix);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:

                    break;
                case ADV_VIDEO_PREFIX:
                    if (videoPlayed) {
                        startCoinCompletePage();
                    }
                    break;
            }
        }

        @Override
        public void onAdVideoComplete(AdInfo adInfo) {
            super.onAdVideoComplete(adInfo);
            log("onAdVideoComplete()====" + resNamePrefix);
            videoPlayed = true;
        }
    }

    //激励视频加载失败，提示用户并关闭弹框
    private void handlerVideoAdvError() {
        ToastUtils.showShort("网络异常");
        GoldCoinDialog.dismiss();
    }


    private void startCoinCompletePage() {
        Intent intent = new Intent(activity, GoldCoinSuccessActivity.class);
        intent.putExtra(GoldCoinSuccessActivity.COIN_NUM, coinCount * 2);
        intent.putExtra(GoldCoinSuccessActivity.AD_ID, isOpenThree() ? getSecondAdvId(cardIndex) : "");
        activity.startActivity(intent);
        GoldCoinDialog.dismiss();
        StatisticsUtils.scratchCardClick(Points.ScratchCard.VIDEO_PAGE_CLOSE_CLICK_CODE, Points.ScratchCard.VIDEO_PAGE_CLOSE_CLICK_NAME, cardIndex, "", Points.ScratchCard.VIDEO_PAGE);
    }

    private String getAdvId(Context context, String resourceName) {
        if (context == null) {
            log("不能加载广告，context为空。");
            return "";
        }
        int resourceId = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());

        if (resourceId == 0) {
            log("不能加载广告，广告resourceId为0");
            return "";
        }
        try {
            return context.getResources().getString(resourceId);
        } catch (Exception e) {
            log("不能加载广告，获取广告id异常。");
        }
        return "";
    }

    private void log(String text) {
        if (BuildConfig.DEBUG) {
            LogUtils.e("scratchCard： " + text);
        }
    }

    private void initOnOff() {
        if (hasInit) {
            return;
        }
        log("================================================检查刮刮卡的广告开关 start");
        hasInit = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenOne = switchInfoList.isOpen();
                }
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenTwo = switchInfoList.isOpen();
                }
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenThree = switchInfoList.isOpen();
                }
            }
        }

        isOpenOne=true;
        isOpenTwo=true;
        isOpenThree=true;

        log("第一个广告位开关信息:isOpen=" + isOpenOne());
        log("第二个广告位开关信息:isOpen=" + isOpenTwo());
        log("第三个广告位开关信息:isOpen=" + isOpenThree());

        log("================================================检查刮刮卡的广告开关 end");
    }

    public void destroy() {
        parameter.advCallBack = null;
        parameter.context = null;
        parameter = null;
        activity = null;
    }
}
