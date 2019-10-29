package com.xiaoniu.cleanking.ui.main.bean;

import java.io.File;
import java.io.Serializable;

public class BigFileInfoEntity implements Serializable {

    private File mFile;

    private long size;

    private String content;

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }
}
