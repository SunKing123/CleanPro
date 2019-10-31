package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.GameListActivity;
import com.xiaoniu.cleanking.ui.main.bean.AnimationItem;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * Created by tie on 2017/5/15.
 */
public class GameListPresenter extends RxPresenter<GameListActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public GameListPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    //获取到可以加速的应用名单Android O以下的获取最近使用情况
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
                    mView.cancelLoadingDialog();
                    mView.getAccessListBelow(strings);
                });
    }

    //当前sdk版本高于22时
    @SuppressLint("CheckResult")
    public void getAccessAbove22() {
//        mView.showLoadingDialog();
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

    //RecyclerView底部弹出动画
    public void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item.getResourceId());

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
