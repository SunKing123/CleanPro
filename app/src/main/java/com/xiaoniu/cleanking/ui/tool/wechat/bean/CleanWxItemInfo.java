package com.xiaoniu.cleanking.ui.tool.wechat.bean;

import java.io.File;

public class CleanWxItemInfo {
    private int Days;
    private boolean canLoadPic = true;
    private File file;
    private long fileSize = 0;
    private int fileType;
    private boolean isChecked;
    private boolean isSelect;

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    private String stringDay;

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long j) {
        this.fileSize = j;
    }

    public String getStringDay() {
        return this.stringDay;
    }

    public void setStringDay(String str) {
        this.stringDay = str;
    }

    public int getDays() {
        return this.Days;
    }

    public void setDays(int i) {
        this.Days = i;
    }

    public boolean isCanLoadPic() {
        return this.canLoadPic;
    }

    public void setCanLoadPic(boolean z) {
        this.canLoadPic = z;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file2) {
        this.file = file2;
    }

    public int getFileType() {
        return this.fileType;
    }

    public void setFileType(int i) {
        this.fileType = i;
    }

    public String toString() {
        return "CleanWxItemInfo{isChecked=" + this.isChecked + ", file=" + this.file.getAbsolutePath() + ", fileType=" + this.fileType + ", canLoadPic=" + this.canLoadPic + ", Days=" + this.Days + '}';
    }
}