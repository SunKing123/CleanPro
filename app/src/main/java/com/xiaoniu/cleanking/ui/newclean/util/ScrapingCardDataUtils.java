package com.xiaoniu.cleanking.ui.newclean.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.jess.arms.utils.LogUtils;
import com.xiaoniu.cleanking.app.H5Urls;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.midas.VideoAbsAdCallBack;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.utils.ActivityUtils;
import com.xiaoniu.cleanking.scheme.utils.Parameters;
import com.xiaoniu.cleanking.scheme.utils.SchemeUtils;
import com.xiaoniu.cleanking.scheme.utils.UrlUtils;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.bean.ScrapingCardBean;
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog;
import com.xiaoniu.cleanking.ui.usercenter.activity.ScrapingCarDetailActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.user.UserHelper;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.model.AdInfoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.xiaoniu.cleanking.ui.newclean.presenter.ScratchCardAvdPresenter.ADV_FIRST_PREFIX;
import static com.xiaoniu.cleanking.ui.newclean.presenter.ScratchCardAvdPresenter.ADV_VIDEO_PREFIX;

/**
 * Created by zhaoyingtao
 * Date: 2020/8/6
 * Describe: 刮刮卡数据统一处理
 */
public class ScrapingCardDataUtils {
    private volatile static ScrapingCardDataUtils cardDataUtils;
    private static List<ScrapingCardBean> cardList = new ArrayList<>();
    //跳转详情页的次数
    private int skipNums = 0;
    private ScrapingCardBean cardBean;
    private int cardIndex;
    private int currentPosition;

    public static ScrapingCardDataUtils init() {
        if (cardDataUtils == null) {
            synchronized (ScrapingCardDataUtils.class) {
                if (cardDataUtils == null) {
                    cardDataUtils = new ScrapingCardDataUtils();
                }
            }
        }
        return cardDataUtils;
    }

    /**
     * 填充数据======必须先调填充数据
     *
     * @param cards
     * @param currentPosition
     */
    public void setScrapingCardData(List<ScrapingCardBean> cards, int currentPosition) {
//        cards = GSON.parseList(dd, ScrapingCardBean.class);
        this.currentPosition = currentPosition;
        cardList.clear();
        if (cards != null && cards.size() > 0) {
            cardList.addAll(cards);
        }
        LogUtils.debugInfo("snow", "====setScrapingCardData========" + skipNums);
        skipNums = 0;
//        cardBean = getCarDataOfPosition(currentPosition);

    }

    public int getCardsListSize() {
        return cardList == null ? 0 : cardList.size();
    }

    /**
     * 刮刮卡下一步处理
     * @param activity
     * @param isShowVideo 是否需要跳激励视频 true 需要跳
     */
    public void scrapingCardNextAction(Activity activity, boolean isShowVideo) {
        cardBean = getCarDataOfPosition(currentPosition);
        if (cardBean == null) {
            return;
        }
        cardIndex = cardBean.getCardPosition();
        //激励视频开关
        boolean isOpenJiLiVideo = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_SCRATCH_CARD, PositionId.DRAW_TWO_CODE);
        if (skipNums % 2 == 0 && isOpenJiLiVideo && isShowVideo) {//先加载广告
            String advId = getCarAdvId(activity, ADV_VIDEO_PREFIX, cardBean.getCardPosition());
            loadVideoAdv(activity, advId);
        } else {//直接跳详情
            goToScrapingCarDetail(activity);
        }
        LogUtils.debugInfo("snow", "====scrapingCardNextAction========" + skipNums + "===skipNums % 2===" + (skipNums % 2));
        skipNums++;
    }

    /**
     * 获取position卡片信息，注意判空处理，没拿到会返回空
     *
     * @return
     */
    private ScrapingCardBean getCarDataOfPosition(int position) {
        if (cardList != null && position < cardList.size()) {
            return cardList.remove(position);//使用remove直接获取数据并移除数据
        }
        return null;
    }

    //加载激励视屏广告
    private void loadVideoAdv(Activity activity, String advId) {
        boolean isFast = AndroidUtil.isFastDoubleBtnClick(4000);
        LogUtils.debugInfo("加载=1====loadVideoAdv===" + isFast + "====" + activity);
        if (isFast || activity == null) {
            return;
        }
        pointVideo();
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(advId).setActivity(activity)
                .setViewContainer((ViewGroup) activity.getWindow().getDecorView()).build();
        LogUtils.debugInfo("加载=2====loadVideoAdv");
        MidasRequesCenter.requestAndShowAd(activity, advId, new VideoAbsAdCallBack() {
            @Override
            public void onAdLoadError(String errorCode, String errorMsg) {
                super.onAdLoadError(errorCode, errorMsg);
                goToScrapingCarDetail(activity);
            }

            @Override
            public void onAdClose(AdInfoModel adInfoModel) {
                super.onAdClose(adInfoModel);
                handlerCloseClick();
                goToScrapingCarDetail(activity);
            }
        });
    }

    /**
     * 跳转刮刮卡详情
     */
    private void goToScrapingCarDetail(Activity activity) {
        if (activity == null || cardBean == null) {
            return;
        }
        if (!UserHelper.init().isLogin()) {//没登录跳登录页面
            UserHelper.init().startToLogin(activity);
            return;
        }
        if (activity instanceof ScrapingCarDetailActivity) {
            activity.finish();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, assembleScrapingCardUrl());
        String title = "";
        if (!TextUtils.isEmpty(title)) {
            bundle.putString(Constant.Title, title);
        }
//        bundle.putBoolean(Constant.NoTitle, isNoTitle);
        //跳转刮刮乐
        Intent toIntent = new Intent(activity, ScrapingCarDetailActivity.class);
        toIntent.putExtras(bundle);
        activity.startActivity(toIntent);
        LogUtils.debugInfo("跳转刮刮卡详情===" + assembleScrapingCardUrl());
    }

    private int parseInt(String numStr) {
        return Integer.parseInt(numStr);
    }

    /**
     * 组装请求连接地址
     *
     * @return
     */
    private String assembleScrapingCardUrl() {
        if (cardBean == null) {
            return "";
        }
        StringBuffer urlBuf = new StringBuffer();
        int goldSectionNum = 0;
        if (!TextUtils.isEmpty(cardBean.getGoldSection()) && cardBean.getGoldSection().contains("-")) {
            String[] goldSectionArray = cardBean.getGoldSection().split("-");
            goldSectionNum = (int) (Math.random() * (parseInt(goldSectionArray[1]) - parseInt(goldSectionArray[0])) + parseInt(goldSectionArray[0]));
        }
        urlBuf.append(H5Urls.SCRATCHCARDS_DETAIL_URL)
                .append("id=" + cardBean.getId())
                .append("&cardPosition=" + cardBean.getCardPosition())
//                .append("&rondaId=" + cardBean.getRondaId())
                .append("&awardType=" + cardBean.getAwardType())
                .append("&hitCode=" + cardBean.getHitCode())
                .append("&num=" + cardBean.getNum())
//                .append("&remark=" + cardBean.getRemark())
//                .append("&cardType=" + cardBean.getCardType())
                .append("&goldSectionNum=" + goldSectionNum)
                .append("&actRdNum=" + cardBean.getActRdNum())
                .append("&doubledMagnification=" + cardBean.getDoubledMagnification());
        return urlBuf.toString();
    }

    /**
     * 获取视频ID
     *
     * @param context
     * @param index
     * @return
     */
    public String getCarAdvId(Context context, String advStyle, int index) {
        String resourceName = advStyle + index;
        if (context == null) {
            LogUtils.debugInfo("不能加载广告，context为空。");
            return "";
        }
        int resourceId = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());

        if (resourceId == 0) {
            LogUtils.debugInfo("不能加载广告，广告resourceId为0");
            return "";
        }
        try {
            return context.getResources().getString(resourceId);
        } catch (Exception e) {
            LogUtils.debugInfo("不能加载广告，获取广告id异常。");
        }
        return "";
    }


    /**
     * 解析url协议
     */
    public boolean parseUrl(Activity mActivity, String url) {
        if (SchemeUtils.isScheme(url)) {
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            Parameters parameters = UrlUtils.getParamsFromUrl(url);
//            parseVideoCallBackParams(parameters);
            boolean needLogin = TextUtils.equals("1", parameters.getParameter(SchemeConstant.NEED_LOGIN));
            if (needLogin) {
                if (!UserHelper.init().isWxLogin() && mActivity != null && !mActivity.isFinishing()) {
                    UserHelper.init().startToLogin(mActivity);
                    return true;
                }
            }
            if (SchemeConstant.REWARD_TOP.equals(path)) {
                //激励后的弹窗 插屏广告
                parseRewardPop(mActivity, parameters);
            } else if (SchemeConstant.CLOSE.equals(path)) {
                //关闭H5协议
                parseClose(mActivity, parameters);
            } else {
                SchemeUtils.openScheme(mActivity, url, null, ActivityUtils.REQUEST_CODE_FROM_BROWSER);
            }
            return true;
        }
        return false;
    }

    /**
     * 激励后的弹窗 插屏广告
     */
    private void parseRewardPop(Activity mActivity, Parameters parameters) {
        if (mActivity == null && parameters == null) {
            return;
        }
        String codeId = parameters.getParameter(SchemeConstant.AD_CODEID);//插屏广告code
        int source = NumberUtils.getInteger(parameters.getParameter(SchemeConstant.AD_SOURCE));//广告来源
        ADUtils.adSource = parameters.getParameter(SchemeConstant.AD_ADDES);//点击的哪个任务了
        String ad_position_id = parameters.getParameter(SchemeConstant.AD_AD_POSITION_ID);
        //总金币数
        String totalCoin = parameters.getParameter(SchemeConstant.TOTAL_COIN);
        //当前奖励金币数
        String coin = parameters.getParameter(SchemeConstant.COIN);
        //area=1  区域1
        //area=2  区域2
        String area = parameters.getParameter(SchemeConstant.AREA);
        //是否需要翻倍
        boolean isDouble = TextUtils.equals("1", parameters.getParameter(SchemeConstant.IS_DOUBLE));
        //h5 可能需要传这个id
        String taskId = parameters.getParameter(SchemeConstant.TASK_ID);
        //翻倍的倍率
        String doubledmagnification = parameters.getParameter(SchemeConstant.DOUBLEDMAGNIFICATION);
        //签到天数
        String signDay = parameters.getParameter(SchemeConstant.SIGNDAY);
        String cardPosition = parameters.getParameter(SchemeConstant.CARD_POSITION);
        if (!TextUtils.isEmpty(totalCoin) && !TextUtils.isEmpty(coin) && !TextUtils.isEmpty(cardPosition)) {
            int obtainCoinCount = Integer.parseInt(coin);//获得的金币
            int totalCoinCount = Integer.parseInt(totalCoin);//用户总金币金额===这里totalCoinCount在h5已经添加过了
            int doubleNum = 2;
            if (!TextUtils.isEmpty(doubledmagnification)) {
                doubleNum = Integer.parseInt(doubledmagnification);
            }
            showDialog(mActivity, obtainCoinCount, totalCoinCount, isDouble, "1".equals(area), doubleNum);
        }
    }

    /**
     * 显示奖励弹窗
     *
     * @param mActivity
     * @param coinCount
     * @param totalCoinCount
     * @param isDouble
     * @param isAreaOne            是否是区域1 true 是  false 刮的区域2
     * @param doubledmagnification
     */
    public void showDialog(Activity mActivity, int coinCount, int totalCoinCount, boolean isDouble, boolean isAreaOne, int doubledmagnification) {
        if (mActivity == null) {
            LogUtils.debugInfo("activity 对象为空，不能弹框");
            return;
        }
        boolean isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_SCRATCH_CARD, PositionId.DRAW_ONE_CODE);
        boolean isOpenTwo = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_SCRATCH_CARD, PositionId.DRAW_TWO_CODE);
        boolean isShowNext = cardList.size() > 0;
        GoldCoinDialogParameter parameter = new GoldCoinDialogParameter();
        parameter.context = mActivity;
        parameter.isDouble = isDouble && !isAreaOne;
        parameter.isRewardOpen = isShowNext && !isAreaOne;
        parameter.advCallBack = new AbsAdBusinessCallback() {
        };
        parameter.onDoubleClickListener = v -> clickNextCard(mActivity);
//        parameter.closeClickListener = v -> handlerCloseClick();
        parameter.totalCoinCount = totalCoinCount;
        parameter.doubleNums = doubledmagnification;
        if (cardBean != null) {
            parameter.adId = isOpenOne && !isAreaOne ? getCarAdvId(mActivity, ADV_FIRST_PREFIX, cardBean.getCardPosition()) : "";
        }
        parameter.obtainCoinCount = coinCount;
        parameter.doubleMsg = "刮下一张";
        GoldCoinDialog.showGoldCoinDialog(parameter);
        goldPoint(coinCount, cardIndex);
//        StatisticsUtils.scratchCardCustom(Points.ScratchCard.WINDOW_UP_EVENT_CODE, Points.ScratchCard.WINDOW_UP_EVENT_NAME, currentPosition, "", Points.ScratchCard.WINDOW_PAGE);
    }

    /**
     * 关闭H5协议
     */
    private void parseClose(Activity mActivity, Parameters parameters) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        String target = parameters.getParameter(SchemeConstant.TARGET);
        if (!TextUtils.isEmpty(target) && SchemeUtils.isScheme(target)) {
            SchemeUtils.openScheme(mActivity, target, null, ActivityUtils.REQUEST_CODE_FROM_BROWSER);
        }
        mActivity.finish();
    }

    /**
     * 点击下一个卡片
     */
    private void clickNextCard(Activity activity) {
        GoldCoinDialog.dismiss();
        this.currentPosition = 0;
        scrapingCardNextAction(activity, true);
        //点击刮下一个埋点
        StatisticsUtils.scratchCardClick(Points.ScratchCard.WINDOW_DOUBLE_CLICK_EVENT_CODE, Points.ScratchCard.WINDOW_DOUBLE_CLICK_EVENT_NAME, cardBean.getCardPosition(), "", Points.ScratchCard.WINDOW_PAGE);
    }

    /**
     * 金币领取埋点
     */
    private void goldPoint(int coinCount, int cardIndex) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("position_id", cardIndex);
        map.put("gold_number", coinCount);
        StatisticsUtils.customTrackEvent("number_of_gold_coins_issued", Points.ScratchCard.WINDOW_GOLD_NUM_NAME, "", Points.ScratchCard.WINDOW_PAGE, map);
    }

    /**
     * 点击关闭按钮事件
     */
    private void handlerCloseClick() {
        StatisticsUtils.scratchCardClick(Points.ScratchCard.WINDOW_CLOSE_CLICK_CODE, Points.ScratchCard.WINDOW_CLOSE_CLICK_NAME, cardIndex, "", Points.ScratchCard.WINDOW_PAGE);
    }

    private void pointVideo() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("position_id", cardIndex);
        StatisticsUtils.customTrackEvent("ad_request_sdk", "刮刮卡翻倍激励视频广告发起请求", "", "scraping_card_list_page", map);
    }
}
