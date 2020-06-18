package com.xiaoniu.cleanking.bean;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

/**
 * @author zhengzhihao
 * @date 2020/6/18 11
 * @mail：zhengzhihao@hellogeek.com
 */
public class ApkFileInfo {

    private Drawable apk_icon;
    private String packageName;
    private String filePath;
    private String versionName;
    private int versionCode;
    private int installed ;
    private PackageInfo packageInfo;
    private long apkSize;

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public int getInstalled() {
        return installed;
    }

    public void setInstalled(int installed) {
        this.installed = installed;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getApk_icon() {
        return apk_icon;
    }

    public void setApk_icon(Drawable apk_icon) {
        this.apk_icon = apk_icon;
    }
}
