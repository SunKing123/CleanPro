package com.xiaoniu.cleanking.ui.main.event;

public class CleanEvent {
    private boolean isScanFinish = false;
    private boolean isCleanAminOver = false;

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
