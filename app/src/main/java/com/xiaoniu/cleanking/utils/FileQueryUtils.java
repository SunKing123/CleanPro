package com.xiaoniu.cleanking.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoClean;
import com.xiaoniu.cleanking.ui.main.bean.AppMemoryInfo;
import com.xiaoniu.cleanking.ui.main.bean.FilePathInfoClean;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.db.CleanDBManager;
import com.xiaoniu.cleanking.utils.encypt.Base64;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.USAGE_STATS_SERVICE;
import static com.jaredrummler.android.processes.AndroidProcesses.getRunningAppProcesses;

public class FileQueryUtils {

    private static final String COLON_SEPARATOR = ":";
    private final Set<String> mMemoryCacheWhite;
    private AppApplication mContext;
    private static List<ApplicationInfo> installedAppList;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private ScanFileListener mScanFileListener;


    public FileQueryUtils() {
        this.mContext = AppApplication.getInstance();
        this.mActivityManager = (ActivityManager) AppApplication.getInstance().getSystemService("activity");
        this.mPackageManager = AppApplication.getInstance().getPackageManager();
        mMemoryCacheWhite = getCacheWhite();
        if (this.mPackageManager != null) {
            try {
                installedAppList = this.mPackageManager.getInstalledApplications(8192);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setScanFileListener(ScanFileListener scanFileListener) {
        mScanFileListener = scanFileListener;
    }

    /**
     * 获取已安装应用的信息
     *
     * @return
     */
    public static List<PackageInfo> getInstalledList() {
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);


        int mTotalCount = installedPackages.size();

        List<PackageInfo> packageList = new ArrayList<>();

        for (int i = 0; i < mTotalCount; i++) {
            PackageInfo info = installedPackages.get(i);
            packageList.add(info);
        }
        return packageList;
    }

    public static ApplicationInfo getApplicationInfo(String str) {
        if (str == null) {
            return null;
        }
        for (ApplicationInfo applicationInfo : installedAppList) {
            if (str.equals(applicationInfo.processName)) {
                return applicationInfo;
            }
        }
        return null;
    }

    /**
     * 获取App名称
     *
     * @param applicationInfo
     * @return
     */
    public String getAppName(ApplicationInfo applicationInfo) {
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        return pm.getApplicationLabel(applicationInfo).toString();
    }

    /**
     * 获取App图标
     *
     * @param applicationInfo
     * @return
     */
    private Drawable getAppIcon(ApplicationInfo applicationInfo) {
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        return pm.getApplicationIcon(applicationInfo);
    }

    /**
     * 获取Android/data中的垃圾信息
     *
     * @return
     */
    public ArrayList<FirstJunkInfo> getAndroidDataInfo() {
        ArrayList<FirstJunkInfo> list = new ArrayList<>();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/";
        List<PackageInfo> installedList = AppApplication.getInstance().getPackageManager().getInstalledPackages(0);
        int total = 0;
        if (installedList != null) {
            total = installedList.size();
        }
        int index = 0;
        for (PackageInfo applicationInfo : installedList) {
            index ++;
            if (index > (float)total * 3 / 4) {
                mScanFileListener.currentNumber();
            }
            final File file = new File(rootPath + applicationInfo.packageName + "/cache");
            final File file2 = new File(rootPath + applicationInfo.packageName + "/files");
            FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
            firstJunkInfo.setAppName(getAppName(applicationInfo.applicationInfo));
            firstJunkInfo.setGarbageIcon(getAppIcon(applicationInfo.applicationInfo));
            firstJunkInfo.setAllchecked(true);
            firstJunkInfo.setGarbageType("TYPE_CACHE");
            firstJunkInfo.setAppPackageName(applicationInfo.packageName);

            if (mScanFileListener != null) {
                mScanFileListener.scanFile(applicationInfo.packageName);
            }
            //cache缓存
            if (file.exists()) {
                SecondJunkInfo secondJunkInfo = listFiles(file);
                if (secondJunkInfo.getFilesCount() > 0 && secondJunkInfo.getGarbageSize() > 0) {
                    secondJunkInfo.setFilecatalog(file.getAbsolutePath());
                    secondJunkInfo.setChecked(true);
                    secondJunkInfo.setPackageName(applicationInfo.packageName);
                    secondJunkInfo.setGarbagetype("TYPE_CACHE");
                    firstJunkInfo.addSecondJunk(secondJunkInfo);
                    firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                }
                if (mScanFileListener != null) {
                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                }
            }
            //files缓存
            if (file2.exists()) {
                for (final Map.Entry<String, String> entry : this.checkOutAllGarbageFolder(file2).entrySet()) {

                    final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
                    if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
                        continue;
                    }
                    listFiles2.setGarbageName(entry.getValue());
                    listFiles2.setFilecatalog(entry.getKey());
                    listFiles2.setChecked(true);
                    listFiles2.setPackageName(applicationInfo.packageName);
                    listFiles2.setGarbagetype("TYPE_CACHE");
                    firstJunkInfo.addSecondJunk(listFiles2);
                    firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
                    if (mScanFileListener != null) {
                        mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                    }
                }
            }

            if (firstJunkInfo.getSubGarbages() == null || firstJunkInfo.getSubGarbages().size() <= 0) {
                continue;
            }
            list.add(firstJunkInfo);
        }

        ArrayList<FirstJunkInfo> appCacheAndAdGarbage = getAppCacheAndAdGarbage(list);
        if (appCacheAndAdGarbage != null) {
            list.addAll(appCacheAndAdGarbage);
        }
        return list;
    }

    /**
     * 获取进程
     *
     * @return
     */
    public ArrayList<FirstJunkInfo> getRunningProcess() {
        String str;
        FirstJunkInfo onelevelGarbageInfo;
        ArrayList arrayList = new ArrayList();
        try {
            HashMap hashMap = new HashMap();
            if(Build.VERSION.SDK_INT >= 26){
                return getTaskInfo26();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                return getTaskInfos24(mContext);
            }
            if (Build.VERSION.SDK_INT >= 20) {
                return getTaskInfos(mContext);
            }
//            UserUnCheckedData memoryUncheckedList = a.getInstance().getMemoryUncheckedList();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : this.mActivityManager.getRunningAppProcesses()) {
                int i2 = runningAppProcessInfo.pid;
                if (runningAppProcessInfo.uid > 10010 || !isSystemAppliation(runningAppProcessInfo.processName)) {
                    try {
                        String packNameByProName = getPackNameByProName(runningAppProcessInfo.processName);
                        int indexOf = packNameByProName.indexOf(COLON_SEPARATOR);
                        if (indexOf > 0) {
                            str = packNameByProName.substring(0, indexOf);
                        } else {
                            str = packNameByProName;
                        }
                        if (str != null) {
                            CharSequence loadLabel = this.mPackageManager.getPackageInfo(str, 0).applicationInfo.loadLabel(this.mPackageManager);
                            if (runningAppProcessInfo.importance >= 200 && !str.equals(this.mContext.getPackageName()) && !str.contains("com.xiaoniu")) {

                                mScanFileListener.scanFile(str);
                                long totalPss = (long) (this.mActivityManager.getProcessMemoryInfo(new int[]{i2})[0].getTotalPss() * 1024);
                                if (hashMap.containsKey(str)) {
                                    FirstJunkInfo onelevelGarbageInfo2 = (FirstJunkInfo) hashMap.get(str);
                                    onelevelGarbageInfo2.setTotalSize(onelevelGarbageInfo2.getTotalSize() + totalPss);
                                    onelevelGarbageInfo = onelevelGarbageInfo2;
                                } else {
                                    FirstJunkInfo onelevelGarbageInfo3 = new FirstJunkInfo();
                                    onelevelGarbageInfo3.setAppGarbageName(loadLabel.toString().trim());
                                    onelevelGarbageInfo3.setAppPackageName(str);
                                    onelevelGarbageInfo3.setGarbageType("TYPE_PROCESS");
                                    onelevelGarbageInfo3.setTotalSize(totalPss);
                                    boolean isIgore = false;
                                    if (mMemoryCacheWhite != null && mMemoryCacheWhite.size() > 0) {
                                        for (String equals : mMemoryCacheWhite) {
                                            if (equals.equals(str)) {
                                                isIgore = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (isIgore) {
                                        continue;
                                    }
                                    hashMap.put(str, onelevelGarbageInfo3);
                                    onelevelGarbageInfo = onelevelGarbageInfo3;
                                }
                                onelevelGarbageInfo.setPid(i2);
                                onelevelGarbageInfo.setAllchecked(true);
                                onelevelGarbageInfo.setAppName(loadLabel.toString().trim());
                                onelevelGarbageInfo.setGarbageIcon(getAppIcon(mPackageManager.getPackageInfo(str, 0).applicationInfo));
                                PackageInfo packageInfo = mPackageManager.getPackageInfo(str, 0);
//                                if (!(memoryUncheckedList == null || memoryUncheckedList.getList() == null || memoryUncheckedList.getList().size() <= 0)) {
//                                    for (String equals : memoryUncheckedList.getList()) {
//                                        if (str.equals(equals)) {
//                                            onelevelGarbageInfo.setAllchecked(false);
//                                        }
//                                    }
//                                }
                                if (mScanFileListener != null) {
                                    mScanFileListener.increaseSize(totalPss);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (Object obj : hashMap.keySet()) {
                FirstJunkInfo onelevelGarbageInfo4 = (FirstJunkInfo) hashMap.get(obj);
                if (onelevelGarbageInfo4.getTotalSize() != 0) {
                    arrayList.add(onelevelGarbageInfo4);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            return arrayList;
        }
    }

    @TargetApi(22)
    private ArrayList<FirstJunkInfo> getTaskInfo26() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }

        ArrayList<FirstJunkInfo> junkList = new ArrayList<>();
        List<UsageStats> lists = usageStatsManager.queryUsageStats(4, System.currentTimeMillis() - 86400000, System.currentTimeMillis());
        if (!(lists == null || lists.size() == 0)) {
            for (UsageStats usageStats : lists) {
                mScanFileListener.scanFile(usageStats.getPackageName());
                if (!(usageStats.getPackageName() == null || usageStats.getPackageName().contains("com.xiaoniu"))) {
                    String packageName = usageStats.getPackageName();
                    if (!isSystemAppliation(packageName)) {
                        List<PackageInfo> installedList = getInstalledList();
                        for (PackageInfo packageInfo : installedList) {
                            if (TextUtils.equals(packageName.trim(), packageInfo.packageName)) {
                                FirstJunkInfo junkInfo = new FirstJunkInfo();
                                junkInfo.setAllchecked(true);
                                junkInfo.setAppName(getAppName(packageInfo.applicationInfo));
                                junkInfo.setGarbageIcon(getAppIcon(packageInfo.applicationInfo));
                                junkInfo.setTotalSize((long)(new Random().nextInt() + 62914560));
                                junkInfo.setGarbageType("TYPE_PROCESS");
                                junkList.add(junkInfo);
                            }
                        }
                    }
                }
            }
        }
        return junkList;
    }

    public ArrayList<FirstJunkInfo> getTaskInfos(Context context) {
        int i2;
        try {
//            UserUnCheckedData memoryUncheckedList = a.getInstance().getMemoryUncheckedList();
            ArrayList arrayList = new ArrayList();
            List runningAppProcesses = getRunningAppProcesses();
            if (runningAppProcesses.isEmpty() || runningAppProcesses.size() == 0) {
                return null;
            }
            int i3 = 0;
            while (i3 < runningAppProcesses.size()) {
                if (((AndroidAppProcess) runningAppProcesses.get(i3)).uid <= 10010 || isSystemAppliation(((AndroidAppProcess) runningAppProcesses.get(i3)).getPackageName())) {
                    runningAppProcesses.remove(i3);
                    i2 = i3 - 1;
                } else {
                    i2 = i3;
                }
                i3 = i2 + 1;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i4 = 0; i4 < runningAppProcesses.size(); i4++) {
                int indexOf = ((AndroidAppProcess) runningAppProcesses.get(i4)).name.indexOf(COLON_SEPARATOR);
                AppMemoryInfo appMemoryInfo = new AppMemoryInfo();
                if (indexOf > 0) {
                    appMemoryInfo.setName(((AndroidAppProcess) runningAppProcesses.get(i4)).name.substring(0, indexOf));
                } else {
                    appMemoryInfo.setName(((AndroidAppProcess) runningAppProcesses.get(i4)).name);
                }
                appMemoryInfo.setId(((AndroidAppProcess) runningAppProcesses.get(i4)).pid);
                appMemoryInfo.setUid(((AndroidAppProcess) runningAppProcesses.get(i4)).uid);
                arrayList2.add(appMemoryInfo);
            }
            for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                for (int size = arrayList2.size() - 1; size > i5; size--) {
                    if (((AppMemoryInfo) arrayList2.get(i5)).getName().equals(((AppMemoryInfo) arrayList2.get(size)).getName())) {
                        arrayList2.remove(size);
                    }
                }
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                AppMemoryInfo appMemoryInfo2 = (AppMemoryInfo) it.next();
                ApplicationInfo applicationInfo = getApplicationInfo(appMemoryInfo2.getName());
                mScanFileListener.scanFile(appMemoryInfo2.getName());
                if (applicationInfo != null && !appMemoryInfo2.getName().contains("com.xiaoniu")) {
                    long totalPss = ((long) mActivityManager.getProcessMemoryInfo(new int[]{appMemoryInfo2.getId()})[0].getTotalPss()) * 1024;
                    if (totalPss != 0) {
                        FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                        onelevelGarbageInfo.setGarbageType("TYPE_PROCESS");
                        onelevelGarbageInfo.setAppPackageName(applicationInfo.packageName);
                        onelevelGarbageInfo.setAllchecked(true);
                        boolean isIgore = false;
                        if (mMemoryCacheWhite != null && mMemoryCacheWhite.size() > 0) {
                            for (String equals : mMemoryCacheWhite) {
                                if (equals.equals(applicationInfo.packageName)) {
                                    isIgore = true;
                                    break;
                                }
                            }
                        }
                        if (isIgore) {
                            continue;
                        }
                        onelevelGarbageInfo.setAppGarbageName(applicationInfo.loadLabel(mPackageManager).toString());
                        onelevelGarbageInfo.setPid(appMemoryInfo2.getId());
                        onelevelGarbageInfo.setTotalSize(totalPss);
                        onelevelGarbageInfo.setAppName(getAppName(applicationInfo));
                        onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
                        if (mScanFileListener != null) {
                            mScanFileListener.increaseSize(totalPss);
                        }
                        arrayList.add(onelevelGarbageInfo);
                    }
                }
            }
            return arrayList;
        } catch (Exception e) {
            return null;
        }
    }

    @TargetApi(19)
    public ArrayList<FirstJunkInfo> getTaskInfos24(Context context) {
        AndroidProcesses.setLoggingEnabled(true);
        int i2;
        try {
//            UserUnCheckedData memoryUncheckedList = a.getInstance().getMemoryUncheckedList();
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            List runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
            int i3 = 0;
            while (i3 < runningServices.size()) {
                if (((ActivityManager.RunningServiceInfo) runningServices.get(i3)).uid <= 10010 || (((ActivityManager.RunningServiceInfo) runningServices.get(i3)).flags & 1) != 0) {
                    //系统应用 排除
                    runningServices.remove(i3);
                    i2 = i3 - 1;
                } else {
                    i2 = i3;
                }
                i3 = i2 + 1;
            }
            ArrayList arrayList = new ArrayList();
            for (int i4 = 0; i4 < runningServices.size(); i4++) {
                int indexOf = ((ActivityManager.RunningServiceInfo) runningServices.get(i4)).service.getPackageName().indexOf(COLON_SEPARATOR);
                AppMemoryInfo appMemoryInfo = new AppMemoryInfo();
                if (indexOf > 0) {
                    appMemoryInfo.setName(((ActivityManager.RunningServiceInfo) runningServices.get(i4)).service.getPackageName().substring(0, indexOf));
                } else {
                    appMemoryInfo.setName(((ActivityManager.RunningServiceInfo) runningServices.get(i4)).service.getPackageName());
                }
                appMemoryInfo.setId(((ActivityManager.RunningServiceInfo) runningServices.get(i4)).pid);
                appMemoryInfo.setUid(((ActivityManager.RunningServiceInfo) runningServices.get(i4)).uid);
                arrayList.add(appMemoryInfo);
            }
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                for (int size = arrayList.size() - 1; size > i5; size--) {
                    if (((AppMemoryInfo) arrayList.get(i5)).getName().equals(((AppMemoryInfo) arrayList.get(size)).getName())) {
                        arrayList.remove(size);
                    }
                }
            }
            PackageManager packageManager = context.getPackageManager();
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                AppMemoryInfo appMemoryInfo2 = (AppMemoryInfo) it.next();
                FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                String name = appMemoryInfo2.getName();
                mScanFileListener.scanFile(name);
                if (name != null && !name.contains("com.xiaoniu")) {
                    long totalPss = ((long) activityManager.getProcessMemoryInfo(new int[]{appMemoryInfo2.getId()})[0].getTotalPss()) * 1024;
                    if (totalPss != 0) {
                        onelevelGarbageInfo.setAllchecked(true);
                        onelevelGarbageInfo.setAppPackageName(name);
                        onelevelGarbageInfo.setPid(appMemoryInfo2.getId());
                        onelevelGarbageInfo.setTotalSize(totalPss);
                        onelevelGarbageInfo.setAppGarbageName(packageManager.getApplicationInfo(name, 0).loadLabel(packageManager).toString());
                        onelevelGarbageInfo.setGarbageType("TYPE_PROCESS");
                        onelevelGarbageInfo.setAppPackageName(name);
                        onelevelGarbageInfo.setAppName(getAppName(packageManager.getApplicationInfo(name, 0)));
                        onelevelGarbageInfo.setGarbageIcon(getAppIcon(packageManager.getApplicationInfo(name, 0)));
//                        onelevelGarbageInfo.setDescp(CleanAppApplication.getInstance().getString(R.string.clean_suggested));
                        boolean isIgore = false;
                        if (mMemoryCacheWhite != null && mMemoryCacheWhite.size() > 0) {
                            for (String equals : mMemoryCacheWhite) {
                                if (name.equals(equals)) {
                                    isIgore = true;
                                    break;
                                }
                            }
                        }
                        if (isIgore) {
                            continue;
                        }
                        if (mScanFileListener != null) {
                            mScanFileListener.increaseSize(totalPss);
                        }
                        arrayList2.add(onelevelGarbageInfo);
                    }
                }
            }
            return arrayList2;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSystemApp(Context context,String packageName) {
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

    /**
     * 查询apk文件
     *
     * @return
     */
    public List<FirstJunkInfo> queryAPkFile() {
        Cursor cursor;
        String absolutePath;
        try {
            ArrayList arrayList = new ArrayList();
            String[] strArr = {"_data", "_size"};
            String str = "content://media/external/file";
            String[] strArr2 = {MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")};
            Cursor query = AppApplication.getInstance().getContentResolver().query(Uri.parse(str), strArr, "mime_type= ?", strArr2, null);
            if (query != null) {
                if (!query.moveToFirst()) {
                    cursor = AppApplication.getInstance().getContentResolver().query(Uri.parse(str), strArr, "_data like ?", new String[]{"%.apk%"}, null);
                } else {
                    cursor = query;
                }
                if (cursor.moveToFirst()) {
                    while (true) {
                        String string = cursor.getString(0);
                        long j = cursor.getLong(1);
                        if (new File(string).exists() && j != 0) {
                            FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                            onelevelGarbageInfo.setAllchecked(false);
                            onelevelGarbageInfo.setGarbageType("TYPE_APK");
                            onelevelGarbageInfo.setTotalSize(j);
                            onelevelGarbageInfo.setGarbageCatalog(string);
                            if (FileUtils.haveSDCard()) {
                                absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            } else {
                                absolutePath = AppApplication.getInstance().getFilesDir().getAbsolutePath();
                            }
                            if (string.contains(absolutePath) || string.contains("sdcard0") || string.contains("sdcard1")) {
                                PackageInfo packageArchiveInfo = mPackageManager.getPackageArchiveInfo(string, 1);
                                if (packageArchiveInfo != null) {
                                    ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                    applicationInfo.sourceDir = string;
                                    applicationInfo.publicSourceDir = string;
                                    onelevelGarbageInfo.setAppPackageName(packageArchiveInfo.packageName);
                                    onelevelGarbageInfo.setVersionName(packageArchiveInfo.versionName);
                                    onelevelGarbageInfo.setVersionCode(packageArchiveInfo.versionCode);
                                    onelevelGarbageInfo.setAppGarbageName(mPackageManager.getApplicationLabel(packageArchiveInfo.applicationInfo).toString());
                                    onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
                                    onelevelGarbageInfo.setAppName(getAppName(applicationInfo));
                                    if (mScanFileListener != null) {
                                        mScanFileListener.increaseSize(j);
                                    }
                                    if (FileUtils.isAppInstalled(packageArchiveInfo.packageName)) {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_install));
                                        onelevelGarbageInfo.setApkInstalled(true);
                                        onelevelGarbageInfo.setAllchecked(true);
                                    } else {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_uninstall));
                                        onelevelGarbageInfo.setApkInstalled(false);
                                        onelevelGarbageInfo.setAllchecked(true);
                                    }
                                } else {
                                    onelevelGarbageInfo.setAppGarbageName(string.substring(string.lastIndexOf("/") + 1));
                                    onelevelGarbageInfo.setAppName(string.substring(string.lastIndexOf("/") + 1));
                                    if (onelevelGarbageInfo.getAppGarbageName().contains(".apk") && !onelevelGarbageInfo.getGarbageCatalog().contains(".apk.")) {
                                        onelevelGarbageInfo.setAllchecked(true);
                                        if (mScanFileListener != null) {
                                            mScanFileListener.increaseSize(j);
                                        }
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_damage));
                                    }
                                }
                                arrayList.add(onelevelGarbageInfo);
                            }
                        }
                        if (!cursor.moveToNext()) {
                            break;
                        }
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
            return arrayList;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<FirstJunkInfo> getAppCacheAndAdGarbage(ArrayList<FirstJunkInfo> list) {

//        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();

        List<AppInfoClean> appInfoCleans = CleanDBManager.queryInfoList();

        if (appInfoCleans == null) {
            return null;
        }

        for (AppInfoClean appInfoClean : appInfoCleans) {

            if (FileUtils.isAppInstalled(appInfoClean.getPackageName())) {
                try {
                    FirstJunkInfo onelevelGarbageInfo = isContain(list, appInfoClean.getPackageName());
                    if (onelevelGarbageInfo == null) {
                        onelevelGarbageInfo = new FirstJunkInfo();
                    }
                    onelevelGarbageInfo.setAllchecked(true);
                    onelevelGarbageInfo.setAppName(appInfoClean.getAppName());
                    onelevelGarbageInfo.setGarbageIcon(getAppIcon(mPackageManager.getApplicationInfo(appInfoClean.getPackageName(), 0)));
                    onelevelGarbageInfo.setAppPackageName(appInfoClean.getPackageName());
                    onelevelGarbageInfo.setGarbageType("TYPE_CACHE");
                    onelevelGarbageInfo.setAppGarbageName(appInfoClean.getAppName());
                    onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_suggested));
//                    onelevelGarbageInfo2.setGarbageType(GarbageType.TYPE_AD);
//                    if ("unknow".equals(appInfoClean.getPackageName())) {
//                        onelevelGarbageInfo2.setAppGarbageName("其他广告垃圾");
//                    } else {
//                        onelevelGarbageInfo2.setAppGarbageName(appInfoClean.getAppName() + this.mContext.getString(R.string.clean_ad_name));
//                    }
                    List<FilePathInfoClean> filePathInfoCleans = CleanDBManager.queryFilePathInfo(appInfoClean.getPackageName());
                    if (filePathInfoCleans == null) {
                        break;
                    }
                    for (FilePathInfoClean filePathInfoClean : filePathInfoCleans) {

                        String filePath = filePathInfoClean.getFilePath();
                        filePath = getFilePath(filePath);
                        if (TextUtils.isEmpty(filePath)) {
                            filePath = null;
                        }
                        filePathInfoClean.setFilePath(filePath);
                        File file = new File(Environment.getExternalStorageDirectory() + filePathInfoClean.getFilePath());
                        if (!file.exists()) {
                            if (file.isDirectory()) {
                                SecondJunkInfo listFiles = listFiles(file);
                                if (!(listFiles.getFilesCount() == 0 || listFiles.getGarbageSize() == 0)) {
                                    listFiles.setFilecatalog(file.getPath());
                                    listFiles.setChecked(true);
                                    listFiles.setGarbageName("TYPE_CACHE");
                                    listFiles.setAppGarbageName(appInfoClean.getAppName());
                                    onelevelGarbageInfo.setTotalSize(listFiles.getGarbageSize() + onelevelGarbageInfo.getTotalSize());
                                    onelevelGarbageInfo.addSecondJunk(listFiles);
                                    if (mScanFileListener != null) {
                                        mScanFileListener.increaseSize(listFiles.getGarbageSize());
                                    }
                                }
                            }
                        } else if (file.isDirectory()) {
                            SecondJunkInfo listFiles2 = listFiles(file);
                            if (!(listFiles2.getFilesCount() == 0 || listFiles2.getGarbageSize() == 0)) {
                                listFiles2.setFilecatalog(file.getPath());
                                listFiles2.setChecked(true);
                                listFiles2.setGarbageName(filePathInfoClean.getGarbageName());
                                listFiles2.setAppGarbageName(appInfoClean.getAppName());
                                onelevelGarbageInfo.setTotalSize(listFiles2.getGarbageSize() + onelevelGarbageInfo.getTotalSize());
                                onelevelGarbageInfo.addSecondJunk(listFiles2);
                                if (mScanFileListener != null) {
                                    mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                                }
                            }
                        }
                    }
                    if (onelevelGarbageInfo.getSubGarbages() != null && onelevelGarbageInfo.getSubGarbages().size() > 0) {
                        arrayList2.add(onelevelGarbageInfo);
                    }
                } catch (Exception e2) {

                }
            }
//           else {
//                OnelevelGarbageInfo onelevelGarbageInfo3 = new OnelevelGarbageInfo();
//                onelevelGarbageInfo3.setAllchecked(true);
//                onelevelGarbageInfo3.setGarbagetype(GarbageType.TYPE_REMAIN_DATA);
//                onelevelGarbageInfo3.setAppPackageName(appInfoClean.getPackageName());
//                onelevelGarbageInfo3.setAppGarbageName(appInfoClean.getAppName() + this.mContext.getString(R.string.clean_garb_name));
//                onelevelGarbageInfo3.setDescp(this.mContext.getString(R.string.clean_suggested));
//                WhereBuilder b2 = WhereBuilder.b();
//                b2.and(com.taobao.accs.common.Constants.KEY_PACKAGE_NAME, "=", appInfoClean.getPackageName());
//                for (FilePathInfoClean filePathInfoClean2 : this.db.selector(FilePathInfoClean.class).where(b2).findAll()) {
//                    if (FragmentViewPagerMainActivity.d) {
//                        break;
//                    }
//                    String filePath2 = filePathInfoClean2.getFilePath();
//                    try {
//                        if (!this.db.getDaoConfig().getDbName().equals("clean_db.db")) {
//                            filePath2 = SimpleCryp.base64Decrypt(CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key9).toUpperCase() + CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key12).toUpperCase() + CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key11).toUpperCase(), filePath2);
//                        } else if (TextUtil.isEmpty((CharSequence) AppUtil.getCleanFilePathVersion())) {
//                            filePath2 = SimpleCryp.decrypt("acan520", filePath2);
//                        } else {
//                            filePath2 = SimpleCryp.base64Decrypt(CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key9).toUpperCase() + CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key12).toUpperCase() + CleanAppApplication.getInstance().getString(R.string.clean_db_encrypt_key3).toUpperCase(), filePath2);
//                        }
//                    } catch (Exception e3) {
//                    }
//                    if (TextUtil.isEmpty((CharSequence) filePath2)) {
//                        filePath2 = null;
//                    }
//                    filePathInfoClean2.setFilePath(filePath2);
//                    if (scanFileListener != null) {
//                        scanFileListener.scanFile(filePathInfoClean2.getFilePath());
//                    }
//                    File file2 = new File(Environment.getExternalStorageDirectory() + filePathInfoClean2.getFilePath());
//                    if (file2.isDirectory()) {
//                        SecondlevelGarbageInfo listFiles3 = FileUtils.listFiles(file2, false);
//                        if (!(listFiles3.getFilesCount() == 0 || listFiles3.getGarbageSize() == 0)) {
//                            listFiles3.setFilecatalog(file2.getPath());
//                            listFiles3.setChecked(true);
//                            listFiles3.setGarbageName(filePathInfoClean2.getGarbageName());
//                            listFiles3.setAppGarbageName(appInfoClean.getAppName());
//                            onelevelGarbageInfo3.setTotalSize(listFiles3.getGarbageSize() + onelevelGarbageInfo3.getTotalSize());
//                            onelevelGarbageInfo3.addSecondGabage(listFiles3);
//                            if (i == 0 && scanFileListener != null) {
//                                scanFileListener.increaseSize(listFiles3.getGarbageSize());
//                            }
//                        }
//                    }
//                }
//                if (onelevelGarbageInfo3.getTotalSize() > 0) {
//                    Logger.d(Logger.TAG, "acan", "----残留卸载应用-----" + onelevelGarbageInfo3.getAppGarbageName() + " --type-- " + onelevelGarbageInfo3.getGarbagetype());
//                    arrayList.add(onelevelGarbageInfo3);
//                }
//            }
        }
        return arrayList2;
    }

    private FirstJunkInfo isContain(ArrayList<FirstJunkInfo> list, String packageName) {
        FirstJunkInfo junkInfoTemp = null;
        for (FirstJunkInfo junkInfo : list) {
            if (packageName.equals(junkInfo.getAppPackageName())) {
                junkInfoTemp = junkInfo;
            }
        }
        list.remove(junkInfoTemp);
        return junkInfoTemp;
    }

    private String getPackNameByProName(String str) {
        if (str.contains(COLON_SEPARATOR)) {
            return str.subSequence(0, str.indexOf(COLON_SEPARATOR)).toString();
        }
        return str;
    }

    public static boolean isSystemAppliation() {
        return (AppApplication.getInstance().getApplicationInfo().flags & 1) != 0;
    }

    public boolean isSystemAppliation(String packageName) {
        try {
            return (mPackageManager.getPackageInfo(packageName, 0).applicationInfo.flags & 1) != 0;
        } catch (Exception e) {

        }
        return false;
    }


    private Map<String, String> checkOutAllGarbageFolder(final File file) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        this.checkFiles(hashMap, file);
        return hashMap;
    }

    private void checkFiles(final Map<String, String> map, final File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    if (file2.getName().toLowerCase().equals("awcn_strategy")) {
                        map.put(file2.getAbsolutePath(), "缓存");
                    } else if (file2.getName().toLowerCase().equals("baidu")) {
                        map.put(file2.getAbsolutePath(), "插件缓存");
                    } else if (file2.getName().toLowerCase().equals("ad")) {
                        map.put(file2.getAbsolutePath(), "广告文件");
                    } else if (file2.getName().toLowerCase().contains("log")) {
                        map.put(file2.getAbsolutePath(), "日志文件");
                    } else if (file2.getName().toLowerCase().contains("cache")) {
                        map.put(file2.getAbsolutePath(), "缓存");
                    } else if (file2.getName().toLowerCase().contains(".cloud")) {
                        map.put(file2.getAbsolutePath(), "缓存");
                    } else if (file2.getName().toLowerCase().contains("update")) {
                        map.put(file2.getAbsolutePath(), "更新缓存");
                    } else if (file2.getName().toLowerCase().equals("apps")) {
                        map.put(file2.getAbsolutePath(), "页面缓存");
                    } else {
                        this.checkFiles(map, file2);
                    }
                }
            }
        }
    }

    /**
     * 遍历文件夹
     *
     * @param file
     * @return
     */
    public SecondJunkInfo listFiles(final File file) {
        final SecondJunkInfo secondlevelGarbageInfo = new SecondJunkInfo();
        innerListFiles(secondlevelGarbageInfo, file);
        return secondlevelGarbageInfo;
    }

    public void innerListFiles(final SecondJunkInfo secondJunkInfo, File file) {
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        try {
            if (listFiles.length == 0) {
                file.delete();
                return;
            }
        } catch (Exception ex) {
        }
        for (int length = listFiles.length, i = 0; i < length; ++i) {
            file = listFiles[i];
            if (file.isDirectory()) {
                innerListFiles(secondJunkInfo, file);
            } else {
                secondJunkInfo.setFilesCount(secondJunkInfo.getFilesCount() + 1);
                secondJunkInfo.setGarbageSize(secondJunkInfo.getGarbageSize() + file.length());
            }
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(File dir) {
        File dirFile = dir;
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]);
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    public static String getFilePath(String s2) {
        try {
            String s = "225e8C70688fD76Ec5C01A392302320A".toUpperCase();
            SecretKeySpec secretKeySpec = new SecretKeySpec(s.getBytes("utf-8"), "AES");
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            instance.init(2, secretKeySpec);
            final byte[] decode = Base64.decode(s2, 0);

            return new String(instance.doFinal(decode), "utf-8");
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获取缓存白名单
     */
    public Set<String> getCacheWhite() {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        return sets;
    }

    public interface ScanFileListener {
        void currentNumber();

        void increaseSize(final long p0);

        void reduceSize(final long p0);

        void scanFile(final String p0);

        void totalSize(final int p0);
    }
}
