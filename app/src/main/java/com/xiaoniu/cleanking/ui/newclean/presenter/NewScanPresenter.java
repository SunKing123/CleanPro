package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.RxFragment;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.fragment.ScanFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.RxUtil;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@Deprecated
public class NewScanPresenter extends RxPresenter<ScanFragment, NewScanModel> {

    private ValueAnimator mScanTranlateColor;
    private RxFragment mContext;
    @Inject
    NoClearSPHelper mSPHelper;

    //背景颜色是否已变为红色
    private boolean isChangeRed = false;
    int shouIndex = 2;

    @Inject
    public NewScanPresenter() {

    }

    private HashMap<Integer, JunkGroup> mJunkGroups = null;

    long total = 0;

    private FileQueryUtils mFileQueryUtils;

    /**
     * 开始进行文件扫描
     */
    @SuppressLint("CheckResult")
    public void startScan(ImageView[] ivs) {
        if (!isChangeRed) {
            shouIndex = 2;
            showColorChange01(ivs, shouIndex);
        }

        total = 0;
        mJunkGroups = new HashMap<>();
        mFileQueryUtils = new FileQueryUtils();
        //文件加载进度回调
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {

            @Override
            public void currentNumber() {
            }

            @Override
            public void increaseSize(long p0) {
                total += p0;
                if (null != mView)
                    mView.showCountNumber(total);
            }

            @Override
            public void reduceSize(long p0) {
                Log.v("onAnimationEnd", "reduceSize ");
            }

            @Override
            public void scanFile(String p0) {
                Log.v("onAnimationEnd", "scanFile ");
                if (null != mView)
                    mView.showScanFile(p0);
            }

            @Override
            public void totalSize(int p0) {
                Log.v("onAnimationEnd", "totalSize ");
            }
        });

        Observable.create(e -> {
            //扫描进程占用内存情况
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            Log.e("info", "内存占用----->" + runningProcess.size());
            e.onNext(runningProcess);
            //扫描apk安装包
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            Log.e("info", "Apk安装包----->" + apkJunkInfos.size());
            e.onNext(apkJunkInfos);

            boolean isScanFile = apkJunkInfos.size() > 0;
            //扫描私有路径下缓存文件
            ArrayList<FirstJunkInfo> androidDataInfo = mFileQueryUtils.getAndroidDataInfo(isScanFile);
            //根据私有路径扫描公用路径
            ArrayList<FirstJunkInfo> publicDataInfo = mFileQueryUtils.getExternalStorageCache(androidDataInfo);
            Log.e("info", "磁盘垃圾----->" + publicDataInfo.size());
            for (FirstJunkInfo firstJunkInfo : publicDataInfo) {
                Log.e("info", "垃圾----->" + firstJunkInfo.getSdPath());
            }
            e.onNext(publicDataInfo);

            //公用路径残留文件
            ArrayList<FirstJunkInfo> leaveDataInfo = mFileQueryUtils.getOmiteCache();
            Log.e("info", "卸载残余----->" + leaveDataInfo.size());
            e.onNext(leaveDataInfo);

            //其他垃圾
            JunkGroup otherGroup = mFileQueryUtils.getOtherCache();
            Log.e("info", "其他垃圾----->" + otherGroup);
            e.onNext(otherGroup);
            //扫描完成表示
            e.onNext("FINISH");
        }).compose(RxUtil.rxObservableSchedulerHelper(mView)).subscribe(o -> {
            Log.e("info", "00000000000000000000000------------<" + o.toString());
            if (o instanceof ArrayList) {
                ArrayList<FirstJunkInfo> a = (ArrayList<FirstJunkInfo>) o;

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

                //卸载残留
                JunkGroup uninstallGroup = mJunkGroups.get(JunkGroup.GROUP_UNINSTALL);
                if (uninstallGroup == null) {
                    uninstallGroup = new JunkGroup();
                    uninstallGroup.mName = ContextUtils.getContext().getString(R.string.uninstall_clean);
                    uninstallGroup.isChecked = true;
                    uninstallGroup.isExpand = true;
                    uninstallGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_UNINSTALL, uninstallGroup);
                    uninstallGroup.mSize += 0;
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

                for (FirstJunkInfo info : a) {
                    if ("TYPE_CACHE".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            cacheGroup.mChildren.add(info);
                            cacheGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_PROCESS".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            processGroup.mChildren.add(info);
                            processGroup.mSize += info.getTotalSize();
                        }
                    } else if ("TYPE_APK".equals(info.getGarbageType())) {
                        apkGroup.mChildren.add(info);
                        apkGroup.mSize += info.getTotalSize();

                    } else if ("TYPE_LEAVED".equals(info.getGarbageType())) {
                        if (!SpCacheConfig.CHAT_PACKAGE.equals(info.getAppPackageName()) && !SpCacheConfig.QQ_PACKAGE.equals(info.getAppPackageName())) {
                            uninstallGroup.mChildren.add(info);
                            uninstallGroup.mSize += info.getTotalSize();
                        }
                    }
                }
            } else if (o instanceof JunkGroup) { //其他垃圾
                //其他垃圾
                JunkGroup adGroup = mJunkGroups.get(JunkGroup.GROUP_OTHER);
                if (adGroup == null) {
                    adGroup = (JunkGroup) o;
                    adGroup.mName = ContextUtils.getContext().getString(R.string.other_clean);
                    adGroup.isChecked = true;
                    adGroup.needExpand = false;//不能展开显示
                    adGroup.isExpand = false;
                    adGroup.mChildren = new ArrayList<>();
                    mJunkGroups.put(JunkGroup.GROUP_OTHER, adGroup);
                }
            } else {
                long totalSize = CleanUtil.getTotalSize(mJunkGroups);
                if (total > totalSize) {//设置为其他垃圾
                    JunkGroup adGroup = mJunkGroups.get(JunkGroup.GROUP_OTHER);
                    if (adGroup != null) {
                        adGroup.mSize += (total - totalSize);
//                        mJunkGroups.put(JunkGroup.GROUP_OTHER, adGroup);
                    } else {
                        JunkGroup otherGroup = new JunkGroup();
                        otherGroup.mName = ContextUtils.getContext().getString(R.string.other_clean);
                        otherGroup.isChecked = true;
                        otherGroup.isExpand = true;
                        adGroup.needExpand = false;//不能展开显示
                        otherGroup.mChildren = new ArrayList<>();
                        otherGroup.mSize += (total - totalSize);
                        mJunkGroups.put(JunkGroup.GROUP_OTHER, otherGroup);
                    }
                }

                //三分钟限制
//                cacheFilter();
                if (mFileQueryUtils.isFinish())
                    return;
                mView.scanFinish(mJunkGroups);
            }
        });

    }


    public void showColorChange01(ImageView[] ivs, int index) {
        if (ivs.length == 3 && index <= 2 && index > 0) {
            Drawable drawable = ivs[index].getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (drawable.getAlpha() == 255) {
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofInt("alpha", 0));
                animator.setTarget(drawable);
                animator.setDuration(2000);
                if (!animator.isRunning()) {
                    animator.start();
                }
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (index == 1) {
                            isChangeRed = true;
                            Log.v("onAnimationEnd", "onAnimationEnd ");
                            mView.setColorChange(true);
                            if (animator != null)
                                animator.cancel();
                        } else {
                            showColorChange01(ivs, (index - 1));
                        }

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

/*    public void showColorChange() {
        mScanTranlateColor = ObjectAnimator.ofInt(mView.getCleanTopLayout(), "backgroundColor", ThirdLevel, SecondLevel, FirstLevel);
        mScanTranlateColor.setEvaluator(new ArgbEvaluator());
        mScanTranlateColor.setDuration(1000);
        if (!mScanTranlateColor.isRunning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mScanTranlateColor.start();
            }
        }
        mScanTranlateColor.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (FirstLevel == animatedValue)
                isChangeRed = true;
            Log.v("onAnimationEnd", "FirstLevel ");
            if (mView == null)
                return;
            if (mView.getViewShow()) {
                //显示状态栏变化
                mView.showBarColor(animatedValue);
            }
        });

        mScanTranlateColor.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v("onAnimationEnd", "onAnimationEnd ");
                mView.setColorChange(true);
                if (mScanTranlateColor != null)
                    mScanTranlateColor.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }*/

  /*  AnimatorSet cleanScanAnimator;

    public AnimatorSet getCleanScanAnimator() {
        return cleanScanAnimator;
    }*/


    /**
     * 波纹扫描动画
     */
    public void startCleanScanAnimation01(LottieAnimationView lottieAnimationView) {

        lottieAnimationView.playAnimation();
    }
    /**
     * 开始扫描动画
     *
     * @param iconOuter
     * @param circleOuter
     * @param circleOuter2
     */
   /* public void startCleanScanAnimation(ImageView iconOuter, View circleOuter, View circleOuter2) {
        try {
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

            cleanScanAnimator = new AnimatorSet new AnimatorSet();

            cleanScanAnimator.playTogether(scaleX, scaleY, rotation, alpha, scaleX2, scaleY2, alpha2, scaleX3, scaleY3, alpha3);
            cleanScanAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 扫描结束
     */
    public void stopCleanScanAnimation() {
       /* if (cleanScanAnimator != null) {
            cleanScanAnimator.end();
        }*/
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
        //umeng -- Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'android.app.FragmentManager android.app.Activity.getFragmentManager()' on a null object reference
        if (null == mView.getActivity()) return;
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
            if (null != activity && !activity.shouldShowRequestPermissionRationale(permission)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }

    /**
     * 外部圈缓慢旋转
     *
     * @param iconOuter
     */
    public void showOuterViewRotation(ImageView iconOuter) {
        try {
            ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
            rotation.setRepeatCount(-1);
            rotation.setRepeatMode(ValueAnimator.RESTART);
            rotation.setDuration(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
