package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhuang on 16/1/14.
 */
public class FirstJunkInfo implements Serializable {
    private String appGarbageName;
    private String appPackageName;
    private String appName;
    private String descp;
    private String garbageCatalog;
    private Drawable garbageIcon;
    private boolean isAllchecked;
    private boolean isApkInstalled;
    private boolean isDeploy;
    private boolean isRemoved;
    private int pid;
    private int position;
    private String garbageType;
    private long selectSize;
    private List<SecondJunkInfo> subGarbages = new ArrayList<>();
    private long totalSize;
    private String versionName;
    private int versionCode;
    private boolean isSelect=true;

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    /**
     * 添加子类垃圾对象
     * @param secondJunkInfo
     */
    public void addSecondJunk(SecondJunkInfo secondJunkInfo) {
        subGarbages.add(secondJunkInfo);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppGarbageName() {
        return appGarbageName;
    }

    public void setAppGarbageName(String appGarbageName) {
        this.appGarbageName = appGarbageName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public String getGarbageCatalog() {
        return garbageCatalog;
    }

    public void setGarbageCatalog(String garbageCatalog) {
        this.garbageCatalog = garbageCatalog;
    }

    public Drawable getGarbageIcon() {
        return garbageIcon;
    }

    public void setGarbageIcon(Drawable garbageIcon) {
        this.garbageIcon = garbageIcon;
    }

    public boolean isAllchecked() {
        return isAllchecked;
    }

    public void setAllchecked(boolean allchecked) {
        isAllchecked = allchecked;
    }

    public boolean isApkInstalled() {
        return isApkInstalled;
    }

    public void setApkInstalled(boolean apkInstalled) {
        isApkInstalled = apkInstalled;
    }

    public boolean isDeploy() {
        return isDeploy;
    }

    public void setDeploy(boolean deploy) {
        isDeploy = deploy;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getGarbageType() {
        return garbageType;
    }

    public void setGarbageType(String garbageType) {
        this.garbageType = garbageType;
    }

    public long getSelectSize() {
        return selectSize;
    }

    public void setSelectSize(long selectSize) {
        this.selectSize = selectSize;
    }

    public List<SecondJunkInfo> getSubGarbages() {
        return subGarbages;
    }

    public void setSubGarbages(List<SecondJunkInfo> subGarbages) {
        this.subGarbages = subGarbages;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

}
