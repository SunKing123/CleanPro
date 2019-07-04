package com.xiaoniu.cleanking.ui.main.bean;

public class AppInfoClean {
    private String appName;
    private long id;
    private String packageName;

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public String toString() {
        return "packname:" + this.packageName + ",appname:" + this.appName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }
}
