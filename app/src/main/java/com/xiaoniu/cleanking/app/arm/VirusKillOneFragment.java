package com.xiaoniu.cleanking.app.arm;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.LeiDaView;
import com.jess.arms.widget.RotationLoadingView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.master.cleanking.interfaces.FragmentCallBack;
import com.xiaoniu.statistic.NiuDataAPI;

import butterknife.BindView;

/**
 * Author: lvdongdong
 * Date :  2020/2/18
 * Desc :
 */
public class VirusKillOneFragment extends SimpleFragment {
    @BindView(R.id.lottie)
    LeiDaView lottie;
    @BindView(R.id.llyContext)
    LinearLayout llyContext;
    @BindView(R.id.txtPro)
    TextView txtPro;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_viruskill_one;
    }

    public static VirusKillOneFragment getIntance() {
        return new VirusKillOneFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    CountDownTimer timer;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        NiuDataAPI.onPageStart("virus_killing_scan_page_view_page", "病毒查杀扫描页浏览");
        NiuDataAPIUtil.onPageEnd("virus_killing_scan_page_view_page", "病毒查杀扫描页浏览","","virus_killing_scan_page");
        timer = new CountDownTimer(3000, 30) {
            public void onTick(long millisUntilFinished) {
                if (txtPro != null) txtPro.setText((100 - millisUntilFinished / 30) + "%");
            }

            public void onFinish() {
                if (lottie != null) {
                    lottie.stopRotationAnimation();
                }
                if (callBack != null) callBack.checkFragment();
            }
        };
        timer.start();
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                lottie.startRotationAnimation();
                addItemView();
            }
        }, 500);
    }



    public void onFragmentDestroy() {
        if (lottie != null) {
            lottie.stopRotationAnimation();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    private int[] tip = {R.string.clean_all_hint, R.string.vircuskill_item_two_tip, R.string.vircuskill_item_three_tip};
    private int[] tips = {R.string.vircuskill_item_one_tips, R.string.vircuskill_item_two_tips, R.string.vircuskill_item_three_tips};
    private int[] iconImage = {R.mipmap.icon_sd_one, R.mipmap.icon_sd_two, R.mipmap.icon_sd_three};

    private void addItemView() {
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_viruskill, null);
            TextView txtTip = view.findViewById(R.id.txtTitle);
            TextView txtTips = view.findViewById(R.id.txtContext);
            ImageView imageView = view.findViewById(R.id.iconImage);
            RotationLoadingView rotationLoading = view.findViewById(R.id.rotationLoading);
            rotationLoading.startRotationAnimation();
            SpannableString spannableString = new SpannableString(String.format(getResources().getString(tip[i]), NumberUtils.getNum(1, 3)));
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6D58")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtTip.setText(spannableString);
            txtTips.setText(tips[i]);
            imageView.setImageDrawable(getResources().getDrawable(iconImage[i]));
            llyContext.addView(view);
        }
    }


    private FragmentCallBack callBack;

    public void setFragmentCallBack(FragmentCallBack callBack) {
        this.callBack = callBack;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
             StatisticsUtils.trackClick("system_return_click","用户在病毒查杀页返回", "", "virus_killing_scan_page");
        }
        return true;
    }
}
