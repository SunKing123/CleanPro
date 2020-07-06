package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinBean;
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

    //刮刮卡广告开关
    private boolean isOpenOne;
    //激励视频广告开关
    private boolean isOpenTwo;
    //翻倍广告开关
    private boolean isOpenThree;

    public ScratchCardAvdPresenter() {
        initOnOff();
    }

    public boolean isOpenOne() {
        return isOpenOne;
    }

    public boolean isOpenTwo() {
        return isOpenTwo;
    }

    public boolean isOpenThree() {
        return isOpenThree;
    }

    /**
     * 两个刮刮卡刮完显示的广告
     *
     * @param coinBean
     */
    private void loadFirstAdv(GoldCoinBean coinBean, ViewGroup mRootRL) {
        loadAdv(coinBean, mRootRL, ADV_FIRST_PREFIX);
    }

    /**
     * 看完激励视频显示的广告
     *
     * @param coinBean
     */
    private void loadSecondAdv(GoldCoinBean coinBean, ViewGroup mRootRL) {
        loadAdv(coinBean, mRootRL, ADV_SECOND_PREFIX);
    }

    /**
     * 点击金币翻倍展示的激励视频广告
     *
     * @param coinBean
     */
    private void loadVideoAdv(GoldCoinBean coinBean, ViewGroup mRootRL) {
        loadAdv(coinBean, mRootRL, ADV_VIDEO_PREFIX);
    }

    private void loadAdv(GoldCoinBean coinBean, ViewGroup mRootRL, String resNamePrefix) {
        log("开始加载广告: " + resNamePrefix);

        if (coinBean.context == null || !(coinBean.context instanceof Activity)) {
            log("context必须为activity级别");
            return;
        }

        String allResourceName = resNamePrefix + coinBean.pageId;
        String advId = getAdvId(coinBean.context, allResourceName);
        log("获取到广告id: " + advId);
        if (TextUtils.isEmpty(advId)) {
            return;
        }

        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(advId).setActivity((Activity) coinBean.context)
                .setViewContainer(mRootRL).build();

        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
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
        });
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
