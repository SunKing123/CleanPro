package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hellogeek.permission.strategy.ExternalInterface;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.bean.JunkResultWrapper;
import com.xiaoniu.cleanking.mvp.BaseFragment;
import com.xiaoniu.cleanking.mvp.InjectPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.ui.newclean.activity.DeepCleanPermissionActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.adapter.ScanResultAdapter;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanResultContact;
import com.xiaoniu.cleanking.ui.newclean.presenter.ScanResultPresenter;
import com.xiaoniu.cleanking.utils.LayoutAnimationHelper;
import com.xiaoniu.cleanking.utils.OnItemClickListener;
import com.xiaoniu.cleanking.widget.CustomLinearLayoutManger;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 扫描结果界面
 * <p>
 * create by wanggang on 2020/05/13
 */
public class ScanResultFragment extends BaseFragment implements ScanResultContact.View, OnItemClickListener<JunkResultWrapper> {

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;
    @BindView(R.id.fl_ad_container)
    FrameLayout adContainer;
    @BindView(R.id.ll_deep_clean)
    LinearLayout ll_deep_clean;
    @BindView(R.id.tv_clean_junk)
    TextView tv_clean_junk;
    @BindView(R.id.tv_junk_total)
    TextView tv_junk_total;
    @BindView(R.id.tv_junk_unit)
    TextView tv_junk_unit;
    @BindView(R.id.tv_checked_total)
    TextView tv_checked_total;
    @BindView(R.id.tv_back)
    TextView tv_back;

    @InjectPresenter
    ScanResultPresenter presenter;

    private String checkedResultSize;
    private ScanResultAdapter mScanResultAdapter;

    public static ScanResultFragment createFragment() {
        return new ScanResultFragment();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_scan_result;
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());

        rv_content_list.setLayoutManager(new CustomLinearLayoutManger(requireActivity()));
        rv_content_list.setAdapter(mScanResultAdapter = new ScanResultAdapter(this));

        if (ExternalInterface.getInstance(requireActivity()).isOpenAllPermission(requireActivity())) {
            ll_deep_clean.setVisibility(View.GONE);
        } else {
            ll_deep_clean.setVisibility(View.VISIBLE);
        }

        ll_deep_clean.setOnClickListener(v -> {
            //判断如果没有授权的话，则进入授权界面
            StatisticsUtils.trackClick("deep_cleaning_click", "深度清理点击",
                    "clean_scan_page", "clean_scan_result_page");
            DeepCleanPermissionActivity.start(requireActivity());
        });

        //计算用户选中需要清理的垃圾文件，并且跳转清理界面
        tv_clean_junk.setOnClickListener(v -> presenter.jumpToCleanPage());

        tv_back.setOnClickListener(v -> ((NowCleanActivity) requireActivity()).backClick(true));
    }

    @Override
    protected void initData() {
        final LinkedHashMap<ScanningResultType, JunkGroup> groupLinkedHashMap = ScanDataHolder.getInstance().getmJunkGroups();
        //构造清理数据模型
        presenter.buildJunkResultModel(groupLinkedHashMap);
//        //广告
//        requestAd();
    }

//    /**
//     * 请求广告
//     */
//    private void requestAd() {
//        AdRequestParamentersBean adRequestParamentersBean = new AdRequestParamentersBean(PositionId.KEY_SCAN_RESULT,
//                PositionId.DRAW_ONE_CODE,
//                requireContext(),
//                AdType.Template,
//                (int) ScreenUtils.getScreenWidthDp(requireContext()),
//                0,
//                "clean_scan_page",
//                "home_page");
//        new AdPresenter().requestAd(adRequestParamentersBean, new AdShowCallBack() {
//            @Override
//            public void onAdShowCallBack(View view) {
//                Log.d("ad_status", " scan onAdShowCallBack" + ((view == null) ? "null" : "not null"));
//
//                if (adContainer != null) {
//                    adContainer.removeAllViews();
//                    adContainer.addView(view);
//                }
//            }
//
//            @Override
//            public void onCloseCallback(int index) {
//                if (adContainer != null) {
//                    adContainer.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(String message) {
//                if (adContainer != null) {
//                    adContainer.setVisibility(View.GONE);
//                }
//            }
//        });
//    }

    /***
     * 展示recyclerView进入动画(非Insert动画)
     */
    private void showInitDataAnimator() {
        LayoutAnimationController controller = new LayoutAnimationController(LayoutAnimationHelper.getAnimationSetFromRight());
        controller.setDelay(0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        rv_content_list.setLayoutAnimation(controller);
        mScanResultAdapter.notifyDataSetChanged();
        rv_content_list.scheduleLayoutAnimation();
    }

    @Override
    public void setInitSubmitResult(List<JunkResultWrapper> junkResultWrappers) {
        //首次填充数据
        mScanResultAdapter.submitList(junkResultWrappers);
        //只在首次进入的时候添加一个从右边进入的动画
        showInitDataAnimator();
    }

    @Override
    public void setSubmitResult(List<JunkResultWrapper> buildJunkDataModel) {
        //非首次填充，根据数据变更动态填充数据
        mScanResultAdapter.submitList(buildJunkDataModel);
    }

    @Override
    public void setJunkTotalResultSize(String totalSize, String unit, long number) {
        HashMap<String, Object> extParam = new HashMap<>();
        extParam.put("garbage_file_size", number);
        StatisticsUtils.customTrackEvent("garbage_file_size", "垃圾清理_扫描结果_垃圾体积",
                "clean_scan_page", "clean_scan_result_page", extParam);
        tv_junk_total.setText(totalSize);
        tv_junk_unit.setText(unit);

//        HashMap<String, Object> numParam = new HashMap<>();
//        numParam.put("number_of_documents", ((NowCleanActivity) requireActivity()).getScanningFileCount());
//        StatisticsUtils.customTrackEvent("number_of_documents", "垃圾清理_扫描结果_文件数量",
//                "clean_scan_page", "clean_scan_result_page", numParam);
    }

    @Override
    public void setCheckedJunkResult(String resultSize) {
        checkedResultSize = resultSize;
        tv_checked_total.setText(getString(R.string.scan_result_check_total, resultSize));
        tv_clean_junk.setText(getString(R.string.clean_btn, resultSize));
    }

    @Override
    public void setUnCheckedItemTip() {
        ToastUtils.showShort("请勾选需要清理的内容");
    }

    @Override
    public void setJumpToCleanPage(LinkedHashMap<ScanningResultType, JunkGroup> junkTitleMap,
                                   LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap) {
        ((NowCleanActivity) requireActivity()).setReadyCleanJunkList(junkTitleMap, junkContentMap);
    }

    @Override
    public void onItemClick(View view, JunkResultWrapper data, int position) {
        switch (view.getId()) {
            case R.id.rl_type_root:
                presenter.updateExpendState(data);
                break;
            case R.id.iv_check_junk_state:
                presenter.updateJunkTypeCheckSate(data);
                break;
            case R.id.iv_check_state:
                presenter.updateJunkContentCheckState(data);
                break;
            case R.id.iv_check_uncareful_state:
                presenter.updateChildJunkContentCheckState(data,1);
                break;
            case R.id.iv_check_careful_state:
                presenter.updateChildJunkContentCheckState(data,0);
                break;
        }
    }

}
