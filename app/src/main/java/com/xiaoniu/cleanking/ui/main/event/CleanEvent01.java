package com.xiaoniu.cleanking.ui.main.event;

public class CleanEvent01 {
    private boolean isScanFinish = false;
    private boolean isCleanAminOver = false;

    private long totalSize = 0;

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public boolean isCleanAminOver() {
        return isCleanAminOver;
    }

    public void setCleanAminOver(boolean cleanAminOver) {
        isCleanAminOver = cleanAminOver;
    }

    public boolean isScanFinish() {
        return isScanFinish;
    }

    public void setScanFinish(boolean scanFinish) {
        isScanFinish = scanFinish;
    }
}
