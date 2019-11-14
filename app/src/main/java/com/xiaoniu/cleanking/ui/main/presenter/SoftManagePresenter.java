package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SoftManageActivity;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.model.MainModel;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by lang.chen on 2019/7/4
 */
public class SoftManagePresenter extends RxPresenter<SoftManageActivity, MainModel> {

    RxAppCompatActivity mContext;

    private List<AppInfoBean> apps = new ArrayList<>();
    //包名大小
    private List<Long> packageSize = new ArrayList<>();

    @Inject
    public SoftManagePresenter(RxAppCompatActivity activity) {
        this.mContext = activity;
    }


    //扫描已安装的apk信息
    public void scanData() {
        apps.clear();
        getApplicaionInfo();

    }


    public List<AppInfoBean> getData() {
        return this.apps;
    }

    /**
     * 获取已安装应用的信息，
     * 包括icon ,名称，apk大小，首次安装时间，存储大小
     * <p>
     * 存储大小对应的是 packname/files;
     */
    public void getApplicaionInfo() {
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                AppInfoBean appInfoBean = new AppInfoBean();
                appInfoBean.name = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                //应用icon
                appInfoBean.icon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                //appInfoBean.installTime = packageInfo.firstInstallTime;
                appInfoBean.packageName = packageInfo.packageName;
                Set<String> whitePkgs =  sp.getStringSet(SpCacheConfig.WHITE_LIST_SOFT_KEY_INSTALL_PACKE_NAME, new HashSet<>());

                if (packageInfo.packageName.equals("com.hellogeek.cleanking") || (whitePkgs.size() > 0 && whitePkgs.contains(packageInfo.packageName))){
                    continue;
                }
                apps.add(appInfoBean);
            }
        }

        //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !hasUsageStatsPermission(mContext)) {
            refreshData();
        } else {
            for (int i = 0; i < apps.size(); i++) {
                AppInfoBean appInfoBean = apps.get(i);
                boolean isLast = i == apps.size() - 1 ? true : false;
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    queryPacakgeSize(appInfoBean.packageName, isLast);
                } else {
                    queryStorageStatus(appInfoBean.packageName, isLast);

                }
            }
        }
        //refreshData();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        //http://stackoverflow.com/a/42390614/878126
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        boolean granted = false;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void queryStorageStatus(String packageName, boolean isLast) {


        StorageStatsManager storageStatsManager = (StorageStatsManager) mContext.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        //获取所有应用的StorageVolume列表
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        UUID uuid = null;
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
                if (isLast) {
                    refreshData();
                }
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
     * 根据获取的安装包大小更新缓存
     *
     * @return
     */
    public void refreshData() {
        if (apps.size() == packageSize.size()) {
            mView.updateData(apps);
            for (int i = 0; i < apps.size(); i++) {
                AppInfoBean appInfoBean = apps.get(i);
                appInfoBean.packageSize = packageSize.get(i);
            }
        }
        mView.updateData(apps);
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
                getPackageSizeInfo.invoke(mPackageManager, pkgName, new PkgSizeObserver(pkgName, isLast));// 方法使用的参数

            } catch (Exception ex) {
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
