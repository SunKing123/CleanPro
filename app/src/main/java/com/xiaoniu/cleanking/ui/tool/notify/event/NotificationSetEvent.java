package com.xiaoniu.cleanking.ui.tool.notify.event;

public class NotificationSetEvent {
    private boolean mEnable = false;

    public NotificationSetEvent(boolean enable) {
        mEnable = enable;
    }

    public boolean isEnable() {
        return mEnable;
    }
}
