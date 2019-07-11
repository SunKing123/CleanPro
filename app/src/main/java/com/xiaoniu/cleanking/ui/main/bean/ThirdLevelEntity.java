package com.xiaoniu.cleanking.ui.main.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanExpandAdapter;

import java.io.File;

public class ThirdLevelEntity implements MultiItemEntity {


    private File mFile;

    private long size;

    private String content;

    private boolean isChecked;

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int getItemType() {
        return CleanExpandAdapter.TYPE_LEVEL_3;
    }
}
