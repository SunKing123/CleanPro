package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.BaseDialog;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinBean;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe:
 */
public class GoldCoinDialog {
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
        ViewGroup rootView = dialog.findViewById(R.id.ad_root_view);
        ImageView adIv = dialog.findViewById(R.id.ad_iv);
        ImageView adIvLogo = dialog.findViewById(R.id.native_insert_ad_logo);
        TextView adDesTv = dialog.findViewById(R.id.ad_title_tv);
        TextView lookInfoTv = dialog.findViewById(R.id.look_info_tv);
        TextView tv_coin_str = dialog.findViewById(R.id.tv_coin_str);
        TextView title = dialog.findViewById(R.id.title_tv);
        TextView download_tv = dialog.findViewById(R.id.download_tv);
        ImageView logo_iv = dialog.findViewById(R.id.ad_small_iv);
        ImageView mLlAdAnim = dialog.findViewById(R.id.ll_ad_anim);
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
        iv_top_one.setVisibility(View.GONE);
        ivAnim.setVisibility(View.GONE);
        rl_type_two.setVisibility(View.GONE);
        tv_coin_str.setVisibility(View.GONE);
        iv_top_three.setVisibility(View.GONE);
        ll_my_coin.setVisibility(View.GONE);
        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) ll_top_content.getLayoutParams();
        if (coinBean.dialogType == 2) {
            rl_type_two.setVisibility(View.VISIBLE);
            layoutParams.topMargin = DisplayUtil.dip2px(coinBean.context, 65);
        } else if (coinBean.dialogType == 3) {
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
            playRotateAnim(ivAnim);
        }
        obtainCoinCountTv.setText(String.valueOf(totalCoin));
        if (coinBean.totalCoinCount > 99) {
            float calculate = Math.round((coinBean.totalCoinCount)) / 10000f;
            String afterCalculate = TimeUtil.getNum(calculate);
            totalCoinCountTv.setText(Html.fromHtml(coinBean.totalCoinCount + "≈<font color=#febf28>" + afterCalculate + "元</font>"));
        } else {
            totalCoinCountTv.setText(Html.fromHtml(coinBean.totalCoinCount + "≈<font color=#febf28>" + 0.00 + "元</font>"));
        }
        countDownTimeViewDelay(1000, 3, adLookTime, closeDlg);
        CoinDoubleRL.setVisibility(View.VISIBLE);
        dialog.show();
        closeDlg.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    //倒计时展示  msc 秒数
    private static void countDownTimeViewDelay(int delayTime,
                                               int msc, TextView adLookTime, View closeDlg) {
        if (adLookTime != null) {
            adLookTime.setVisibility(View.GONE);
        }
        new Handler().postDelayed(() -> {
            if (adLookTime != null && closeDlg != null) {
                adLookTime.setVisibility(View.VISIBLE);
                countDownTimeView(msc, adLookTime, closeDlg);
            }

        }, delayTime);
    }

    //倒计时展示  msc 秒数
    private static void countDownTimeView(int msc, TextView adLookTime, View closeDlg) {
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

    public static void playRotateAnim(View ivAnim) {
        Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(10000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        ivAnim.startAnimation(animation);//開始动画
//        animation.setAnimationListener(new AnimationListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                super.onAnimationEnd(animation);
//                playRotateAnim(ivAnim);
//            }
//        });
    }
}
