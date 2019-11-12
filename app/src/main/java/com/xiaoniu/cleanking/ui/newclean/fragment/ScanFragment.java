package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

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

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 扫描
 */
public class ScanFragment extends BaseFragment<NewScanPresenter> {

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @BindView(R.id.view_lottie_home)
    LottieAnimationView mLottieHomeView;
    @BindView(R.id.view_lottie_ripple)
    LottieAnimationView lottieRipple;
 /*   @BindView(R.id.icon_outer)
    ImageView mIconOuter;*/
   /* @BindView(R.id.circle_outer)
    View mCircleOuter;*/
/*    @BindView(R.id.circle_outer2)
    View mCircleOuter2;*/

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


    /*    */
    /**
     * 清理的分类列表
     *//*
    public static HashMap<Integer, JunkGroup> mJunkGroups;*/
    private CountEntity mCountEntity = new CountEntity();

    /**
     * 首页是否显示
     */
    private boolean isShow = true;

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

    private MyHandler mHandler = new MyHandler(getActivity());

    class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity con) {
            this.mActivity = new WeakReference<>(con);
        }

        public void handleMessage(android.os.Message msg) {
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
    }


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
     *
     * @param total
     */
    public void showCountNumber(long total) {
        if (getActivity() == null || total <= 0) {
            return;
        }
        try {
            mCountEntity = CleanUtil.formatShortFileSize(total);
            getActivity().runOnUiThread(() -> {
                CountEntity countEntity = CleanUtil.formatShortFileSize(total);
                if (mTextCount == null || mTextUnit == null)
                    return;
                if (countEntity != null) {
                    mTextCount.setText(countEntity.getTotalSize());
                    mTextUnit.setText(countEntity.getUnit());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FrameLayout getCleanTopLayout() {
        return null;
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

//        mPresenter.showOuterViewRotation(mIconOuter);
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
    /*    if (mPresenter.getCleanScanAnimator() != null)
            mPresenter.getCleanScanAnimator().cancel();*/

    /*    mCircleOuter2.setVisibility(GONE);
        mCircleOuter.setVisibility(GONE);*/
        lottieRipple.pauseAnimation();
        showHomeLottieView(true);
    }

    /**
     * 扫描动画结束
     */
    public void endScanAnimation() {
        if (lottieRipple != null) {
            lottieRipple.pauseAnimation();
//            lottieRipple.setVisibility(GONE);
        }
        showHomeLottieView(true);
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (getActivity() == null)
            return;
        ((NowCleanActivity) getActivity()).getToolBar().setBackgroundColor(animatedValue);
        mLayoutCleanTop.setBackgroundColor(animatedValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(getActivity(), animatedValue, false);
        }
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
            isShow = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), false);
            }
        } else {
            isShow = false;
        }

    }

    /**
     * 获取当前Fragment是否显示
     *
     * @return
     */
    public boolean getViewShow() {
        return isShow;
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
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
     /*   if (rotate == null) {
            mIconOuter.setAnimation(rotate);
        }*/
        if (mLottieHomeView == null)
            return;
        mLottieHomeView.useHardwareAcceleration();
        mLottieHomeView.setAnimation("data_home.json");
        mLottieHomeView.setImageAssetsFolder("images");
        if (isMove) {
//            mIconOuter.startAnimation(rotate);
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        } else {
            mHandler.removeCallbacksAndMessages(null);
//            mIconOuter.clearAnimation();
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
