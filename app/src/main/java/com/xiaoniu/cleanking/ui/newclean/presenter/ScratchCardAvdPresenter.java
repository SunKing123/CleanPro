package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.utils.LogUtils;
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


    public void showDialog(int cardIndex) {
        this.cardIndex = cardIndex;
        String advId = getFirstAdvId();
        CardAdCallBack cardAdCallBack = new CardAdCallBack(ADV_FIRST_PREFIX);
        GoldCoinDialogParameter parameter = new GoldCoinDialogParameter();
        parameter.adId = advId;
        parameter.context = activity;
        parameter.fromType = GoldCoinDialogParameter.FROM_SCRATCH_CARD;
        parameter.advCallBack = cardAdCallBack;
        parameter.onDoubleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideoAdv(getVideoAdvId());
            }
        };

        GoldCoinDialog.showGoldCoinDialog(parameter);
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

    private void loadVideoAdv(String advId) {

        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(advId).setActivity(activity)
                .setViewContainer((ViewGroup) activity.getWindow().getDecorView()).build();

        MidasRequesCenter.requestAd(params, new CardAdCallBack(advId));
    }

    private String getAdvId(String resNamePrefix, int index) {
        String allResourceName = resNamePrefix + index;
        String advId = getAdvId(activity, allResourceName);
        return advId;
    }

    class CardAdCallBack extends AbsAdCallBack {
        String resNamePrefix;

        public CardAdCallBack(String resNamePrefix) {
            this.resNamePrefix = resNamePrefix;
        }

        @Override
        public void onAdError(AdInfo adInfo, int i, String s) {
            super.onAdError(adInfo, i, s);
            log("onAdError()====" + resNamePrefix + "====" + s);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_SECOND_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    break;
            }
        }

        @Override
        public void onShowError(int i, String s) {
            super.onShowError(i, s);
            log("onShowError()====" + resNamePrefix + "====" + s);
            switch (resNamePrefix) {
                case ADV_FIRST_PREFIX:
                    break;
                case ADV_SECOND_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
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
                case ADV_SECOND_PREFIX:
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
                case ADV_SECOND_PREFIX:
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
                case ADV_SECOND_PREFIX:
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
                case ADV_SECOND_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
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
                case ADV_SECOND_PREFIX:
                    break;
                case ADV_VIDEO_PREFIX:
                    break;
            }
        }
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
    }
}
