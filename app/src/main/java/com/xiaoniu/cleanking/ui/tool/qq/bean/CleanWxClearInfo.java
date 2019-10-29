package com.xiaoniu.cleanking.ui.tool.qq.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.Date;

public class CleanWxClearInfo implements MultiItemEntity, Serializable {
    private String appName;
    private Date date;
    private double definition;
    private String fileName;
    private String filePath;
    private int id;
    private boolean isChecked;
    private boolean isOptimal;
    private boolean isPicLoadFaile = true;
    private String packageName;
    private int position;
    private long size;
    private long time;
    private int type;
    private boolean isSelect;
    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }
    public boolean isPicLoadFaile() {
        return this.isPicLoadFaile;
    }

    public void setPicLoadFaile(boolean z) {
        this.isPicLoadFaile = z;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public int getItemType() {
        return 0;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int i) {
        this.position = i;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date2) {
        this.date = date2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }

    public boolean isOptimal() {
        return this.isOptimal;
    }

    public void setOptimal(boolean z) {
        this.isOptimal = z;
    }

    public double getDefinition() {
        return this.definition;
    }

    public void setDefinition(double d) {
        this.definition = d;
    }

    public String toString() {
        return "CleanWxClearInfo{id=" + this.id + ", appName='" + this.appName + '\'' + ", packageName='" + this.packageName + '\'' + ", type=" + this.type + ", isChecked=" + this.isChecked + ", filePath='" + this.filePath + '\'' + ", size=" + this.size + '}';
    }
}