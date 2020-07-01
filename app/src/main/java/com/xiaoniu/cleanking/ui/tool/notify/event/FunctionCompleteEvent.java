package com.xiaoniu.cleanking.ui.tool.notify.event;

/**
 * Created by xinxiaolong on 2020/7/1.
 * email：xinxiaolong123@foxmail.com
 */
public class FunctionCompleteEvent {

    private String title;

    public FunctionCompleteEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
