package com.xiaoniu.cleanking.ui.main.presenter;

import android.annotation.SuppressLint;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.fragment.CleanMainFragment;
import com.xiaoniu.cleanking.ui.main.model.CleanMainModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xiaoniu.cleanking.utils.ResourceUtils.getString;

public class CleanMainPresenter extends RxPresenter<CleanMainFragment,CleanMainModel> {

    @Inject
    public CleanMainPresenter() {
    }

    private HashMap<Integer, JunkGroup> mJunkGroups = null;

    long total = 0;

    /**
     * 开始进行文件扫描
     */
    @SuppressLint("CheckResult")
    public void startScan() {

        mJunkGroups = new HashMap<>();

        JunkGroup cacheGroup = new JunkGroup();
        cacheGroup.mName = getString(R.string.cache_clean);
        cacheGroup.isChecked = true;
        cacheGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);

        JunkGroup processGroup = new JunkGroup();
        processGroup.mName = getString(R.string.process_clean);
        processGroup.isChecked = true;
        processGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_PROCESS, processGroup);

        JunkGroup apkGroup = new JunkGroup();
        apkGroup.mName = getString(R.string.apk_clean);
        apkGroup.isChecked = true;
        apkGroup.mChildren = new ArrayList<>();
        mJunkGroups.put(JunkGroup.GROUP_APK, apkGroup);

        FileQueryUtils mFileQueryUtils = new FileQueryUtils();


        //文件加载进度回调
        mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
            @Override
            public void currentNumber() {

            }

            @Override
            public void increaseSize(long p0) {
                total += p0;
                mView.showCountNumber(total);
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

        Observable.create(e -> {
            ArrayList<FirstJunkInfo> runningProcess = mFileQueryUtils.getRunningProcess();
            e.onNext(runningProcess);

            List<FirstJunkInfo> apkJunkInfos = mFileQueryUtils.queryAPkFile();
            e.onNext(apkJunkInfos);
            ArrayList<FirstJunkInfo> androidDataInfo = mFileQueryUtils.getAndroidDataInfo();
            e.onNext(androidDataInfo);
            e.onNext("FINISH");
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if(o instanceof ArrayList) {
                ArrayList<FirstJunkInfo> a = (ArrayList<FirstJunkInfo>) o;
                for (FirstJunkInfo info : a) {
                    if ("TYPE_CACHE".equals(info.getGarbageType())) {
                        JunkGroup cacheGroup1 = mJunkGroups.get(JunkGroup.GROUP_CACHE);
                        cacheGroup1.mChildren.add(info);
                        cacheGroup1.mSize += info.getTotalSize();
                    } else if ("TYPE_PROCESS".equals(info.getGarbageType())) {
                        JunkGroup cacheGroup1 = mJunkGroups.get(JunkGroup.GROUP_PROCESS);
                        cacheGroup1.mChildren.add(info);
                        cacheGroup1.mSize += info.getTotalSize();
                    } else if ("TYPE_APK".equals(info.getGarbageType())) {
                        JunkGroup cacheGroup1 = mJunkGroups.get(JunkGroup.GROUP_APK);
                        cacheGroup1.mChildren.add(info);
                        cacheGroup1.mSize += info.getTotalSize();
                    }
                }
            }else {
                mView.scanFinish(mJunkGroups);
            }
        });




    }
}
