package com.xiaoniu.cleanking.ui.main.bean;

import com.xiaoniu.cleanking.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by sahara on 2017/4/25.
 */

public class FileEntity implements Serializable {
    public String type;
    public String size;
    public String time;
    public String path;
    public boolean isCheck;

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
