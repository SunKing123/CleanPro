package com.xiaoniu.cleanking.app.arm;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.LeiDaView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;

import static android.view.View.VISIBLE;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.COMPLETE;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.PAGE_VIEW;
import static com.xiaoniu.cleanking.app.arm.VirusKillStatus.SCAN;

/**
 * Author: lvdongdong
 * Date :  2020/2/18
 * Desc :
 */
public class VirusKillTwoFragment extends SimpleFragment {


    @BindView(R.id.lottie)
    LeiDaView lottie;
    @BindView(R.id.txtPro)
    TextView txtPro;
    @BindView(R.id.txtTips)
    TextView txtTips;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.llyContext)
    LinearLayout llyContext;
    @BindView(R.id.view_lottie)
    LottieAnimationView viewLottie;
    @BindView(R.id.tv_anim_title)
    TextView tvAnimTitle;
    @BindView(R.id.fl_anim)
    FrameLayout flAnim;
    @BindView(R.id.flyTop)
    RelativeLayout flyTop;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_viruskill_two;
    }

    public static VirusKillTwoFragment getIntance() {
        return new VirusKillTwoFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lottie != null) {
                    lottie.startRotationAnimation();
                    setProgressBar();
                }
            }
        }, 3000);
    }

    CountDownTimer timer;


    private void setProgressBar() {

        Log.e("virusKill","=========病毒查杀扫描页面展示=========");

        StatisticsUtils.onPageStart("virus_killing_animation_page_view_page","病毒查杀动画页浏览");
        StatisticsUtils.onPageEnd("virus_killing_animation_page_view_page","病毒查杀动画页浏览","virus_killing_scan_page","virus_killing_animation_page");


        VirusKillStatus.code=SCAN;
        timer = new CountDownTimer(5000, 50) {
            public void onTick(long millisUntilFinished) {
                long pro = (100 - millisUntilFinished / 50);
                if (pro >= 66) {
                    txtTips.setText(getString(R.string.vircuskill_fg_two_tip_two));
                }
                txtPro.setText(String.valueOf(pro));
                progressBar.setProgress((int) pro);
            }

            public void onFinish() {
                showFinishLottie();
            }
        };
        timer.start();

    }

    @Override
    public void setData(@Nullable Object data) {

    }
    public void onFragmentDestroy() {
        if (lottie != null) {
            lottie.stopRotationAnimation();
        }
        if (viewLottie != null) {
            viewLottie.cancelAnimation();
            viewLottie.clearAnimation();
            viewLottie.setImageAlpha(0);
        }
        if (timer != null) {
            timer.cancel();
        }
    }


    private void showFinishLottie() {

        VirusKillStatus.code=COMPLETE;
        Log.e("virusKill","=========病毒查杀完成页面===========");

        StatisticsUtils.onPageStart("virus_killing_finish_animation_page_view_page","病毒查杀动画完成页浏览");
        StatisticsUtils.onPageEnd("virus_killing_finish_animation_page_view_page","病毒查杀动画完成页浏览","virus_killing_animation_page","virus_killing_finish_animation_page");

        tvAnimTitle.setVisibility(VISIBLE);
        flyTop.setVisibility(View.GONE);
        flAnim.setVisibility(VISIBLE);
//        mAnimationView.useHardwareAcceleration();
        viewLottie.setAnimation("yindao2.json");
        viewLottie.setImageAssetsFolder("images_game_yindao2");
        viewLottie.playAnimation();
        viewLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (lottie != null) {
                    lottie.stopRotationAnimation();
                }
                killedCompleteCallBack();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void killedCompleteCallBack(){
        //设置锁屏数据
        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(2);
        btnInfo.setNormal(true);
        btnInfo.setCheckResult("500");
        btnInfo.setReShowTime(System.currentTimeMillis() + 1000 * 60 * 5);
        PreferenceUtil.getInstants().save("lock_pos03", new Gson().toJson(btnInfo));
        EventBus.getDefault().post(btnInfo);
        //保存杀毒完成时间
        PreferenceUtil.saveVirusKillTime();
        AppHolder.getInstance().setCleanFinishSourcePageId("virus_killing_animation_page");

        EventBus.getDefault().post(new FromHomeCleanFinishEvent(getString(R.string.virus_kill)));

        Intent mIntent = new Intent(getActivity(), ScreenFinishBeforActivity.class);
        mIntent.putExtra(ExtraConstant.TITLE, getString(R.string.virus_kill));
        if(getActivity().getIntent().hasExtra(ExtraConstant.ACTION_NAME) && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra(ExtraConstant.ACTION_NAME))){
            mIntent.putExtra(ExtraConstant.ACTION_NAME, getActivity().getIntent().getStringExtra(ExtraConstant.ACTION_NAME));
        }
        startActivity(mIntent);
        getActivity().finish();
    }
}
