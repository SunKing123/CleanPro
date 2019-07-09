package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

public class CountEntity implements Serializable {

    private String totalSize;

    private String unit;

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
