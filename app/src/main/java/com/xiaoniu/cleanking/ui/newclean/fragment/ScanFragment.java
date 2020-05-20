package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.mvp.BaseFragment;
import com.xiaoniu.cleanking.mvp.InjectPresenter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.adapter.ScanningJunkAdapter;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanningContact;
import com.xiaoniu.cleanking.ui.newclean.presenter.ScanningPresenter;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.widget.ArgbEvaluator;
import com.xiaoniu.cleanking.widget.FuturaRoundTextView;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.xiaoniu.cleanking.app.Constant.WHITE_LIST;

/**
 * 扫描垃圾
 */
public class ScanFragment extends BaseFragment implements ScanningContact.View {

    @BindView(R.id.iv_scanning_background)
    ImageView iv_scanning_background;
    @BindView(R.id.lottie_animation_view)
    LottieAnimationView lottie_animation_view;
    @BindView(R.id.tv_junk_total)
    FuturaRoundTextView tv_junk_total;
    @BindView(R.id.tv_junk_unit)
    FuturaRoundTextView tv_junk_unit;
    @BindView(R.id.tv_scanning_progress_file)
    TextView tv_scanning_progress_file;
    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;
    @BindView(R.id.tv_stop_clean)
    TextView tv_stop_clean;
    @BindView(R.id.tv_back)
    TextView tv_back;

    @InjectPresenter
    ScanningPresenter scanningPresenter;

    private ScanningJunkAdapter scanningJunkAdapter;
    private boolean isGotoSetting = false;
    private RxPermissions rxPermissions;
    private AlertDialog dlg;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
        lottie_animation_view.useHardwareAcceleration();
        rxPermissions = new RxPermissions(requireActivity());

        scanningJunkAdapter = new ScanningJunkAdapter();
        rv_content_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_content_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(requireContext())
                .sizeResId(R.dimen.dimen_0_5dp)
                .colorResId(R.color.color_EDEDED)
                .marginResId(R.dimen.dimen_16dp, R.dimen.dimen_16dp)
                .build());
        rv_content_list.setAdapter(scanningJunkAdapter);

        tv_stop_clean.setOnClickListener(v -> {
            StatisticsUtils.trackClick("stop_it_button_click", "停止按钮点击", "home_page", "clean_scan_page");
            ((NowCleanActivity) requireActivity()).backClick(true);
        });
        tv_back.setOnClickListener(v -> {
            StatisticsUtils.trackClick("return_click", "返回点击", "home_page", "clean_scan_page");
            ((NowCleanActivity) requireActivity()).backClick(true);
        });
    }

    @Override
    protected void initData() {
        //开始扫描之前准备工作
        scanningPresenter.readyScanningJunk();
        //检查存贮权限是否开启
        checkStoragePermission();
    }

    @Override
    public void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void setScanningJunkTotal(String totalResult, String unit) {
        tv_junk_total.setText(totalResult);
        tv_junk_unit.setText(unit);
    }

    @Override
    public void setScanningFilePath(String filePath) {
        for (String packageName : WHITE_LIST) {
            if (filePath.indexOf(packageName) != -1) {   // 如果存在于白名单
                return;
            }
        }
        tv_scanning_progress_file.setText(getString(R.string.scanning_file, filePath));
    }

    @Override
    public void setScanningBackgroundColor(int oldColor, int newColor) {
        if (oldColor == newColor) {
            iv_scanning_background.setBackgroundColor(oldColor);
        } else {
            ValueAnimator anim = new ValueAnimator();
            anim.setIntValues(oldColor, newColor);
            anim.setEvaluator(ArgbEvaluator.getInstance());
            anim.setDuration(200);
            anim.addUpdateListener(animation ->
                    iv_scanning_background.setBackgroundColor((Integer) animation.getAnimatedValue()));
            anim.start();
        }
    }

    @Override
    public void setInitScanningModel(List<JunkGroup> scanningModelList) {
        scanningJunkAdapter.submitList(scanningModelList);
    }

    @Override
    public void setScanningFinish(LinkedHashMap<ScanningResultType, JunkGroup> junkGroups) {
        long totalJunkSize = 0;
        for (Map.Entry<ScanningResultType, JunkGroup> map : junkGroups.entrySet()) {
            if (!ScanningResultType.MEMORY_JUNK.equals(map.getKey())) {
                totalJunkSize += map.getValue().mSize;
            }
        }
        CountEntity mCountEntity = CleanUtil.formatShortFileSize(totalJunkSize);
        ((NowCleanActivity) getActivity()).setCountEntity(mCountEntity);
        ((NowCleanActivity) getActivity()).setJunkGroups(junkGroups);
        ((NowCleanActivity) getActivity()).scanFinish();

        //重置颜色变化状态
        lottie_animation_view.pauseAnimation();
    }

    @Override
    public void setScanningCountTime(long scanningCountTime) {
        HashMap<String, Object> extParams = new HashMap<>();
        extParams.put("scanning_time", scanningCountTime);
        StatisticsUtils.customTrackEvent("scanning_time", "垃圾清理_扫描结果_扫描时长",
                "clean_scan_page", "clean_scan_result_page", extParams);
    }

    @Override
    public void setScanningFileCount(int fileCount) {
        ((NowCleanActivity) requireActivity()).setScanningFileCount(fileCount);
    }

    /**
     * 检查文件存贮权限
     */
    private void checkStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Disposable disposable = rxPermissions.request(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        scanningPresenter.scanningJunk();
                    } else {
                        if (hasPermissionDeniedForever()) {
                            showPermissionDialog();
                        } else {
                            checkStoragePermission();
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        int color = R.color.color_4690FD;

        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(requireActivity(), ContextCompat.getColor(requireContext(), color), true);
            } else {
                StatusBarCompat.setStatusBarColor(requireActivity(), getResources().getColor(color), false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("clean_up_scan_page_view_page", "用户在清理扫描页浏览");
        if (isGotoSetting) {
            checkStoragePermission();
            isGotoSetting = false;
        }
    }

    public void setIsGotoSetting(boolean isGotoSetting) {
        this.isGotoSetting = isGotoSetting;
    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getSourcePageId(), "clean_up_scan_page", "clean_up_scan_page_view_page", "用户在清理扫描页浏览");
        lottie_animation_view.cancelAnimation();
    }

    /**
     * 是否有权限被永久拒绝
     */
    private boolean hasPermissionDeniedForever() {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    private void showPermissionDialog() {
        dlg = new AlertDialog.Builder(requireContext()).create();
        if (requireActivity().isFinishing()) {
            return;
        }
        dlg.show();
        Window window = dlg.getWindow();
        if (window != null) {
            window.setContentView(R.layout.alite_redp_send_dialog);
            WindowManager.LayoutParams lp = window.getAttributes();
            //这里设置居中
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView btnOk = window.findViewById(R.id.btnOk);

            TextView btnCancle = window.findViewById(R.id.btnCancle);
            TextView tipTxt = window.findViewById(R.id.tipTxt);
            TextView content = window.findViewById(R.id.content);
            btnCancle.setText("退出");
            btnOk.setText("去设置");
            tipTxt.setText("提示!");
            content.setText("清理功能无法使用，请先开启文件读写权限。");
            btnOk.setOnClickListener(v -> {
                dlg.dismiss();
                goSetting();
            });
            btnCancle.setOnClickListener(v -> {
                dlg.dismiss();
                requireActivity().finish();
            });
        }
    }


    public void goSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            setIsGotoSetting(true);
            requireActivity().startActivity(intent);
        }
    }
}
