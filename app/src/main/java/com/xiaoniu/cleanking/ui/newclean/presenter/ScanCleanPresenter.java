package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.xiaoniu.cleanking.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanCleanContact;
import com.xiaoniu.cleanking.ui.newclean.model.ScanCleanModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.FileUtils;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScanCleanPresenter extends BasePresenter<ScanCleanContact.View, ScanCleanModel>
        implements ScanCleanContact.IPresenter {

    private LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap;
    private boolean isCacheCheckAll = true;  //运行内存是否全选
    private boolean isCheckAll = true;  //运行内存是否全选
    private NoClearSPHelper mSPHelper;

    public ScanCleanPresenter() {
        mSPHelper = new NoClearSPHelper();
    }

    @Override
    public void startClean(LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap) {
        this.junkContentMap = junkContentMap;

        if (CollectionUtils.isEmpty(junkContentMap)) {
            return;
        }

        //设置垃圾清理总量数据
        long junkTotal = setCheckedJunkResult();
        CountEntity countEntity = CleanUtil.formatShortFileSize(junkTotal);
        if (getView() != null) {
            //统计清理总数
            getView().setTotalJunkCount(countEntity.getTotalSize(), countEntity.getUnit());

            //开始清理垃圾
            getView().setStartCleanJunk(Float.parseFloat(countEntity.getTotalSize()), countEntity.getUnit());
        }

        clearAll();
    }

    /**
     * 删除文件
     */
    @SuppressLint("CheckResult")
    private void clearAll() {
        if (junkContentMap == null || junkContentMap.size() < 1) {
            if (getView() != null) {
                getView().setCleanFinish();
            }
            return;
        }
        Observable.create(e -> {
            long total = 0;
            for (Map.Entry<ScanningResultType, ArrayList<FirstJunkInfo>> entry : junkContentMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().size() > 0) {
                    if (ScanningResultType.MEMORY_JUNK.equals(entry.getKey())) { //运行内存
                        for (FirstJunkInfo info : entry.getValue()) {
                            if (info.isAllchecked()) {
                                total += info.getTotalSize();
                                CleanUtil.killAppProcesses(info.getAppPackageName(), info.getPid());
                            } else {
                                isCheckAll = isCacheCheckAll = false;
                            }
                        }
                    } else if (ScanningResultType.CACHE_JUNK.equals(entry.getKey())) { //缓存
                        for (FirstJunkInfo info : entry.getValue()) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long l = CleanUtil.freeJunkInfos(entry.getValue());
                        total += l;
                    } else if (ScanningResultType.AD_JUNK.equals(entry.getKey())) { //ad文件
                        for (FirstJunkInfo info : entry.getValue()) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long l1 = CleanUtil.freeJunkInfos(entry.getValue());
                        total += l1;
                    } else if (ScanningResultType.UNINSTALL_JUNK.equals(entry.getKey())) {//残留垃圾
                        for (FirstJunkInfo info : entry.getValue()) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }

                        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/data/";
                        for (FirstJunkInfo info : entry.getValue()) {
                            File rootPathFile = new File(rootPath + info.getAppPackageName());
                            if (rootPathFile.exists()) {
                                com.xiaoniu.common.utils.FileUtils.deleteFileAndFolder(rootPathFile);
                            }
                        }
                        long leavedCache = CleanUtil.freeJunkInfos(entry.getValue());
                        total += leavedCache;
                    } else if (ScanningResultType.APK_JUNK.equals(entry.getKey())) { //apk垃圾
                        for (FirstJunkInfo info : entry.getValue()) {
                            if (!info.isAllchecked()) {
                                isCheckAll = false;
                                break;
                            }
                        }
                        long leavedCache = CleanUtil.freeJunkInfos(entry.getValue());
                        total += leavedCache;
                    }
                }
            }
            PreferenceUtil.saveIsCheckedAll(isCheckAll);
            PreferenceUtil.saveCacheIsCheckedAll(isCacheCheckAll);
            PreferenceUtil.saveMulCacheNum(PreferenceUtil.getMulCacheNum() * 0.3f);
            e.onNext(total);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            //清理完成，存储时间点
            CleanEvent cleanEvent = new CleanEvent();
            cleanEvent.setCleanAminOver(true);
            EventBus.getDefault().post(cleanEvent);
            mSPHelper.saveCleanTime(System.currentTimeMillis());

            if (getView() != null) {
                getView().setCleanJunkOver();
            }
        });
    }

    /**
     * 获取选中的垃圾清理量
     */
    private long setCheckedJunkResult() {
        long checkedTotalSize = 0;
        for (Map.Entry<ScanningResultType, ArrayList<FirstJunkInfo>> contentMap : junkContentMap.entrySet()) {
            if (!CollectionUtils.isEmpty(contentMap.getValue())) {
                for (FirstJunkInfo firstJunkInfo : contentMap.getValue()) {
                    if (firstJunkInfo.isAllchecked()) {
                        checkedTotalSize += firstJunkInfo.getTotalSize();
                    }
                }
            }
        }
        return checkedTotalSize;
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    @Override
    public void getAccessListBelow() {
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
                    if (getView() != null) {
                        getView().getAccessListBelow(strings);
                    }
                });
    }
}
