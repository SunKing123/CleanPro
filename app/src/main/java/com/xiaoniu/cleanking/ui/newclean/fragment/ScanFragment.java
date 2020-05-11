package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewScanPresenter;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 扫描
 *
 * use {@link }
 */
@Deprecated
public class ScanFragment extends BaseFragment<NewScanPresenter> {

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.view_lottie_ripple)
    LottieAnimationView lottieRipple;

    @BindView(R.id.layout_count)
    RelativeLayout mLayoutCount;
    @BindView(R.id.text_count)
    AppCompatTextView mTextCount;
    @BindView(R.id.text_unit)
    TextView mTextUnit;
    @BindView(R.id.layout_scan)
    LinearLayout mLayoutScan;
    @BindView(R.id.text_scan_trace)
    TextView mTextScanTrace;
    @BindView(R.id.view_arrow)
    ImageView mArrowRight;
    @BindView(R.id.layout_clean_top)
    FrameLayout mLayoutCleanTop;
    @BindView(R.id.iv_scan_bg03)
    ImageView ivScanBg03;
    @BindView(R.id.iv_scan_bg02)
    ImageView ivScanBg02;
    @BindView(R.id.iv_scan_bg01)
    ImageView ivScanBg01;
    @BindView(R.id.btn_left_scan)
    ImageView btnLeftScan;
    @BindView(R.id.tv_scan_left_title)
    TextView tvScanLeftTitle;
    ImageView[] ivs;

    private CountEntity mCountEntity = new CountEntity();

    /**
     * 当前的首页的状态
     */
    private int type = 0;

    /**
     * 扫描完成
     */
    private static final int TYPE_SCAN_FINISH = 1;
    /**
     * 清理完成
     */
    public static final int TYPE_CLEAN_FINISH = 2;
    /**
     * 扫描中
     */
    public static final int TYPE_SCANING = 3;
    /**
     * 扫描时颜色变化是否完成
     */
    private boolean mChangeFinish;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mLottieHomeView != null)
                    mLottieHomeView.playAnimation();
            }
            if (msg.what == 2) {
                try {
                    ((NowCleanActivity) getActivity()).setCountEntity(new CountEntity());
                    ((NowCleanActivity) getActivity()).setJunkGroups(new HashMap());
                    mPresenter.startScan(ivs);
                    mPresenter.startCleanScanAnimation01(lottieRipple);
                    type = TYPE_SCANING;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initView() {
        tvScanLeftTitle.setText(getString(R.string.scaning));
        ivs = new ImageView[]{ivScanBg01, ivScanBg02, ivScanBg03};

        lottieRipple.setVisibility(View.VISIBLE);
        lottieRipple.useHardwareAcceleration();
        lottieRipple.setAnimation("leida.json");
        lottieRipple.setImageAssetsFolder("ripple");
        lottieRipple.setRepeatCount(5);
        try {
            mPresenter.checkPermission();
            mTextCount.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));
            mTextUnit.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_left_scan})
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left_scan:
                ((NowCleanActivity) getActivity()).backClick(true);
                break;
        }
    }

    public void startScan() {
        mPresenter.setIsFinish(false);
        mHandler.sendEmptyMessageDelayed(2, 50);
    }

    /**
     * 扫描完成
     *
     * @param junkGroups
     */
    public void scanFinish(HashMap<Integer, JunkGroup> junkGroups) {
        type = TYPE_SCAN_FINISH;
        if (mTextScanTrace == null)
            return;
        if (mCountEntity != null) {
            if (mCountEntity.getNumber() > 0) {
                mCountEntity = CleanUtil.formatShortFileSize(CleanUtil.getTotalSize(junkGroups));
                ((NowCleanActivity) getActivity()).setCountEntity(mCountEntity);
                ((NowCleanActivity) getActivity()).setJunkGroups(junkGroups);
                ((NowCleanActivity) getActivity()).scanFinish();
                mArrowRight.setVisibility(VISIBLE);
            } else {
                //没有扫描到垃圾
                cleanFinishSign();
            }
        }

        //重置扫描状态
        mPresenter.setIsFinish(false);
        //重置颜色变化状态
        mChangeFinish = false;

        mPresenter.stopCleanScanAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 统计总数
     */
    public void showCountNumber(long total) {
        if (getActivity() == null || total <= 0) {
            return;
        }
        try {
            mCountEntity = CleanUtil.formatShortFileSize(total);
            getActivity().runOnUiThread(() -> {
                if (mTextCount == null || mTextUnit == null)
                    return;
                if (mCountEntity != null) {
                    mTextCount.setText(mCountEntity.getTotalSize());
                    mTextUnit.setText(mCountEntity.getUnit());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理很干净标识
     */
    public void cleanFinishSign() {
        mLayoutCount.setVisibility(GONE);
        mLayoutScan.setVisibility(GONE);
        //清理完成标识
        type = TYPE_CLEAN_FINISH;
        setColorChange(false);
    }

    public void showScanFile(String p0) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (mTextScanTrace != null) {
                mTextScanTrace.setText("扫描:  " + p0);
            }
        });

    }

    /**
     * 终止扫描
     */
    public void stopScan() {
        mPresenter.setIsFinish(true);
        lottieRipple.pauseAnimation();
        showHomeLottieView(true);
    }

    /**
     * 扫描动画结束
     */
    public void endScanAnimation() {
        if (lottieRipple != null) {
            lottieRipple.pauseAnimation();
        }
        showHomeLottieView(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        int color = R.color.color_4690FD;
        if (type == TYPE_SCAN_FINISH || mChangeFinish) {
            //扫描完成
            color = R.color.color_FD6F46;
        }

        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(requireActivity(), ContextCompat.getColor(requireContext(), color), true);
            } else {
                StatusBarCompat.setStatusBarColor(requireActivity(), getResources().getColor(color), false);
            }
        }
    }

    /**
     * 是否颜色变化完成
     *
     * @param b
     */
    public void setColorChange(boolean b) {
        mChangeFinish = b;
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("clean_up_scan_page_view_page", "用户在清理扫描页浏览");
        if (isGotoSetting) {
            mPresenter.checkPermission();
            isGotoSetting = false;
        }
    }

    boolean isGotoSetting = false;

    public void setIsGotoSetting(boolean isGotoSetting) {
        this.isGotoSetting = isGotoSetting;
    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getSourcePageId(), "clean_up_scan_page", "clean_up_scan_page_view_page", "用户在清理扫描页浏览");
        if (mLottieHomeView != null)
            mLottieHomeView.cancelAnimation();
    }

    /**
     * 静止时动画
     *
     * @param isMove true转动 false 停止
     */
    private void showHomeLottieView(boolean isMove) {
        if (mLottieHomeView == null)
            return;
        mLottieHomeView.useHardwareAcceleration();
        mLottieHomeView.setAnimation("data_home.json");
        mLottieHomeView.setImageAssetsFolder("images");
        if (isMove) {
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mLottieHomeView.cancelAnimation();
            mLottieHomeView.setVisibility(GONE);
        }
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    public void goSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            setIsGotoSetting(true);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null)
            mPresenter.stopCleanScanAnimation();
        super.onDestroy();
    }
}
