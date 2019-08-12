package com.xiaoniu.cleanking.ui.main.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.RequiresApi;
import android.telephony.mbms.FileInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.PhoneThinActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.FileInfoEntity;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * Created by lang.chen on 2019/7/9
 */
public class PhoneThinPresenter extends RxPresenter<PhoneThinActivity, MainModel> {

    private RxAppCompatActivity mContext;
    private List<AppInfoBean> apps = new ArrayList<>();
    //包名大小
    private List<Long> packageSize = new ArrayList<>();
    /**
     * 扫描目录，共计文件大小
     */
    private long mFileTotalSize = 0;

    @Inject
    public PhoneThinPresenter(RxAppCompatActivity activity) {
        this.mContext = activity;
    }


    public void scanFile(String path) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                scanDirectory(path, e);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String o) {
                        mView.updateText(o,mFileTotalSize);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        mView.onComplete();

                    }
                });
    }


    private void scanDirectory(String path, ObservableEmitter<String> e) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (null != f) {
                for (File file1 : f) {
                    String fileName = file1.getName().toLowerCase();
                    e.onNext(fileName);
                    if (file1.isDirectory()) {
                        scanDirectory(path + "/" + file1.getName(),e);
                    } else if(fileName.endsWith(".mp4")){
                        mFileTotalSize += file1.length();
                    }
                }
            }
        }
    }


    /**
     * 计算百分比
     *
     * @param num   当前大小
     * @param total 总大小
     * @param scale 保留小数点
     * @return
     */
    public String accuracy(double num, double total, int scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num) + "";
    }


    /**
     * 获取存储大小
     *
     * @param path
     * @return
     */
    public long queryStorageSize(String path) {
        StatFs statFs = new StatFs(path);

        //存储块总数量
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();

        return blockSize * blockCount;

    }


    public long getFileSize() {
        return this.mFileTotalSize;
    }


    //扫描中动画
    public ObjectAnimator setScaningAnim(View viewY) {
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", -1 * DeviceUtils.dip2px(99), DeviceUtils.getScreenWidth());
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(viewY, translationX);
        animator.setDuration(600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.INFINITE);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewY.setVisibility(VISIBLE);
            }
        });
        animator.start();
        return animator;
    }

    public ObjectAnimator playRoundAnim(ImageView iconOuter) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(iconOuter, "rotation", 0, 360);
        rotation.setDuration(500);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
        return rotation;
    }


    //扫描已安装的apk信息
    public void scanData() {
        try {
            apps.clear();
            getApplicaionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取已安装应用的信息，
     * 包括icon ,名称，apk大小，首次安装时间，存储大小
     * <p>
     * 存储大小对应的是 packname/files;
     */
    public void getApplicaionInfo() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);


        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {


                AppInfoBean appInfoBean = new AppInfoBean();
                appInfoBean.name = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                //应用icon
                appInfoBean.icon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                appInfoBean.installTime = packageInfo.firstInstallTime;
                appInfoBean.packageName = packageInfo.packageName;
                apps.add(appInfoBean);
            }
        }

        for (int i = 0; i < apps.size(); i++) {
            AppInfoBean appInfoBean = apps.get(i);
            boolean isLast = i == apps.size() - 1 ? true : false;
            //小于八点0
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                queryPacakgeSize(appInfoBean.packageName, isLast);
            } else {
                queryStorageStatus(appInfoBean.packageName, isLast);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void queryStorageStatus(String packageName, boolean isLast) {

        StorageStatsManager storageStatsManager = (StorageStatsManager) mContext.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        //获取所有应用的StorageVolume列表
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        UUID uuid=null;
        for (StorageVolume item : storageVolumes) {
            String uuidStr = item.getUuid();

            if (uuidStr == null) {
                uuid = StorageManager.UUID_DEFAULT;
            } else {
                uuid = UUID.fromString(uuidStr);
            }
            int uid = getUid(mContext, packageName);
            //通过包名获取uid
            StorageStats storageStats = null;
            try {
                storageStats = storageStatsManager.queryStatsForUid(uuid, uid);
                packageSize.add(storageStats.getAppBytes());
                if (isLast) {
                    refreshData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    /**
     * 根据应用包名获取对应uid
     */
    public int getUid(Context context, String pakName) {
        try {
            return context.getPackageManager().getApplicationInfo(pakName, PackageManager.GET_META_DATA).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 根据获取的安装包大小
     *
     * @return
     */
    public void refreshData() {
        mFileTotalSize = 0L;
        for (int i = 0; i < apps.size(); i++) {
            AppInfoBean appInfoBean = apps.get(i);
            appInfoBean.packageSize = packageSize.get(i);
            mFileTotalSize += appInfoBean.packageSize;
        }
        mView.updateData(apps.size(), mFileTotalSize);
    }

    /**
     * @param pkgName 包名
     * @param isLast  是否为最后一个
     */
    public void queryPacakgeSize(String pkgName, boolean isLast) {

        if (!TextUtils.isEmpty(pkgName)) {// pkgName不能为空
            // 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo

            PackageManager mPackageManager = mContext.getPackageManager();
            try {
                String methodName = "getPackageSizeInfo";// 想通过反射机制调用的方法名
                Class<?> parameterType1 = String.class;// 被反射的方法的第一个参数的类型
                Class<?> parameterType2 = IPackageStatsObserver.class;// 被反射的方法的第二个参数的类型
                Method getPackageSizeInfo = mPackageManager.getClass().getMethod(
                        methodName, parameterType1, parameterType2);
                getPackageSizeInfo.invoke(mPackageManager, pkgName, new PhoneThinPresenter.PkgSizeObserver(pkgName, isLast));// 方法使用的参数

            } catch (Exception ex) {
                Log.e("asdfg",""+ex.getMessage());
                ex.printStackTrace();

            }


        }

    }

    class PkgSizeObserver extends IPackageStatsObserver.Stub {
        //包名
        private String mPackageName;
        //是否为获取最后一个包名
        private boolean mIsLast;

        public PkgSizeObserver(String packageName, boolean isLast) {
            this.mPackageName = packageName;
            this.mIsLast = isLast;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            packageSize.add(pStats.codeSize);
            if (mIsLast) {
                refreshData();
            }
        }
    }


}
