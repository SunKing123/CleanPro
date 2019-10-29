package com.xiaoniu.cleanking.ui.main.event;

/**
 * 首页清理完成后，事件
 */
public class HomeCleanEvent {
    boolean isNowClean = false;

    public void setNowClean(boolean nowClean) {
        isNowClean = nowClean;
    }

    public boolean isNowClean() {
        return isNowClean;
    }
}
