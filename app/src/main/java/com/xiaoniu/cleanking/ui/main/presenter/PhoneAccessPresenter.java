package com.xiaoniu.cleanking.ui.main.presenter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.WhiteListSpeedManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * Created by tie on 2017/5/15.
 */
public class PhoneAccessPresenter extends RxPresenter<PhoneAccessActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;


    @Inject
    public PhoneAccessPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }


    //获取到可以加速的应用名单Android O以下的获取最忌使用情况
    public void getAccessListBelow() {
        mView.showLoadingDialog();
        Observable.create(new ObservableOnSubscribe<ArrayList<FirstJunkInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FirstJunkInfo>> e) throws Exception {
                //获取到可以加速的应用名单
                FileQueryUtils mFileQueryUtils = new FileQueryUtils();
                //文件加载进度回调
                mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                    @Override
                    public void currentNumber() {

                    }

                    @Override
                    public void increaseSize(long p0) {

                    }

                    @Override
                    public void reduceSize(long p0) {

                    }

                    @Override
                    public void scanFile(String p0) {

                    }

                    @Override
                    public void totalSize(int p0) {

                    }
                });
                ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
                if (listInfo == null) {
                    listInfo = new ArrayList<>();
                }
                e.onNext(listInfo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<FirstJunkInfo>>() {
                    @Override
                    public void accept(ArrayList<FirstJunkInfo> strings) throws Exception {
                        mView.cancelLoadingDialog();
                        mView.getAccessListBelow(strings);
                    }
                });
    }

    //当前sdk版本高于22时
    @SuppressLint("CheckResult")
    public void getAccessAbove22() {
        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<List<ActivityManager.RunningAppProcessInfo>>) e -> {
            //获取到可以加速的应用名单
            List<ActivityManager.RunningAppProcessInfo> listApp = getProcessAbove();

            List<ActivityManager.RunningAppProcessInfo> listTemp = new ArrayList<>();
            for (int i = 0; i < listApp.size(); i++) {
                if (!isSystemApp(mView, listApp.get(i).processName)) {
                    listTemp.add(listApp.get(i));
                }
            }
            e.onNext(listTemp);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    mView.cancelLoadingDialog();
                    mView.getAccessListAbove22(strings);
                });
    }


    public List<String> getSystemOrStoppedProcess(Context context) {
        List<String> listPck = new ArrayList<>();
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            if (packageInfo.applicationInfo.flags == packageInfo.applicationInfo.FLAG_SYSTEM || packageInfo.applicationInfo.flags == packageInfo.applicationInfo.FLAG_UPDATED_SYSTEM_APP) {
                listPck.add(packageInfo.packageName);
            }
        }
        return listPck;
    }

    public boolean isSystemApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        if (packageName != null) {
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                return (info != null) && (info.applicationInfo != null) &&
                        ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public AlertDialog alertBanLiveDialog(Context context, int deleteNum, final ClickListener okListener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnOk = (Button) window.findViewById(R.id.btnOk);

        Button btnCancle = (Button) window.findViewById(R.id.btnCancle);
        TextView tipTxt = (TextView) window.findViewById(R.id.tipTxt);
        TextView content = (TextView) window.findViewById(R.id.content);
        tipTxt.setText("确定清理这" + deleteNum + "项？");
        content.setText("删除后手机更加流畅");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.clickOKBtn();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.cancelBtn();
            }
        });
        return dlg;
    }

    //Android O以上用这个方法获取最近使用情况
    @TargetApi(22)
    public List<ActivityManager.RunningAppProcessInfo> getProcessAbove() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) mView.getSystemService(USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }
        List<UsageStats> lists = usageStatsManager.queryUsageStats(4, System.currentTimeMillis() - 86400000, System.currentTimeMillis());
        ArrayList arrayList = new ArrayList();
        if (!(lists == null || lists.size() == 0)) {
            for (UsageStats usageStats : lists) {
                if (!(usageStats == null || usageStats.getPackageName() == null || usageStats.getPackageName().contains("com.cleanmaster.mguard_cn"))) {
                    ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
                    runningAppProcessInfo.processName = usageStats.getPackageName();
                    runningAppProcessInfo.pkgList = new String[]{usageStats.getPackageName()};
                    int uidForName = Process.getUidForName(usageStats.getPackageName());
                    arrayList.add(runningAppProcessInfo);
                }
            }
        }

        return arrayList;
    }

    //recyclerview底部弹出动画
    public void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item.getResourceId());

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * 数字动画
     *
     * @param
     * @param startNum
     * @param endNum
     * @param type     1是MB，2是GB
     */
    boolean canPlaying = true;

    public void setNumAnim(TextView tv_size, TextView tv_gb, View viewt, View view_top, int startNum, int endNum, int type) {
        ValueAnimator anim = ValueAnimator.ofInt(startNum, endNum);
        anim.setDuration(2000);
        anim.setInterpolator(new DecelerateInterpolator());
        canPlaying = true;
        anim.addUpdateListener(animation -> {
            if (tv_size == null) return;
            int currentValue = (int) animation.getAnimatedValue();
            tv_size.setText(currentValue + "");
            Log.d("asdf", "cuurent time " + animation.getCurrentPlayTime());

            if (currentValue == endNum) {
                tv_size.setText(type == 1 ? String.valueOf(currentValue) : String.valueOf(NumberUtils.getFloatStr2(currentValue / 1024)));
                tv_gb.setText(type == 1 ? "MB" : "GB");
            }
        });
        ValueAnimator colorAnim1 = ObjectAnimator.ofInt(viewt, "backgroundColor", FirstLevel, SecondLevel, ThirdLevel);
        colorAnim1.setEvaluator(new ArgbEvaluator());
        colorAnim1.setDuration(800);
        colorAnim1.setStartDelay(1200);

        colorAnim1.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            view_top.setBackgroundColor(animatedValue);
            mView.setStatusBarNum(animatedValue);
        });

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mView.setCanClickDelete(true);
            }
        });
//        anim.start();

        AnimatorSet animatorSetTimer = new AnimatorSet();
        animatorSetTimer.playTogether(anim, colorAnim1);
        animatorSetTimer.start();

    }


    /**
     * 第一阶段  红色
     */
    private static final int FirstLevel = 0xff06C581;

    private static final int SecondLevel = 0xffF1D53B;
    /**
     * 第三阶段 绿色
     */
    private static final int ThirdLevel = 0xffFD6F46;

    public void setBgChanged(View viewt, View view_top) {



//        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
//        anim.setDuration(time);
//        anim.setInterpolator(new DecelerateInterpolator());
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int currentValue = (int) animation.getAnimatedValue();
//                if (currentValue < 33) {
//                    viewt.setBackgroundColor(mView.getResources().getColor(R.color.color_06C581));
//                    view_top.setBackgroundColor(mView.getResources().getColor(R.color.color_06C581));
//                    mView.setStatusBar(R.color.color_06C581);
//                } else if (currentValue < 44) {
//                    viewt.setBackgroundColor(mView.getResources().getColor(R.color.color_F1D53B));
//                    view_top.setBackgroundColor(mView.getResources().getColor(R.color.color_F1D53B));
//                    mView.setStatusBar(R.color.color_F1D53B);
//                } else {
//                    viewt.setBackgroundColor(mView.getResources().getColor(R.color.color_FD6F46));
//                    view_top.setBackgroundColor(mView.getResources().getColor(R.color.color_FD6F46));
//                    mView.setStatusBar(R.color.color_FD6F46);
//                }
//
//            }
//        });
//        anim.start();
    }

    /**
     * 显示可绑定银行列表
     *
     * @param statusView
     */
    public void showPopupWindow(View statusView) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mView).inflate(
                R.layout.layout_clean_more_infoacc, null);
        View textMemory = contentView.findViewById(R.id.text_memory);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //内存加速白名单
        contentView.setOnClickListener(v -> {
            popupWindow.dismiss();
            mView.setFromProtect(true);
            mView.startActivity(WhiteListSpeedManageActivity.class);
        });
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v, event) -> {
            return false;
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(statusView);
    }

    boolean isFromClick = false;

    public AlertDialog showPermissionDialog(Context context, final ClickListener okListener) {
        isFromClick = false;
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.setCancelable(true);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_permission_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_goto = (TextView) window.findViewById(R.id.tv_goto);
        tv_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromClick = true;
                dlg.dismiss();
                okListener.clickOKBtn();
            }
        });
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isFromClick)
                    mView.finish();
            }
        });
        return dlg;
    }

    public boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    public interface ClickListener {
        public void clickOKBtn();

        public void cancelBtn();
    }

}
