package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseDialog;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasConstants;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.activity.GoldCoinSuccessActivity;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinBean;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;
import com.xiaoniu.cleanking.utils.DimenUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationRotateUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationScaleUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationsContainer;
import com.xnad.sdk.ad.entity.AdInfo;
import com.xnad.sdk.ad.listener.AbsAdCallBack;

import androidx.appcompat.widget.AppCompatButton;
import androidx.room.util.StringUtil;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe:
 */
public class GoldCoinDialog {

    //广告id资源前缀
    public static final String ADV_FIRST_PREFIX = "scratch_card_first";
    public static final String ADV_SECOND_PREFIX = "scratch_card_second";
    public static final String ADV_VIDEO_PREFIX = "scratch_card_video";

    public static void showGoldCoinDialog(GoldCoinBean coinBean) {
        if (coinBean == null || coinBean.obtainCoinCount < 0) {
            return;
        }
        BaseDialog dialog = new BaseDialog(coinBean.context, R.style.common_dialog_style);
        dialog.setContentView(R.layout.gold_coin_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setGravityLayout(BaseDialog.CENTER);
        dialog.setFullScreen();

        Typeface typ_ME = Typeface.createFromAsset(coinBean.context.getAssets(), "DIN-Medium.otf");
        Typeface typ_RE = Typeface.createFromAsset(coinBean.context.getAssets(), "DIN-Regular.otf");
        TextView adLookTime = dialog.findViewById(R.id.ad_look_time);
        ImageView closeDlg = dialog.findViewById(R.id.closeDlg);
        TextView obtainCoinCountTv = dialog.findViewById(R.id.obtain_coin_count);
        obtainCoinCountTv.setTypeface(typ_ME);
        TextView totalCoinCountTv = dialog.findViewById(R.id.total_coin_count_tv);
        totalCoinCountTv.setTypeface(typ_RE);
        RelativeLayout CoinDoubleRL = dialog.findViewById(R.id.coin_double_rl);
        TextView tv_coin_str = dialog.findViewById(R.id.tv_coin_str);
        // ImageView mLlAdAnim = dialog.findViewById(R.id.ll_ad_anim);
        FrameLayout mRootRL = dialog.findViewById(R.id.root_fl);
        RelativeLayout ll_top = dialog.findViewById(R.id.ll_top);
        NativeAdContainer container = dialog.findViewById(R.id.native_ad_container);
        LinearLayout middle_ll = dialog.findViewById(R.id.middle_ll);
        LinearLayout ll_top_content = dialog.findViewById(R.id.ll_top_content);
        LinearLayout ll_my_coin = dialog.findViewById(R.id.ll_my_coin);
        RelativeLayout rl_type_two = dialog.findViewById(R.id.rl_type_two);
        ImageView ivAnim = dialog.findViewById(R.id.iv_anim);
        ImageView iv_top_one = dialog.findViewById(R.id.iv_top_one);
        ImageView iv_top_three = dialog.findViewById(R.id.iv_top_three);
        AppCompatButton see_video_to_double = dialog.findViewById(R.id.see_video_to_double);
        iv_top_one.setVisibility(View.GONE);
        ivAnim.setVisibility(View.GONE);
        rl_type_two.setVisibility(View.GONE);
        tv_coin_str.setVisibility(View.GONE);
        iv_top_three.setVisibility(View.GONE);
        ll_my_coin.setVisibility(View.GONE);

//        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) mRootRL.getLayoutParams();
//        layoutParams1.height = DimenUtils.dp2px(coinBean.context, 150);
//        mRootRL.setLayoutParams(layoutParams1);

        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) ll_top_content.getLayoutParams();
        //dialog的类型 1 转圈  2 撒花 3 清理金币奖励 默认是1
        if (coinBean.dialogType == 2) {
            rl_type_two.setVisibility(View.VISIBLE);
            layoutParams.topMargin = DisplayUtil.dip2px(coinBean.context, 65);
        } else if (coinBean.dialogType == 3) {
            see_video_to_double.setVisibility(View.VISIBLE);
            see_video_to_double.setOnClickListener(view -> {
                requestAd(coinBean, mRootRL, true);
            });
            iv_top_three.setVisibility(View.VISIBLE);
            layoutParams.topMargin = DisplayUtil.dip2px(coinBean.context, 86);
        } else {
            layoutParams.topMargin = DisplayUtil.dip2px(coinBean.context, 48);
            tv_coin_str.setVisibility(View.VISIBLE);
            iv_top_one.setVisibility(View.VISIBLE);
            ivAnim.setVisibility(View.VISIBLE);
            ll_my_coin.setVisibility(View.VISIBLE);
        }
        ll_top_content.setLayoutParams(layoutParams);
        int totalCoin;
        if (coinBean.fbTip) {//手动点击翻倍和自动翻倍后的需要展示插屏结果
//            tv_coin_str.setText("获得翻倍奖励");
            adLookTime.setVisibility(View.GONE);
            totalCoin = coinBean.obtainCoinCount * 2;
        } else {
            adLookTime.setVisibility(View.VISIBLE);
            tv_coin_str.setText("恭喜获得");
            totalCoin = coinBean.obtainCoinCount;
            AnimationRotateUtils.getInstance().playRotateAnim(ivAnim, 10000);
        }
        if (coinBean.isDouble) {
            CoinDoubleRL.setVisibility(View.VISIBLE);
            AnimationScaleUtils.getInstance().playScaleAnimation(CoinDoubleRL, 1000);
        }
        if (coinBean.dialogType == 3) {
            obtainCoinCountTv.setText("+" + totalCoin);
            requestAd(coinBean, mRootRL, false);
        } else {
            obtainCoinCountTv.setText(String.valueOf(totalCoin));
        }

        if (coinBean.totalCoinCount > 99) {
            float calculate = Math.round((coinBean.totalCoinCount)) / 10000f;
            String afterCalculate = TimeUtil.getNum(calculate);
            totalCoinCountTv.setText(Html.fromHtml(coinBean.totalCoinCount + "≈<font color=#febf28>" + afterCalculate + "元</font>"));
        } else {
            totalCoinCountTv.setText(Html.fromHtml(coinBean.totalCoinCount + "≈<font color=#febf28>" + 0.00 + "元</font>"));
        }
        dialog.show();
        countDownTimeViewDelay(3, adLookTime, closeDlg);
        //边上跑的动画
        AnimationsContainer.FrameseAnim animaDra = null;
        //  animaDra = AnimationsContainer.getInstance(R.array.small_ad_anim, 80).createAnim(mLlAdAnim);
        //  animaDra.start();
        closeDlg.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }


    /**
     *
     */
    private static void requestAd(GoldCoinBean coinBean, ViewGroup mRootRL, boolean isVideo) {
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(isVideo ? coinBean.adVideoId : coinBean.adId).setActivity((Activity) coinBean.context)
                .setViewContainer(mRootRL).build();
        MidasRequesCenter.requestAd(params, new AbsAdCallBack() {
            @Override
            public void onAdVideoComplete(AdInfo adInfo) {
                super.onAdVideoComplete(adInfo);
                if (isVideo && AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GET_DOUBLE_GOLD_COIN_SUCCESS)) {
                    Intent intent = new Intent(coinBean.context, GoldCoinSuccessActivity.class);
                    intent.putExtra(GoldCoinSuccessActivity.COIN_NUM, coinBean.obtainCoinCount * 2);
                    intent.putExtra(GoldCoinSuccessActivity.AD_ID, MidasConstants.GET_DOUBLE_GOLD_COIN_SUCCESS);
                    coinBean.context.startActivity(intent);
                }
            }
        });
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
            }

            @Override
            public void onShowError(int i, String s) {
                super.onShowError(i, s);
                log("onShowError()====" + resNamePrefix + "====" + s);
            }

            @Override
            public void onAdShow(AdInfo adInfo) {
                super.onAdShow(adInfo);
                log("onAdShow()====" + resNamePrefix);
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                super.onAdClicked(adInfo);
                log("onAdClicked()====" + resNamePrefix);
            }

            @Override
            public void onAdClose(AdInfo adInfo) {
                super.onAdClose(adInfo);
                log("onAdClose()====" + resNamePrefix);
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

    //倒计时展示  msc 秒数
    private static void countDownTimeViewDelay(int msc, TextView adLookTime, View closeDlg) {
        if (adLookTime != null) {
            adLookTime.setVisibility(View.GONE);
        }
        new Handler().post(() -> {
            if (adLookTime != null && closeDlg != null) {
                countDownTimeView(msc, adLookTime, closeDlg);
            }
        });
    }

    //倒计时展示  msc 秒数
    private static void countDownTimeView(int msc, TextView adLookTime, View closeDlg) {
        adLookTime.setVisibility(View.VISIBLE);
        adLookTime.setText(msc + "");
        new CountDownTimer(msc * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                adLookTime.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                adLookTime.setVisibility(View.GONE);
                closeDlg.setVisibility(View.VISIBLE);
            }
        }.start();
    }

}
