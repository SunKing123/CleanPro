package com.xiaoniu.cleanking.ui.main.bean;

import android.graphics.drawable.Drawable;

/**
 * @author zhengzhihao
 * @date 2019/10/14 17
 * @mail：zhengzhihao@hellogeek.com
 */
public class AppUsageInfo {

    Drawable appIcon;
    String appName, packageName;
    long timeInForeground;
    int launchCount;

    public AppUsageInfo(String pName) {
        this.packageName=pName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimeInForeground() {
        return timeInForeground;
    }

    public void setTimeInForeground(long timeInForeground) {
        this.timeInForeground = timeInForeground;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }
}
