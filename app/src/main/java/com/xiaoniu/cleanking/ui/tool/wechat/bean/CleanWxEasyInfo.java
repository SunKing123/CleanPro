package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.ui.tool.wechat.util.PrefsCleanUtil;

import java.util.ArrayList;
import java.util.List;

public class CleanWxEasyInfo {
    private List<MultiItemEntity> filterList = new ArrayList();
    private boolean isChecked;
    private boolean isFinished;
    private List<MultiItemEntity> list = new ArrayList();
    private boolean mergTemp = false;
    private List<CleanWxItemInfo> mineList = new ArrayList();
    private int selectNum;
    private long selectSize;
    private int tag = -1;
    private List<CleanWxItemInfo> tempList = new ArrayList();
    private int totalNum;
    private long totalSize;

    public CleanWxEasyInfo(int i, boolean z) {
        this.tag = i;
        this.isChecked = z;
    }

    public List<MultiItemEntity> getFilterList() {
        return this.filterList;
    }

    public void setFilterList(List<MultiItemEntity> list2) {
        this.filterList = list2;
    }

    public boolean isMergTemp() {
        return this.mergTemp;
    }

    public void setMergTemp(boolean z) {
        this.mergTemp = z;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }

    public List<MultiItemEntity> getList() {
        return this.list;
    }

    public void setList(List<MultiItemEntity> list2) {
        this.list = list2;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public List<CleanWxItemInfo> getTempList() {
        return this.tempList;
    }

    public void setTempList(List<CleanWxItemInfo> list2) {
        this.tempList = list2;
    }

    public void setTotalSize(long j) {
        this.totalSize = j;
    }

    public long getSelectSize() {
        return this.selectSize;
    }

    public void setSelectSize(long j) {
        this.selectSize = j;
    }

    public int getTotalNum() {
        return this.totalNum;
    }

    public void setTotalNum(int i) {
        this.totalNum = i;
    }

    public int getSelectNum() {
        return this.selectNum;
    }

    public void setSelectNum(int i) {
        this.selectNum = i;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void setFinished(boolean z) {
        this.isFinished = z;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int i) {
        this.tag = i;
    }

    public List<CleanWxItemInfo> getMineList() {
        return this.mineList;
    }

    public void setMineList(List<CleanWxItemInfo> list2) {
        this.mineList = list2;
    }

    public void reDataInfo() {
        switch (this.tag) {
            case 1:
                this.isChecked = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_GARBAGE_FILES_CHECKED, true);
                break;
            case 2:
                this.isChecked = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_FACE_CACHE_CHECKED, true);
                break;
            case 3:
                this.isChecked = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_OTHERS_CACHE_CHECKED, true);
                break;
            case 4:
                this.isChecked = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_WX_FRIENDS_CACHE_CHECKED, true);
                break;
        }
        this.isFinished = false;
        this.mergTemp = false;
        this.totalSize = 0;
        this.selectSize = 0;
        this.totalNum = 0;
        this.selectNum = 0;
        this.list.clear();
        this.tempList.clear();
        this.filterList.clear();
        this.mineList.clear();
    }
}