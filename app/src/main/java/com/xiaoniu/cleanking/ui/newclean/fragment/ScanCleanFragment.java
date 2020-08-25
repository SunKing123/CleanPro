package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.mvp.BaseFragment;
import com.xiaoniu.cleanking.mvp.InjectPresenter;
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity;
import com.xiaoniu.cleanking.ui.localpush.LocalPushUtils;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanCleanContact;
import com.xiaoniu.cleanking.ui.newclean.presenter.ScanCleanPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.SimpleAnimatorListener;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.FuturaRoundTextView;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 扫描内容清理
 */
public class ScanCleanFragment extends BaseFragment implements ScanCleanContact.View {

    @BindView(R.id.tv_back)
    TextView tv_back;
    @BindView(R.id.iv_clean_bg01)
    TextView iv_clean_bg01;
    @BindView(R.id.iv_clean_bg02)
    TextView iv_clean_bg02;
    @BindView(R.id.iv_clean_bg03)
    TextView iv_clean_bg03;
    @BindView(R.id.view_lottie_bottom)
    LottieAnimationView view_lottie_bottom;
    @BindView(R.id.view_lottie_top)
    LottieAnimationView view_lottie_top;
    @BindView(R.id.tv_clean_count)
    FuturaRoundTextView tv_clean_count;
    @BindView(R.id.tv_clean_unit)
    FuturaRoundTextView tv_clean_unit;

    @InjectPresenter
    ScanCleanPresenter presenter;

    private int shouIndex = 2;
    private TextView[] ivs;
    private String cleanTotalSize;
    private String cleanTotalUnit;
    private int mNotifySize; //通知条数
    private int mPowerSize; //耗电应用数
    private int mRamScale; //使用内存占总RAM的比例
    private long startCleanTime;

    public static ScanCleanFragment createFragment() {
        return new ScanCleanFragment();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_scan_clean;
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());

        view_lottie_bottom.clearAnimation();
        view_lottie_bottom.clearAnimation();
//        view_lottie_bottom.useHardwareAcceleration();
//        view_lottie_top.useHardwareAcceleration();
        ivs = new TextView[]{iv_clean_bg01, iv_clean_bg02, iv_clean_bg03};
        view_lottie_bottom.setAnimation("cleanbottom.json");
        view_lottie_bottom.setImageAssetsFolder("cleanbottom");
        view_lottie_top.setAnimation("cleantop.json");
        view_lottie_top.setImageAssetsFolder("cleantop");

        view_lottie_top.addAnimatorListener(new SimpleAnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                //清理完成动画开始
                NiuDataAPI.onPageStart("clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv_clean_count.setVisibility(View.GONE);
                tv_clean_unit.setVisibility(View.GONE);
                NiuDataAPIUtil.onPageEnd("scanning_result_page", "clean_finish_annimation_page",
                        "clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");

                //清理完成，跳转清理完成界面
                cleanComplete();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (view_lottie_bottom != null) {
            view_lottie_bottom.clearAnimation();
        }
        if (view_lottie_top != null) {
            view_lottie_top.clearAnimation();
        }
        super.onDestroyView();
    }

    /**
     * 清理完成之后保存状态，并且跳转清理完成界面
     */
    private void cleanComplete() {
        AppHolder.getInstance().setCleanFinishSourcePageId("clean_finish_annimation_page");
        if (PreferenceUtil.getNowCleanTime()) {
            PreferenceUtil.saveNowCleanTime();
        }
        LocalPushUtils.getInstance().updateLastUsedFunctionTime(SpCacheConfig.KEY_FUNCTION_CLEAR_RUBBISH);
        //状态栏恢复正常
        NotificationEvent event = new NotificationEvent();
        event.setType("clean");
        event.setFlag(0);
        EventBus.getDefault().post(event);

        PreferenceUtil.saveCleanAllUsed(true);
        boolean isOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_CLEAN_FINSH, PositionId.DRAW_THREE_CODE);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        EventBus.getDefault().post(new FunctionCompleteEvent(getString(R.string.tool_suggest_clean)));
        if (getActivity() != null && this.isAdded()) {
            PreferenceUtil.saveCleanStorageNum(cleanTotalSize,cleanTotalUnit);
            NewCleanFinishPlusActivity.Companion.start(getActivity(),getResources().getString(R.string.tool_suggest_clean),true);
            getActivity().finish();
        }
    }

    @Override
    protected void initData() {
        mNotifySize = NotifyCleanManager.getInstance().getAllNotifications().size();
        mPowerSize = new FileQueryUtils().getRunningProcess().size();
        presenter.startClean(((NowCleanActivity) requireActivity()).getJunkContentMap());
        startCleanTime = System.currentTimeMillis();
    }

    @Override
    public void setTotalJunkCount(String totalSize, String unit) {
        cleanTotalSize = totalSize;
        cleanTotalUnit = unit;
        tv_clean_count.setText(totalSize);
        tv_clean_unit.setText(unit);
    }

    @Override
    public void setStartCleanJunk(float junkTotal, String unit) {
        view_lottie_top.addAnimatorUpdateListener(animation -> {
            float animatedValue = animation.getAnimatedFraction();
            float currentValue = junkTotal * (1F - animatedValue * 1.5F);
            BigDecimal bigDecimal = new BigDecimal(currentValue);
            tv_clean_count.setText(String.format("%s", bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR).floatValue()));
            tv_clean_unit.setText(unit);
            if (animatedValue * 1.5F > 0.99F) {
                tv_clean_unit.setVisibility(View.GONE);
                tv_clean_count.setVisibility(View.GONE);
            }
        });

        if (!view_lottie_bottom.isAnimating()) {
            view_lottie_bottom.playAnimation();
        }
        if (!view_lottie_top.isAnimating()) {
            view_lottie_top.playAnimation();
        }

        shouIndex = 2;
        showColorChange(ivs, shouIndex);
    }

    public void showColorChange(TextView[] ivs, int index) {
        if (ivs.length == 3 && index <= 2 && index > 0) {
            Drawable drawable = ivs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ObjectAnimator animatorHide = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                animatorHide.setTarget(drawable);
                animatorHide.setDuration(1000);
                if (!animatorHide.isRunning()) {
                    animatorHide.start();
                }
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showColorChange(ivs, (index - 1));
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
    }

    @Override
    public void getAccessListBelow(ArrayList<FirstJunkInfo> listInfo) {
        if (listInfo == null || listInfo.size() <= 0) return;
        mRamScale = new FileQueryUtils().computeTotalSize(listInfo);
    }

    @Override
    public void setCleanFinish() {
        cleanComplete();
    }

    @Override
    public void setCleanJunkOver() {
        long cleanCountTime = System.currentTimeMillis() - startCleanTime;
        Map<String, Object> extParam = new HashMap<>();
        extParam.put("cleaning_time", cleanCountTime);
        StatisticsUtils.customTrackEvent("cleaning_time", "垃圾清理_清理时长",
                "clean_scan_result_page", "clean_animation_page", extParam);
    }
}
