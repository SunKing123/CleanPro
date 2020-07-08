package com.xiaoniu.cleanking.ui.newclean.dialog;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.comm.jksdk.utils.DisplayUtil;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.BaseDialog;
import com.xiaoniu.cleanking.midas.AdRequestParams;
import com.xiaoniu.cleanking.midas.MidasRequesCenter;
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter;
import com.xiaoniu.cleanking.ui.newclean.util.OutlineProvider;
import com.xiaoniu.cleanking.ui.tool.wechat.util.TimeUtil;
import com.xiaoniu.cleanking.utils.DimenUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationRotateUtils;
import com.xiaoniu.cleanking.utils.anim.AnimationScaleUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xnad.sdk.MidasAdSdk;
import com.xnad.sdk.ad.listener.AbsAdCallBack;
import com.xnad.sdk.ad.listener.AskReadyCallBack;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/1
 * Describe:
 */
public class GoldCoinDialog {

    private static BaseDialog dialog;

    public static void showGoldCoinDialog(GoldCoinDialogParameter parameter) {

        int fromType = parameter.fromType;
        Activity context = parameter.context;
        AbsAdCallBack advCallBack = parameter.advCallBack;
        View.OnClickListener onDoubleClickListener = parameter.onDoubleClickListener;

        if (context == null || advCallBack == null || onDoubleClickListener == null || parameter == null || parameter.obtainCoinCount < 0) {
            if (BuildConfig.DEBUG) {
                ToastUtils.showShort("加载广告请求参数错误！！！");
            }
            return;
        }

        dialog = new BaseDialog(context, R.style.common_dialog_style);
        dialog.setContentView(R.layout.gold_coin_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setGravityLayout(BaseDialog.CENTER);
        dialog.setFullScreen();
        Typeface typ_ME = Typeface.createFromAsset(context.getAssets(), "DIN-Medium.otf");
        Typeface typ_RE = Typeface.createFromAsset(context.getAssets(), "DIN-Regular.otf");
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mRootRL.setOutlineProvider(new OutlineProvider(DimenUtils.dp2px(context,8)));
            mRootRL.setClipToOutline(true);
        }
        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) ll_top_content.getLayoutParams();
        //dialog的类型 1 转圈  2 撒花 3 清理金币奖励 默认是1
        if (parameter.dialogType == 2) {
            rl_type_two.setVisibility(View.VISIBLE);
            layoutParams.topMargin = DisplayUtil.dip2px(context, 65);
        } else if (parameter.dialogType == 3) {
            see_video_to_double.setVisibility(View.VISIBLE);
            see_video_to_double.setOnClickListener(onDoubleClickListener);
            iv_top_three.setVisibility(View.VISIBLE);
            layoutParams.topMargin = DisplayUtil.dip2px(context, 86);
        } else {
            layoutParams.topMargin = DisplayUtil.dip2px(context, 48);
            tv_coin_str.setVisibility(View.VISIBLE);
            iv_top_one.setVisibility(View.VISIBLE);
            ivAnim.setVisibility(View.VISIBLE);
            ll_my_coin.setVisibility(View.VISIBLE);
        }


        switch (fromType) {
            case GoldCoinDialogParameter.FROM_SCRATCH_CARD:
                CoinDoubleRL.setVisibility(View.VISIBLE);
                break;
            case GoldCoinDialogParameter.FROM_FINISH_COMPLETE:

                break;
        }
        ll_top_content.setLayoutParams(layoutParams);
        int totalCoin;
        if (parameter.fbTip) {//手动点击翻倍和自动翻倍后的需要展示插屏结果
//            tv_coin_str.setText("获得翻倍奖励");
            adLookTime.setVisibility(View.GONE);
            totalCoin = parameter.obtainCoinCount * 2;
        } else {
            adLookTime.setVisibility(View.VISIBLE);
            tv_coin_str.setText("恭喜获得");
            totalCoin = parameter.obtainCoinCount;
            AnimationRotateUtils.getInstance().playRotateAnim(ivAnim, 10000);
        }
        if (parameter.isDouble) {
            CoinDoubleRL.setVisibility(View.VISIBLE);
            AnimationScaleUtils.getInstance().playScaleAnimation(CoinDoubleRL, 1000);
        }
        if (parameter.dialogType == 3) {
            obtainCoinCountTv.setText("+" + totalCoin);
        } else {
            obtainCoinCountTv.setText(String.valueOf(totalCoin));
        }

        if (parameter.totalCoinCount > 99) {
            float calculate = Math.round((parameter.totalCoinCount)) / 10000f;
            String afterCalculate = TimeUtil.getNum(calculate);
            totalCoinCountTv.setText(Html.fromHtml(parameter.totalCoinCount + "≈<font color=#febf28>" + afterCalculate + "元</font>"));
        } else {
            totalCoinCountTv.setText(Html.fromHtml(parameter.totalCoinCount + "≈<font color=#febf28>" + 0.00 + "元</font>"));
        }

        CoinDoubleRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击翻倍，显示激励广告
                onDoubleClickListener.onClick(v);
            }
        });
        //需求设计不需要倒计时
        adLookTime.setVisibility(View.GONE);
        closeDlg.setVisibility(View.VISIBLE);
//        countDownTimeViewDelay(3, adLookTime, closeDlg);
        closeDlg.setOnClickListener(view -> {
            if (parameter.closeClickListener != null) {
                parameter.closeClickListener.onClick(view);
            }
            dialog.dismiss();
        });

        dialog.setOnDismissListener(parameter.dismissListener);
        //当传进来的id为空时，不加载广告
        if (TextUtils.isEmpty(parameter.adId)) {
            middle_ll.setVisibility(View.GONE);
            dialog.show();
            return;
        }
        requestAd(context, advCallBack, parameter, mRootRL);
    }

    private static void requestAd(Activity context, AbsAdCallBack callBack, GoldCoinDialogParameter coinBean, ViewGroup mRootRL) {
        AdRequestParams params = new AdRequestParams.Builder()
                .setAdId(coinBean.adId).setActivity(context)
                .setViewContainer(mRootRL).build();
        MidasAdSdk.getAdsManger().askIsReady(context, coinBean.adId, new AskReadyCallBack() {
            @Override
            public void onReady(boolean b) {
                if (dialog != null && !context.isFinishing()) {
                    //延迟显示，体验更好
                    new CountDownTimer(500, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            dialog.show();
                        }
                    }.start();
                    MidasRequesCenter.requestAd(params, callBack);
                }
            }
        });
    }

    public static void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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

    private void doubleClick() {

    }
}
