package com.xiaoniu.cleanking.ui.tool.notify.event;

/**
 * Created by Xilei on 2019/10/24.
 * <p>
 * 首页一键加速、通知栏清理、超强省电清理完成 改变首页状态
 */

public class FromHomeCleanFinishEvent {

    private String title;

    public FromHomeCleanFinishEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
