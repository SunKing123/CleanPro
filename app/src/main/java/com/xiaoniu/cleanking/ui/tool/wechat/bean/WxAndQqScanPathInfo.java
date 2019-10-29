package com.xiaoniu.cleanking.ui.tool.wechat.bean;

public class WxAndQqScanPathInfo {
    private String currentPath;
    private int fileType;

    public WxAndQqScanPathInfo(int i, String str) {
        this.fileType = i;
        this.currentPath = str;
    }

    public int getType() {
        return this.fileType;
    }

    public String getFilePath() {
        return this.currentPath;
    }

    public void setType(int i) {
        this.fileType = i;
    }

    public void setPath(String str) {
        this.currentPath = str;
    }
}