package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

public class FilePathInfoClean implements Serializable {
    private String appName;
    private String filePath;
    private String garbageName;
    private String garbagetype;
    private long id;
    private String packageName;
    private String rootPath;

    public String toString() {
        return "包名:" + this.packageName + ",类型:" + this.garbagetype + "垃圾名：" + this.garbageName + ",path:" + this.filePath + ",rootPath:" + this.rootPath;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String str) {
        this.rootPath = str;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public String getGarbagetype() {
        return this.garbagetype;
    }

    public void setGarbagetype(String str) {
        this.garbagetype = str;
    }

    public String getGarbageName() {
        return this.garbageName;
    }

    public void setGarbageName(String str) {
        this.garbageName = str;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }
}
