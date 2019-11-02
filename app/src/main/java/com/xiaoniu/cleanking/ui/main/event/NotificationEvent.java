package com.xiaoniu.cleanking.ui.main.event;

public class NotificationEvent {
    private String type;
    private int flag = 0;
    private int appendValue = 30;

    public int getAppendValue() {
        return appendValue;
    }

    public void setAppendValue(int appendValue) {
        this.appendValue = appendValue;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
