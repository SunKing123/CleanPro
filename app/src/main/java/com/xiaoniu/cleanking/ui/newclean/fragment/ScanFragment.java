package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.NewScanPresenter;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;

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
    @BindView(R.id.view_lottie)
    LottieAnimationView mAnimationView;

    @BindView(R.id.view_lottie_star)
    LottieAnimationView mLottieStarView;
    @BindView(R.id.icon_outer)
    ImageView mIconOuter;
    @BindView(R.id.circle_outer)
    View mCircleOuter;
    @BindView(R.id.circle_outer2)
    View mCircleOuter2;
    @BindView(R.id.icon_inner)
    ImageView mIconInner;
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

    /**
     * 清理的分类列表
     */
    public static HashMap<Integer, JunkGroup> mJunkGroups;
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
     * 未扫描
     */
    private static final int TYPE_NOT_SCAN = 0;
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

    /**
     * 之前清理完成时间
     */
    private long preCleanTime;
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
        mPresenter.checkPermission();
    }

    public void startScan() {
        new Handler().postDelayed(() -> {
            mPresenter.startScan();
            mPresenter.startCleanScanAnimation(mIconOuter, mCircleOuter, mCircleOuter2);
            type = TYPE_SCANING;
            //TODO 正在扫描

        }, 500);
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
                mJunkGroups = junkGroups;
                ((NowCleanActivity)getActivity()).setJunkGroups(mJunkGroups);
                mTextScanTrace.setVisibility(GONE);
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
        if (getActivity() == null) {
            return;
        }
        mCountEntity = CleanUtil.formatShortFileSize(total);
        ((NowCleanActivity)getActivity()).setCountEntity(mCountEntity);
        getActivity().runOnUiThread(() -> {
            CountEntity countEntity = CleanUtil.formatShortFileSize(total);
            if (mTextCount == null)
                return;
            mTextCount.setText(countEntity.getTotalSize());
            mTextUnit.setText(countEntity.getUnit());
        });
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
        //播放lottie动画
        mLottieStarView.setVisibility(VISIBLE);
        playStarAnimation();
        mPresenter.showOuterViewRotation(mIconOuter);
    }

    private void playStarAnimation() {
        mLottieStarView.setImageAssetsFolder("images");
        mLottieStarView.setAnimation("data_star.json");
        mLottieStarView.playAnimation();
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

    public void endScanAnimation() {
        mCircleOuter2.setVisibility(GONE);
        mCircleOuter.setVisibility(GONE);
        showHomeLottieView(true);
    }

    /**
     * 显示lottie动画
     */
    public void showLottieView() {
        mAnimationView.useHardwareAcceleration();
        mAnimationView.setImageAssetsFolder("images");
        mAnimationView.setAnimation("data.json");
        mAnimationView.playAnimation();
    }

    public LottieAnimationView getLottieView() {
        return mAnimationView;
    }

    /**
     * 清理完成后的页面
     *
     * @return
     */
    public View getCleanFinish() {
        return null;
    }

    public RelativeLayout getCleanTextLayout() {
        return mLayoutCount;
    }

    public LinearLayout getScanLayout() {
        return mLayoutScan;
    }

    private long firstTime;

    public void onKeyBack() {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - firstTime > 1500) {
                Toast.makeText(getActivity(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = currentTimeMillis;
            } else {
                SPUtil.setInt(getContext(), "turnask", 0);
                AppManager.getAppManager().AppExit(getContext(), false);
            }

    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        if (((NowCleanActivity)getActivity()).getToolBar() == null)
            return;
        ((NowCleanActivity)getActivity()).getToolBar().setBackgroundColor(animatedValue);
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
            NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
            isShow = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(color), false);
            }
        } else {
            isShow = false;
            NiuDataAPI.onPageEnd("home_page_view_page", "首页浏览");
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
        if (b){
            ((NowCleanActivity)getActivity()).scanFinish();
        }

    }

    /**
     * 获取总量显示的view
     *
     * @return
     */
    public AppCompatTextView getTextCountView() {
        return mTextCount;
    }

    /**
     * 获取总量显示的view
     *
     * @return
     */
    public TextView getTextUnitView() {
        return mTextUnit;
    }

    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        NiuDataAPI.onPageStart("check_garbage_view_page", "清理垃圾浏览");
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
        NiuDataAPI.onPageStart("home_page_view_page", "首页浏览");
        NiuDataAPI.onPageEnd("check_garbage_view_page", "清理垃圾浏览");

        if (mLottieHomeView != null)
            mLottieHomeView.cancelAnimation();
    }

    /**
     * 获取结束的lottieView
     *
     * @return
     */
    public LottieAnimationView getFinishAnimator() {
        return null;
    }

    public FrameLayout getmFlAnim() {
        return null;
    }


    /**
     * 静止时动画
     *
     * @param isMove true转动 false 停止
     */
    private void showHomeLottieView(boolean isMove) {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
        if (rotate == null) {
            mIconOuter.setAnimation(rotate);
        }
        mLottieHomeView.useHardwareAcceleration();
        mLottieHomeView.setAnimation("data_home.json");
        mLottieHomeView.setImageAssetsFolder("images");
        if (isMove) {
            mIconOuter.startAnimation(rotate);
            mLottieHomeView.playAnimation();
            mLottieHomeView.setVisibility(VISIBLE);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mIconOuter.clearAnimation();
            mLottieHomeView.cancelAnimation();
            mLottieHomeView.setVisibility(GONE);
        }
        mLottieHomeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(1,1500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}