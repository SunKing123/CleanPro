package com.xiaoniu.cleanking.base;

import com.comm.jksdk.utils.MmkvUtil;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.utils.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Singleton;

/**
 * @author zhengzhihao
 * @date 2020/6/12 08
 * @mail：zhengzhihao@hellogeek.com
 */
public class ScanDataHolder {

    private volatile static  ScanDataHolder scanDataHolder;

    private ScanDataHolder(){}

    public static ScanDataHolder getInstance() {
        if (scanDataHolder == null) {
            synchronized (ScanDataHolder.class) {           //线程安全
                if (scanDataHolder == null) {
                    scanDataHolder = new ScanDataHolder();
                }
            }
        }
        return scanDataHolder;
    }

    private int scanState = 0;
    private CountEntity mCountEntity;
    private int scanningFileCount = 0;
    private long prevScanTime = 0;  //扫描缓存时间
    private LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups;   //扫描缓存
    private LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap;


    public int getScanState() {
        if (System.currentTimeMillis() - prevScanTime > 5 * 60 * 1000) {  //五分钟缓存
            scanState = 0;
        }
        return scanState;
    }

    public void setScanState(int scanState) {
        LogUtils.i("zz----setScanState()--"+scanState);

        this.scanState = scanState;
    }

    public CountEntity getmCountEntity() {
        return mCountEntity;
    }

    public void setmCountEntity(CountEntity mCountEntity) {
        this.mCountEntity = mCountEntity;
    }

    public LinkedHashMap<ScanningResultType, JunkGroup> getmJunkGroups() {
        return mJunkGroups;
    }

    public void setmJunkGroups(LinkedHashMap<ScanningResultType, JunkGroup> mJunkGroups) {
        this.mJunkGroups = mJunkGroups;
        prevScanTime = System.currentTimeMillis();
    }

    public LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> getJunkContentMap() {
        return junkContentMap;
    }

    public void setJunkContentMap(LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap) {
        this.junkContentMap = junkContentMap;
    }

    public int getScanningFileCount() {
        return scanningFileCount;
    }

    public void setScanningFileCount(int scanningFileCount) {
        this.scanningFileCount = scanningFileCount;
    }
}
