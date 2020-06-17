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
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jess.arms.base.delegate.AppDelegate;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.bean.path.AppPath;
import com.xiaoniu.cleanking.bean.path.UninstallList;
import com.xiaoniu.cleanking.bean.path.UselessApk;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoClean;
import com.xiaoniu.cleanking.ui.main.bean.AppMemoryInfo;
import com.xiaoniu.cleanking.ui.main.bean.FilePathInfoClean;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.OtherJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.PathData;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.utils.db.CleanDBManager;
import com.xiaoniu.cleanking.utils.encypt.Base64;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.ContextUtils;
import com.xiaoniu.common.utils.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;

import static android.content.Context.USAGE_STATS_SERVICE;
import static com.jaredrummler.android.processes.AndroidProcesses.getRunningAppProcesses;
import static com.xiaoniu.cleanking.app.Constant.WHITE_LIST;

public class FileQueryUtils {

    private static final String COLON_SEPARATOR = ":";
    private final Set<String> mMemoryCacheWhite;
    private AppApplication mContext;
    public static List<ApplicationInfo> installedAppList;
    public static List<PackageInfo> installedPackages;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private ScanFileListener mScanFileListener;
    HashMap<String, PathData> pathMap;//json定义的关联扫描文件
    HashMap<String, FirstJunkInfo> outJunkMap; //根目录筛选出来的文件，文件名key
    List<PackageInfo> installedList; //已经安装的应用信息
    private boolean isService = false;
    private boolean isRandRamNum = false;// 随机展示

    /**
     * 是否终止标识符， 如果停止，结束查询数据
     */
    private boolean isFinish = false;

    public FileQueryUtils() {
        this.mContext = AppApplication.getInstance();
        this.mActivityManager = (ActivityManager) AppApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        this.mPackageManager = AppApplication.getInstance().getPackageManager();
        mMemoryCacheWhite = getCacheWhite();
        pathMap = FilePathUtil.getPathMap(mContext);
        if (this.mPackageManager != null) {
            try {
                installedAppList = this.mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }




    public void setRandRamNum(boolean randRamNum) {
        isRandRamNum = randRamNum;
    }

    public void setIsService(boolean isServiceScan) {
        isService = isServiceScan;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setScanFileListener(ScanFileListener scanFileListener) {
        mScanFileListener = scanFileListener;
    }

    /**
     * 获取已安装包的信息
     *
     * @return
     */
    public static List<PackageInfo> getInstalledList() {
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        installedPackages = pm.getInstalledPackages(0);
        return installedPackages;
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return applicationInfo.loadIcon(mContext.getPackageManager());
        }
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        return pm.getApplicationIcon(applicationInfo);
    }


    /**
     * 获取App图标
     *
     * @param applicationInfo
     * @return
     */
    private int getAppIconSource(ApplicationInfo applicationInfo) {
        return applicationInfo.icon;
    }

    //其他垃圾
    public JunkGroup getOtherCache() {
        JunkGroup otherGroup = new JunkGroup();
        otherGroup.mName = ContextUtils.getContext().getString(R.string.other_clean);
        otherGroup.isChecked = true;
        otherGroup.isExpand = true;
        otherGroup.mChildren = new ArrayList<>();
        otherGroup.otherChildren = new ArrayList<>();
        otherGroup.mSize += 0;

        ArrayList<OtherJunkInfo> otherList = new ArrayList<>();
        File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        Map<String, String> otherPathMap = FileUtils.otherAllkFiles(outFile);
        for (final Map.Entry<String, String> entry : otherPathMap.entrySet()) {
            if (new File(entry.getKey()).isFile()) { //文件路径
                File cachefile = new File(entry.getKey());
                if (cachefile.exists()) {
                    OtherJunkInfo otherJunkInfo = new OtherJunkInfo();
                    otherJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                    otherJunkInfo.setChecked(true);
                    otherJunkInfo.setPackageName("other");
                    otherJunkInfo.setGarbagetype("TYPE_OTHER");
                    otherJunkInfo.setGarbageSize(cachefile.length());

                    otherList.add(otherJunkInfo);

                    otherGroup.mSize += otherJunkInfo.getGarbageSize();
                    if (mScanFileListener != null) {
                        mScanFileListener.increaseSize(cachefile.length());
                    }
                }
            }
        }
        otherGroup.otherChildren.addAll(otherList);
        return otherGroup;
    }

    /**
     * 残留文件扫描
     * 锁定文件夹下所有子文件
     */
    public ArrayList<FirstJunkInfo> getOmiteCache() {
        ArrayList<FirstJunkInfo> junkInfoArrayList = new ArrayList<>();
        //已安裝应用map
        HashMap<String, PackageInfo> packMap = new HashMap<>();
        if (installedList == null)
            installedList = getInstalledList();
        if (installedList != null && installedList.size() > 0) {
            for (PackageInfo installList : installedList) {
                packMap.put(installList.packageName, installList);
            }
        }
        if (pathMap == null) {
            return junkInfoArrayList;
        }

        //开始扫描uninstallList_db文件中路径
        //包名去重列表
        List<String> packNameList = ApplicationDelegate.getAppPathDatabase().uninstallListDao().getUninstallList();
//        LinkedHashMap<String,List<UninstallList>> leavedCache = new LinkedHashMap<>();
        for (String packageName : packNameList) {
            if (packMap.containsKey(packageName.trim())) {//排除当前已安装应用
                continue;
            }
            if (TextUtils.isEmpty(packageName)) {
                continue;
            }
            LogUtils.i("getOmiteCache()-packageName-"+packageName);
            //根据包名筛选的——未安装应用路径列表
            List<UninstallList> uninstallLists = ApplicationDelegate.getAppPathDatabase().uninstallListDao().getPathList(packageName);
            if (!CollectionUtils.isEmpty(uninstallLists)) {
                FirstJunkInfo junkInfo = new FirstJunkInfo();
                junkInfo.setAllchecked(true);
                junkInfo.setAppName(uninstallLists.get(0).getNameZh());
                junkInfo.setAppPackageName(uninstallLists.get(0).getPackageName());
                if (!isService) {
                    junkInfo.setGarbageIcon(mContext.getResources().getDrawable(R.drawable.icon_other_cache));
                }
                junkInfo.setGarbageType("TYPE_LEAVED");

                for (UninstallList pathObj : uninstallLists) {
                    String filePath = AESUtils01.decrypt(pathObj.getFilePath());
                    if (TextUtils.isEmpty(filePath)) {
                        continue;
                    }
                    File scanExtFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filePath);
//                    LogUtils.i("getOmiteCache()-scanExtFile-"+scanExtFile.getAbsolutePath());
                    Map<String, String> filePathMap = checkAllGarbageFolder(scanExtFile);

                    for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                        if (new File(entry.getKey()).isFile()) { //文件路径
                            File cachefile = new File(entry.getKey());
//                            LogUtils.i("getOmiteCache()-scanExtFile-"+cachefile.getAbsolutePath());
                            SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                            if (cachefile.exists()) {
                                secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                                secondJunkInfo.setChecked(true);
                                secondJunkInfo.setPackageName(scanExtFile.getName());
                                secondJunkInfo.setGarbagetype("TYPE_LEAVED");
                                secondJunkInfo.setGarbageSize(cachefile.length());
                                junkInfo.setTotalSize(junkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                                junkInfo.addSecondJunk(secondJunkInfo);
                                if (mScanFileListener != null) {
                                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                                }
                            }
                        }
                    }
                }
                if (junkInfo.getSubGarbages() == null || junkInfo.getSubGarbages().size() <= 0) {
                    continue;
                }
                junkInfoArrayList.add(junkInfo);
            }
        }

        return junkInfoArrayList;

    }

    /**
     * 根据/Android/data/私有目录中的垃圾信息获取 ExternalStorage下匹配缓存文件
     *
     * @param firstJunkInfos
     * @return
     */
    public ArrayList<FirstJunkInfo> getExternalStorageCache(ArrayList<FirstJunkInfo> firstJunkInfos) {
        HashMap<String, FirstJunkInfo> junkInfoMap = new HashMap<String, FirstJunkInfo>();
        ArrayList<FirstJunkInfo> externalList = new ArrayList<>();
        externalList.addAll(firstJunkInfos);

        for (FirstJunkInfo firstJunkInfo : externalList) {
            if (pathMap.containsKey(firstJunkInfo.getAppPackageName())) { //关联路径包含此包
                //todo 暂时默认一个文件夹扫描
                firstJunkInfo.setSdPath(pathMap.get(firstJunkInfo.getAppPackageName()).getFileList().get(0).getFolderName());
            }
            junkInfoMap.put(firstJunkInfo.getAppPackageName(), firstJunkInfo);
        }

        //外部存储共有路径（sd根目录）
        String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File externalFile = new File(externalPath);
        File[] listFiles = externalFile.listFiles();
        outJunkMap = new HashMap<>();//sd根目录需要扫描的文件
        if (listFiles != null) {
            for (File file : listFiles) {
                if (junkInfoMap.containsKey(file.getName()) || checkHasPath(junkInfoMap, file.getName()) != null) { //sd根目录下包含packName || //该file 是package外部关联路径

                    FirstJunkInfo firstJunkInfo = junkInfoMap.containsKey(file.getName()) ? junkInfoMap.get(file.getName()) : checkHasPath(junkInfoMap, file.getName());
                    if (firstJunkInfo == null)
                        continue;

                    outJunkMap.put(firstJunkInfo.getAppPackageName(), firstJunkInfo);
                    if (mScanFileListener != null) {
                        mScanFileListener.scanFile(firstJunkInfo.getAppPackageName());
                    }
                    Map<String, String> filePathMap = this.checkOutAllGarbageFolder(file, file.getName());
                    for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                        if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
                            final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
                            if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
                                continue;
                            }
                            listFiles2.setGarbageName(entry.getValue());
                            listFiles2.setFilecatalog(entry.getKey());
                            listFiles2.setChecked(true);
                            listFiles2.setPackageName(file.getName());
                            listFiles2.setGarbagetype("TYPE_CACHE");
                            firstJunkInfo.addSecondJunk(listFiles2);
                            firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
                            if (mScanFileListener != null) {
                                mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                            }
                        } else if (new File(entry.getKey()).isFile()) { //文件路径
                            File cachefile = new File(entry.getKey());
                            SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                            if (cachefile.exists()) {
                                secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                                secondJunkInfo.setChecked(true);
                                secondJunkInfo.setPackageName(file.getName());
                                secondJunkInfo.setGarbagetype("TYPE_CACHE");
                                secondJunkInfo.setGarbageSize(file.length());

                                firstJunkInfo.addSecondJunk(secondJunkInfo);
                                firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                                if (mScanFileListener != null) {
                                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                                }
                            }
                        }
                        if (firstJunkInfo.getSubGarbages() == null || firstJunkInfo.getSubGarbages().size() <= 0) {
                            continue;
                        }
                        //put更新后的juckInfo
                        junkInfoMap.put(firstJunkInfo.getAppPackageName(), firstJunkInfo);
                    }
                }
            }
        }
        ArrayList<FirstJunkInfo> junnkInfoList = new ArrayList<FirstJunkInfo>(junkInfoMap.values());
        return junnkInfoList;
    }

    //检测是否包含关联路径
    public FirstJunkInfo checkHasPath(HashMap<String, FirstJunkInfo> hashMap, String fileName) {
        for (final Map.Entry<String, FirstJunkInfo> entry : hashMap.entrySet()) {
            FirstJunkInfo firstJunkInfo = entry.getValue();
            if (!TextUtils.isEmpty(firstJunkInfo.getSdPath()) && firstJunkInfo.getSdPath().equals(fileName)) {
                return firstJunkInfo;
            }
            return null;
        }
        return null;
    }

    public interface OnScanJunkListener {

        /**
         * 缓存垃圾扫描结果
         */
        void scanCacheJunkResult(ArrayList<FirstJunkInfo> cacheJunkLists);

        /**
         * 广告垃圾扫描结果
         */
        void scanAdJunkResult(ArrayList<FirstJunkInfo> adJunkLists);
    }

    /**
     * 扫描文件垃圾，主要匹配缓存垃圾 和  广告垃圾
     */
    public HashMap<ScanningResultType, ArrayList<FirstJunkInfo>> getFileJunkResult() {
        //缓存垃圾列表
        ArrayList<FirstJunkInfo> cacheJunkListInfo = new ArrayList<>();
        //广告垃圾列表
        ArrayList<FirstJunkInfo> adJunkListInfo = new ArrayList<>();

        //外部存储私有存储父文件
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/";
        if (installedList == null) {
            installedList = getInstalledList();
        }

        //开始扫描关联文件夹目录
        for (PackageInfo applicationInfo : installedList) {
            //packageName文件下（私有目录）
            final File appPackageDir = new File(rootPath + applicationInfo.packageName);
            //缓存文件（私有目录）
            final File appCacheDir = new File(rootPath + applicationInfo.packageName + "/cache");
            //文件夹集合（私有目录）
            final File appFileDir = new File(rootPath + applicationInfo.packageName + "/files");

            //一级目（私有目录）
            FirstJunkInfo cacheJunkInfo = new FirstJunkInfo();
            cacheJunkInfo.setAppName(getAppName(applicationInfo.applicationInfo));
            cacheJunkInfo.setIsthreeLevel(true);  //三级文件目录
            cacheJunkInfo.setUncarefulIsChecked(true);
            if (!isService){
                cacheJunkInfo.setGarbageIcon(getAppIcon(applicationInfo.applicationInfo));
            }

            cacheJunkInfo.setAllchecked(true);
            cacheJunkInfo.setGarbageType("TYPE_CACHE");
            cacheJunkInfo.setAppPackageName(applicationInfo.packageName);

            FirstJunkInfo adJunkInfo = new FirstJunkInfo();
            adJunkInfo.setAppName(getAppName(applicationInfo.applicationInfo));
            if (!isService){
//                adJunkInfo.setIconSource(getAppIconSource(applicationInfo.applicationInfo));
                adJunkInfo.setGarbageIcon(getAppIcon(applicationInfo.applicationInfo));
            }
            adJunkInfo.setAllchecked(true);
            adJunkInfo.setGarbageType("TYPE_AD");
            adJunkInfo.setAppPackageName(applicationInfo.packageName);

            if (mScanFileListener != null) {
                mScanFileListener.scanFile(applicationInfo.packageName);
            }

    /*        //开始扫描Android/data/packageName/cache目录
            if (appCacheDir.exists()) {
                SecondJunkInfo secondJunkInfo = FileUtils.cacheListFiles(appCacheDir);
                if (secondJunkInfo.getFilesCount() > 0 && secondJunkInfo.getGarbageSize() > 0) {
                    secondJunkInfo.setFilecatalog(appCacheDir.getAbsolutePath());
                    secondJunkInfo.setChecked(true);
                    secondJunkInfo.setPackageName(applicationInfo.packageName);
                    secondJunkInfo.setGarbagetype("TYPE_CACHE");
                    cacheJunkInfo.addSecondJunk(secondJunkInfo);
                    cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                }
                if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                }
            }*/
/*
            //开始扫描Android/data/packageName/file目录
            if (appFileDir.exists()) {
                //路径集合
                Map<String, String> filePathMap = this.checkOutAllGarbageFolder(appFileDir, applicationInfo.packageName);
                for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                    if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
                        final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
                        if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
                            continue;
                        }
                        listFiles2.setGarbageName(entry.getValue());
                        listFiles2.setFilecatalog(entry.getKey());
                        listFiles2.setChecked(true);
                        listFiles2.setPackageName(applicationInfo.packageName);
                        listFiles2.setGarbagetype("TYPE_CACHE");
                        cacheJunkInfo.addSecondJunk(listFiles2);
                        cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
                        if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                            mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                        }
                    } else if (new File(entry.getKey()).isFile()) { //文件路径
                        File cachefile = new File(entry.getKey());
                        SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                        if (cachefile.exists()) {
                            secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                            secondJunkInfo.setChecked(true);
                            secondJunkInfo.setPackageName(applicationInfo.packageName);
                            secondJunkInfo.setGarbagetype("TYPE_CACHE");
                            secondJunkInfo.setGarbageSize(cachefile.length());

                            cacheJunkInfo.addSecondJunk(secondJunkInfo);
                            cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                            if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                                mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                            }
                        }
                    }
                }
            }*/

//            //开始扫描Android/data/packageName/目录
//            if (appPackageDir.exists()) {
//                //路径集合_排除指定路径
//                Map<String, String> filePathMap = this.checkOutAllGarbageFolder(appPackageDir, applicationInfo.packageName, "cache", "files");
//                for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
//                    if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
//                        final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
//                        if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
//                            continue;
//                        }
//                        listFiles2.setGarbageName(entry.getValue());
//                        listFiles2.setFilecatalog(entry.getKey());
//                        listFiles2.setChecked(true);
//                        listFiles2.setPackageName(applicationInfo.packageName);
//
//                        //根据文件夹类型名称判断区分广告垃圾还是缓存垃圾
//                        if ("ad广告文件夹".equals(entry.getValue()) || "splash媒体文件夹".equals(entry.getValue())) {
//                            listFiles2.setGarbagetype("TYPE_AD");
//                            adJunkInfo.addSecondJunk(listFiles2);
//                            adJunkInfo.setTotalSize(adJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
//                        } else {
//                            listFiles2.setGarbagetype("TYPE_CACHE");
//                            cacheJunkInfo.addSecondJunk(listFiles2);
//                            cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
//                        }
//
//                        if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
//                            mScanFileListener.increaseSize(listFiles2.getGarbageSize());
//                        }
//                    } else if (new File(entry.getKey()).isFile()) { //文件路径
//                        File cachefile = new File(entry.getKey());
//                        SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
//                        if (cachefile.exists()) {
//                            secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
//                            secondJunkInfo.setChecked(true);
//                            secondJunkInfo.setPackageName(applicationInfo.packageName);
//                            secondJunkInfo.setGarbagetype("TYPE_CACHE");
//                            secondJunkInfo.setGarbageSize(cachefile.length());
//
//
//                            cacheJunkInfo.addSecondJunk(secondJunkInfo);
//                            cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
//                            if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
//                                mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
//                            }
//                        }
//                    }
//                }
//            }
//
//            //开始扫描已安装app上的硬盘文件夹
//            List<String> appExtDir = ScanPathExt.getScanExtPath(applicationInfo.packageName);
//            if (appExtDir != null && appExtDir.size() > 0) {
//                Map<String, String> filePathMap = new HashMap<>();
//                for (String dirPath : appExtDir) {
//                    File scanExtFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirPath);
//                    Map<String, String> innerFilePathMap = this.checkOutAllGarbageFolder(scanExtFile, applicationInfo.packageName);
//                    for (Map.Entry<String, String> entry : innerFilePathMap.entrySet()) {
//                        filePathMap.put(entry.getKey(), entry.getValue());
//                    }
//                }
//                for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
//                    if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
//                        final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
//                        if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
//                            continue;
//                        }
//                        listFiles2.setGarbageName(entry.getValue());
//                        listFiles2.setFilecatalog(entry.getKey());
//                        listFiles2.setChecked(true);
//                        listFiles2.setPackageName(applicationInfo.packageName);
//
//                        //根据文件夹类型名称判断区分广告垃圾还是缓存垃圾
//                        if ("ad广告文件夹".equals(entry.getValue()) || "splash媒体文件夹".equals(entry.getValue())) {
//                            listFiles2.setGarbagetype("TYPE_AD");
//                            adJunkInfo.addSecondJunk(listFiles2);
//                            adJunkInfo.setTotalSize(adJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
//                        } else {
//                            listFiles2.setGarbagetype("TYPE_CACHE");
//                            cacheJunkInfo.addSecondJunk(listFiles2);
//                            cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
//                        }
//
//                        if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
//                            mScanFileListener.increaseSize(listFiles2.getGarbageSize());
//                        }
//                    } else if (new File(entry.getKey()).isFile()) { //文件路径
//                        File cachefile = new File(entry.getKey());
//                        SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
//                        if (cachefile.exists()) {
//                            secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
//                            secondJunkInfo.setChecked(true);
//                            secondJunkInfo.setPackageName(applicationInfo.packageName);
//                            secondJunkInfo.setGarbagetype("TYPE_CACHE");
//                            secondJunkInfo.setGarbageSize(cachefile.length());
//                            cacheJunkInfo.addSecondJunk(secondJunkInfo);
//                            cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
//                            if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
//                                mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
//                            }
//                        }
//                    }
//                }
//            }

            //开始扫描clean_db文件中路径
            List<AppPath> pathList = ApplicationDelegate.getAppPathDatabase().cleanPathDao().getPathList(applicationInfo.packageName);

            if(!CollectionUtils.isEmpty(pathList)){
                    Map<String, String> filePathMap = new HashMap<>();
                    for (AppPath pathObj : pathList) {
                        String adspath = AESUtils01.decrypt(pathObj.getFile_path());
                        LogUtils.i("---path---"+adspath);
                        if(TextUtils.isEmpty(adspath)){
                            continue;
                        }
                        File scanExtFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +adspath);
                        Map<String, String> innerFilePathMap = this.checkOutAllGarbageFolderFromDb(scanExtFile, applicationInfo.packageName,pathObj);
                        for (Map.Entry<String, String> entry : innerFilePathMap.entrySet()) {
                            filePathMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                        if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
                            final SecondJunkInfo listFiles2 = listFiles(new File(entry.getKey()));
                            if (listFiles2.getFilesCount() <= 0 || listFiles2.getGarbageSize() <= 0L) {
                                continue;
                            }
                            listFiles2.setGarbageName(entry.getValue());
                            listFiles2.setFilecatalog(entry.getKey());
                            listFiles2.setChecked(true);
                            listFiles2.setPackageName(applicationInfo.packageName);

                            //根据文件夹类型名称判断区分广告垃圾还是缓存垃圾
                            if ("ad广告文件夹".equals(entry.getValue()) || "splash媒体文件夹".equals(entry.getValue())) {
                                listFiles2.setGarbagetype("TYPE_AD");
                                adJunkInfo.addSecondJunk(listFiles2);
                                adJunkInfo.setTotalSize(adJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
                            } else {
                                listFiles2.setGarbagetype("TYPE_CACHE");
                                cacheJunkInfo.addSecondJunk(listFiles2);
                                cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + listFiles2.getGarbageSize());
                            }

                            if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                                mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                            }
                        } else if (new File(entry.getKey()).isFile()) { //文件路径
                            File cachefile = new File(entry.getKey());
                            SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                            if (cachefile.exists()) {
                                secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                                if(!TextUtils.isEmpty(entry.getValue()) && entry.getValue().endsWith("_手动")){
//                                    LogUtils.i("zz--手动");
                                    secondJunkInfo.setChecked(false);
                                    cacheJunkInfo.setCarefulIsChecked(false);
                                }else{
                                    secondJunkInfo.setChecked(true);
                                }
                                secondJunkInfo.setPackageName(applicationInfo.packageName);
                                secondJunkInfo.setGarbagetype("TYPE_CACHE");
                                secondJunkInfo.setGarbageSize(cachefile.length());

                                cacheJunkInfo.addSecondJunk(secondJunkInfo);
                                cacheJunkInfo.setTotalSize(cacheJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                                if (mScanFileListener != null && !WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                                }
                            }
                        }
                    }

            }

            //排除当前应用
            if (!WHITE_LIST.contains(cacheJunkInfo.getAppPackageName())) {
                if (cacheJunkInfo.getTotalSize() > 0) {
                    cacheJunkListInfo.add(cacheJunkInfo);
                }
            }

            if (!WHITE_LIST.contains(adJunkInfo.getAppPackageName())) {
                if (adJunkInfo.getTotalSize() > 0) {
                    adJunkListInfo.add(adJunkInfo);
                }
            }
        }

        //扫描指定的广告文件夹
        Map<String, String> adExtPathList = ScanPathExt.getAdExtPath();
        for (Map.Entry<String, String> adExtPath : adExtPathList.entrySet()) {
            File rootPathFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + adExtPath.getKey());
            if (rootPathFile.exists()) {
                FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
                firstJunkInfo.setAppName(adExtPath.getValue());
                firstJunkInfo.setAllchecked(true);
                firstJunkInfo.setGarbageType("TYPE_AD");
                firstJunkInfo.setAppPackageName(adExtPath.getKey());
                SecondJunkInfo secondJunkInfo = FileUtils.listFiles(rootPathFile);
                if (mScanFileListener != null) {
                    mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                }
                firstJunkInfo.setTotalSize(secondJunkInfo.getGarbageSize());
                firstJunkInfo.addSecondJunk(secondJunkInfo);
                adJunkListInfo.add(firstJunkInfo);
            }
        }

        HashMap<ScanningResultType, ArrayList<FirstJunkInfo>> listHashMap = new HashMap<>();
        listHashMap.put(ScanningResultType.CACHE_JUNK, cacheJunkListInfo);
        listHashMap.put(ScanningResultType.AD_JUNK, adJunkListInfo);
        return listHashMap;
    }

    /**
     * 获取外部存储_/Android/data/私有目录中的垃圾信息
     *
     * @param isScanFile 是否扫描到文件
     * @return
     */
    public ArrayList<FirstJunkInfo> getAndroidDataInfo(boolean isScanFile) {
        if (isFinish) {
            return new ArrayList<>();
        }
        ArrayList<FirstJunkInfo> list = new ArrayList<>();
        //外部存储私有存储父文件
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/";
        //已经安装的应用信息
        if (installedList == null)
            installedList = getInstalledList();
        int total = 0;
        if (installedList != null) {
            total = installedList.size();
        }
        int index = 0;
        for (PackageInfo applicationInfo : installedList) {
            if (isFinish) {
                return list;
            }
            index++;
            //packageName文件下（私有目录）
            final File mainfile = new File(rootPath + applicationInfo.packageName);
            //缓存文件（私有目录）
            final File file = new File(rootPath + applicationInfo.packageName + "/cache");
            //文件夹集合（私有目录）
            final File file2 = new File(rootPath + applicationInfo.packageName + "/files");

            //一级目（私有目录）
            FirstJunkInfo firstJunkInfo = new FirstJunkInfo();
            firstJunkInfo.setAppName(getAppName(applicationInfo.applicationInfo));
            if (!isService){
                firstJunkInfo.setGarbageIcon(getAppIcon(applicationInfo.applicationInfo));
//                firstJunkInfo.setIconSource(getAppIconSource(applicationInfo.applicationInfo));
            }

            firstJunkInfo.setAllchecked(true);
            firstJunkInfo.setGarbageType("TYPE_CACHE");
            firstJunkInfo.setAppPackageName(applicationInfo.packageName);

            if (mScanFileListener != null) {
                mScanFileListener.scanFile(applicationInfo.packageName);
            }

            //cache目录下缓存
            if (file.exists()) {
                SecondJunkInfo secondJunkInfo = FileUtils.cacheListFiles(file);
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

            //files目录下缓存
            if (file2.exists()) {
                //路径集合
                Map<String, String> filePathMap = this.checkOutAllGarbageFolder(file2, applicationInfo.packageName);
                for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                    if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
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
                        if (mScanFileListener != null && !WHITE_LIST.contains(firstJunkInfo.getAppPackageName())) {
                            mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                        }
                    } else if (new File(entry.getKey()).isFile()) { //文件路径
                        File cachefile = new File(entry.getKey());
                        SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                        if (cachefile.exists()) {
                            secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                            secondJunkInfo.setChecked(true);
                            secondJunkInfo.setPackageName(applicationInfo.packageName);
                            secondJunkInfo.setGarbagetype("TYPE_CACHE");
                            secondJunkInfo.setGarbageSize(cachefile.length());

                            firstJunkInfo.addSecondJunk(secondJunkInfo);
                            firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                            if (mScanFileListener != null && !WHITE_LIST.contains(firstJunkInfo.getAppPackageName())) {
                                mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                            }
                        }
                    }
                }
            }

            //packageName文件
            if (mainfile.exists()) {
                //路径集合_排除指定路径
                Map<String, String> filePathMap = this.checkOutAllGarbageFolder(mainfile, applicationInfo.packageName, "cache", "files");
                for (final Map.Entry<String, String> entry : filePathMap.entrySet()) {
                    if (new File(entry.getKey()).isDirectory()) {    //文件夹路径
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
                        if (mScanFileListener != null && !WHITE_LIST.contains(firstJunkInfo.getAppPackageName())) {
                            mScanFileListener.increaseSize(listFiles2.getGarbageSize());
                        }
                    } else if (new File(entry.getKey()).isFile()) { //文件路径
                        File cachefile = new File(entry.getKey());
                        SecondJunkInfo secondJunkInfo = new SecondJunkInfo();
                        if (cachefile.exists()) {
                            secondJunkInfo.setFilecatalog(cachefile.getAbsolutePath());
                            secondJunkInfo.setChecked(true);
                            secondJunkInfo.setPackageName(applicationInfo.packageName);
                            secondJunkInfo.setGarbagetype("TYPE_CACHE");
                            secondJunkInfo.setGarbageSize(cachefile.length());

                            firstJunkInfo.addSecondJunk(secondJunkInfo);
                            firstJunkInfo.setTotalSize(firstJunkInfo.getTotalSize() + secondJunkInfo.getGarbageSize());
                            if (mScanFileListener != null && !WHITE_LIST.contains(firstJunkInfo.getAppPackageName())) {
                                mScanFileListener.increaseSize(secondJunkInfo.getGarbageSize());
                            }
                        }
                    }

                }
            }

            if (firstJunkInfo.getSubGarbages() == null || firstJunkInfo.getSubGarbages().size() <= 0) {
                continue;
            }
            //排除当前应用
            if (!WHITE_LIST.contains(firstJunkInfo.getAppPackageName()))
                list.add(firstJunkInfo);
            /**
             * 遍历文件3/4以上 && 未扫描出apk文件 && 遍历私有路径下包含垃圾
             */
            if (index > (float) total * 3 / 4 && !isScanFile && list.size() > 0) {
//                Log.v("onAnimationEnd", "mScanFileListener " + mScanFileListener);

                if (mScanFileListener != null) {
                    mScanFileListener.currentNumber();
                }
            }
        }
//        ArrayList<FirstJunkInfo> appCacheAndAdGarbage = getAppCacheAndAdGarbage(list);
//        if (appCacheAndAdGarbage != null) {
//            list.addAll(appCacheAndAdGarbage);
//        }
        return list;
    }

    public ArrayList<FirstJunkInfo> getRunningProcess() {
        return getRunningProcess(true);
    }

    /**
     * 获取进程
     *
     * @param isShowSize 是否展示扫描文件大小
     * @return
     */
    public ArrayList<FirstJunkInfo> getRunningProcess(boolean isShowSize) {
        if (isFinish) {
            return new ArrayList<>();
        }
        String str;
        FirstJunkInfo onelevelGarbageInfo;
        ArrayList arrayList = new ArrayList();
        try {
            HashMap hashMap = new HashMap();
            //8.0以上
            if (Build.VERSION.SDK_INT >= 26) {  //特殊处理
                return getTaskInfo26(isShowSize);
            }
            //7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                return getTaskInfos24(mContext, isShowSize);
            }
            //4.4以上
            if (Build.VERSION.SDK_INT >= 20) {
                return getTaskInfos(isShowSize);
            }

            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : this.mActivityManager.getRunningAppProcesses()) {

                if (isFinish) {
                    return arrayList;
                }
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
                        CharSequence loadLabel = this.mPackageManager.getPackageInfo(str, 0).applicationInfo.loadLabel(this.mPackageManager);
                        if (runningAppProcessInfo.importance >= 200 && !str.equals(this.mContext.getPackageName()) && !ArrayUtil.arrayContainsStr(WHITE_LIST, str)) {
                            if (mScanFileListener != null) {
                                mScanFileListener.scanFile(str);
                            }
                            long totalPss = this.mActivityManager.getProcessMemoryInfo(new int[]{i2})[0].getTotalPss() * 1024;
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
                            if (!isService){
                                onelevelGarbageInfo.setGarbageIcon(getAppIcon(mPackageManager.getPackageInfo(str, 0).applicationInfo));
//                                onelevelGarbageInfo.setIconSource(getAppIconSource(mPackageManager.getPackageInfo(str, 0).applicationInfo));
                            }
                            if (mScanFileListener != null && isShowSize) {
                                mScanFileListener.increaseSize(totalPss);
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
                    // 在白名单内的，不进行扫描
                    if (!WHITE_LIST.contains(onelevelGarbageInfo4.getAppPackageName()))
                        arrayList.add(onelevelGarbageInfo4);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            e2.printStackTrace();
            return arrayList;
        }
    }

    @TargetApi(22)
    private ArrayList<FirstJunkInfo> getTaskInfo26(boolean isShowSize) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return new ArrayList<>();
        }
        ArrayList<FirstJunkInfo> junkList = new ArrayList<>();
        List<UsageStats> lists = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                System.currentTimeMillis() - 86400000, System.currentTimeMillis());
        if (!(lists == null || lists.size() == 0)) {
            return getTaskInfos26(junkList, lists, isShowSize);  //授权过
        } else {
            return getTaskInfos26UnGrant(junkList, isShowSize);   //没有读取记录权限，造随机内存
        }
    }

    private ArrayList<FirstJunkInfo> getTaskInfos26UnGrant(ArrayList<FirstJunkInfo> junkList, boolean isShowSize) {
        //外部存储私有存储父文件
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/";
        //已经安装的应用信息
        if (installedList == null)
            installedList = getInstalledList();
        int packageSize = installedList.size();
        if (packageSize == 0)
            return junkList;
        int sizeNum = 50;
//        if(isService)
        if(!isRandRamNum){
            float sizeNumF = (float) sizeNum * PreferenceUtil.getMulCacheNum();
            sizeNum = (int) sizeNumF;
        }
        List<PackageInfo> customAppList = new ArrayList<>();
        List<String> packageNameList = new ArrayList<>();
        for (PackageInfo p : installedList) {
            if (!isSystemAppliation(p.packageName) && !ArrayUtil.arrayContainsStr(WHITE_LIST, p.packageName) && !packageNameList.contains(p.packageName)) {
                customAppList.add(p);
                packageNameList.add(p.packageName);
            }
        }
        List<PackageInfo> junkAppList = new ArrayList<>();
        if (customAppList.size() <= 15) {
            junkAppList.addAll(customAppList);
        } else {
            List<Integer> randomNumList = new ArrayList<>();
            for (int i = 0; i < customAppList.size(); i++) {
                randomNumList.add(i);
            }

            List<Integer> resultNumList = new ArrayList<>();
            while (resultNumList.size() < 15) {
                int randomNum = randomNumList.remove((int) (Math.random() * randomNumList.size()));
                if (!resultNumList.contains(randomNum)) {
                    resultNumList.add(randomNum);
                }
            }

            for (int random : resultNumList) {
                PackageInfo packageInfo = customAppList.get(random);
                junkAppList.add(packageInfo);
            }
        }

        for (PackageInfo p : junkAppList) {
            if (mScanFileListener != null) {
                mScanFileListener.scanFile(rootPath + p.packageName);
            }
            String packageName = p.packageName;
            FirstJunkInfo junkInfo = new FirstJunkInfo();
            junkInfo.setAllchecked(true);
            junkInfo.setAppName(getAppName(p.applicationInfo));
            junkInfo.setAppPackageName(packageName);
            if (!isService){
                junkInfo.setGarbageIcon(getAppIcon(p.applicationInfo));
//                junkInfo.setIconSource(getAppIconSource(p.applicationInfo));
            }

            long totalSize = (long) ((Math.random() * 1024 * 1024 * sizeNum) + 1024 * 1024 * sizeNum);
            if (mScanFileListener != null && isShowSize) {
                mScanFileListener.increaseSize(totalSize);
            }
            junkInfo.setTotalSize(totalSize);
            junkInfo.setGarbageType("TYPE_PROCESS");
            junkList.add(junkInfo);
        }

        return junkList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<FirstJunkInfo> getTaskInfos26(ArrayList<FirstJunkInfo> junkList, List<UsageStats> lists, boolean isShowSize) {
        Collections.sort(lists, (o1, o2) -> Long.compare(o1.getLastTimeStamp(), o2.getLastTimeStamp()));
        if (installedList == null)
            installedList = getInstalledList();
        HashMap<String, PackageInfo> installedCustomApp = new HashMap<>();
        for (PackageInfo packageInfo : installedList) {
            if (!isSystemAppliation(packageInfo.packageName) && !ArrayUtil.arrayContainsStr(WHITE_LIST, packageInfo.packageName)) {
                installedCustomApp.put(packageInfo.packageName, packageInfo);
            }
        }
        List<String> packageNameList = new ArrayList<>();
        for (UsageStats usageStats : lists) {
            if (mScanFileListener != null) {
                mScanFileListener.scanFile(usageStats.getPackageName());
            }
            if (usageStats.getPackageName() != null) {
                String packageName = usageStats.getPackageName();
                PackageInfo packageInfo = installedCustomApp.get(packageName);
                if (packageInfo != null && !packageNameList.contains(packageName)) {
                    FirstJunkInfo junkInfo = new FirstJunkInfo();
                    junkInfo.setAllchecked(true);
                    junkInfo.setAppName(getAppName(packageInfo.applicationInfo));
                    junkInfo.setAppPackageName(packageName);
                    if (!isService) {
                        junkInfo.setGarbageIcon(getAppIcon(packageInfo.applicationInfo));
//                        junkInfo.setIconSource(getAppIconSource(packageInfo.applicationInfo));
                    }
                    long totalSize = (long) ((Math.random() * 1024 * 1024 * 50) + 1024 * 1024 * 50);
                    if (mScanFileListener != null && isShowSize) {
                        mScanFileListener.increaseSize(totalSize);
                    }
                    junkInfo.setTotalSize(totalSize);
                    junkInfo.setGarbageType("TYPE_PROCESS");
                    junkList.add(junkInfo);

                    packageNameList.add(packageName);
                }
            }
        }
        ArrayList<FirstJunkInfo> result = new ArrayList<>();
        if (junkList.size() > 30) {
            result.addAll(junkList.subList(0, 30));
        } else {
            result.addAll(junkList);
        }
        return result;
    }


    public ArrayList<FirstJunkInfo> getTaskInfos(boolean isShowSize) {
        int i2;
        try {
            if (isFinish) {
                return new ArrayList<>();
            }
            ArrayList<FirstJunkInfo> arrayList = new ArrayList<>();
            List runningAppProcesses = getRunningAppProcesses();
            if (runningAppProcesses.isEmpty() || runningAppProcesses.size() == 0) {
                return new ArrayList<>();
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
            ArrayList<AppMemoryInfo> arrayList2 = new ArrayList<>();
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
                    if (arrayList2.get(i5).getName().equals(arrayList2.get(size).getName())) {
                        arrayList2.remove(size);
                    }
                }
            }
            Iterator it = arrayList2.iterator();
            int indexP = 0;
            double size = arrayList2.size() * NoClearSPHelper.getMemoryShow();
            while (it.hasNext()) {
                if (indexP >= size) {
                    return arrayList;
                }
                indexP++;
                if (isFinish) {
                    return arrayList;
                }
                AppMemoryInfo appMemoryInfo2 = (AppMemoryInfo) it.next();
                ApplicationInfo applicationInfo = getApplicationInfo(appMemoryInfo2.getName());
                if (mScanFileListener != null) {
                    mScanFileListener.scanFile(appMemoryInfo2.getName());
                }
                if (applicationInfo != null && !ArrayUtil.arrayContainsStr(WHITE_LIST, appMemoryInfo2.getName())) {
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
                        if (!isService){
                            onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
//                            onelevelGarbageInfo.setIconSource(getAppIconSource(applicationInfo));
                        }

                        if (mScanFileListener != null && isShowSize) {
                            mScanFileListener.increaseSize(totalPss);
                        }
                        arrayList.add(onelevelGarbageInfo);
                    }
                }
            }
            return arrayList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TargetApi(19)
    public ArrayList<FirstJunkInfo> getTaskInfos24(Context context, boolean isShowSize) {
        AndroidProcesses.setLoggingEnabled(true);
        if (isFinish) {
            return new ArrayList<>();
        }
        int i2;
        try {
//            UserUnCheckedData memoryUncheckedList = a.getInstance().getMemoryUncheckedList();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
            int indexP = 0;
            double size = arrayList.size() * NoClearSPHelper.getMemoryShow();
            while (it.hasNext()) {
                if (indexP >= size) {
                    return arrayList2;
                }
                indexP++;
                if (isFinish) {
                    return arrayList2;
                }
                AppMemoryInfo appMemoryInfo2 = (AppMemoryInfo) it.next();
                FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                String name = appMemoryInfo2.getName();
                if (mScanFileListener != null) {
                    mScanFileListener.scanFile(name);
                }
                if (name != null && !ArrayUtil.arrayContainsStr(WHITE_LIST, name)) {
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
                        if (!isService){
                            onelevelGarbageInfo.setGarbageIcon(getAppIcon(packageManager.getApplicationInfo(name, 0)));
//                            onelevelGarbageInfo.setIconSource(getAppIconSource(packageManager.getApplicationInfo(name,0)));
                        }

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
                        if (mScanFileListener != null && isShowSize) {
                            mScanFileListener.increaseSize(totalPss);
                        }
                        arrayList2.add(onelevelGarbageInfo);
                    }
                }
            }
            return arrayList2;
        } catch (Exception e) {
            return new ArrayList<>();
        }
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

    /**
     * 查询apk文件
     *
     * @return
     */
    public List<FirstJunkInfo> queryAPkFile() {
        if (isFinish) {
            return new ArrayList<>();
        }
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
                    cursor = AppApplication.getInstance().getContentResolver().query(Uri.parse(str), strArr, "_data like ?", new String[]{"%.apk"}, null);
                } else {
                    cursor = query;
                }
                if (cursor.moveToFirst()) {
                    while (true) {
                        if (isFinish) {
                            return arrayList;
                        }
                        String string = cursor.getString(0);
                        long j = cursor.getLong(1);
                        if (new File(string).exists() && !FileUtils.isNotHiddenPath(new File(string)) && j != 0) {
                            FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                            onelevelGarbageInfo.setAllchecked(true);
                            onelevelGarbageInfo.setGarbageType("TYPE_APK");
                            onelevelGarbageInfo.setTotalSize(j);
                            onelevelGarbageInfo.setGarbageCatalog(string);
                            if (com.xiaoniu.common.utils.FileUtils.isSDCardEnable()) {
                                absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            } else {
                                absolutePath = AppApplication.getInstance().getFilesDir().getAbsolutePath();
                            }
                            if (string.contains(absolutePath) || string.contains("sdcard0") || string.contains("sdcard1")) {
                                PackageInfo packageArchiveInfo = mPackageManager.getPackageArchiveInfo(string, PackageManager.GET_ACTIVITIES);
                                if (packageArchiveInfo != null && !isSystemAppliation(packageArchiveInfo.packageName)) {
                                    ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                    applicationInfo.sourceDir = string;
                                    applicationInfo.publicSourceDir = string;
                                    onelevelGarbageInfo.setAppPackageName(packageArchiveInfo.packageName);
                                    onelevelGarbageInfo.setVersionName(packageArchiveInfo.versionName);
                                    onelevelGarbageInfo.setVersionCode(packageArchiveInfo.versionCode);
                                    onelevelGarbageInfo.setAppGarbageName(mPackageManager.getApplicationLabel(packageArchiveInfo.applicationInfo).toString());
                                    if (!isService){
                                        onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
//                                        onelevelGarbageInfo.setIconSource(getAppIconSource(applicationInfo));
                                    }
                                    onelevelGarbageInfo.setAppName(getAppName(applicationInfo));
                                    if (FileUtils.isAppInstalled(packageArchiveInfo.packageName)) {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_install));
                                        onelevelGarbageInfo.setApkInstalled(true);
                                        onelevelGarbageInfo.setAllchecked(true);
                                    } else {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_uninstall));
                                        onelevelGarbageInfo.setApkInstalled(false);
                                        onelevelGarbageInfo.setAllchecked(false);
                                    }
                                    if (!WHITE_LIST.contains(packageArchiveInfo.packageName)) {
                                        arrayList.add(onelevelGarbageInfo);
                                        if (mScanFileListener != null) {
                                            mScanFileListener.increaseSize(j);
                                        }
                                    }
                                }
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
            return new ArrayList<>();
        }
    }


    /**
     * 查询apk文件
     *
     * @return
     */
    public List<FirstJunkInfo> queryAPkFileByDb() {
        if (isFinish) {
            return new ArrayList<>();
        }
        Cursor cursor;
        String absolutePath;
        try {
            List<UselessApk> apks = ApplicationDelegate.getAppPathDatabase().uselessApkDao().getAll();
            List<FirstJunkInfo> arrayList = new ArrayList();
            for(int i=0;i<apks.size();i++){
                String apkPath = AESUtils01.decrypt(apks.get(i).getFilePath());
                File scanExtFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + apkPath);
                if(scanExtFile.isDirectory()){   //文件夹处理方式
                    List<File> resultList = new ArrayList();
                    findFiles(scanExtFile.getAbsolutePath(), "*apk", resultList);
                    if(!CollectionUtils.isEmpty(resultList)){
                        for(File file:resultList){
                            String scanFilePath = file.getAbsolutePath();
                            FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                            onelevelGarbageInfo.setAllchecked(true);
                            onelevelGarbageInfo.setGarbageType("TYPE_APK");
                            onelevelGarbageInfo.setTotalSize(file.length());
                            onelevelGarbageInfo.setGarbageCatalog(file.getAbsolutePath());
                            if (com.xiaoniu.common.utils.FileUtils.isSDCardEnable()) {
                                absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            } else {
                                absolutePath = AppApplication.getInstance().getFilesDir().getAbsolutePath();
                            }
                            if (scanFilePath.contains(absolutePath) || scanFilePath.contains("sdcard0") || scanFilePath.contains("sdcard1")) {
                                PackageInfo packageArchiveInfo = mPackageManager.getPackageArchiveInfo(scanFilePath, PackageManager.GET_ACTIVITIES);
                                if (packageArchiveInfo != null && !isSystemAppliation(packageArchiveInfo.packageName)) {
                                    ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                    applicationInfo.sourceDir = scanFilePath;
                                    applicationInfo.publicSourceDir = scanFilePath;
                                    onelevelGarbageInfo.setAppPackageName(packageArchiveInfo.packageName);
                                    onelevelGarbageInfo.setVersionName(packageArchiveInfo.versionName);
                                    onelevelGarbageInfo.setVersionCode(packageArchiveInfo.versionCode);
                                    onelevelGarbageInfo.setAppGarbageName(mPackageManager.getApplicationLabel(packageArchiveInfo.applicationInfo).toString());
                                    if (!isService){
                                        onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
//                                        onelevelGarbageInfo.setIconSource(getAppIconSource(applicationInfo));
                                    }
                                    onelevelGarbageInfo.setAppName(getAppName(applicationInfo));
                                    if (FileUtils.isAppInstalled(packageArchiveInfo.packageName)) {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_install));
                                        onelevelGarbageInfo.setApkInstalled(true);
                                        onelevelGarbageInfo.setAllchecked(true);
                                    } else {
                                        onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_uninstall));
                                        onelevelGarbageInfo.setApkInstalled(false);
                                        onelevelGarbageInfo.setAllchecked(false);
                                    }
                                    if (!WHITE_LIST.contains(packageArchiveInfo.packageName)) {
                                        arrayList.add(onelevelGarbageInfo);
                                        if (mScanFileListener != null) {
                                            mScanFileListener.increaseSize(file.length());
                                        }
                                    }
                                }
                            }
                        }
                    }

                }else{//文件处理方式
                    if(scanExtFile.isFile() && scanExtFile.length()>0){
                        String scanFilePath = scanExtFile.getAbsolutePath();
                        FirstJunkInfo onelevelGarbageInfo = new FirstJunkInfo();
                        onelevelGarbageInfo.setAllchecked(true);
                        onelevelGarbageInfo.setGarbageType("TYPE_APK");
                        onelevelGarbageInfo.setTotalSize(scanExtFile.length());
                        onelevelGarbageInfo.setGarbageCatalog(scanExtFile.getAbsolutePath());
                        if (com.xiaoniu.common.utils.FileUtils.isSDCardEnable()) {
                            absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        } else {
                            absolutePath = AppApplication.getInstance().getFilesDir().getAbsolutePath();
                        }
                        if (scanFilePath.contains(absolutePath) || scanFilePath.contains("sdcard0") || scanFilePath.contains("sdcard1")) {
                            PackageInfo packageArchiveInfo = mPackageManager.getPackageArchiveInfo(scanFilePath, PackageManager.GET_ACTIVITIES);
                            if (packageArchiveInfo != null && !isSystemAppliation(packageArchiveInfo.packageName)) {
                                ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                applicationInfo.sourceDir = scanFilePath;
                                applicationInfo.publicSourceDir = scanFilePath;
                                onelevelGarbageInfo.setAppPackageName(packageArchiveInfo.packageName);
                                onelevelGarbageInfo.setVersionName(packageArchiveInfo.versionName);
                                onelevelGarbageInfo.setVersionCode(packageArchiveInfo.versionCode);
                                onelevelGarbageInfo.setAppGarbageName(mPackageManager.getApplicationLabel(packageArchiveInfo.applicationInfo).toString());
                                if (!isService){
                                    onelevelGarbageInfo.setGarbageIcon(getAppIcon(applicationInfo));
//                                        onelevelGarbageInfo.setIconSource(getAppIconSource(applicationInfo));
                                }
                                onelevelGarbageInfo.setAppName(getAppName(applicationInfo));
                                if (FileUtils.isAppInstalled(packageArchiveInfo.packageName)) {
                                    onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_install));
                                    onelevelGarbageInfo.setApkInstalled(true);
                                    onelevelGarbageInfo.setAllchecked(true);
                                } else {
                                    onelevelGarbageInfo.setDescp(this.mContext.getString(R.string.clean_apk_file_uninstall));
                                    onelevelGarbageInfo.setApkInstalled(false);
                                    onelevelGarbageInfo.setAllchecked(false);
                                }
                                if (!WHITE_LIST.contains(packageArchiveInfo.packageName)) {
                                    arrayList.add(onelevelGarbageInfo);
                                    if (mScanFileListener != null) {
                                        mScanFileListener.increaseSize(scanExtFile.length());
                                    }
                                }
                            }
                        }
                    }
                }

                }

            return arrayList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 递归查找文件
     * @param baseDirName  查找的文件夹路径
     * @param targetFileName  需要查找的文件名
     * @param fileList  查找到的文件集合
     */
    public static void findFiles(String baseDirName, String targetFileName, List fileList) {

        File baseDir = new File(baseDirName);		// 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {	// 判断目录是否存在
//            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if(tempFile.isDirectory()){
                findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
            }else if(tempFile.isFile()){
                tempName = tempFile.getName();
                if(wildcardMatch(targetFileName, tempName)){
                    // 匹配成功，将文件名添加到结果集
                    fileList.add(tempFile.getAbsoluteFile());
                }
            }
        }
    }


    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    public ArrayList<FirstJunkInfo> getAppCacheAndAdGarbage(ArrayList<FirstJunkInfo> list) {

        if (isFinish) {
            return new ArrayList<>();
        }

        ArrayList arrayList2 = new ArrayList();

        List<AppInfoClean> appInfoCleans = CleanDBManager.queryInfoList();

        if (appInfoCleans == null) {
            return new ArrayList<>();
        }

        for (AppInfoClean appInfoClean : appInfoCleans) {

            if (isFinish) {
                return arrayList2;
            }

            if (FileUtils.isAppInstalled(appInfoClean.getPackageName())) {
                try {
                    FirstJunkInfo onelevelGarbageInfo = isContain(list, appInfoClean.getPackageName());
                    if (onelevelGarbageInfo == null) {
                        onelevelGarbageInfo = new FirstJunkInfo();
                    }
                    onelevelGarbageInfo.setAllchecked(true);
                    onelevelGarbageInfo.setAppName(appInfoClean.getAppName());
                    onelevelGarbageInfo.setGarbageIcon(getAppIcon(mPackageManager.getApplicationInfo(appInfoClean.getPackageName(), 0)));
//                    onelevelGarbageInfo.setIconSource(getAppIconSource(mPackageManager.getApplicationInfo(appInfoClean.getPackageName(),0)));
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

                    if (arrayList2.size() > 0) {
                        mScanFileListener.currentNumber();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }

    private Map<String, String> checkOutAllGarbageFolder(final File file, String packageName, String... args) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        this.checkFiles(hashMap, file, packageName, args);
        return hashMap;
    }


    private Map<String, String> checkOutAllGarbageFolderFromDb(final File file, String packageName, AppPath appPath) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        this.checkPointTyepFiles(hashMap, file, packageName, appPath);
        return hashMap;
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

    //递归遍历file下的所有文件
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
            ex.printStackTrace();
        }
        for (int length = listFiles.length, i = 0; i < length; ++i) {
            file = listFiles[i];
            if (file.isDirectory()) {//文件夹类型
                innerListFiles(secondJunkInfo, file);
            } else {   //文件类型
                secondJunkInfo.setFilesCount(secondJunkInfo.getFilesCount() + 1);
                secondJunkInfo.setGarbageSize(secondJunkInfo.getGarbageSize() + file.length());
            }
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
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取缓存白名单
     */
    private Set<String> getCacheWhite() {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        return sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
    }

    public interface ScanFileListener {
        void currentNumber();

        void increaseSize(final long p0);

        void scanFile(final String p0);
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


    /**
     * 筛选file下需要清理的文件夹
     *
     * @param map
     * @param file
     * @param args 需要排除的文件夹名称
     */
    private void checkFiles(final Map<String, String> map, final File file, String packageName, String... args) {

        File[] listFiles = file.listFiles();

        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (args.length > 0 && !TextUtils.isEmpty(packageName)) {//排除指定包下的指定目录
                    boolean isSame = false;
                    for (String x : args) {
                        if (file2.getAbsoluteFile().getAbsolutePath().endsWith(packageName + "/" + x)) {
                            isSame = true;
                            break;
                        }
                    }
                    //排除args文件
                    if (isSame)
                        continue;
                }
                String fileName = file2.getName();
                if (file2.isDirectory()) {
                    if (fileName.toLowerCase().equals("awcn_strategy")) {
                        map.put(file2.getAbsolutePath(), "awcn_strategy缓存文件夹");
                    } else if (fileName.toLowerCase().equals("baidu")) {
                        map.put(file2.getAbsolutePath(), "baidu插件缓存文件夹");
                    } else if (fileName.toLowerCase().equals("ad") || fileName.toLowerCase().equals("ads")) {
                        map.put(file2.getAbsolutePath(), "ad广告文件夹");
                    } else if (fileName.toLowerCase().contains("log")) {
                        map.put(file2.getAbsolutePath(), "log日志文件夹");
                    } else if (fileName.toLowerCase().contains("temp")) {
                        map.put(file2.getAbsolutePath(), "temp日志文件夹");
                    } else if (fileName.toLowerCase().contains("tmp")) {
                        map.put(file2.getAbsolutePath(), "tmp日志文件夹");
                    } else if (fileName.toLowerCase().contains("crash")) {
                        map.put(file2.getAbsolutePath(), "crash日志文件夹");
                    } else if (fileName.toLowerCase().contains("trace")) {
                        map.put(file2.getAbsolutePath(), "trace日志文件夹");
                    } else if (fileName.toLowerCase().contains("hprof")) {
                        map.put(file2.getAbsolutePath(), "hprof日志文件夹");
                    } else if (fileName.toLowerCase().contains("cache")) {
                        map.put(file2.getAbsolutePath(), "cache缓存文件夹");
                    } else if (fileName.toLowerCase().contains("cloud")) {
                        map.put(file2.getAbsolutePath(), "cloud缓存文件夹");
                    } else if (fileName.toLowerCase().contains("update")) {
                        map.put(file2.getAbsolutePath(), "update更新缓存文件夹");
                    } else if (fileName.toLowerCase().equals("apps")) {
                        map.put(file2.getAbsolutePath(), "apps页面缓存文件夹");
                    } else if (fileName.toLowerCase().equals("anr")) {
                        map.put(file2.getAbsolutePath(), "anr缓存日志");
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && fileName.toLowerCase().contains("image")) {
                        map.put(file2.getAbsolutePath(), "图片文件夹");
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && fileName.toLowerCase().contains("video")) {
                        map.put(file2.getAbsolutePath(), "视频文件夹");
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && fileName.toLowerCase().contains("audio")) {
                        map.put(file2.getAbsolutePath(), "音频文件夹");
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && fileName.toLowerCase().contains("splash")) {
                        map.put(file2.getAbsolutePath(), "splash媒体文件夹");
                    } else if (file2.getAbsolutePath().toLowerCase().endsWith("download")) {
                        if (DateUtils.isOverThreeDay(file2.lastModified(), 1)) {
                            map.put(file2.getAbsolutePath(), "download文件夹");
                        }
                    } else {
                        this.checkFiles(map, file2, packageName);
                    }
                } else {
                    String fileLow = fileName.toLowerCase();
                    /**
                     * 日志相关文件*/
                    if (fileLow.endsWith(".log")) {
                        map.put(file2.getAbsolutePath(), "log日志文件");
                    } else if (fileLow.endsWith(".tmp") || fileLow.endsWith(".temp")) {
                        map.put(file2.getAbsolutePath(), "tmp日志文件");
                    } else if (fileLow.endsWith(".tmp") || fileLow.endsWith(".temp")) {
                        map.put(file2.getAbsolutePath(), "tmp日志文件");
                    } else if (fileLow.endsWith(".hprof")) {
                        map.put(file2.getAbsolutePath(), "hprof日志文件");
                    } else if (fileLow.endsWith(".trace")) {
                        map.put(file2.getAbsolutePath(), "trace日志文件");
                    }

                    //多媒体相关文件
                    //bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd
                    if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".png") || fileLow.endsWith(".bmp") || fileLow.endsWith(".jpg") || fileLow.endsWith(".tif") || fileLow.endsWith(".gif") || fileLow.endsWith(".pcx") || fileLow.endsWith(".tga") || fileLow.endsWith(".exif") || fileLow.endsWith(".fpx") || fileLow.endsWith(".svg") || fileLow.endsWith(".psd"))) {
                        map.put(file2.getAbsolutePath(), "图片文件");
                        //cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".cdr") || fileLow.endsWith(".pcd") || fileLow.endsWith(".dxf") || fileLow.endsWith(".ufo") || fileLow.endsWith(".eps") || fileLow.endsWith(".ai") || fileLow.endsWith(".raw") || fileLow.endsWith(".WMF") || fileLow.endsWith(".webp"))) {
                        map.put(file2.getAbsolutePath(), "图片文件");
                        //rm，rmvb，mpeg1-4 mov mtv dat wmv avi 3gp amv dmv flv
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".rm") || fileLow.endsWith(".rmvb") || fileLow.endsWith(".mpeg1-4") || fileLow.endsWith(".mov") || fileLow.endsWith(".mtv") || fileLow.endsWith(".dat") || fileLow.endsWith(".wmv") || fileLow.endsWith(".avi") || fileLow.endsWith(".amv") || fileLow.endsWith(".dmv") || fileLow.endsWith(".flv"))) {
                        map.put(file2.getAbsolutePath(), "视频文件");
                    } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".aac") || fileLow.endsWith(".flac") || fileLow.endsWith(".ape") || fileLow.endsWith(".amr") || fileLow.endsWith(".oggvorbis") || fileLow.endsWith(".vqf") || fileLow.endsWith(".realaudio") || fileLow.endsWith(".wma") || fileLow.endsWith(".midi") || fileLow.endsWith(".mpeg-4") || fileLow.endsWith(".wave") || fileLow.endsWith(".cd") || fileLow.endsWith(".mp3") || fileLow.endsWith(".cd") || fileLow.endsWith(".wave") || fileLow.endsWith(".aiff") || fileLow.endsWith(".mpeg"))) {
                        map.put(file2.getAbsolutePath(), "音频文件");
                    } else if (fileLow.endsWith(".hprof")) {
                        map.put(file2.getAbsolutePath(), "hprof日志文件");
                    } else if (fileLow.endsWith(".trace")) {
                        map.put(file2.getAbsolutePath(), "trace日志文件");
                    }
                }
            }
        }
    }




    /**
     * 组装file下需要清理的文件夹
     *
     * @param map
     * @param file
     * @param
     */
    private void checkPointTyepFiles(final Map<String, String> map, final File file, String packageName, AppPath appPath) {
        File[] listFiles = file.listFiles();

        if (listFiles != null) {
            for (File file2 : listFiles) {
                String fileName = file2.getName();
                if (file2.isDirectory()) {
                    this.checkPointTyepFiles(map, file2, packageName, appPath);
                } else {
                    String fileLow = fileName.toLowerCase();
                    int fileType = appPath.getFile_type();
                    /*图片 =1
                    音频 =2
                    视频 =3
                    其他文件 =4*/
                    if (fileType == 1) {
                        map.put(file2.getAbsolutePath(), "图片文件");
                    } else if (fileType == 2) {
                        map.put(file2.getAbsolutePath(), "音频文件");
                    } else if (fileType == 3) {
                        map.put(file2.getAbsolutePath(), "视频文件");
                    } else if (fileType == 4){
                        map.put(file2.getAbsolutePath(), "其他文件");
                    }  else {
                        /**
                         * 日志相关文件*/
                        if (fileLow.endsWith(".log")) {
                            map.put(file2.getAbsolutePath(), "log日志文件");
                        } else if (fileLow.endsWith(".tmp") || fileLow.endsWith(".temp")) {
                            map.put(file2.getAbsolutePath(), "tmp日志文件");
                        } else if (fileLow.endsWith(".tmp") || fileLow.endsWith(".temp")) {
                            map.put(file2.getAbsolutePath(), "tmp日志文件");
                        } else if (fileLow.endsWith(".hprof")) {
                            map.put(file2.getAbsolutePath(), "hprof日志文件");
                        } else if (fileLow.endsWith(".trace")) {
                            map.put(file2.getAbsolutePath(), "trace日志文件");
                        }

                        //多媒体相关文件
                        //bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd
                        if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".png") || fileLow.endsWith(".bmp") || fileLow.endsWith(".jpg") || fileLow.endsWith(".tif") || fileLow.endsWith(".gif") || fileLow.endsWith(".pcx") || fileLow.endsWith(".tga") || fileLow.endsWith(".exif") || fileLow.endsWith(".fpx") || fileLow.endsWith(".svg") || fileLow.endsWith(".psd"))) {
                            map.put(file2.getAbsolutePath(), "图片文件");
                            //cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp
                        } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".cdr") || fileLow.endsWith(".pcd") || fileLow.endsWith(".dxf") || fileLow.endsWith(".ufo") || fileLow.endsWith(".eps") || fileLow.endsWith(".ai") || fileLow.endsWith(".raw") || fileLow.endsWith(".WMF") || fileLow.endsWith(".webp"))) {
                            map.put(file2.getAbsolutePath(), "图片文件");
                            //rm，rmvb，mpeg1-4 mov mtv dat wmv avi 3gp amv dmv flv
                        } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".rm") || fileLow.endsWith(".rmvb") || fileLow.endsWith(".mpeg1-4") || fileLow.endsWith(".mov") || fileLow.endsWith(".mtv") || fileLow.endsWith(".dat") || fileLow.endsWith(".wmv") || fileLow.endsWith(".avi") || fileLow.endsWith(".amv") || fileLow.endsWith(".dmv") || fileLow.endsWith(".flv"))) {
                            map.put(file2.getAbsolutePath(), "视频文件");
                        } else if (DateUtils.isOverThreeDay(file2.lastModified(), 1) && (fileLow.endsWith(".aac") || fileLow.endsWith(".flac") || fileLow.endsWith(".ape") || fileLow.endsWith(".amr") || fileLow.endsWith(".oggvorbis") || fileLow.endsWith(".vqf") || fileLow.endsWith(".realaudio") || fileLow.endsWith(".wma") || fileLow.endsWith(".midi") || fileLow.endsWith(".mpeg-4") || fileLow.endsWith(".wave") || fileLow.endsWith(".cd") || fileLow.endsWith(".mp3") || fileLow.endsWith(".cd") || fileLow.endsWith(".wave") || fileLow.endsWith(".aiff") || fileLow.endsWith(".mpeg"))) {
                            map.put(file2.getAbsolutePath(), "音频文件");
                        } else if (fileLow.endsWith(".hprof")) {
                            map.put(file2.getAbsolutePath(), "hprof日志文件");
                        } else if (fileLow.endsWith(".trace")) {
                            map.put(file2.getAbsolutePath(), "trace日志文件");
                        }
                    }

                    if (!TextUtils.isEmpty(map.get(file2.getAbsolutePath())) && appPath.getClean_type() == 0) {  //手动清理
                        map.put(file2.getAbsolutePath(), map.get(file2.getAbsolutePath()) + "_手动");
                    }
                }
            }
        }
    }



    /**
     * 筛选file下需要清理的文件夹
     *
     * @param file
     */

    private Map<String, String> checkAllGarbageFolder(final File file) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        checAllkFiles(hashMap, file, 0);
        return hashMap;
    }


    /**
     * 筛选file下需要清理的文件夹
     *
     * @param map
     * @param file
     */
    private void checAllkFiles(final Map<String, String> map, final File file, int t) {
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0 && t < 2) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {//文件夹
                    checAllkFiles(map, file2, t + 1);
                } else { //文件类型
                    if (file2.length() > 0)
                        map.put(file2.getAbsolutePath(), "残留文件");
                }
            }
        } else {
            return;
        }

    }

    /**
     * 获取系统RAM运存总大小(单位：MB)
     *
     * @return
     */
    public static int getTotalRam() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }
        return totalRam * 1024;
    }

    /**
     * 计算总的缓存大小
     *
     * @param listInfo
     */
    public int computeTotalSize(ArrayList<FirstJunkInfo> listInfo) {
        long totalSizes = 0;
        for (FirstJunkInfo firstJunkInfo : listInfo)
            totalSizes += !isCacheWhite(firstJunkInfo.getAppPackageName()) ? firstJunkInfo.getTotalSize() : 0;
        return setCleanSize(totalSizes);
    }

    /**
     * 获取缓存白名单
     */
    public boolean isCacheWhite(String packageName) {
        SharedPreferences sp = AppApplication.getInstance().getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        Set<String> sets = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_INSTALL_PACKE_NAME, new HashSet<>());
        return sets.contains(packageName);
    }

    /**
     * 使用内存占总RAM的比例
     *
     * @param totalSizes
     * @return
     */
    public int setCleanSize(long totalSizes) {
        int sizeMb = 0;
        String str_totalSize = CleanAllFileScanUtil.byte2FitSize(totalSizes);
        if (str_totalSize.endsWith("KB")) {
            sizeMb = 0;
        }
        //数字动画转换，GB转成Mb播放，kb太小就不扫描
        if (str_totalSize.endsWith("MB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
        } else if (str_totalSize.endsWith("GB")) {
            sizeMb = NumberUtils.getInteger(str_totalSize.substring(0, str_totalSize.length() - 2));
            sizeMb *= 1024;
        }
        return new BigDecimal(sizeMb).divide(new BigDecimal(FileQueryUtils.getTotalRam()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
    }

}
