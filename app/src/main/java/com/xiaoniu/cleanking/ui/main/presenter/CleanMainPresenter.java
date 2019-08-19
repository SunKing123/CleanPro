package com.xiaoniu.cleanking.ui.main.presenter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.event.HomeCleanEvent;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.ui.main.widget.PromptDialog;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.TimeUtil;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.UpdateAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static android.view.View.VISIBLE;
import static com.xiaoniu.cleanking.utils.ResourceUtils.getString;

public class CleanMainPresenter extends RxPresenter<CleanMainFragment, CleanMainModel> {

    private ValueAnimator mScanTranlateColor;

    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public CleanMainPresenter() {
    }

    private HashMap<Integer, JunkGroup> mJunkGroups = null;
    private HashMap<Integer, JunkGroup> mJunkResults = null;

    long total = 0;

    private FileQueryUtils mFileQueryUtils;

    /**
     * 开始进行文件扫描
     */
    @SuppressLint("CheckResult")
    public void startScan() {

        total = 0;

        mJunkGroups = new HashMap<>();
        mJunkResults = new HashMap<>();

        mFileQueryUtils = new FileQueryUtils();

        showColorChange();

        //文件加载进度回调
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
            private boolean isFirst = true;

            @Override
            public void currentNumber() {
                if (isFirst) {
                    isFirst = false;
                    FragmentActivity activity = mView.getActivity();
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(() -> {
                        if (!mScanTranlateColor.isRunning()) {
                            mScanTranlateColor.start();
                        }
                    });
                }
            }

            @Override
            public void increaseSize(long p0) {
                total += p0;
                mView.showCountNumber(total);
            }

            @Override
            public void reduceSize(long p0) {

            }

            @Override
            public void scanFile(String p0) {
                mView.showScanFile(p0);
            }

            @Override
            public void totalSize(int p0) {

            }
        });

        Observable.create(e -> {
            //扫描进程
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);
            //扫描apk安装包
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            e.onNext(apkJunkInfos);
            //获取前两个扫描的结果
            boolean isScanFile = (runningProcess.size() + apkJunkInfos.size()) > 0;
            //扫描数据文件
            ArrayList<FirstJunkInfo> androidDataInfo = mFileQueryUtils.getAndroidDataInfo(isScanFile);
            e.onNext(androidDataInfo);
            e.onNext("FINISH");
        }).compose(RxUtil.rxObservableSchedulerHelper(mView)).subscribe(o -> {
            if (o instanceof ArrayList) {
                ArrayList<FirstJunkInfo> a = (ArrayList<FirstJunkInfo>) o;
                for (FirstJunkInfo info : a) {
                    if ("TYPE_CACHE".equals(info.getGarbageType())) {
                        JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_CACHE);
                        if (cacheGroup == null) {
                            cacheGroup = new JunkGroup();
                            cacheGroup.mName = getString(R.string.cache_clean);
                            cacheGroup.isChecked = true;
                            cacheGroup.isExpand = true;
                            cacheGroup.mChildren = new ArrayList<>();
                            mJunkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);
                        }
                        cacheGroup.mChildren.add(info);
                        cacheGroup.mSize += info.getTotalSize();
                    } else if ("TYPE_PROCESS".equals(info.getGarbageType())) {
                        JunkGroup processGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                        if (processGroup == null) {
                            processGroup = new JunkGroup();
                            processGroup.mName = getString(R.string.process_clean);
                            processGroup.isChecked = true;
                            processGroup.isExpand = true;
                            processGroup.mChildren = new ArrayList<>();
                            mJunkGroups.put(JunkGroup.GROUP_PROCESS, processGroup);
                        }
                        processGroup.mChildren.add(info);
                        processGroup.mSize += info.getTotalSize();
                    } else if ("TYPE_APK".equals(info.getGarbageType())) {
                        JunkGroup apkGroup = mJunkGroups.get(JunkGroup.GROUP_APK);
                        if (apkGroup == null) {
                            apkGroup = new JunkGroup();
                            apkGroup.mName = getString(R.string.apk_clean);
                            apkGroup.isChecked = true;
                            apkGroup.isExpand = true;
                            apkGroup.mChildren = new ArrayList<>();
                            mJunkGroups.put(JunkGroup.GROUP_APK, apkGroup);
                        }
                        apkGroup.mChildren.add(info);
                        apkGroup.mSize += info.getTotalSize();
                    }
                }
            } else {
                int i = 0;
                for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                    mJunkResults.put(i++, entry.getValue());
                }
                mView.scanFinish(mJunkResults);
            }
        });
    }

    public void showColorChange() {
        mScanTranlateColor = ObjectAnimator.ofInt(mView.getCleanTopLayout(), "backgroundColor", ThirdLevel, SecondLevel, FirstLevel);
        mScanTranlateColor.setEvaluator(new ArgbEvaluator());
        mScanTranlateColor.setDuration(500);
        mScanTranlateColor.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mView == null)
                return;
            if (mView.getViewShow()) {
                //只有首页显示的时候会显示状态栏变化
                mView.showBarColor(animatedValue);
            }
        });

        mScanTranlateColor.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mView.setColorChange(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void showTransAnim(FrameLayout mLayoutCleanTop) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(mLayoutCleanTop.getMeasuredHeight(), ScreenUtils.getFullActivityHeight());
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
            layoutParams.height = currentValue;
            mLayoutCleanTop.setLayoutParams(layoutParams);
        });
        if (mView.getActivity() != null) {
            FrameLayout frameLayout = mView.getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            frameLayout.setLayoutParams(layoutParams);
            mView.getActivity().findViewById(R.id.bottomBar).setVisibility(View.GONE);
            mView.getActivity().findViewById(R.id.bottom_shadow).setVisibility(View.GONE);
            valueAnimator.start();
        }
    }

    AnimatorSet cleanScanAnimator;

    /**
     * 开始扫描动画
     *
     * @param iconOuter
     * @param circleOuter
     * @param circleOuter2
     */
    public void startCleanScanAnimation(ImageView iconOuter, View circleOuter, View circleOuter2) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconOuter, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconOuter, "scaleY", 1f, 1.3f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(iconOuter, "alpha", 1f, 0.4f, 1f);

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(circleOuter, "scaleX", 1f, 1.8f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(circleOuter, "scaleY", 1f, 1.8f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(circleOuter, "alpha", 1f, 0.0f);

        ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(circleOuter2, "scaleX", 1f, 1.6f);
        ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(circleOuter2, "scaleY", 1f, 1.6f);
        ObjectAnimator alpha3 = ObjectAnimator.ofFloat(circleOuter2, "alpha", 1f, 0.0f);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setRepeatCount(-1);
        scaleY.setRepeatCount(-1);
        rotation.setRepeatCount(-1);
        alpha.setRepeatCount(-1);
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);
        rotation.setRepeatMode(ValueAnimator.RESTART);
        rotation.setDuration(1000);
        alpha.setDuration(2000);
        alpha.setRepeatMode(ValueAnimator.REVERSE);

        //第一圈
        initAnim(alpha2, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleY2, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleX2, 2000, ValueAnimator.RESTART, -1);

        initAnim(alpha3, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleY3, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleX3, 2000, ValueAnimator.RESTART, -1);

        cleanScanAnimator = new AnimatorSet();

        cleanScanAnimator.playTogether(scaleX, scaleY, rotation, alpha, scaleX2, scaleY2, alpha2, scaleX3, scaleY3, alpha3);
        cleanScanAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        cleanScanAnimator.start();
    }

    /**
     * 扫描结束
     */
    public void stopCleanScanAnimation() {
        if (cleanScanAnimator != null) {
            cleanScanAnimator.end();
        }
        mView.endScanAnimation();
    }

    public void initAnim(ValueAnimator animator, long time, int repeatMode, int count) {
        animator.setRepeatCount(count);
        animator.setDuration(time);
        animator.setRepeatMode(repeatMode);
    }

    /**
     * 开始清理动画
     *  @param iconInner
     * @param iconOuter
     * @param layoutScan
     * @param countEntity
     */
    public void startCleanAnimation(ImageView iconInner, ImageView iconOuter, LinearLayout layoutScan, RelativeLayout layoutCount, CountEntity countEntity) {
        iconInner.setVisibility(VISIBLE);

        int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - iconOuter.getMeasuredHeight();
        ObjectAnimator outerY = ObjectAnimator.ofFloat(iconOuter, "translationY", iconOuter.getTranslationY(), height);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(layoutScan, "translationY", layoutScan.getTranslationY(), height);
        ObjectAnimator countY = ObjectAnimator.ofFloat(layoutCount, "translationY", layoutCount.getTranslationY(), height);
        ObjectAnimator innerY = ObjectAnimator.ofFloat(iconInner, "translationY", iconInner.getTranslationY(), height);

        ObjectAnimator innerAlpha = ObjectAnimator.ofFloat(iconInner, "alpha", 0, 1);

        outerY.setDuration(1000);
        scanY.setDuration(1000);
        innerY.setDuration(1000);
        innerAlpha.setDuration(1000);
        countY.setDuration(1000);

        //第一阶段倒转
        ObjectAnimator rotationFistStep = ObjectAnimator.ofFloat(iconInner, "rotation", 0, -35f);
        rotationFistStep.setDuration(600);

        innerAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                iconInner.setVisibility(VISIBLE);
                new Handler().postDelayed(rotationFistStep::start, 400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //第二阶段开始
                secondLevel(iconInner, iconOuter, countEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(outerY, innerY, innerAlpha, scanY, countY);
        animatorSet.start();


    }

    public void secondLevel(ImageView iconInner, ImageView iconOuter, CountEntity countEntity) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(iconInner, "rotation", -35, 0, 360, 0, 360, 0, 360, 0);
        ObjectAnimator rotation3 = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360, 0, 360, 0, 360, 0, 360);
        ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", 0, 360);

        rotation.setDuration(1200);
        rotation2.setDuration(1100);

        rotation3.setDuration(200);
        rotation3.setRepeatCount(-1);
        rotation4.setRepeatCount(-1);
        rotation4.setDuration(200);
        rotation4.setInterpolator(new LinearInterpolator());
        rotation3.setInterpolator(new LinearInterpolator());

        new Handler().postDelayed(() -> mView.showLottieView(), 700);

        rotation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

//                animatorStep2.start();

                startClean(iconInner,iconOuter,rotation4, rotation3, countEntity);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        rotation.setInterpolator(new AccelerateInterpolator());
        rotation2.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorStep2 = new AnimatorSet();
        animatorStep2.playSequentially(rotation, rotation3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotation2, rotation4);
        animatorSet.start();
        animatorStep2.start();

    }

    private static final int FirstLevel = 0xffFD6F46;
    private static final int SecondLevel = 0xffF1D53B;
    private static final int ThirdLevel = 0xff06C581;

    /**
     * 开始清理操作
     *
     * @param iconInner
     * @param iconOuter
     * @param animatorSet
     * @param countEntity
     */
    public void startClean(ImageView iconInner, ImageView iconOuter, ObjectAnimator animatorSet, ObjectAnimator animatorSet2, CountEntity countEntity) {
        if (countEntity == null) {
            countEntity = new CountEntity();
        }
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(Float.valueOf(countEntity.getTotalSize()), 0);
        valueAnimator.setDuration(5000);
        String unit = countEntity.getUnit();
        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            TextView mTextCount = mView.getTextCountView();
            TextView textUnit = mView.getTextUnitView();
            if (mTextCount != null) {
                mTextCount.setText(NumberUtils.getFloatStr2(animatedValue));
            }
            if (textUnit != null) {
                textUnit.setText(unit);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatorSet != null) {
                    //清理完成
                    animatorSet.end();
//                    setViewTrans();
                    startFinishAnimator(iconInner,iconOuter);
                }
                if (animatorSet2 != null) {
                    animatorSet2.end();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ValueAnimator colorAnim = ObjectAnimator.ofInt(mView.getCleanTopLayout(), "backgroundColor", FirstLevel, SecondLevel, ThirdLevel);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(1000);
        colorAnim.setStartDelay(4000);
        colorAnim.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mView.getViewShow()) {
                //只有首页显示的时候会显示状态栏变化
                mView.showBarColor(animatedValue);
            }
        });

        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(valueAnimator, colorAnim);
        animatorSetTimer.start();

        clearAll();
    }

    @SuppressLint("CheckResult")
    private void clearAll() {
        Observable.create(e -> {

            long total = 0;
            for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                JunkGroup value = entry.getValue();
                if (value.mChildren != null && value.mChildren.size() > 0) {
                    if ("TYPE_PROCESS".equals(value.mChildren.get(0).getGarbageType())) {

                        for (FirstJunkInfo info : value.mChildren) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                            }
                        }
                    } else if ("TYPE_CACHE".equals(value.mChildren.get(0).getGarbageType())) {
                        long l = CleanUtil.freeJunkInfos(value.mChildren);
                        total += l;
                    } else if ("TYPE_APK".equals(value.mChildren.get(0).getGarbageType())) {
                        long l1 = CleanUtil.freeJunkInfos(value.mChildren);

                        total += l1;
                    }
                }
            }
            e.onNext(total);
        }).compose(RxUtil.rxObservableSchedulerHelper(mView)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                double memoryShow = TimeUtil.getMemoryShow();
                if (memoryShow == 1) {
                    //清理完成，存储时间点
                    mSPHelper.saveCleanTime(System.currentTimeMillis());
                }
            }
        });
    }

    public void startFinishAnimator(ImageView iconInner, ImageView iconOuter) {
        mView.getLottieView().setVisibility(View.GONE);
        LottieAnimationView finishAnimator = mView.getFinishAnimator();
        mView.getmFlAnim().setVisibility(VISIBLE);
        finishAnimator.setVisibility(VISIBLE);
        iconOuter.setVisibility(View.GONE);
        iconInner.setVisibility(View.GONE);
        mView.getScanLayout().setVisibility(View.INVISIBLE);
        mView.getCleanTextLayout().setVisibility(View.INVISIBLE);
        finishAnimator.useHardwareAcceleration();
        finishAnimator.setImageAssetsFolder("images");
        finishAnimator.setAnimation("data_clean_finish.json");
        finishAnimator.playAnimation();
        finishAnimator.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setViewTrans();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //数字动画播放完后火箭上移，布局高度缩小
    public void setViewTrans() {
        View cleanFinish = mView.getCleanFinish();
        int startHeight = DeviceUtils.getScreenHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, 0);
        anim.setDuration(500);
        anim.setInterpolator(new LinearInterpolator());
        CoordinatorLayout.LayoutParams rlp = (CoordinatorLayout.LayoutParams) cleanFinish.getLayoutParams();
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            rlp.topMargin = currentValue;
            cleanFinish.setLayoutParams(rlp);
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                HomeCleanEvent homeCleanEvent = new HomeCleanEvent();
                homeCleanEvent.setNowClean(true);
                EventBus.getDefault().post(homeCleanEvent);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    /**
     * 设置扫描是否终止
     *
     * @param isFinish
     */
    public void setIsFinish(boolean isFinish) {
        if (mFileQueryUtils != null) {
            mFileQueryUtils.setFinish(isFinish);
        }
    }

    /**
     * 底部广告接口
     */
    public void requestBottomAd() {
        mModel.getBottomAd(new CommonSubscriber<ImageAdEntity>() {
            @Override
            public void getData(ImageAdEntity imageAdEntity) {
                ArrayList<ImageAdEntity.DataBean> dataList = imageAdEntity.getData();
                if (dataList != null && dataList.size() > 0) {
                    mView.showFirstAd(dataList.get(0), 0);
                }
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    public void checkPermission() {
        String permissionsHint = "需要打开文件读写权限";
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        new RxPermissions(mView.getActivity()).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //开始
                    mView.startScan();
                } else {
                    if (hasPermissionDeniedForever(mView.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //永久拒绝权限
                        showPermissionDialog(mView.getContext());
                    } else {
                        //拒绝权限
                        checkPermission();
                    }
                }
            }
        });
    }

    /**
     * 显示权限设置弹窗
     *
     * @param stringRes  内容
     */
    PromptDialog dialog;

//    public void showPermissionDialog(String stringRes) {
//        if (dialog != null && dialog.isShowing())
//            return;
//        dialog = PromptDialog.builder(mView.getContext())
//                .setTitle("提示", R.color.color_111111, R.dimen.dimen_18sp)
//                .setMessage(stringRes, R.color.color_262626, R.dimen.dimen_15sp)
//                .setMessagePadding(R.dimen.dimen_16dp, R.dimen.dimen_16dp, R.dimen.dimen_16dp, R.dimen.dimen_16dp)
//                .setMessageGravity(Gravity.CENTER)
//                .setNegativeBtnStyle(R.dimen.dimen_16sp, R.color.color_FF2F31, R.color.white)
//                .setNegativeButton("去设置", (dialog1, which) -> {
//                    dialog1.dismiss();
//                    if (!TextUtils.isEmpty(stringRes) && stringRes.contains("悬浮窗权限")) {
//                        try {
//                            mView.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mView.getActivity().getPackageName())));
//                        } catch (Exception e) {
//                        }
//                    } else {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.setData(Uri.parse("package:" + mView.getActivity().getPackageName()));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        if (intent.resolveActivity(mView.getActivity().getPackageManager()) != null) {
//                            mView.setIsGotoSetting(true);
//                            mView.getActivity().startActivity(intent);
//                        }
//                    }
////                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
//
//
//                })
//                .setPositiveBtnStyle(R.dimen.dimen_16sp, R.color.color_AEB0B3, R.color.white)
//                .setPositiveButton("退出", (dialog12, which) -> {
//                    mView.getActivity().finish();
//                    dialog12.dismiss();
//                })
//                .create();
//        dialog.show();
//    }

    AlertDialog dlg;

    public void showPermissionDialog(Context context) {
        if (dialog != null && dialog.isShowing())
            return;
        dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return;
        }
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = (TextView) window.findViewById(R.id.btnOk);

        TextView btnCancle = (TextView) window.findViewById(R.id.btnCancle);
        TextView tipTxt = (TextView) window.findViewById(R.id.tipTxt);
        TextView content = (TextView) window.findViewById(R.id.content);
        btnCancle.setText("退出");
        btnOk.setText("去设置");
        tipTxt.setText("提示!");
        content.setText("清理功能无法使用，请先开启文件读写权限。");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mView.getActivity().getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(mView.getActivity().getPackageManager()) != null) {
                    mView.setIsGotoSetting(true);
                    mView.getActivity().startActivity(intent);
                }
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                mView.getActivity().finish();
            }
        });
    }

    /**
     * 是否有权限被永久拒绝
     *
     * @param activity   当前activity
     * @param permission 权限
     * @return
     */
    private static boolean hasPermissionDeniedForever(Activity activity, String permission) {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    /**
     * 开始下载
     *
     * @param downloadUrl
     */
    public void startDownload(String downloadUrl) {
        AppVersion appVersion = new AppVersion();
        AppVersion.DataBean dataBean = new AppVersion.DataBean();
        dataBean.downloadUrl = downloadUrl;
        appVersion.setData(dataBean);
        new UpdateAgent(mView.getActivity(), appVersion, null).customerDownload();
    }

    /**
     * 外部圈缓慢旋转
     * @param iconOuter
     */
    public void showOuterViewRotation(ImageView iconOuter) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        rotation.setRepeatCount(-1);
        rotation.setRepeatMode(ValueAnimator.RESTART);
        rotation.setDuration(1500);
    }
}
