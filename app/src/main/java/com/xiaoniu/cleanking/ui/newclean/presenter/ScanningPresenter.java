package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.trello.rxlifecycle2.android.FragmentEvent;
import com.xiaoniu.cleanking.base.ScanDataHolder;
import com.xiaoniu.cleanking.bean.JunkWrapper;
import com.xiaoniu.cleanking.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.bean.SecondJunkInfo;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningLevel;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanningContact;
import com.xiaoniu.cleanking.ui.newclean.model.ScanningModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScanningPresenter extends BasePresenter<ScanningContact.View, ScanningModel>
        implements ScanningContact.IPresenter {

    private LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups = new LinkedHashMap<>();
    private FileQueryUtils mFileQueryUtils;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long totalJunk = 0;
    private ScanningLevel currentLevel;
    private long scanningStartTime;
    private int fileCount = 0;

    /**
     * 监听扫描状态
     */
    private void initScanningListener() {
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {

            @Override
            public void currentNumber() {
            }

            @Override
            public void increaseSize(long increaseSize) {
                totalJunk += increaseSize;
                mHandler.post(() -> {
                    if (getView() != null) {
                        final CountEntity countEntity = CleanUtil.formatShortFileSize(totalJunk);
                        getView().setScanningJunkTotal(countEntity.getTotalSize(), countEntity.getUnit());
                        if (countEntity.getNumber() < 1024 * 1024 * 50) {
                            if (currentLevel == ScanningLevel.Little) {
                                return;
                            }
                            currentLevel = ScanningLevel.Little;
                            getView().setScanningBackgroundColor(ScanningLevel.Little.getColor(), ScanningLevel.Little.getColor());
                        } else if (countEntity.getNumber() >= 1024 * 1024 * 50 && countEntity.getNumber() < 1024 * 1024 * 100) {
                            if (currentLevel == ScanningLevel.Middle) {
                                return;
                            }
                            currentLevel = ScanningLevel.Middle;
                            getView().setScanningBackgroundColor(ScanningLevel.Little.getColor(), ScanningLevel.Middle.getColor());
                        } else {
                            if (currentLevel == ScanningLevel.Large) {
                                return;
                            }
                            currentLevel = ScanningLevel.Large;
                            getView().setScanningBackgroundColor(ScanningLevel.Middle.getColor(), ScanningLevel.Large.getColor());
                        }
                    }
                });
            }

            @Override
            public void scanFile(String filePath) {
                mHandler.post(() -> {
                    if (getView() != null && !TextUtils.isEmpty(filePath)) {
                        getView().setScanningFilePath(filePath);
                    }
                });
            }
        });
    }

    @Override
    public void readyScanningJunk() {
        mFileQueryUtils = new FileQueryUtils();
        //初始化扫描模型
        initScanningJunkModel();
        //初始化扫描动画
        initScanningJunkAnimator();
        //初始化扫描监听
        initScanningListener();
    }

    @SuppressLint("CheckResult")
    @Override
    public void scanningJunk() {
        ScanDataHolder.getInstance().setScanState(10);
        scanningStartTime = System.currentTimeMillis();
        Observable.create(e -> {
            //扫描进程占用内存情况
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(new JunkWrapper(ScanningResultType.MEMORY_JUNK, runningProcess));

            //扫描apk安装包
            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getView().bindFragmentEvent(FragmentEvent.DESTROY))
                .subscribe(this::dispatchScanningJunkResult);
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
                setAdJunkResult(wrapper);
            }
        }

        if (scanningResult instanceof String && "FINISH".equals(scanningResult)) {
            if (getView() != null) {
                JunkGroup junkGroup = mJunkGroups.get(ScanningResultType.MEMORY_JUNK);
                if (junkGroup != null) {
                    junkGroup.isScanningOver = true;
                }
                final List<JunkGroup> scanningModelList = new ArrayList<>(mJunkGroups.values());

                getView().setInitScanningModel(scanningModelList);
                getView().setScanningFinish(mJunkGroups);

                //计算总的扫描时间，并回传记录
                long scanningCountTime = System.currentTimeMillis() - scanningStartTime;
                getView().setScanningCountTime(scanningCountTime);

                //计算扫描文件总数
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
        updateScanningModelState();
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
        updateScanningModelState();
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
        updateScanningModelState();
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
        updateScanningModelState();
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
        updateScanningModelState();
    }

    /**
     * 初始化扫描动画
     */
    private void initScanningJunkAnimator() {

        //初始化扫描背景展示颜色
        if (getView() != null) {
            getView().setScanningBackgroundColor(ScanningLevel.Little.getColor(), ScanningLevel.Little.getColor());
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

        //界面中渲染初始化状态
        updateScanningModelState();
    }

    private void updateScanningModelState() {
        if (getView() != null) {
            final List<JunkGroup> scanningModelList = new ArrayList<>(mJunkGroups.values());
            getView().setInitScanningModel(scanningModelList);
        }
    }
}
