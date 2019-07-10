package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

/**
 * Created by hc on 2019/6/28.
 */

public class MediaBean implements Serializable {
    public String path;
    public String thumbPath;
    public long size;
    public int duration;

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String displayName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MediaBean(Object p0, String path, int size, String displayName) {
        this.path = path;
        this.size = size;
        this.displayName = displayName;
    }

    public MediaBean(Object p0, String path, String thumbPath, int duration, long size, String displayName) {
        this.thumbPath = thumbPath;
        this.path = path;
        this.duration = duration;
        this.size = size;
        this.displayName = displayName;
    }

    public enum Type {
        Image,
        Video
    }
}
