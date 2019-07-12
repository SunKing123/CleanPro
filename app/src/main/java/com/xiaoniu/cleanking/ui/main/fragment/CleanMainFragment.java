package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.activity.CleanFinishActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.ui.main.widget.MyLinearLayout;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanMainFragment extends BaseFragment<CleanMainPresenter> {

    @BindView(R.id.text_count)
    TextView mTextCount;
    @BindView(R.id.layout_root)
    MyLinearLayout mLayoutRoot;
    @BindView(R.id.layout_clean_top)
    FrameLayout mLayoutCleanTop;
    @BindView(R.id.btn_ljql)
    Button mButtonCleanNow;
    @BindView(R.id.icon_outer)
    ImageView mIconOuter;
    @BindView(R.id.icon_inner)
    ImageView mIconInner;
    @BindView(R.id.circle_outer)
    View mCircleOuter;
    @BindView(R.id.circle_outer2)
    View mCircleOuter2;
    @BindView(R.id.text_scan_trace)
    TextView mTextScanTrace;
    @BindView(R.id.text_content)
    TextView mTextContent;
    @BindView(R.id.layout_scan)
    LinearLayout mLayoutScan;
    @BindView(R.id.view_arrow)
    ImageView mArrowRight;
    @BindView(R.id.layout_scroll)
    ScrollView mScrollView;
    @BindView(R.id.view_lottie)
    LottieAnimationView mAnimationView;

    private boolean isScanFinish = false;
    public static HashMap<Integer, JunkGroup> mJunkGroups;
    private CountEntity mCountEntity;
    private List<ImageView> mTopViews;
    private Handler mHandler;

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clean_mainnew;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);


        new Handler().postDelayed(() -> {
            mPresenter.startScan();
            mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
        }, 500);
    }


    //    @OnClick(R.id.text_cooling)
//    public void onCoolingViewClicked() {
//        //手机降温
//        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
//    }
    @OnClick(R.id.text_wjgl)
    public void wjgl() {
        //文件管理
        startActivity(FileManagerHomeActivity.class);
    }

    @OnClick(R.id.text_acce)
    public void text_acce() {
        //一键加速
        startActivity(PhoneAccessActivity.class);
    }

    @OnClick(R.id.line_ql)
    public void line_ql() {
        //手机清理
        startActivity(RouteConstants.CLEAN_BIG_FILE_ACTIVITY);
    }

    @OnClick(R.id.btn_ljql)
    public void btnLjql() {
        if (isScanFinish) {
            mPresenter.showTransAnim(mLayoutCleanTop);
            mPresenter.startCleanAnimation(mIconInner, mIconOuter, mLayoutScan, mTextCount, mCountEntity);
            mButtonCleanNow.setVisibility(View.GONE);
            mTextScanTrace.setText("垃圾清理中...");
            mArrowRight.setVisibility(View.GONE);

            mLayoutRoot.setIntercept(true);
        } else {
            ToastUtils.show("正在扫描中");
        }
    }

    @OnClick(R.id.layout_scan)
    public void mClickLayoutScan() {
        //查看详情
        if (isScanFinish) {
            startActivity(RouteConstants.JUNK_CLEAN_ACTIVITY);
        } else {
            ToastUtils.show("正在扫描中");
        }
    }

    /**
     * 扫描完成
     *
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        isScanFinish = true;
        mJunkGroups = junkGroups;

        mTextScanTrace.setText("查看垃圾详情");
        mArrowRight.setVisibility(View.VISIBLE);

        mPresenter.stopCleanScanAnimation();
    }

    /**
     * 统计总数
     *
     * @param total
     */
    public void showCountNumber(long total) {
        if (getActivity() == null) {
            return;
        }
        mCountEntity = CleanUtil.formatShortFileSize(total);
        getActivity().runOnUiThread(() -> mTextCount.setText(CleanUtil.formatShortFileSize(getActivity(), total)));
    }


    @Subscribe
    public void cleanFinish(String string) {
        if ("clean_finish".equals(string)) {
            //清理完成
            restoreLayout();
        }
    }

    public FrameLayout getCleanTopLayout() {
        return mLayoutCleanTop;
    }

    /**
     * 恢复布局
     */
    private void restoreLayout() {
        mLayoutRoot.setIntercept(false);
        mIconInner.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
        layoutParams.height = DeviceUtils.dip2px(400);
        mLayoutCleanTop.setLayoutParams(layoutParams);

        if (getActivity() != null) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            marginParams.bottomMargin = DeviceUtils.dip2px(53);
            frameLayout.setLayoutParams(marginParams);
            getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
        }

        mIconOuter.setTranslationY(0);
        mIconInner.setTranslationY(0);
        mLayoutScan.setTranslationY(0);
        mTextCount.setTranslationY(0);

        mTextContent.setVisibility(View.VISIBLE);
        mTextCount.setVisibility(View.GONE);
        mLayoutScan.setVisibility(View.GONE);
        mButtonCleanNow.setText("完成");
        mButtonCleanNow.setVisibility(View.VISIBLE);
        mButtonCleanNow.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void showScanFile(String p0) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> mTextScanTrace.setText(p0));

    }

    public void endScanAnimation() {
        mCircleOuter2.setVisibility(View.GONE);
        mCircleOuter.setVisibility(View.GONE);
    }

    public void showCleanFinish(Long o) {
        Bundle bundle = new Bundle();
        bundle.putString("CLEAN_TYPE", CleanFinishActivity.TYPE_CLEAN_CACHE);
        bundle.putLong("clean_count", o);
        startActivity(RouteConstants.CLEAN_FINISH_ACTIVITY, bundle);
    }

    /**
     * 显示lottie动画
     */
    public void showLottieView() {
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data.json");
        mAnimationView.playAnimation();
    }
}
