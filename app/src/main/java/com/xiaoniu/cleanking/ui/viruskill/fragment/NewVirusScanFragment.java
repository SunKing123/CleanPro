package com.xiaoniu.cleanking.ui.viruskill.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.SimpleFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.widget.LeiDaView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.viruskill.ITransferPagePerformer;
import com.xiaoniu.cleanking.ui.viruskill.adapter.VirusScanIconItemAdapter;
import com.xiaoniu.cleanking.ui.viruskill.adapter.VirusScanTextItemAdapter;
import com.xiaoniu.cleanking.ui.viruskill.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.model.ScanTextItemModel;
import com.xiaoniu.cleanking.ui.viruskill.presenter.VirusScanPresenter;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
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

    //隐私风险背景颜色
    private static final int pBackGround = 0xff02D086;
    //病毒查杀背景颜色
    private static final int vBackGround = 0xffFFAD00;
    //网络安全扫描背景颜色
    private static final int nBackGround = 0xffFF6D58;

    @BindView(R.id.root_view)
    ViewGroup rootView;
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
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.toolBar)
    LinearLayout toolBar;

    private CountDownTimer timer;

    private long millisInFuture = 5000;
    private long countDownInterval = 50;

    private NewVirusKillContract.VirusScanPresenter presenter;
    private VirusScanTextItemAdapter textAdapter;
    private VirusScanIconItemAdapter iconAdapter;
    private ITransferPagePerformer transferPagePerformer;
    private boolean isFirst=true;

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
        isFirst=true;
        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams) toolBar.getLayoutParams();
        layoutParams.topMargin= DeviceUtils.getStatusBarHeight(mContext);

        presenter = new VirusScanPresenter(this);
        presenter.onCreate();
        initRecycleView();
        startScanLoading();
        tvTitle.setText("病毒查杀");
        toolBar.setOnClickListener(v -> finish());
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

        StatisticsUtils.onPageStart(Points.Virus.SCAN_PAGE_EVENT_CODE, Points.Virus.SCAN_PAGE_EVENT_NAME);

        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                if(isFinishing()){
                    return;
                }
                int progress = (int) (100 - millisUntilFinished / countDownInterval);
                if (txtPro != null) txtPro.setText(getProgressText(progress));
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

    private SpannableString getProgressText(int progress){
        String head=String.valueOf(progress);
        String text=head+" %";
        return  AndroidUtil.zoomText(text,4,0,head.length());
    }


    /**
     * 更新扫描的标题
     *
     * @param title
     */
    @Override
    public void setScanTitle(String title) {
        if(isFinishing()){
            return;
        }
        tvScanTitle.setText(title);
    }



    /*
     * *********************************************************************************************************************************************************
     * **************************************************************privacy start******************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 设置扫描的风险项
     *
     * @param count
     */
    @Override
    public void setPrivacyCount(int count) {
        if(isFinishing()){
            return;
        }
        tvPrivacy.setVisibility(View.VISIBLE);
        imgPrivacy.setVisibility(View.GONE);
        String head = count + "";
        String end = "项风险";
        SpannableString spannable = AndroidUtil.zoomText(head + end, 1.5f, 0, head.length());
        tvPrivacy.setText(spannable);
    }

    /**
     * 添加隐私风险扫描条目
     *
     * @param model
     */
    @Override
    public void addScanPrivacyItem(ScanTextItemModel model) {
        if(isFinishing()){
            return;
        }
        if (textAdapter.getItemCount() == 4) {
            textAdapter.removeTop();
        }
        textAdapter.addData(model);
    }

    /**
     * 隐私风险扫描完毕
     */
    @Override
    public void setScanPrivacyComplete() {
        if(isFinishing()){
            return;
        }
        textAdapter.updateState();
        transitionBackgroundVirus();
    }

    /*
     * *********************************************************************************************************************************************************
     * **************************************************************virus start********************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 设置病毒查杀扫描图标
     *
     * @param list
     */
    @Override
    public void showScanVirusIcons(ArrayList<FirstJunkInfo> list) {
        if(isFinishing()){
            return;
        }
        recycleViewText.setVisibility(View.GONE);
        recycleViewIcon.setVisibility(View.VISIBLE);
        iconAdapter.setDataList(list);
    }

    /**
     * 病毒查杀扫描完毕
     */
    @Override
    public void setScanVirusComplete() {
        if(isFinishing()){
            return;
        }
        imgVirusScan.setImageResource(R.drawable.icon_virus_ok);
        transitionBackgroundNet();
    }


    /*
     * *********************************************************************************************************************************************************
     * **************************************************************network start******************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 开始网络安全扫描
     */
    @Override
    public void startScanNetwork() {
        if(isFinishing()){
            return;
        }
        textAdapter.clean();
        recycleViewText.setVisibility(View.VISIBLE);
        recycleViewIcon.setVisibility(View.GONE);
    }

    /**
     * 添加网络安全扫描条目
     *
     * @param model
     */
    @Override
    public void addScanNetWorkItem(ScanTextItemModel model) {
        if(isFinishing()){
            return;
        }
        if (textAdapter.getItemCount() == 4) {
            textAdapter.removeTop();
        }
        textAdapter.addData(model);
    }

    /**
     * 所有扫描已结束
     */
    @Override
    public void scanAllComplete(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList) {
        if(isFinishing()){
            return;
        }
        textAdapter.updateState();
        txtPro.setText(getProgressText(100));
        imgNetwork.setImageResource(R.drawable.icon_network_scan_complete);
        if(isFirst){
            isFirst=false;
            new Handler().postDelayed(() -> transferPagePerformer.onTransferResultPage(pList, nList), 1000);
        }
    }

    /*
     * *********************************************************************************************************************************************************
     * **************************************************************animation start****************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 过渡到病毒查杀背景
     */
    private void transitionBackgroundVirus() {
        if(isFinishing()){
            return;
        }
        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(rootView, "backgroundColor", pBackGround, vBackGround);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(500);
        colorAnim1.start();
    }

    /**
     * 过渡到网络安全背景
     */
    private void transitionBackgroundNet() {
        if(isFinishing()){
            return;
        }
        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(rootView, "backgroundColor", vBackGround, nBackGround);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(500);
        colorAnim1.start();
    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    public void setTransferPagePerformer(ITransferPagePerformer transferPagePerformer) {
        this.transferPagePerformer = transferPagePerformer;
    }

    private boolean isFinishing(){
        return tvScanTitle==null;
    }

    public void finish() {
        timer.cancel();
        getActivity().finish();
        StatisticsUtils.trackClick("return_click",Points.Virus.SCAN_SYSTEM_RETURN_EVENT_NAME,"", Points.Virus.SCAN_PAGE);
    }
}
