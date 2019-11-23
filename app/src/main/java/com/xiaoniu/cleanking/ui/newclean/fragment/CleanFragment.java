package com.xiaoniu.cleanking.ui.newclean.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.callback.OnCleanListSelectListener;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.ui.main.adapter.DockingExpandableListViewAdapter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.LockScreenBtnInfo;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.newclean.presenter.CleanPresenter;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.SystemUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 清理操作页面
 */
public class CleanFragment extends BaseFragment<CleanPresenter> {

    @BindView(R.id.junk_list)
    ExpandableListView mExpandableListView;
    /*    @BindView(R.id.view_clean_anim)
        NewCleanAnimView mCleanAnimView;*/
    @BindView(R.id.do_junk_clean)
    TextView doJunkClean;

    @BindView(R.id.iv_clean_bg03)
    TextView ivCleanBg03;
    @BindView(R.id.iv_clean_bg02)
    TextView ivCleanBg02;
    @BindView(R.id.iv_clean_bg01)
    TextView ivCleanBg01;
    @BindView(R.id.rel_clean_content)
    RelativeLayout relCleanContent;
    @BindView(R.id.view_lottie_bottom)
    LottieAnimationView viewLottieBottom;
    @BindView(R.id.view_lottie_top)
    LottieAnimationView viewLottieTop;
    @BindView(R.id.btn_left_scan)
    ImageView btnLeftScan;
    @BindView(R.id.tv_clean_count)
    AppCompatTextView tvCleanCount;
    @BindView(R.id.tv_clean_unit)
    TextView tvCleanUnit;
    @BindView(R.id.layout_show_list)
    RelativeLayout layoutShowList;
    @BindView(R.id.clean_toolbar)
    Toolbar cleanToolbar;
    View mHeadView;
    TextView[] ivs;
    TextView tvCheckedSize;
    private DockingExpandableListViewAdapter mAdapter;
    private CountEntity totalCountEntity;
    private CountEntity checkCountEntity;
    private long checkedSize = 0;
    private long totalSize = 0;
    private HashMap<Integer, JunkGroup> mJunkGroups;

    int shouIndex = 2;

    @Inject
    NoClearSPHelper mSPHelper;

    public static CleanFragment newInstance() {
        return new CleanFragment();
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
        return R.layout.fragment_clean;
    }

    @Override
    protected void initView() {
        mHeadView = getLayoutInflater().inflate(R.layout.layout_head_now_clean, null);
        TextView tvSize = mHeadView.findViewById(R.id.tv_size);
        TextView tvUnit = mHeadView.findViewById(R.id.tv_clear_finish_gb_title);

        tvSize.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        tvUnit.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/FuturaRound-Medium.ttf"));

        tvCheckedSize = mHeadView.findViewById(R.id.tv_checked_size);
        mJunkGroups = ((NowCleanActivity) getActivity()).getJunkGroups();

        totalSize = checkedSize = CleanUtil.getTotalSize(mJunkGroups);
        checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(totalSize);

        if (totalCountEntity != null) {
            tvSize.setText(totalCountEntity.getTotalSize());
            tvUnit.setText(totalCountEntity.getUnit());
            tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
            doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
        }

        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setChildIndicator(null);
        mExpandableListView.setDividerHeight(0);
        mExpandableListView.addHeaderView(mHeadView);

        mExpandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            JunkGroup junkGroup = mJunkGroups.get(groupPosition);
            if (junkGroup != null) {
                junkGroup.isExpand = !junkGroup.isExpand();
                mAdapter.notifyDataSetChanged();
            }
            return false;
        });

        mAdapter = new DockingExpandableListViewAdapter(getActivity(), mExpandableListView);
        mAdapter.setOnCleanListSelectListener(new OnCleanListSelectListener() {
            @Override
            public void onGroupSelected(int groupPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.isChecked = isChecked;
                mJunkGroups.put(groupPosition, junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (checkCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                }
            }

            @Override
            public void onFistChilSelected(int groupPosition, int childPosition, boolean isChecked) {
                JunkGroup junkGroup = mJunkGroups.get(groupPosition);
                junkGroup.mChildren.get(childPosition).setAllchecked(isChecked);
                mJunkGroups.put(groupPosition, junkGroup);
                checkedSize = CleanUtil.getTotalSize(mJunkGroups);
                checkCountEntity = totalCountEntity = CleanUtil.formatShortFileSize(checkedSize);
                if (totalCountEntity != null) {
                    tvCheckedSize.setText(mContext.getString(R.string.select_already) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());
                    doJunkClean.setText(mContext.getString(R.string.text_clean) + checkCountEntity.getTotalSize() + checkCountEntity.getUnit());

                }
            }
        });
    /*    mAdapter.setOnItemSe lectListener(() -> {
            long totalSize = CleanUtil.getTotalSize(mJunkGroups);
            totalCountEntity = CleanUtil.formatShortFileSize(totalSize);
        });*/

        mExpandableListView.setAdapter(mAdapter);

        mAdapter.setData(mJunkGroups);
        //solve umeng error -> 'int java.util.HashMap.size()' on a null object reference
        if (mJunkGroups != null) {
            for (int i = 0; i < mJunkGroups.size(); i++) {
                mExpandableListView.expandGroup(i);
            }
        }
        /*
        mCleanAnimView.setStateListener(new AnimationStateListener() {
            @Override
            public void onAnimationStart() {
                //清理完成动画开始
                NiuDataAPI.onPageStart("clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
            }

            @Override
            public void onAnimationEnd() {
                NiuDataAPIUtil.onPageEnd("scanning_result_page", "clean_finish_annimation_page", "clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
                cleanFinish();
            }
        });

        mCleanAnimView.setOnColorChangeListener(animation -> showBarColor(animation));

        mCleanAnimView.setCleanOverListener(() -> {
            if (getActivity() != null) {
                ((NowCleanActivity) getActivity()).setClean(false);
            }
        });*/
    }


    @OnClick({R.id.layout_junk_clean, R.id.btn_left_scan})
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.layout_junk_clean:
                starClean();
                break;
            case R.id.btn_left_scan:
                ((NowCleanActivity) getActivity()).backClick(true);
                break;
        }

    }


    public void starClean() {
        //扫描中弹框_确认按钮
        StatisticsUtils.trackClick("cleaning_button_click", "用户在扫描结果页点击【清理】按钮", "clean_up_scan_page", "scanning_result_page");
        startClean();
    }

    /**
     * 清理完成
     */
    public void cleanFinish() {
        AppHolder.getInstance().setCleanFinishSourcePageId("clean_finish_annimation_page");
        if (PreferenceUtil.getNowCleanTime() || TextUtils.isEmpty(Constant.APP_IS_LIVE)) {
            PreferenceUtil.saveNowCleanTime();
        }
        //设置锁屏数据
        LockScreenBtnInfo btnInfo = new LockScreenBtnInfo(0);
        btnInfo.setNormal(true);
        btnInfo.setCheckResult("500");
        PreferenceUtil.getInstants().save("lock_pos01",new Gson().toJson(btnInfo));

        //状态栏恢复正常
        NotificationEvent event = new NotificationEvent();
        event.setType("clean");
        event.setFlag(0);
        EventBus.getDefault().post(event);

        PreferenceUtil.saveCleanAllUsed(true);
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());
        if (getActivity() != null && this.isAdded()) {
            startActivity(new Intent(getActivity(), ScreenFinishBeforActivity.class)
                    .putExtra(ExtraConstant.TITLE, getString(R.string.tool_suggest_clean))
                    .putExtra(ExtraConstant.NUM, checkCountEntity.getTotalSize())
                    .putExtra(ExtraConstant.UNIT, checkCountEntity.getUnit()));
            getActivity().finish();
        }
    }

    private void startClean() {
/*        mCleanAnimView.setStopClean(false);
        mCleanAnimView.setVisibility(View.VISIBLE);
        mCleanAnimView.setData(checkCountEntity);
        //清理动画
        mCleanAnimView.startCleanAnim(false);*/
//        layoutShowList.setVisibility(View.GONE);
        cleanToolbar.setVisibility(View.GONE);
        relCleanContent.setVisibility(View.VISIBLE);
        ivCleanBg01.setVisibility(View.VISIBLE);
        ivCleanBg02.setVisibility(View.VISIBLE);
        ivCleanBg03.setVisibility(View.VISIBLE);
        ivs = new TextView[]{ivCleanBg01, ivCleanBg02, ivCleanBg03};
//        viewLottieBottom.useHardwareAcceleration();
        viewLottieBottom.setAnimation("cleanbottom.json");
        viewLottieBottom.setImageAssetsFolder("cleanbottom");

//        viewLottieTop.useHardwareAcceleration();
        viewLottieTop.setAnimation("cleantop.json");
        viewLottieTop.setImageAssetsFolder("cleantop");

        tvCleanCount.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));
        tvCleanUnit.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/FuturaRound-Medium.ttf"));


        if (!viewLottieBottom.isAnimating()) {
            viewLottieBottom.playAnimation();
        }
        if (!viewLottieTop.isAnimating()) {
            viewLottieTop.playAnimation();
        }
        tvCleanCount.setText(checkCountEntity.getTotalSize());
        tvCleanUnit.setText(checkCountEntity.getUnit());

        viewLottieBottom.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //清理完成动画开始
                NiuDataAPI.onPageStart("clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                NiuDataAPIUtil.onPageEnd("scanning_result_page", "clean_finish_annimation_page", "clean_finish_annimation_page_view_page", "清理完成动画展示页浏览");
                if (getActivity() != null) { //作为返回键判断条件
                    ((NowCleanActivity) getActivity()).setClean(false);
                }
                cleanFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }


        });
        viewLottieBottom.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedFraction();
                if (animatedValue <= 0.74f) {
                    float currentValue = Float.valueOf(checkCountEntity.getTotalSize()) * (0.74f - animatedValue);
                    tvCleanCount.setText(String.format("%s", Math.round(currentValue)));
                    tvCleanUnit.setText(checkCountEntity.getUnit());
                } else if (animatedValue > 0.76f && animatedValue < 0.76f) {
                    tvCleanCount.setText("0");
                    tvCleanUnit.setText(checkCountEntity.getUnit());
                } else {
                    tvCleanCount.setVisibility(View.GONE);
                    tvCleanUnit.setVisibility(View.GONE);

                }
            }
        });


        shouIndex = 2;
        showColorChange(ivs, shouIndex);
        clearAll();
    }

    public void reStartClean() {
        if (viewLottieBottom != null) {
            viewLottieBottom.playAnimation();
        }
    }

    boolean isCacheCheckAll = true;  //运行内存是否全选
    boolean isCheckAll = true;  //运行内存是否全选

    @SuppressLint("CheckResult")
    private void clearAll() {
        if (mJunkGroups == null || mJunkGroups.size() < 1) {
            cleanFinish();
            return;
        }
        Observable.create(e -> {
            long total = 0;

            for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                JunkGroup value = entry.getValue();
                if (value.mChildren != null && value.mChildren.size() > 0) {
                    if ("TYPE_PROCESS".equals(value.mChildren.get(0).getGarbageType())) { //运行内存
                        for (FirstJunkInfo info : value.mChildren) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                            } else {
                                isCheckAll = isCacheCheckAll = false;
                            }
                        }

                    } else if ("TYPE_CACHE".equals(value.mChildren.get(0).getGarbageType())) { //缓存
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                            }
                        }
                        long l = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l;
                    } else if ("TYPE_APK".equals(value.mChildren.get(0).getGarbageType())) { //apk文件
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                            }
                        }
                        long l1 = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l1;
                    } else if ("TYPE_LEAVED".equals(value.mChildren.get(0).getGarbageType())) {//残留垃圾
                        for (FirstJunkInfo info : value.mChildren) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                            }
                        }
                        long leavedCache = CleanUtil.freeJunkInfos(value.mChildren);
                        total += leavedCache;
                    }
                } else if (entry.getKey() == 4 && value.otherChildren.size() > 0) {//其他垃圾处理
                    if (!value.isChecked()) {
                        isCheckAll = false;
                    }

                    long otherCache = CleanUtil.freeOtherJunkInfos(value.otherChildren);
                    total += otherCache;
                }
            }
            PreferenceUtil.saveIsCheckedAll(isCheckAll);
            PreferenceUtil.saveCacheIsCheckedAll(isCacheCheckAll);
            PreferenceUtil.saveMulCacheNum(PreferenceUtil.getMulCacheNum() * 0.3f);
            e.onNext(total);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            double memoryShow = NoClearSPHelper.getMemoryShow();
            if (memoryShow == 1) {
                //清理完成，存储时间点
                mSPHelper.saveCleanTime(System.currentTimeMillis());
            }
        });

    }

/*    private long getTotalSize() {
        long size = 0L;
        for (JunkGroup group : mJunkGroups.values()) {
            for (FirstJunkInfo firstJunkInfo : group.mChildren) {
                if (firstJunkInfo.isAllchecked()) {
                    size += firstJunkInfo.getTotalSize();
                }
            }
        }
        return size;
    }*/


    /**
     * 停止清理
     */
    public void stopClean() {
        viewLottieBottom.pauseAnimation();
//        mCleanAnimView.stopClean();
    }


    @Override
    public void onResume() {
        super.onResume();
        NiuDataAPI.onPageStart("scanning_result_page_view_page", "用户在扫描结果页浏览");

    }

    @Override
    public void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd("clean_up_scan_page", "scanning_result_page", "scanning_result_page_view_page", "用户在扫描结果页浏览");
    }

    public void showColorChange(TextView[] ivs, int index) {
        if (ivs.length == 3 && index <= 2 && index > 0) {
            Drawable drawable = ivs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (drawable.getAlpha() == 255) {
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
                        /* if(index==1){

                         *//*          Log.v("onAnimationEnd", "onAnimationEnd ");
                            mView.setColorChange(true);
                           *//*
                            if (animatorHide != null)
                                animatorHide.cancel();
                        }else{

                        }*/
                        showColorChange(ivs, (index - 1));
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
//                }
            }
        }

    }
}
