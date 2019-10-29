package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

public class CountEntity implements Serializable {

    /**
     * 数值 eg: "20.0"
     */
    private String totalSize = "0.0";

    /**
     * 单位 eg: "KB"
     */
    private String unit = "MB";

    /**
     * 拼接完成的字符串  eg: "20.0MB"
     */
    private String resultSize = "0.0MB";

    /**
     * 扫描的文件大小 单位 B
     */
    private long number;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getResultSize() {
        return resultSize;
    }

    public void setResultSize(String resultSize) {
        this.resultSize = resultSize;
    }

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
