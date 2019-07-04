package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhuang on 16/1/14.
 */
public class FirstJunkInfo implements Parcelable {
    private String appGarbageName;
    private String appPackageName;
    private String appName;
    private String descp;
    private String garbageCatalog;
    private Bitmap garbageIcon;
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

    public Bitmap getGarbageIcon() {
        return garbageIcon;
    }

    public void setGarbageIcon(Bitmap garbageIcon) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appGarbageName);
        dest.writeString(this.appPackageName);
        dest.writeString(this.appName);
        dest.writeString(this.descp);
        dest.writeString(this.garbageCatalog);
        dest.writeParcelable(this.garbageIcon, flags);
        dest.writeByte(this.isAllchecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isApkInstalled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeploy ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRemoved ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pid);
        dest.writeInt(this.position);
        dest.writeString(this.garbageType);
        dest.writeLong(this.selectSize);
        dest.writeList(this.subGarbages);
        dest.writeLong(this.totalSize);
    }

    public FirstJunkInfo() {
    }

    protected FirstJunkInfo(Parcel in) {
        this.appGarbageName = in.readString();
        this.appPackageName = in.readString();
        this.appName = in.readString();
        this.descp = in.readString();
        this.garbageCatalog = in.readString();
        this.garbageIcon = in.readParcelable(Drawable.class.getClassLoader());
        this.isAllchecked = in.readByte() != 0;
        this.isApkInstalled = in.readByte() != 0;
        this.isDeploy = in.readByte() != 0;
        this.isRemoved = in.readByte() != 0;
        this.pid = in.readInt();
        this.position = in.readInt();
        this.garbageType = in.readString();
        this.selectSize = in.readLong();
        this.subGarbages = new ArrayList<SecondJunkInfo>();
        in.readList(this.subGarbages, SecondJunkInfo.class.getClassLoader());
        this.totalSize = in.readLong();
    }

    public static final Parcelable.Creator<FirstJunkInfo> CREATOR = new Parcelable.Creator<FirstJunkInfo>() {
        @Override
        public FirstJunkInfo createFromParcel(Parcel source) {
            return new FirstJunkInfo(source);
        }

        @Override
        public FirstJunkInfo[] newArray(int size) {
            return new FirstJunkInfo[size];
        }
    };
}
