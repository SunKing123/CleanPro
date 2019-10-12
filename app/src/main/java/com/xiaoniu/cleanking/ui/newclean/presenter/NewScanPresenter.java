package com.xiaoniu.cleanking.ui.newclean.presenter;

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
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.common.utils.ContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class NewScanPresenter extends RxPresenter<ScanFragment, NewScanModel> {

    private ValueAnimator mScanTranlateColor;

    @Inject
    NoClearSPHelper mSPHelper;

    //背景颜色是否已变为红色
    private boolean isChangeRed = false;

    @Inject
    public NewScanPresenter() {
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
        if (!isChangeRed)
            showColorChange();
        total = 0;
        mJunkGroups = new HashMap<>();
        mJunkResults = new HashMap<>();
        mFileQueryUtils = new FileQueryUtils();
        //文件加载进度回调
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
            private boolean isFirst = true;

            @Override
            public void currentNumber() {
                Log.v("onAnimationEnd","currentNumber " + isFirst);
                if (isFirst) {
                    isFirst = false;
                    if (mView.getActivity() == null) {
                        return;
                    }
                    mView.getActivity().runOnUiThread(() -> {
                        if (!mScanTranlateColor.isRunning()) {
//                            mScanTranlateColor.start();
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
                Log.v("onAnimationEnd","reduceSize ");
            }

            @Override
            public void scanFile(String p0) {
                Log.v("onAnimationEnd","scanFile ");
                mView.showScanFile(p0);
            }

            @Override
            public void totalSize(int p0) {
                Log.v("onAnimationEnd","totalSize ");
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
                //其他垃圾
//                JunkGroup adGroup = mJunkGroups.get(JunkGroup.GROUP_AD);
//                if (adGroup == null) {
//                    adGroup = new JunkGroup();
//                    adGroup.mName = ContextUtils.getContext().getString(R.string.other_clean);
//                    adGroup.isChecked = true;
//                    adGroup.isExpand = true;
//                    adGroup.mChildren = new ArrayList<>();
//                    mJunkGroups.put(JunkGroup.GROUP_AD, adGroup);
//                    adGroup.mSize += 0;
//                }
                //卸载残留
//                JunkGroup uninstallGroup = mJunkGroups.get(JunkGroup.GROUP_UNINSTALL);
//                if (uninstallGroup == null) {
//                    uninstallGroup = new JunkGroup();
//                    uninstallGroup.mName = ContextUtils.getContext().getString(R.string.uninstall_clean);
//                    uninstallGroup.isChecked = true;
//                    uninstallGroup.isExpand = true;
//                    uninstallGroup.mChildren = new ArrayList<>();
//                    mJunkGroups.put(JunkGroup.GROUP_UNINSTALL, uninstallGroup);
//                    uninstallGroup.mSize += 0;
//                }

                //缓存垃圾
                JunkGroup cacheGroup = mJunkGroups.get(JunkGroup.GROUP_CACHE);
                if (cacheGroup == null) {
                    cacheGroup = new JunkGroup();
                    cacheGroup.mName = ContextUtils.getContext().getString(R.string.cache_clean);
                    cacheGroup.isChecked = true;
                    cacheGroup.isExpand = true;
                    cacheGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);
                    cacheGroup.mSize += 0;
                }

                //内存清理
                JunkGroup processGroup = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                if (processGroup == null) {
                    processGroup = new JunkGroup();
                    processGroup.mName = ContextUtils.getContext().getString(R.string.process_clean);
                    processGroup.isChecked = true;
                    processGroup.isExpand = true;
                    processGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_PROCESS, processGroup);
                    processGroup.mSize += 0;
                }

                //无用安装包
                JunkGroup apkGroup = mJunkGroups.get(JunkGroup.GROUP_APK);
                if (apkGroup == null) {
                    apkGroup = new JunkGroup();
                    apkGroup.mName = ContextUtils.getContext().getString(R.string.apk_clean);
                    apkGroup.isChecked = true;
                    apkGroup.isExpand = true;
                    apkGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_APK, apkGroup);
                    apkGroup.mSize += 0;
                }

                for (FirstJunkInfo info : a) {
                    if ("TYPE_CACHE".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName()) ) {
                            cacheGroup.mChildren.add(info);
                            cacheGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_PROCESS".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName()) ) {
                            processGroup.mChildren.add(info);
                            processGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_APK".equals(info.getGarbageType())) {
                        apkGroup.mChildren.add(info);
                        apkGroup.mSize += info.getTotalSize();
                    }
                }
            } else {
                int i = 0;
                for (Map.Entry<Integer, JunkGroup> entry : mJunkGroups.entrySet()) {
                    mJunkResults.put(i++, entry.getValue());
                }
                if (mFileQueryUtils.isFinish())
                    return;
                mView.scanFinish(mJunkResults);
            }
        });

    }

    public void showColorChange() {

            mScanTranlateColor = ObjectAnimator.ofInt(mView.getCleanTopLayout(), "backgroundColor", ThirdLevel, SecondLevel, FirstLevel);
            mScanTranlateColor.setEvaluator(new ArgbEvaluator());
            mScanTranlateColor.setDuration(1000);
            if (!mScanTranlateColor.isRunning()) {
                mScanTranlateColor.start();
            }
            mScanTranlateColor.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                if (FirstLevel == animatedValue)
                    isChangeRed = true;
                Log.v("onAnimationEnd","FirstLevel ");
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
                    Log.v("onAnimationEnd","onAnimationEnd ");
                    mView.setColorChange(true);
                    if(mScanTranlateColor!=null)
                    mScanTranlateColor.cancel();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
    }

    AnimatorSet cleanScanAnimator;

    public AnimatorSet getCleanScanAnimator() {
        return cleanScanAnimator;
    }

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
        cleanScanAnimator.start();
    }

    /**
     * 扫描结束
     */
    public void stopCleanScanAnimation() {
        if (cleanScanAnimator != null) {
            cleanScanAnimator.end();
        }
        if (mFileQueryUtils != null && !mFileQueryUtils.isFinish() && mView != null)
            mView.endScanAnimation();
    }

    public void initAnim(ValueAnimator animator, long time, int repeatMode, int count) {
        animator.setRepeatCount(count);
        animator.setDuration(time);
        animator.setRepeatMode(repeatMode);
    }
    private static final int FirstLevel = 0xffFD6F46;
    private static final int SecondLevel = 0xffF1D53B;
    private static final int ThirdLevel = 0xff06C581;

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

    @SuppressLint("CheckResult")
    public void checkPermission() {
        if (mView == null)
            return;
        String permissionsHint = "需要打开文件读写权限";
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        new RxPermissions(mView.getActivity()).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //开始
                    if (mView == null)
                        return;
                    mView.startScan();
                } else {
                    if (mView == null)
                        return;
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
    private AlertDialog dlg;

    public void showPermissionDialog(Context context) {
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
            mView.goSetting();
        });
        btnCancle.setOnClickListener(v -> {
            dlg.dismiss();
            mView.getActivity().finish();
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
