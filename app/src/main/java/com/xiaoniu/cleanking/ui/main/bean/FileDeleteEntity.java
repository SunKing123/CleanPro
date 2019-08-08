package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

/**
 * Created by lang.chen on 2019/8/1
 */
public class FileDeleteEntity implements Serializable {

    //文件路径
    public String path;
    //文件大小
    public long size;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
