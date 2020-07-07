package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

/**
 * Created by xinxiaolong on 2020/7/6.
 * email：xinxiaolong123@foxmail.com
 */
public class ScratchCardAvdPresenter {

    //广告id资源前缀
    public static final String ADV_FIRST_PREFIX = "scratch_card_first";
    public static final String ADV_SECOND_PREFIX = "scratch_card_second";
    public static final String ADV_VIDEO_PREFIX = "scratch_card_video";

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
    GoldCoinDialogParameter parameter;

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

        log("调用弹框 showDialog()" + cardIndex + "    " + coinCount);

        if (activity == null) {
            new Throwable("activity 不可为空！");
        }
        this.cardIndex = cardIndex;
        this.coinCount = coinCount;
        if (parameter == null) {
            parameter = new GoldCoinDialogParameter();
            parameter.context = activity;
            parameter.advCallBack = new CardAdCallBack(ADV_FIRST_PREFIX);
            parameter.fromType = GoldCoinDialogParameter.FROM_SCRATCH_CARD;
            parameter.onDoubleClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadVideoAdv();
                }
            };
        }
        parameter.adId = isOpenOne() ? getFirstAdvId() : "";
        parameter.obtainCoinCount = coinCount;
        GoldCoinDialog.showGoldCoinDialog(parameter);
    }

    //加载激励视频广告
    private void loadVideoAdv() {
        if (isOpenTwo()) {
            loadVideoAdv(getVideoAdvId());
        } else {
            handlerVideoAdvError();
        }
    }

    /**
     * 两个刮刮卡刮完显示的广告id
     */
    private String getFirstAdvId() {
        return getAdvId(ADV_FIRST_PREFIX, cardIndex);
    }

    /**
     * 获取翻倍完成的广告id
     */
    public String getSecondAdvId() {
        return getAdvId(ADV_SECOND_PREFIX, cardIndex);
    }

    /**
     * 点击金币翻倍展示的激励视频广告id
     */
    private String getVideoAdvId() {
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
        log("resNamePrefix="+resNamePrefix+"   index="+index+"   获取到的广告id=" + advId);
        return advId;
    }

    class CardAdCallBack extends AbsAdCallBack {
        String resNamePrefix;

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
                    break;
            }
        }

        @Override
        public void onAdLoadSuccess(AdInfo adInfo) {
            super.onAdLoadSuccess(adInfo);
            log("onAdLoadSuccess()====" + resNamePrefix);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
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
                    //用户关闭激励视频，给金币翻倍，跳转至金币成功页面
                    openCoinCompletePage();
                    break;
            }
        }

        @Override
        public void onAdVideoComplete(AdInfo adInfo) {
            super.onAdVideoComplete(adInfo);
            log("onAdVideoComplete()====" + resNamePrefix);
        }

        @Override
        public void onAdSkippedVideo(AdInfo adInfo) {
            super.onAdSkippedVideo(adInfo);
            log("onAdSkippedVideo()====" + resNamePrefix);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    break;
            }
        }
    }

    //激励视频加载失败，提示用户并关闭弹框
    private void handlerVideoAdvError() {
        ToastUtils.showShort("网络异常");
        GoldCoinDialog.dismiss();
    }

    private void openCoinCompletePage() {
        Intent intent = new Intent(activity, GoldCoinSuccessActivity.class);
        intent.putExtra(GoldCoinSuccessActivity.COIN_NUM, coinCount * 2);
        intent.putExtra(GoldCoinSuccessActivity.AD_ID, isOpenThree() ? getSecondAdvId() : "");
        activity.startActivity(intent);
        GoldCoinDialog.dismiss();
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

        log("===========================检查刮刮卡的广告开关 start======================================");
        hasInit = true;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenOne = switchInfoList.isOpen();
                    log("第一个广告位开关信息:isOpen=" + isOpenOne());
                }
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenTwo = switchInfoList.isOpen();
                    log("第二个广告位开关信息:isOpen=" + isOpenTwo());
                }
                if (PositionId.KEY_AD_PAGE_SCRATCH_CARD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_THREE_CODE.equals(switchInfoList.getAdvertPosition())) {
                    isOpenThree = switchInfoList.isOpen();
                    log("第三个广告位开关信息:isOpen=" + isOpenThree());
                }
            }
        }
        log("==============================检查刮刮卡的广告开关 end========================================");
    }

    public void destroy() {
        parameter.advCallBack = null;
        parameter.context = null;
        parameter = null;
        activity = null;
    }
}
