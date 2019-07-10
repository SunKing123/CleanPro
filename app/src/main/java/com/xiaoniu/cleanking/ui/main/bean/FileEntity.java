package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by sahara on 2017/4/25.
 */

public class FileEntity implements Serializable {
    public String size;
    public String path;
    public boolean isSelect = false;

    public FileEntity(String size, String path) {
        this.size = size;
        this.path = path;
    }

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
