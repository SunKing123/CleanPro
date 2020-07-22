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
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.viruskill.newversion.adapter.VirusScanIconItemAdapter;
import com.xiaoniu.cleanking.ui.viruskill.newversion.adapter.VirusScanTextItemAdapter;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.ScanTextItemModel;
import com.xiaoniu.cleanking.ui.viruskill.newversion.presenter.VirusScanPresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.common.utils.Points;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    @BindView(R.id.recycle_virus_scan_text)
    RecyclerView recycleViewText;
    @BindView(R.id.recycle_virus_scan_icon)
    RecyclerView recycleViewIcon;
    @BindView(R.id.tv_virus_scan_privacy)
    TextView tvPrivacy;
    @BindView(R.id.image_virus_scan_privacy)
    ImageView imgPrivacy;
    @BindView(R.id.image_virus_scan)
    ImageView imgVirusScan;
    @BindView(R.id.image_virus_network)
    ImageView imgNetwork;

    private CountDownTimer timer;

    private long millisInFuture = 10000;
    private long countDownInterval = 100;

    private NewVirusKillContract.VirusScanPresenter presenter;
    private VirusScanTextItemAdapter textAdapter;
    private VirusScanIconItemAdapter iconAdapter;

    public static NewVirusScanFragment getInstance() {
        return new NewVirusScanFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_virus_kill_scan_layout;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        presenter = new VirusScanPresenter(this);
        presenter.onCreate();
        initRecycleView();
        startScanLoading();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    private void initRecycleView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycleViewText.setLayoutManager(layoutManager);
        textAdapter = new VirusScanTextItemAdapter();
        recycleViewText.setAdapter(textAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        recycleViewIcon.setLayoutManager(gridLayoutManager);
        iconAdapter = new VirusScanIconItemAdapter();
        recycleViewIcon.setAdapter(iconAdapter);
    }

    @Override
    public void startScanLoading() {
        startCountDown();
        lottie.startRotationAnimation();
    }

    private void startCountDown() {
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                int progress = (int) (100 - millisUntilFinished / 100);
                if (txtPro != null) txtPro.setText(progress + "%");
                presenter.onScanLoadingProgress(progress);
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
        tvScanTitle.setText(title);
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
    public void addScanPrivacyItem(ScanTextItemModel model) {
        if (textAdapter.getItemCount() == 4) {
            textAdapter.removeTop();
        }
        textAdapter.addData(model);
    }

    @Override
    public void setScanPrivacyComplete() {
        textAdapter.updateState();
    }

    @Override
    public void showScanVirusIcons(ArrayList<FirstJunkInfo> list) {
        recycleViewText.setVisibility(View.GONE);
        recycleViewIcon.setVisibility(View.VISIBLE);
        iconAdapter.setDataList(list);
    }

    @Override
    public void setScanVirusComplete() {
        imgVirusScan.setImageResource(R.drawable.icon_virus_ok);
    }

    @Override
    public void startScanNetwork() {
        textAdapter.clean();
        recycleViewText.setVisibility(View.VISIBLE);
        recycleViewIcon.setVisibility(View.GONE);
    }

    @Override
    public void addScanNetWorkItem(ScanTextItemModel model) {
        if (textAdapter.getItemCount() == 4) {
            textAdapter.removeTop();
        }
        textAdapter.addData(model);
    }

    @Override
    public void scanAllComplete() {
        textAdapter.updateState();
    }

    @Override
    public void showMessage(@NonNull String message) {

    }
}
