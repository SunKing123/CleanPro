package com.xiaoniu.cleanking.ui.tool.notify.event;

/**
 * Created by wangbz on 2019/5/27.
 */

public class ResidentUpdateEvent {

    private Boolean isAllNotifyClean; //是否所有通知被清理(用于清理通知后改变首页通知栏清理icon和文字状态)

    public ResidentUpdateEvent(Boolean isAllNotifyClean) {
        this.isAllNotifyClean = isAllNotifyClean;
    }

    public Boolean isAllNotifyClean() {
        return isAllNotifyClean;
    }
}
