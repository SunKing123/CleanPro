package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhuang on 16/1/14.
 */
public class FirstJunkInfo implements Serializable {
    private int position;
    private String appGarbageName;
    private String appPackageName;
    private String appProcessName;
    private String appName;
    private String descp;
    private String garbageCatalog;
    private Drawable garbageIcon;
    private boolean isAllchecked;
    private boolean isApkInstalled;
    private boolean isDeploy;
    private boolean isRemoved;
    private int pid;
    private String garbageType;
    private long selectSize;
    private List<SecondJunkInfo> subGarbages = new ArrayList<>();
    private long totalSize;
    private String versionName;
    private int versionCode;
    private boolean isSelect;
    private boolean isLock;
    private String sdPath;
    private boolean isFirstItem;

    public String getAppProcessName() {
        return appProcessName;
    }

    public void setAppProcessName(String appProcessName) {
        this.appProcessName = appProcessName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getSdPath() {
        return sdPath;
    }

    public void setSdPath(String sdPath) {
        this.sdPath = sdPath;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    /**
     * 添加子类垃圾对象
     *
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

    public boolean isFirstItem() {
        return isFirstItem;
    }

    public void setFirstItem(boolean firstItem) {
        isFirstItem = firstItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FirstJunkInfo that = (FirstJunkInfo) o;

        if (position != that.position) return false;
        if (isAllchecked != that.isAllchecked) return false;
        if (isApkInstalled != that.isApkInstalled) return false;
        if (isDeploy != that.isDeploy) return false;
        if (isRemoved != that.isRemoved) return false;
        if (pid != that.pid) return false;
        if (selectSize != that.selectSize) return false;
        if (totalSize != that.totalSize) return false;
        if (versionCode != that.versionCode) return false;
        if (isSelect != that.isSelect) return false;
        if (isLock != that.isLock) return false;
        if (isFirstItem != that.isFirstItem) return false;
        if (appGarbageName != null ? !appGarbageName.equals(that.appGarbageName) : that.appGarbageName != null)
            return false;
        if (appPackageName != null ? !appPackageName.equals(that.appPackageName) : that.appPackageName != null)
            return false;
        if (appProcessName != null ? !appProcessName.equals(that.appProcessName) : that.appProcessName != null)
            return false;
        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        if (descp != null ? !descp.equals(that.descp) : that.descp != null) return false;
        if (garbageCatalog != null ? !garbageCatalog.equals(that.garbageCatalog) : that.garbageCatalog != null)
            return false;
        if (garbageIcon != null ? !garbageIcon.equals(that.garbageIcon) : that.garbageIcon != null)
            return false;
        if (garbageType != null ? !garbageType.equals(that.garbageType) : that.garbageType != null)
            return false;
        if (subGarbages != null ? !subGarbages.equals(that.subGarbages) : that.subGarbages != null)
            return false;
        if (versionName != null ? !versionName.equals(that.versionName) : that.versionName != null)
            return false;
        return sdPath != null ? sdPath.equals(that.sdPath) : that.sdPath == null;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (appGarbageName != null ? appGarbageName.hashCode() : 0);
        result = 31 * result + (appPackageName != null ? appPackageName.hashCode() : 0);
        result = 31 * result + (appProcessName != null ? appProcessName.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (descp != null ? descp.hashCode() : 0);
        result = 31 * result + (garbageCatalog != null ? garbageCatalog.hashCode() : 0);
        result = 31 * result + (garbageIcon != null ? garbageIcon.hashCode() : 0);
        result = 31 * result + (isAllchecked ? 1 : 0);
        result = 31 * result + (isApkInstalled ? 1 : 0);
        result = 31 * result + (isDeploy ? 1 : 0);
        result = 31 * result + (isRemoved ? 1 : 0);
        result = 31 * result + pid;
        result = 31 * result + (garbageType != null ? garbageType.hashCode() : 0);
        result = 31 * result + (int) (selectSize ^ (selectSize >>> 32));
        result = 31 * result + (subGarbages != null ? subGarbages.hashCode() : 0);
        result = 31 * result + (int) (totalSize ^ (totalSize >>> 32));
        result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
        result = 31 * result + versionCode;
        result = 31 * result + (isSelect ? 1 : 0);
        result = 31 * result + (isLock ? 1 : 0);
        result = 31 * result + (sdPath != null ? sdPath.hashCode() : 0);
        result = 31 * result + (isFirstItem ? 1 : 0);
        return result;
    }
}
