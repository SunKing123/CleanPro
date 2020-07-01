package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.bean.JunkWrapper;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningLevel;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewPlusCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewPlusCleanMainPresenter extends RxPresenter<NewPlusCleanMainFragment, NewScanModel> {

    private FileQueryUtils mFileQueryUtils;
    private CompositeDisposable compositeDisposable;
    private LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups = new LinkedHashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Inject
    public NewPlusCleanMainPresenter() {
        compositeDisposable = new CompositeDisposable();
    }

    private int fileCount = 0;

    private long totalJunk = 0;

    boolean isScaning = false;   //避免重复扫描

    /**
     * 互动式广告开关
     */
    public void getInteractionSwitch() {
        mModel.getInteractionSwitch(new Common4Subscriber<InteractionSwitchList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InteractionSwitchList switchInfoList) {
                for (InteractionSwitchList.DataBean dataBean : switchInfoList.getData()) {
                    if (TextUtils.equals(dataBean.getSwitcherKey(), "page_lock")) {
                        try {
                            MmkvUtil.saveString(PositionId.LOCK_INTERACTIVE, JSONObject.toJSONString(dataBean));
                            Log.e("dong", JSONObject.toJSONString(dataBean));
                        } catch (Exception e) {
                        }
                    }
                }
                mView.getInteractionSwitchSuccess(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

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
//                    mView.cancelLoadingDialog();
                    try {
                       // mView.getAccessListBelow(strings);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void readyScanningJunk() {
        if (isScaning == true)
            return;
        LogUtils.i("readyScanningJunk()");
        mFileQueryUtils = new FileQueryUtils();
        //初始化扫描模型
        initScanningJunkModel();
        //初始化扫描监听
        initScanningListener();

    }

    /**
     * 检查文件存贮权限
     */
    @SuppressLint("CheckResult")
    public void checkStoragePermission() {
        if (mView.getActivity() == null || mView.getActivity().isFinishing()) {
            return;
        }
        //动画开始播放
//        mView.startScan();
        LogUtils.i("checkStoragePermission()");
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        new RxPermissions(mView.getActivity()).request(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LogUtils.i("checkStoragePermission()---true");
                        readyScanningJunk();
                        scanningJunk();
                    } else {
                        if (hasPermissionDeniedForever()) {//点击拒绝
                            LogUtils.i("checkStoragePermission()---denied");
                            mView.permissionDenied();
                        } else {//点击永久拒绝
                            LogUtils.i("checkStoragePermission()---denied--faile");
                            //mView.showPermissionDialog();
                        }
                    }
                });
    }

    /**
     * 是否有权限被永久拒绝
     */
    private boolean hasPermissionDeniedForever() {
        boolean hasDeniedForever = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mView.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasDeniedForever = true;
            }
        }
        return hasDeniedForever;
    }


    /**
     * 监听扫描状态
     */
    private void initScanningListener() {
        if (isScaning == true)
            return;
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }

        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {

            @Override
            public void currentNumber() {
            }

            @Override
            public void increaseSize(long increaseSize) {
                totalJunk += increaseSize;
                mHandler.post(() -> {
                    if (mView != null)
                        mView.setScanningJunkTotal(totalJunk);
                });
            }

            @Override
            public void scanFile(String filePath) {

            }
        });
    }

    private Disposable disposable;

    @SuppressLint("CheckResult")
    public void scanningJunk() {
        fileCount = 0;
        if (isScaning == true)
            return;
        isScaning = true;
        disposable = Observable.create(e -> {
            try {
                //扫描进程占用内存情况
                ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
                e.onNext(new JunkWrapper(ScanningResultType.MEMORY_JUNK, runningProcess));

                //扫描apk安装包
                List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFileByDb();
                if (CollectionUtils.isEmpty(apkJunkInfos)) {
                    apkJunkInfos.addAll(mFileQueryUtils.queryAPkFile());
                }
                e.onNext(new JunkWrapper(ScanningResultType.APK_JUNK, apkJunkInfos));

                //扫描卸载残余垃圾
                ArrayList<FirstJunkInfo> leaveDataInfo = mFileQueryUtils.getOmiteCache();
                e.onNext(new JunkWrapper(ScanningResultType.UNINSTALL_JUNK, leaveDataInfo));

                //缓存垃圾
                HashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkResultMap = mFileQueryUtils.getFileJunkResult();
                if (!CollectionUtils.isEmpty(junkResultMap)) {
                    ArrayList<FirstJunkInfo> adJunkInfoList = junkResultMap.get(ScanningResultType.AD_JUNK);
                    ArrayList<FirstJunkInfo> cacheJunkInfoList = junkResultMap.get(ScanningResultType.CACHE_JUNK);
                    e.onNext(new JunkWrapper(ScanningResultType.CACHE_JUNK, cacheJunkInfoList));
                    e.onNext(new JunkWrapper(ScanningResultType.AD_JUNK, adJunkInfoList));
                }

                //扫描完成表示
                e.onNext("FINISH");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::dispatchScanningJunkResult);

        compositeDisposable.add(disposable);

    }

    //清除扫描任务
    public void stopScanning() {
        fileCount = 0;
        totalJunk = 0;
        isScaning = false;
        ScanDataHolder.getInstance().setScanState(0);
        if (null != disposable && !disposable.isDisposed()) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        if (null != compositeDisposable && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }

    /**
     * 分发扫描结果
     */
    private void dispatchScanningJunkResult(Object scanningResult) {

        if (scanningResult instanceof JunkWrapper) {
            JunkWrapper wrapper = (JunkWrapper) scanningResult;
            if (wrapper.type == ScanningResultType.UNINSTALL_JUNK) {
                setUninstallJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.APK_JUNK) {
                setApkJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.MEMORY_JUNK) {
                setMemoryJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.CACHE_JUNK) {
                setCacheJunkResult(wrapper);
            } else if (wrapper.type == ScanningResultType.AD_JUNK) {
//                setAdJunkResult(wrapper);
            }
        }

        if (scanningResult instanceof String && "FINISH".equals(scanningResult)) {
            if (mView != null) {
                JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.MEMORY_JUNK);
                if (junkGroup != null) {
                    junkGroup.isScanningOver = true;
                }
//                final List<JunkGroup> scanningModelList = new ArrayList<>(mJunkGroups.values());
                mView.setScanningFinish(mJunkGroups);
                isScaning = false;
//                getView().setInitScanningModel(scanningModelList);
//                //计算总的扫描时间，并回传记录
//                long scanningCountTime = System.currentTimeMillis() - scanningStartTime;
//                getView().setScanningCountTime(scanningCountTime);

//                //计算扫描文件总数
//                getView().setScanningFileCount(fileCount);
            }
        }

    }


    /**
     * 回传广告垃圾
     */
    private void setAdJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.AD_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传缓存垃圾
     */
    private void setCacheJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.CACHE_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();

                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传卸载残余垃圾
     */
    private void setUninstallJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.UNINSTALL_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();

                if (info.getSubGarbages() != null && info.getSubGarbages().size() > 0) {
                    for (SecondJunkInfo secondJunk : info.getSubGarbages()) {
                        fileCount += secondJunk.getFilesCount();
                    }
                }
            }
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传无用安装包垃圾
     */
    private void setApkJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.APK_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
            }
            fileCount += mJunkGroups.size();
            junkGroup.isScanningOver = true;
        }
    }

    /**
     * 回传内存垃圾
     */
    private void setMemoryJunkResult(JunkWrapper wrapper) {
        List<FirstJunkInfo> firstJunkList = wrapper.junkInfoList;
        JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.MEMORY_JUNK);
        if (junkGroup != null) {
            for (FirstJunkInfo info : firstJunkList) {
                junkGroup.mChildren.add(info);
                junkGroup.mSize += info.getTotalSize();
            }
        }
    }


    /**
     * 初始化扫描模型
     */
    private void initScanningJunkModel() {
        //缓存垃圾
        mJunkGroups.put(ScanningResultType.CACHE_JUNK, new JunkGroup(ScanningResultType.CACHE_JUNK.getTitle(),
                ScanningResultType.CACHE_JUNK.getType()));

        //卸载残留
        mJunkGroups.put(ScanningResultType.UNINSTALL_JUNK, new JunkGroup(ScanningResultType.UNINSTALL_JUNK.getTitle(),
                ScanningResultType.UNINSTALL_JUNK.getType()));

        //广告垃圾
        mJunkGroups.put(ScanningResultType.AD_JUNK, new JunkGroup(ScanningResultType.AD_JUNK.getTitle(),
                ScanningResultType.AD_JUNK.getType()));

        //无用安装包
        mJunkGroups.put(ScanningResultType.APK_JUNK, new JunkGroup(ScanningResultType.APK_JUNK.getTitle(),
                ScanningResultType.APK_JUNK.getType()));

        //内存加速
        mJunkGroups.put(ScanningResultType.MEMORY_JUNK, new JunkGroup(ScanningResultType.MEMORY_JUNK.getTitle(),
                ScanningResultType.MEMORY_JUNK.getType()));

    }
}
