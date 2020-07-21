package com.xiaoniu.cleanking.ui.viruskill.newversion.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.SimpleFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.widget.LeiDaView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class NewVirusScanFragment extends SimpleFragment implements NewVirusKillContract.VirusScanView {

    @BindView(R.id.lottie)
    LeiDaView lottie;
    @BindView(R.id.txtPro)
    TextView txtPro;
    @BindView(R.id.tv_scan_title)
    TextView tvScanTitle;
    @BindView(R.id.recycle_virus_scan)
    RecyclerView recycleView;
    @BindView(R.id.tv_virus_scan_privacy)
    TextView tvPrivacy;
    @BindView(R.id.image_virus_scan_privacy)
    ImageView imgPrivacy;
    @BindView(R.id.image_virus_scan)
    ImageView imgVirusScan;
    @BindView(R.id.image_virus_network)
    ImageView imgNetwork;


    private CountDownTimer timer;


    long millisInFuture=10000;
    long countDownInterval=100;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_virus_kill_scan_layout;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void startScanLoading() {
        startCountDown();
        lottie.startRotationAnimation();
    }

    private void startCountDown(){
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                if (txtPro != null) txtPro.setText((100 - millisUntilFinished / countDownInterval) + "%");
            }

            public void onFinish() {
                if (lottie != null) {
                    lottie.stopRotationAnimation();
                }
                StatisticsUtils.onPageEnd(Points.Virus.SCAN_PAGE_EVENT_CODE, Points.Virus.SCAN_PAGE_EVENT_NAME, "", Points.Virus.SCAN_PAGE);
            }
        };
        timer.start();
    }
    @Override
    public void setScanTitle(String title) {

    }

    @Override
    public void setPrivacyCount(int count) {
        tvPrivacy.setVisibility(View.VISIBLE);
        imgPrivacy.setVisibility(View.GONE);
        String head = count + "";
        String end = "项风险";
        SpannableString spannable = AndroidUtil.zoomText(head + end, 1.5f, 0, head.length());
        tvPrivacy.setText(spannable);
    }


    @Override
    public void addScanItem(String text) {

    }

    @Override
    public void showScanVirusIcons() {

    }

    @Override
    public void setScanVirusComplete() {
        imgVirusScan.setImageResource(R.drawable.icon_virus_complete);
    }

    @Override
    public void addScanNetWorkItem(String text) {

    }

    @Override
    public void scanComplete() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }
}
