package com.xiaoniu.cleanking.ui.main.presenter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.ui.main.widget.ScreenUtils;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xiaoniu.cleanking.utils.ResourceUtils.getString;

public class CleanMainPresenter extends RxPresenter<CleanMainFragment,CleanMainModel> {

    @Inject
    public CleanMainPresenter() {
    }

    private HashMap<Integer, JunkGroup> mJunkGroups = null;
    private HashMap<Integer, JunkGroup> mJunkResults = null;

    long total = 0;

    /**
     * 开始进行文件扫描
     */
    @SuppressLint("CheckResult")
    public void startScan() {

        mJunkGroups = new HashMap<>();
        mJunkResults = new HashMap<>();

        FileQueryUtils mFileQueryUtils = new FileQueryUtils();


        //文件加载进度回调
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
            @Override
            public void currentNumber() {

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
            long v1 = System.currentTimeMillis();
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);
            long l1 = System.currentTimeMillis();
            long v2 = l1 - v1;
            System.out.println("--------------" + v2);
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            e.onNext(apkJunkInfos);
            long l2 = System.currentTimeMillis();
            System.out.println("--------------" + (l2 - l1));
            ArrayList<FirstJunkInfo> androidDataInfo = mFileQueryUtils.getAndroidDataInfo();
            long l3 = System.currentTimeMillis();
            System.out.println("--------------" + (l3 - l2));
            e.onNext(androidDataInfo);
            e.onNext("FINISH");
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if(o instanceof ArrayList) {
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
                        if(apkGroup == null){
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
            }else {
                int i = 0;
                for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                    mJunkResults.put(i++, entry.getValue());
                }
                mView.scanFinish(mJunkResults);
            }
        });
    }

    public void showTransAnim(FrameLayout mLayoutCleanTop) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(mLayoutCleanTop.getMeasuredHeight(), ScreenUtils.getScreenHeight(AppApplication.getInstance()));
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLayoutCleanTop.getLayoutParams();
            layoutParams.height = currentValue;
            mLayoutCleanTop.setLayoutParams(layoutParams);
        });
        if(mView.getActivity() != null) {
            FrameLayout frameLayout = mView.getActivity().findViewById(R.id.frame_layout);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            frameLayout.setLayoutParams(layoutParams);
            mView.getActivity().findViewById(R.id.bottomBar).setVisibility(View.GONE);
            valueAnimator.start();
        }
    }

    AnimatorSet cleanScanAnimator;

    public void startCleanScanAnimation(ImageView iconOuter, View circleOuter,View circleOuter2) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconOuter, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconOuter, "scaleY", 1f, 1.3f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(iconOuter, "alpha", 1f, 0.4f);

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
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        rotation.setRepeatMode(ValueAnimator.RESTART);
        rotation.setDuration(1000);
        alpha.setDuration(1000);
        alpha.setRepeatMode(ValueAnimator.REVERSE);

        //第一圈
        initAnim(alpha2, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleY2, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleX2, 2000, ValueAnimator.RESTART, -1);

        initAnim(alpha3, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleY3, 2000, ValueAnimator.RESTART, -1);
        initAnim(scaleX3, 2000, ValueAnimator.RESTART, -1);

        cleanScanAnimator = new AnimatorSet();

        cleanScanAnimator.playTogether(scaleX,scaleY,rotation,alpha,scaleX2,scaleY2,alpha2,scaleX3,scaleY3,alpha3);
        cleanScanAnimator.start();
    }

    public void stopCleanScanAnimation() {
        if (cleanScanAnimator != null) {
            cleanScanAnimator.cancel();
        }
        mView.endScanAnimation();
    }

    public void initAnim(ValueAnimator animator, long time, int repeatMode, int count) {
        animator.setRepeatCount(count);
        animator.setDuration(time);
        animator.setRepeatMode(repeatMode);
    }

    public void startCleanAnimation(ImageView iconInner, ImageView iconOuter, LinearLayout layoutScan, TextView textCount, CountEntity countEntity) {
        iconInner.setVisibility(View.VISIBLE);

        int height = ScreenUtils.getScreenHeight(AppApplication.getInstance()) / 2 - iconOuter.getMeasuredHeight();
        ObjectAnimator outerY = ObjectAnimator.ofFloat(iconOuter, "translationY", iconOuter.getTranslationY(), height);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(layoutScan, "translationY", layoutScan.getTranslationY(), height);
        ObjectAnimator countY = ObjectAnimator.ofFloat(textCount, "translationY", textCount.getTranslationY(), height);
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
                iconInner.setVisibility(View.VISIBLE);
                new Handler().postDelayed(rotationFistStep::start, 400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //第二阶段开始
                secondLevel(iconInner, iconOuter,textCount,countEntity);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(outerY,innerY,innerAlpha,scanY,countY);
        animatorSet.start();


    }

    public void secondLevel(ImageView iconInner, ImageView iconOuter, TextView textCount, CountEntity countEntity) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(iconInner, "rotation", -35,0, 360,0,360,0,360);
        ObjectAnimator rotation3 = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360,0,360,0,360,0,360);
        ObjectAnimator rotation4 = ObjectAnimator.ofFloat(iconInner, "rotation", 0, 360,0,360,0,360,0,360);

        rotation.setDuration(2000);
        rotation2.setDuration(2000);

        rotation3.setDuration(300);
        rotation3.setRepeatCount(-1);
        rotation4.setRepeatCount(-1);
        rotation4.setDuration(300);

        rotation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(rotation3,rotation4);
                animatorSet.start();

                startClean(animatorSet,textCount,countEntity);
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

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, rotation2);
        animatorSet.start();

    }

    public void startClean(AnimatorSet animatorSet, TextView textCount, CountEntity countEntity) {
        new Handler().postDelayed(() -> {
            if (animatorSet != null) {
                animatorSet.cancel();
            }
        }, 6000);

        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(Float.valueOf(countEntity.getTotalSize()), 0);
        valueAnimator.setDuration(6000);
        String unit = countEntity.getUnit();
        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            textCount.setText(String.format("%s%s", Math.round(animatedValue), unit));
        });
        valueAnimator.start();
//        clearAll();
    }

    @SuppressLint("CheckResult")
    private void clearAll() {
        Observable.create(e -> {

            long total = 0;
            for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                JunkGroup value = entry.getValue();
                if (value.mChildren != null && value.mChildren.size() > 0) {
                    if("TYPE_PROCESS".equals(value.mChildren.get(0).getGarbageType())){

                        for (FirstJunkInfo info : value.mChildren) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(),info.getPid());
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            mView.showCleanFinish(o);
            EventBus.getDefault().post("clean_finish");
        });


    }

}
