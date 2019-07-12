package com.xiaoniu.cleanking.ui.main.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanExpandAdapter;

public class SecondLevelEntity extends AbstractExpandableItem<ThirdLevelEntity> implements MultiItemEntity {

    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_ZIP = 2;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_IMAGE = 4;
    public static final int TYPE_WORD = 5;
    public static final int TYPE_APK = 6;


    private String name;

    private long totalSize = 0;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void addSize(long size) {
        totalSize += size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public int getItemType() {
        return CleanExpandAdapter.TYPE_LEVEL_2;
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
