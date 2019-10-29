package com.xiaoniu.clean.function_lib.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class SecondJunkInfo implements Serializable {
    private String appGarbageName;
    private String filecatalog;
    private int filesCount;
    private Drawable garbageIcon;
    private String garbageName;
    private long garbageSize;
    private String garbagetype;
    private boolean isChecked;
    private String packageName;
    
    public String getAppGarbageName() {
        return this.appGarbageName;
    }
    
    public String getFilecatalog() {
        return this.filecatalog;
    }
    
    public int getFilesCount() {
        return this.filesCount;
    }
    
    public Drawable getGarbageIcon() {
        return this.garbageIcon;
    }
    
    public String getGarbageName() {
        return this.garbageName;
    }
    
    public long getGarbageSize() {
        return this.garbageSize;
    }
    
    public String getGarbagetype() {
        return this.garbagetype;
    }
    
    public int getItemType() {
        return 2;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public boolean isChecked() {
        return this.isChecked;
    }
    
    public void setAppGarbageName(final String appGarbageName) {
        this.appGarbageName = appGarbageName;
    }
    
    public void setChecked(final boolean isChecked) {
        this.isChecked = isChecked;
    }
    
    public void setFilecatalog(final String filecatalog) {
        this.filecatalog = filecatalog;
    }
    
    public void setFilesCount(final int filesCount) {
        this.filesCount = filesCount;
    }
    
    public void setGarbageIcon(final Drawable garbageIcon) {
        this.garbageIcon = garbageIcon;
    }
    
    public void setGarbageName(final String garbageName) {
        this.garbageName = garbageName;
    }
    
    public void setGarbageSize(final long garbageSize) {
        this.garbageSize = garbageSize;
    }
    
    public void setGarbagetype(final String garbagetype) {
        this.garbagetype = garbagetype;
    }
    
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }
}
