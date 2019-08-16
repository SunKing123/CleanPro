package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by mazhuang on 16/1/14.
 */
public class AboveJunkInfo implements Serializable {
    private String appPackageName;
    private String appName;
    private Drawable garbageIcon;
    private long totalSize;
    private boolean isSelect=true;

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getGarbageIcon() {
        return garbageIcon;
    }

    public void setGarbageIcon(Drawable garbageIcon) {
        this.garbageIcon = garbageIcon;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
